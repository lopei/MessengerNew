package com.anotap.messenger.mvp.view;

import android.support.annotation.NonNull;

import com.anotap.messenger.model.Document;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by ruslan.kolbasa on 11.10.2016.
 * phoenix
 */
public interface IBasicDocumentView extends IMvpView, IAccountDependencyView, IToastView, IErrorView {

    void shareDocument(int accountId, @NonNull Document document);
    void requestWriteExternalStoragePermission();

}
