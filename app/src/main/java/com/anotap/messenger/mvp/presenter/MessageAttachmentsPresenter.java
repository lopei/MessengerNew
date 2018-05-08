package com.anotap.messenger.mvp.presenter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.anotap.messenger.Injection;
import com.anotap.messenger.db.AttachToType;
import com.anotap.messenger.db.Stores;
import com.anotap.messenger.db.interfaces.IStores;
import com.anotap.messenger.db.interfaces.IUploadQueueStore;
import com.anotap.messenger.domain.IAttachmentsRepository;
import com.anotap.messenger.model.AbsModel;
import com.anotap.messenger.model.AttachmenEntry;
import com.anotap.messenger.model.LocalPhoto;
import com.anotap.messenger.model.ModelsBundle;
import com.anotap.messenger.model.Photo;
import com.anotap.messenger.mvp.presenter.base.RxSupportPresenter;
import com.anotap.messenger.mvp.view.IMessageAttachmentsView;
import com.anotap.messenger.settings.Settings;
import com.anotap.messenger.upload.UploadDestination;
import com.anotap.messenger.upload.UploadIntent;
import com.anotap.messenger.upload.UploadObject;
import com.anotap.messenger.upload.UploadUtils;
import com.anotap.messenger.util.Analytics;
import com.anotap.messenger.util.AppPerms;
import com.anotap.messenger.util.AssertUtils;
import com.anotap.messenger.util.FileUtil;
import com.anotap.messenger.util.Pair;
import com.anotap.messenger.util.RxUtils;
import com.anotap.mvp.reflect.OnGuiCreated;
import io.reactivex.Single;
import io.reactivex.functions.Predicate;

import static com.anotap.messenger.util.Objects.isNull;
import static com.anotap.messenger.util.Objects.nonNull;
import static com.anotap.messenger.util.Utils.findIndexByPredicate;
import static com.anotap.messenger.util.Utils.nonEmpty;

/**
 * Created by admin on 14.04.2017.
 * phoenix
 */
public class MessageAttachmentsPresenter extends RxSupportPresenter<IMessageAttachmentsView> {

    private static final String TAG = MessageAttachmentsPresenter.class.getSimpleName();

    private final int accountId;
    private final int messageOwnerId;
    private final int messageId;
    private final List<AttachmenEntry> entries;

    private final IAttachmentsRepository attachmentsRepository;
    private final IStores repositories;
    private final UploadDestination destination;

    private Uri currentPhotoCameraUri;

    public MessageAttachmentsPresenter(int accountId, int messageOwnerId, int messageId, @Nullable ModelsBundle bundle, @Nullable Bundle savedInstanceState) {
        super(savedInstanceState);
        this.accountId = accountId;
        this.messageId = messageId;
        this.messageOwnerId = messageOwnerId;
        this.destination = UploadDestination.forMessage(messageId);
        this.entries = new ArrayList<>();
        this.repositories = Stores.getInstance();
        this.attachmentsRepository = Injection.provideAttachmentsRepository();

        if(nonNull(savedInstanceState)){
            this.currentPhotoCameraUri = savedInstanceState.getParcelable(SAVE_CAMERA_FILE_URI);
            ArrayList<AttachmenEntry> accompanying = savedInstanceState.getParcelableArrayList(SAVE_ACCOMPANYING_ENTRIES);
            AssertUtils.requireNonNull(accompanying);
            this.entries.addAll(accompanying);
        } else {
            handleInputModels(bundle);
        }

        loadData();

        Predicate<IAttachmentsRepository.IBaseEvent> predicate = event -> event.getAttachToType() == AttachToType.MESSAGE
                && event.getAttachToId() == messageId
                && event.getAccountId() == messageOwnerId;

        appendDisposable(attachmentsRepository
                .observeAdding()
                .filter(predicate)
                .observeOn(Injection.provideMainThreadScheduler())
                .subscribe(event -> onAttachmentsAdded(event.getAttachments())));

        appendDisposable(attachmentsRepository
                .observeRemoving()
                .filter(predicate)
                .observeOn(Injection.provideMainThreadScheduler())
                .subscribe(event -> onAttachmentRemoved(event.getGeneratedId())));

        appendDisposable(repositories.uploads()
                .observeQueue()
                .observeOn(Injection.provideMainThreadScheduler())
                .subscribe(this::onUploadQueueChanged));

        appendDisposable(repositories.uploads()
                .observeStatusUpdates()
                .observeOn(Injection.provideMainThreadScheduler())
                .subscribe(this::onUploadStatusChanges));

        appendDisposable(repositories.uploads()
                .observeProgress()
                .observeOn(Injection.provideMainThreadScheduler())
                .subscribe(this::onUploadProgressUpdates));
    }

