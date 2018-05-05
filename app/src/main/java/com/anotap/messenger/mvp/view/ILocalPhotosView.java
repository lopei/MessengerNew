package com.anotap.messenger.mvp.view;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.model.LocalPhoto;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 03.10.2016.
 * phoenix
 */
public interface ILocalPhotosView extends IMvpView, IErrorView {
    void displayData(@NonNull List<LocalPhoto> data);
    void setEmptyTextVisible(boolean visible);
    void displayProgress(boolean loading);
    void returnResultToParent(ArrayList<LocalPhoto> photos);
    void updateSelectionAndIndexes();
    void setFabVisible(boolean visible, boolean anim);
}
