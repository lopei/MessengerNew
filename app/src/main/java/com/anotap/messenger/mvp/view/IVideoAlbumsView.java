package com.anotap.messenger.mvp.view;

import android.support.annotation.NonNull;

import java.util.List;

import com.anotap.messenger.model.VideoAlbum;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 21.11.2016.
 * phoenix
 */
public interface IVideoAlbumsView extends IMvpView, IAccountDependencyView, IErrorView {

    void displayData(@NonNull List<VideoAlbum> data);
    void notifyDataAdded(int position, int count);
    void displayLoading(boolean loading);
    void notifyDataSetChanged();

    void openAlbum(int accountId, int ownerId, int albumId, String action, String title);
}
