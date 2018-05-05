package com.anotap.messenger.mvp.view;

import java.util.List;

import com.anotap.messenger.model.Photo;
import com.anotap.messenger.model.wrappers.SelectablePhotoWrapper;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.messenger.upload.UploadObject;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by Ruslan Kolbasa on 13.07.2017.
 * phoenix
 */
public interface IVkPhotosView extends IMvpView, IAccountDependencyView, IErrorView, IToolbarView {
    String ACTION_SHOW_PHOTOS = "com.anotap.messenger.ACTION_SHOW_PHOTOS";
    String ACTION_SELECT_PHOTOS = "com.anotap.messenger.ACTION_SELECT_PHOTOS";

    void displayData(List<SelectablePhotoWrapper> photos, List<UploadObject> uploads);
    void notifyDataSetChanged();
    void notifyPhotosAdded(int position, int count);

    void displayRefreshing(boolean refreshing);

    void notifyUploadAdded(int position, int count);

    void notifyUploadRemoved(int index);

    void setButtonAddVisible(boolean visible, boolean anim);

    void notifyUploadItemChanged(int index);

    void notifyUploadProgressChanged(int id, int progress);

    void displayGallery(int accountId, int albumId, int ownerId, Integer focusToId);

    void displayDefaultToolbarTitle();

    void setDrawerPhotosSelected(boolean selected);

    void returnSelectionToParent(List<Photo> selected);

    void showSelectPhotosToast();

    void startLocalPhotosSelection();

    void startLocalPhotosSelectionIfHasPermission();
}