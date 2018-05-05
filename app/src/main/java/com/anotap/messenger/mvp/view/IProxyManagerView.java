package com.anotap.messenger.mvp.view;

import java.util.List;

import com.anotap.messenger.model.ProxyConfig;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 10.07.2017.
 * phoenix
 */
public interface IProxyManagerView extends IMvpView, IErrorView {
    void displayData(List<ProxyConfig> configs, ProxyConfig active);

    void notifyItemAdded(int position);
    void notifyItemRemoved(int position);

    void setActiveAndNotifyDataSetChanged(ProxyConfig config);
    void goToAddingScreen();
}