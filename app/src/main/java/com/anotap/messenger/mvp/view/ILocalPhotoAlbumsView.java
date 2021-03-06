package com.anotap.messenger.mvp.view;

import android.support.annotation.NonNull;

import java.util.List;

import com.anotap.messenger.model.LocalImageAlbum;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 03.10.2016.
 * phoenix
 */
public interface ILocalPhotoAlbumsView extends IMvpView {

    void displayData(@NonNull List<LocalImageAlbum> data);
    void setEmptyTextVisible(boolean visible);
    void displayProgress(boolean loading);
    void openAlbum(@NonNull LocalImageAlbum album);
    void notifyDataChanged();

    void requestReadExternalStoragePermission();
}
