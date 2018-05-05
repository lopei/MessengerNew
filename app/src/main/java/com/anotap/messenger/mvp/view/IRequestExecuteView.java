package com.anotap.messenger.mvp.view;

import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by Ruslan Kolbasa on 05.07.2017.
 * phoenix
 */
public interface IRequestExecuteView extends IMvpView, IErrorView, IProgressView, IAccountDependencyView, IToastView {
    void displayBody(String body);
    void hideKeyboard();
    void requestWriteExternalStoragePermission();
}