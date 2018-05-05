package com.anotap.messenger.mvp.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import com.anotap.messenger.model.Owner;
import com.anotap.messenger.model.PhotoAlbum;
import com.anotap.messenger.model.PhotoAlbumEditor;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by ruslan.kolbasa on 29.11.2016.
 * phoenix
 */
public interface IPhotoAlbumsView extends IMvpView, IAccountDependencyView, IErrorView {

    void displayData(@NonNull List<PhotoAlbum> data);
    void displayLoading(boolean loading);

    void notifyDataSetChanged();
    void setToolbarSubtitle(String subtitle);
    void openAlbum(int accountId, @NonNull PhotoAlbum album, @Nullable Owner owner, @Nullable String action);
    void showAlbumContextMenu(@NonNull PhotoAlbum album);
    void showDeleteConfirmDialog(@NonNull PhotoAlbum album);
    void doSelection(@NonNull PhotoAlbum album);
    void setCreateAlbumFabVisible(boolean visible);
    void goToAlbumCreation(int accountId, int ownerId);
    void goToAlbumEditing(int accountId, @NonNull PhotoAlbum album, @NonNull PhotoAlbumEditor editor);
    void seDrawertPhotoSectionActive(boolean active);
    void notifyItemRemoved(int index);

    void notifyDataAdded(int position, int size);
}