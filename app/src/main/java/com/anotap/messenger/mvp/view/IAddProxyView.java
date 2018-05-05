package com.anotap.messenger.mvp.view;

import com.anotap.mvp.core.IMvpView;

/**
 * Created by Ruslan Kolbasa on 11.07.2017.
 * phoenix
 */
public interface IAddProxyView extends IMvpView, IErrorView {
    void setAuthFieldsEnabled(boolean enabled);
    void setAuthChecked(boolean checked);

    void goBack();
}
