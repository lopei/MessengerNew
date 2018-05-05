package com.anotap.messenger.mvp.view;

import java.util.List;

import com.anotap.messenger.model.AppChatUser;
import com.anotap.messenger.model.User;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by Ruslan Kolbasa on 19.09.2017.
 * phoenix
 */
public interface IChatMembersView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(List<AppChatUser> users);
    void notifyItemRemoved(int position);
    void notifyDataSetChanged();
    void notifyDataAdded(int position, int count);
    void openUserWall(int accountId, User user);
    void displayRefreshing(boolean refreshing);

    void startSelectUsersActivity(int accountId);
}