package com.anotap.messenger.mvp.view;

import java.util.List;

import com.anotap.messenger.model.User;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by Ruslan Kolbasa on 11.09.2017.
 * phoenix
 */
public interface IFaveUsersView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(List<User> videos);
    void notifyDataSetChanged();
    void notifyDataAdded(int position, int count);
    void showRefreshing(boolean refreshing);
    void openUserWall(int accountId, User user);

    void notifyItemRemoved(int index);
}