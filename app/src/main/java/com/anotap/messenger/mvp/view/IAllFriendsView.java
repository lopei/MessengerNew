package com.anotap.messenger.mvp.view;

import java.util.List;

import com.anotap.messenger.model.User;
import com.anotap.messenger.model.UsersPart;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by Ruslan Kolbasa on 08.09.2017.
 * phoenix
 */
public interface IAllFriendsView extends IMvpView, IErrorView, IAccountDependencyView {
    void notifyDatasetChanged(boolean grouping);
    void setSwipeRefreshEnabled(boolean enabled);

    void displayData(List<UsersPart> data, boolean grouping);

    void notifyItemRangeInserted(int position, int count);

    void showUserWall(int accountId, User user);

    void showRefreshing(boolean refreshing);
}