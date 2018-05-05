package com.anotap.messenger.mvp.view;

import com.anotap.messenger.model.User;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by Ruslan Kolbasa on 21.06.2017.
 * phoenix
 */
public interface ICommunityManagerEditView extends IMvpView, IAccountDependencyView, IErrorView, IProgressView, IToastView {
    void displayUserInfo(User user);

    void showUserProfile(int accountId, User user);

    void checkModerator();
    void checkEditor();
    void checkAdmin();

    void setShowAsContactCheched(boolean cheched);
    void setContactInfoVisible(boolean visible);

    void displayPosition(String position);
    void displayEmail(String email);
    void displayPhone(String phone);

    void configRadioButtons(boolean isCreator);

    void goBack();

    void setDeleteOptionVisible(boolean visible);
}