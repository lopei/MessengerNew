package com.anotap.messenger.mvp.view;

import java.util.List;

import com.anotap.messenger.model.Audio;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 1/4/2018.
 * Phoenix-for-VK
 */
public interface IAudiosView extends IMvpView, IErrorView, IAccountDependencyView {
    void displayList(List<Audio> audios);
    void notifyListChanged();
    void displayRefreshing(boolean refresing);
    void setBlockedScreen(boolean visible);
}