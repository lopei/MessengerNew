package com.anotap.messenger.mvp.view;

import java.util.List;

import com.anotap.messenger.model.User;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 09.07.2017.
 * phoenix
 */
public interface IUserBannedView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayUserList(List<User> users);

    void notifyItemsAdded(int position, int count);
    void notifyDataSetChanged();
    void notifyItemRemoved(int position);

    void displayRefreshing(boolean refreshing);

    void startUserSelection(int accountId);
    void showSuccessToast();

    void scrollToPosition(int position);

    void showUserProfile(int accountId, User user);
}