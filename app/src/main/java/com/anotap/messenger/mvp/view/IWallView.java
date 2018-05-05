package com.anotap.messenger.mvp.view;

import android.support.annotation.Nullable;

import java.util.List;

import com.anotap.messenger.model.EditingPostType;
import com.anotap.messenger.model.LoadMoreState;
import com.anotap.messenger.model.Owner;
import com.anotap.messenger.model.Post;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by ruslan.kolbasa on 23.01.2017.
 * phoenix
 */
public interface IWallView extends IAttachmentsPlacesView,
        IAccountDependencyView, IMvpView, ISnackbarView, IErrorView, IToastView {

    void displayWallData(List<Post> data);

    void notifyWallDataSetChanged();

    void notifyWallItemChanged(int position);

    void notifyWallDataAdded(int position, int count);

    void setupLoadMoreFooter(@LoadMoreState int state);

    void showRefreshing(boolean refreshing);

    void openPhotoAlbums(int accountId, int ownerId, @Nullable Owner owner);

    void openAudios(int accountId, int ownerId, @Nullable Owner owner);

    void openVideosLibrary(int accountId, int ownerId, @Nullable Owner owner);

    void goToPostCreation(int accountId, int ownerId, @EditingPostType int postType);

    void copyToClipboard(String label, String body);

    void openPhotoAlbum(int accountId, int ownerId, int albumId, @Nullable Integer focusPhotoId);

    void goToWallSearch(int accountId, int ownerId);

    void openPostEditor(int accountId, Post post);

    void notifyWallItemRemoved(int index);
}