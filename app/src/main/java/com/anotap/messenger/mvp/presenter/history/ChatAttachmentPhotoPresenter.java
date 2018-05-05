package com.anotap.messenger.mvp.presenter.history;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.R;
import com.anotap.messenger.api.Apis;
import com.anotap.messenger.api.model.VKApiPhoto;
import com.anotap.messenger.api.model.response.AttachmentsHistoryResponse;
import com.anotap.messenger.db.Stores;
import com.anotap.messenger.db.serialize.Serializers;
import com.anotap.messenger.domain.mappers.Dto2Model;
import com.anotap.messenger.model.Photo;
import com.anotap.messenger.model.TmpSource;
import com.anotap.messenger.mvp.view.IChatAttachmentPhotosView;
import com.anotap.messenger.util.Analytics;
import com.anotap.messenger.util.DisposableHolder;
import com.anotap.messenger.util.Pair;
import com.anotap.messenger.util.RxUtils;
import com.anotap.mvp.reflect.OnGuiCreated;
import io.reactivex.Single;

import static com.anotap.messenger.util.Objects.nonNull;
import static com.anotap.messenger.util.Utils.safeCountOf;

/**
 * Created by admin on 29.03.2017.
 * phoenix
 */
public class ChatAttachmentPhotoPresenter extends BaseChatAttachmentsPresenter<Photo, IChatAttachmentPhotosView> {

    public ChatAttachmentPhotoPresenter(int peerId, int accountId, @Nullable Bundle savedInstanceState) {
        super(peerId, accountId, savedInstanceState);
    }

    @Override
    Single<Pair<String, List<Photo>>> requestAttachments(int peerId, String nextFrom) {
        return Apis.get().vkDefault(getAccountId())
                .messages()
                .getHistoryAttachments(peerId, "photo", nextFrom, 50, null)
                .map(response -> {
                    List<Photo> photos = new ArrayList<>();

                    for (AttachmentsHistoryResponse.One one : response.items) {
                        if (nonNull(one) && nonNull(one.entry) && one.entry.attachment instanceof VKApiPhoto) {
                            VKApiPhoto dto = (VKApiPhoto) one.entry.attachment;
                            photos.add(Dto2Model.transform(dto));
                        }
                    }

                    return Pair.create(response.next_from, photos);
                });
    }

    @Override
    void onDataChanged() {
        super.onDataChanged();
        resolveToolbar();
    }

    @OnGuiCreated
    private void resolveToolbar() {
        if (isGuiReady()) {
            getView().setToolbarTitle(getString(R.string.attachments_in_chat));
            getView().setToolbarSubtitle(getString(R.string.photos_count, safeCountOf(data)));
        }
    }

    @Override
    protected String tag() {
        return ChatAttachmentPhotoPresenter.class.getSimpleName();
    }

    private DisposableHolder<Void> openGalleryDisposableHolder = new DisposableHolder<>();

    @Override
    public void onDestroyed() {
        openGalleryDisposableHolder.dispose();
        super.onDestroyed();
    }

    @SuppressWarnings("unused")
    public void firePhotoClick(int position, Photo photo) {
        final List<Photo> photos = super.data;

        TmpSource source = new TmpSource(getInstanceId(), 0);

        fireTempDataUsage();

        openGalleryDisposableHolder.append(Stores.getInstance()
                .tempStore()
                .put(source.getOwnerId(), source.getSourceId(), data, Serializers.PHOTOS_SERIALIZER)
                .compose(RxUtils.applyCompletableIOToMainSchedulers())
                .subscribe(() -> onPhotosSavedToTmpStore(position, source), Analytics::logUnexpectedError));
    }

    private void onPhotosSavedToTmpStore(int index, TmpSource source) {
        callView(view -> view.goToTempPhotosGallery(getAccountId(), source, index));
    }
}