    private void handleInputModels(ModelsBundle bundle){
        if(isNull(bundle)){
            return;
        }

        for(AbsModel model : bundle){
            this.entries.add(new AttachmenEntry(true, model)
                    .setAccompanying(true));
        }
    }

    @OnGuiCreated
    private void resolveEmptyViewVisibility(){
        if(isGuiReady()){
            getView().setEmptyViewVisible(entries.isEmpty());
        }
    }

    private void onUploadProgressUpdates(List<IUploadQueueStore.IProgressUpdate> updates) {
        for (IUploadQueueStore.IProgressUpdate update : updates) {
            int index = findUploadObjectIndex(update.getId());
            if (index != -1) {
                UploadObject upload = (UploadObject) entries.get(index).getAttachment();
                if (upload.getStatus() != UploadObject.STATUS_UPLOADING) {
                    // for uploading only
                    continue;
                }

                upload.setProgress(update.getProgress());
                callView(view -> view.changePercentageSmoothly(index, update.getProgress()));
            }
        }
    }

    private void onUploadStatusChanges(IUploadQueueStore.IStatusUpdate update) {
        int index = findUploadObjectIndex(update.getId());
        if (index != -1) {
            ((UploadObject) entries.get(index).getAttachment()).setStatus(update.getStatus());
            callView(view -> view.notifyItemChanged(index));
        }
    }

    private void onUploadQueueChanged(List<IUploadQueueStore.IQueueUpdate> updates) {
        int addCount = 0;
        for (int i = updates.size() - 1; i >= 0; i--) {
            IUploadQueueStore.IQueueUpdate update = updates.get(i);

            if (update.isAdding()) {
                UploadObject o = update.object();
                AttachmenEntry entry = new AttachmenEntry(true, o);

                entries.add(0, entry);
                addCount++;
            } else {
                int index = findUploadObjectIndex(update.getId());
                if (index != -1) {
                    entries.remove(index);
                    callView(view -> view.notifyEntryRemoved(index));
                }
            }
        }

        final int finalAddCount = addCount;
        callView(view -> view.notifyDataAdded(0, finalAddCount));
        resolveEmptyViewVisibility();
    }

    private int findUploadObjectIndex(int id) {
        return findIndexByPredicate(entries, entry -> {
            AbsModel model = entry.getAttachment();
            return model instanceof UploadObject && ((UploadObject) model).getId() == id;
        });
    }

