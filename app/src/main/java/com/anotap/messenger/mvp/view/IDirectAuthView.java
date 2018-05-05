package com.anotap.messenger.mvp.view;

import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 16.07.2017.
 * phoenix
 */
public interface IDirectAuthView extends IMvpView, IErrorView {
    void setLoginButtonEnabled(boolean enabled);
    void setSmsRootVisible(boolean visible);
    void setAppCodeRootVisible(boolean visible);

    void moveFocusToSmsCode();
    void moveFocusToAppCode();

    void displayLoading(boolean loading);

    void setCaptchaRootVisible(boolean visible);

    void displayCaptchaImage(String img);

    void moveFocusToCaptcha();

    void hideKeyboard();

    void returnSuccessToParent(int userId, String accessToken);

    void returnLoginViaWebAction();
}