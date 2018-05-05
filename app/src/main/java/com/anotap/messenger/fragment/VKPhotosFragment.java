package com.anotap.messenger.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.ActivityFeatures;
import com.anotap.messenger.activity.PhotosActivity;
import com.anotap.messenger.adapter.BigVkPhotosAdapter;
import com.anotap.messenger.dialog.ImageSizeAlertDialog;
import com.anotap.messenger.fragment.base.BasePresenterFragment;
import com.anotap.messenger.listener.EndlessRecyclerOnScrollListener;
import com.anotap.messenger.listener.OnSectionResumeCallback;
import com.anotap.messenger.listener.PicassoPauseOnScrollListener;
import com.anotap.messenger.model.LocalPhoto;
import com.anotap.messenger.model.Owner;
import com.anotap.messenger.model.ParcelableOwnerWrapper;
import com.anotap.messenger.model.Photo;
import com.anotap.messenger.model.PhotoAlbum;
import com.anotap.messenger.model.wrappers.SelectablePhotoWrapper;
import com.anotap.messenger.mvp.presenter.VkPhotosPresenter;
import com.anotap.messenger.mvp.view.IVkPhotosView;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.upload.UploadObject;
import com.anotap.messenger.util.AppPerms;
import com.anotap.messenger.util.ViewUtils;
import com.anotap.mvp.core.IPresenterFactory;

import static com.anotap.messenger.util.Objects.nonNull;
import static com.anotap.messenger.util.Utils.nonEmpty;