    private void onAttachmentRemoved(int optionId) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getOptionalId() == optionId) {
                entries.remove(i);
                int finalI = i;
                callView(view -> view.notifyEntryRemoved(finalI));
                break;
            }
        }

        resolveEmptyViewVisibility();
    }

    private static List<AttachmenEntry> create(List<Pair<Integer, AbsModel>> pairs) {
        List<AttachmenEntry> entries = new ArrayList<>(pairs.size());
        for (Pair<Integer, AbsModel> pair : pairs) {
            entries.add(new AttachmenEntry(true, pair.getSecond())
                    .setOptionalId(pair.getFirst()));
        }
        return entries;
    }

    private void onAttachmentsAdded(List<Pair<Integer, AbsModel>> pairs) {
        onDataReceived(create(pairs));
    }

    private void loadData() {
        appendDisposable(createLoadAllSingle()
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onDataReceived, Analytics::logUnexpectedError));
    }

    private Single<List<AttachmenEntry>> createLoadAllSingle() {
        return attachmentsRepository
                .getAttachmentsWithIds(messageOwnerId, AttachToType.MESSAGE, messageId)
                .map(MessageAttachmentsPresenter::create)
                .zipWith(repositories.uploads().getByDestination(messageOwnerId, destination), (atts, uploads) -> {
                    List<AttachmenEntry> data = new ArrayList<>(atts.size() + uploads.size());
                    for (UploadObject u : uploads) {
                        data.add(new AttachmenEntry(true, u));
                    }

                    data.addAll(atts);
                    return data;
                });
    }

    private void onDataReceived(List<AttachmenEntry> data) {
        if (data.isEmpty()) {
            return;
        }

        int startCount = this.entries.size();
        this.entries.addAll(data);

        resolveEmptyViewVisibility();
        callView(view -> view.notifyDataAdded(startCount, data.size()));
    }

    @Override
    public void onGuiCreated(@NonNull IMessageAttachmentsView viewHost) {
        super.onGuiCreated(viewHost);
        viewHost.displayAttachments(entries);
    }

    @Override
    protected String tag() {
        return TAG;
    }

    public void fireAddPhotoButtonClick() {
        // Если сообщения группы - предлагать фотографии сообщества, а не группы
        getView().addPhoto(accountId, messageOwnerId);
    }

    public void firePhotosSelected(ArrayList<Photo> photos, ArrayList<LocalPhoto> localPhotos) {
        if (nonEmpty(photos)) {
            fireAttachmentsSelected(photos);
        } else if (nonEmpty(localPhotos)) {
            doUploadPhotos(localPhotos);
        }
    }

    private void doUploadPhotos(List<LocalPhoto> photos) {
        Integer size = Settings.get()
                .main()
                .getUploadImageSize();

        if (isNull(size)) {
            getView().displaySelectUploadPhotoSizeDialog(photos);
        } else {
            doUploadPhotos(photos, size);
        }
    }

    private void doUploadPhotos(List<LocalPhoto> photos, int size) {
        List<UploadIntent> intents = UploadUtils.createIntents(messageOwnerId, destination, photos, size, true);
        UploadUtils.upload(getApplicationContext(), intents);
    }

    public void fireRemoveClick(AttachmenEntry entry) {
        if (entry.getOptionalId() != 0) {
            attachmentsRepository
                    .remove(messageOwnerId, AttachToType.MESSAGE, messageId, entry.getOptionalId())
                    .compose(RxUtils.applyCompletableIOToMainSchedulers())
                    .subscribe(() -> {}, Analytics::logUnexpectedError);
            return;
        }

        if(entry.getAttachment() instanceof UploadObject){
            UploadUtils.cancelById(getApplicationContext(), ((UploadObject) entry.getAttachment()).getId());
            return;
        }

        if(entry.isAccompanying()){
            for(int i = 0; i < entries.size(); i++){
                if(entries.get(i).getId() == entry.getId()){
                    entries.remove(i);
                    getView().notifyEntryRemoved(i);
                    syncAccompanyingWithParent();
                    break;
                }
            }
        }
    }

    public void fireUploadPhotoSizeSelected(List<LocalPhoto> photos, int imageSize) {
        doUploadPhotos(photos, imageSize);
    }

    public void fireCameraAndWritePermissionResolved() {
        if(AppPerms.hasCameraPermision(getApplicationContext())){
            makePhotoInternal();
        }
    }

    public void fireButtonCameraClick() {
        if(AppPerms.hasCameraPermision(getApplicationContext()) && AppPerms.hasWriteStoragePermision(getApplicationContext())){
            makePhotoInternal();
        } else {
            getView().requestCameraAndWritePermission();
        }
    }

    private void makePhotoInternal(){
        try {
            File file = FileUtil.createImageFile();
            this.currentPhotoCameraUri = FileUtil.getExportedUriForFile(getApplicationContext(), file);
            getView().startCamera(currentPhotoCameraUri);
        } catch (IOException e) {
            e.printStackTrace();

            safeShowError(getView(), e.getMessage());
        }
    }

    private static final String SAVE_CAMERA_FILE_URI = "save_camera_file_uri";
    private static final String SAVE_ACCOMPANYING_ENTRIES = "save_accompanying_entries";

    @Override
    public void saveState(@NonNull Bundle outState) {
        super.saveState(outState);
        outState.putParcelable(SAVE_CAMERA_FILE_URI, currentPhotoCameraUri);

        // сохраняем в outState только неПерсистентные данные
        ArrayList<AttachmenEntry> accompanying = new ArrayList<>();
        for(AttachmenEntry entry : entries){
            if(entry.isAccompanying()){
                accompanying.add(entry);
            }
        }

        outState.putParcelableArrayList(SAVE_ACCOMPANYING_ENTRIES, accompanying);
    }

    private void syncAccompanyingWithParent(){
        ModelsBundle bundle = new ModelsBundle();
        for(AttachmenEntry entry : entries){
            if(entry.isAccompanying()){
                bundle.append(entry.getAttachment());
            }
        }

        getView().syncAccompanyingWithParent(bundle);
    }

    public void firePhotoMaked() {
        final Uri uri = this.currentPhotoCameraUri;
        this.currentPhotoCameraUri = null;

        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        getApplicationContext().sendBroadcast(scanIntent);

        LocalPhoto makedPhoto = new LocalPhoto().setFullImageUri(uri);
        doUploadPhotos(Collections.singletonList(makedPhoto));
    }

    public void fireButtonVideoClick() {
        getView().startAddVideoActivity(accountId, messageOwnerId);
    }

    public void fireButtonDocClick() {
        getView().startAddDocumentActivity(accountId); // TODO: 16.08.2017
    }

    public void fireAttachmentsSelected(ArrayList<? extends AbsModel> attachments) {
        attachmentsRepository
                .attach(messageOwnerId, AttachToType.MESSAGE, messageId, attachments)
                .compose(RxUtils.applyCompletableIOToMainSchedulers())
                .subscribe(() -> {}, Analytics::logUnexpectedError);
    }
}