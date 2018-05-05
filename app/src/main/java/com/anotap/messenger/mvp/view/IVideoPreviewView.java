package com.anotap.messenger.mvp.view;

import com.anotap.messenger.model.Commented;
import com.anotap.messenger.model.Video;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 09.07.2017.
 * phoenix
 */
public interface IVideoPreviewView extends IAccountDependencyView, IMvpView, IErrorView {

    void displayLoading();
    void displayLoadingError();
    void displayVideoInfo(Video video);

    void displayLikes(int count, boolean userLikes);
    void setCommentButtonVisible(boolean visible);
    void displayCommentCount(int count);

    void showSuccessToast();

    void showOwnerWall(int accountId, int ownerId);

    void showSubtitle(String subtitle);

    void showComments(int accountId, Commented commented);

    interface IOptionView {
        void setCanAdd(boolean can);
    }

    void displayShareDialog(int accountId, Video video, boolean canPostToMyWall);

    void showVideoPlayMenu(int accountId, Video video);
}