public class VKPhotosFragment extends BasePresenterFragment<VkPhotosPresenter, IVkPhotosView>
        implements BigVkPhotosAdapter.PhotosActionListener, BigVkPhotosAdapter.UploadActionListener, IVkPhotosView {

    private static final String TAG = VKPhotosFragment.class.getSimpleName();
    private static final int REQUEST_PERMISSION_READ_EXTARNAL_STORAGE = 14;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BigVkPhotosAdapter mAdapter;
    private TextView mEmptyText;
    private FloatingActionButton mFab;
    private String mAction;

    public static Bundle buildArgs(int accountId, int ownerId, int albumId, String action) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putInt(Extra.OWNER_ID, ownerId);
        args.putInt(Extra.ALBUM_ID, albumId);
        args.putString(Extra.ACTION, action);
        return args;
    }

    public static VKPhotosFragment newInstance(Bundle args) {
        VKPhotosFragment fragment = new VKPhotosFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static VKPhotosFragment newInstance(int accountId, int ownerId, int albumId, String action) {
        return newInstance(buildArgs(accountId, ownerId, albumId, action));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mAction = getArguments().getString(Extra.ACTION, ACTION_SHOW_PHOTOS);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));

        int columnCount = getResources().getInteger(R.integer.local_gallery_column_count);
        RecyclerView.LayoutManager manager = new GridLayoutManager(getActivity(), columnCount);

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().fireRefresh());

        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(getActivity(), mSwipeRefreshLayout);

        RecyclerView mRecyclerView = root.findViewById(R.id.list);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addOnScrollListener(new PicassoPauseOnScrollListener(TAG));
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onScrollToLastElement() {
                getPresenter().fireScrollToEnd();
            }
        });

        mEmptyText = root.findViewById(R.id.empty);

        mFab = root.findViewById(R.id.fr_photo_gallery_attach);
        mFab.setOnClickListener(v -> onFabClicked());

        mAdapter = new BigVkPhotosAdapter(getActivity(), Collections.emptyList(), Collections.emptyList(), TAG);
        mAdapter.setPhotosActionListener(this);
        mAdapter.setUploadActionListener(this);
        mRecyclerView.setAdapter(mAdapter);
        return root;
    }

    private void resolveEmptyTextVisibility() {
        if (nonNull(mEmptyText) && nonNull(mAdapter)) {
            mEmptyText.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    private void resolveFabVisibility(boolean anim, boolean show) {
        if (!isAdded() || mFab == null) return;

        if (mFab.isShown() && !show) {
            mFab.hide();
        }

        if (!mFab.isShown() && show) {
            mFab.show();
        }
    }

    private void onFabClicked() {
        if(isSelectionMode()){
            getPresenter().fireSelectionCommitClick();
        } else {
            getPresenter().fireAddPhotosClick();
        }
    }

    private boolean isSelectionMode() {
        return ACTION_SELECT_PHOTOS.equals(mAction);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_UPLOAD_LOCAL_PHOTO && resultCode == Activity.RESULT_OK) {
            ArrayList<LocalPhoto> photos = data.getParcelableArrayListExtra(Extra.PHOTOS);

            if (nonEmpty(photos)) {
                onPhotosForUploadSelected(photos);
            }
        }
    }

    private void onPhotosForUploadSelected(@NonNull final List<LocalPhoto> photos) {
        ImageSizeAlertDialog.showUploadPhotoSizeIfNeed(getActivity(), size -> doUploadPhotosToAlbum(photos, size));
    }

    private void doUploadPhotosToAlbum(@NonNull List<LocalPhoto> photos, int size) {
        getPresenter().firePhotosForUploadSelected(photos, size);
    }

    @Override
    public void onResume() {
        super.onResume();
        new ActivityFeatures.Builder()
                .begin()
                .setBlockNavigationDrawer(false)
                .setStatusBarColored(getActivity(),true)
                .build()
                .apply(getActivity());
    }

    @Override
    public void onDestroyView() {
        mAdapter.cleanup();
        super.onDestroyView();
    }

    @Override
    protected String tag() {
        return TAG;
    }

    @Override
    public void onPhotoClick(BigVkPhotosAdapter.PhotoViewHolder holder, SelectablePhotoWrapper wrapper) {
        if(isSelectionMode()){
            getPresenter().firePhotoSelectionChanged(wrapper);
            mAdapter.updatePhotoHoldersSelectionAndIndexes();
        } else {
            getPresenter().firePhotoClick(wrapper);
        }
    }

    @Override
    public void onUploadRemoveClicked(UploadObject uploadObject) {
        getPresenter().fireUploadRemoveClick(uploadObject);
    }

    @Override
    public void displayData(List<SelectablePhotoWrapper> photos, List<UploadObject> uploads) {
        if (nonNull(mAdapter)) {
            mAdapter.setData(BigVkPhotosAdapter.DATA_TYPE_UPLOAD, uploads);
            mAdapter.setData(BigVkPhotosAdapter.DATA_TYPE_PHOTO, photos);
            mAdapter.notifyDataSetChanged();
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if (nonNull(mAdapter)) {
            mAdapter.notifyDataSetChanged();
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void notifyPhotosAdded(int position, int count) {
        if (nonNull(mAdapter)) {
            mAdapter.notifyItemRangeInserted(position, count, BigVkPhotosAdapter.DATA_TYPE_PHOTO);
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void displayRefreshing(boolean refreshing) {
        if (nonNull(mSwipeRefreshLayout)) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    @Override
    public void notifyUploadAdded(int position, int count) {
        if (nonNull(mAdapter)) {
            mAdapter.notifyItemRangeInserted(position, count, BigVkPhotosAdapter.DATA_TYPE_UPLOAD);
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void notifyUploadRemoved(int index) {
        if (nonNull(mAdapter)) {
            mAdapter.notifyItemRemoved(index, BigVkPhotosAdapter.DATA_TYPE_UPLOAD);
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void setButtonAddVisible(boolean visible, boolean anim) {
        if (nonNull(mFab)) {
            resolveFabVisibility(anim, visible);
        }
    }

    @Override
    public void notifyUploadItemChanged(int index) {
        if (nonNull(mAdapter)) {
            mAdapter.notifyItemChanged(index, BigVkPhotosAdapter.DATA_TYPE_UPLOAD);
        }
    }

    @Override
    public void notifyUploadProgressChanged(int id, int progress) {
        if (nonNull(mAdapter)) {
            mAdapter.updateUploadHoldersProgress(id, true, progress);
        }
    }

    @Override
    public void displayGallery(int accountId, int albumId, int ownerId, Integer focusToId) {
        PlaceFactory.getPhotoAlbumGalleryPlace(accountId, albumId, ownerId, focusToId).tryOpenWith(getActivity());
    }

    @Override
    public void displayDefaultToolbarTitle() {
        super.setToolbarTitle(getString(R.string.photos));
    }

    @Override
    public void setDrawerPhotosSelected(boolean selected) {
        if(getActivity() instanceof OnSectionResumeCallback){
            if (selected) {
                ((OnSectionResumeCallback) getActivity()).onSectionResume(NavigationFragment.SECTION_ITEM_PHOTOS);
            } else {
                ((OnSectionResumeCallback) getActivity()).onClearSelection();
            }
        }
    }

    @Override
    public void returnSelectionToParent(List<Photo> selected) {
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(Extra.ATTACHMENTS, new ArrayList<>(selected));
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void showSelectPhotosToast() {
        Toast.makeText(getActivity(), getString(R.string.select_attachments), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startLocalPhotosSelection() {
        if (!AppPerms.hasReadStoragePermision(getActivity())) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTARNAL_STORAGE);
            return;
        }

        startLocalPhotosSelectionActibity();
    }

    private static final int REQUEST_UPLOAD_LOCAL_PHOTO = 121;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(REQUEST_PERMISSION_READ_EXTARNAL_STORAGE == requestCode){
            getPresenter().fireReadStoragePermissionChanged();
        }
    }

    private void startLocalPhotosSelectionActibity(){
        Intent intent = new Intent(getActivity(), PhotosActivity.class);
        intent.putExtra(PhotosActivity.EXTRA_MAX_SELECTION_COUNT, Integer.MAX_VALUE);
        startActivityForResult(intent, REQUEST_UPLOAD_LOCAL_PHOTO);
    }

    @Override
    public void startLocalPhotosSelectionIfHasPermission() {
        if(AppPerms.hasReadStoragePermision(getActivity())){
            startLocalPhotosSelectionActibity();
        }
    }

    @Override
    public IPresenterFactory<VkPhotosPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            ParcelableOwnerWrapper ownerWrapper = getArguments().getParcelable(Extra.OWNER);
            Owner owner = nonNull(ownerWrapper) ? ownerWrapper.get() : null;
            PhotoAlbum album = getArguments().getParcelable(Extra.ALBUM);

            return new VkPhotosPresenter(
                    getArguments().getInt(Extra.ACCOUNT_ID),
                    getArguments().getInt(Extra.OWNER_ID),
                    getArguments().getInt(Extra.ALBUM_ID),
                    getArguments().getString(Extra.ACTION, ACTION_SHOW_PHOTOS),
                    owner,
                    album,
                    saveInstanceState
            );
        };
    }
}