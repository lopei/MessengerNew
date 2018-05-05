package com.anotap.messenger.mvp.view;

import com.anotap.messenger.model.FriendsCounters;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by Ruslan Kolbasa on 08.09.2017.
 * phoenix
 */
public interface IFriendsTabsView extends IMvpView, IAccountDependencyView, IErrorView {
    void displayConters(FriendsCounters counters);
    void configTabs(int accountId, int userId, boolean showMutualTab);
    void displayUserNameAtToolbar(String userName);

    void setDrawerFriendsSectionSelected(boolean selected);
}