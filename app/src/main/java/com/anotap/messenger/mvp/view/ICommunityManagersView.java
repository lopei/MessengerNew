package com.anotap.messenger.mvp.view;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.model.Manager;
import com.anotap.messenger.model.User;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 13.06.2017.
 * phoenix
 */
public interface ICommunityManagersView extends IAccountDependencyView, IErrorView, IMvpView, IToastView {

    void notifyDataSetChanged();

    void displayRefreshing(boolean loadingNow);

    void displayData(List<Manager> managers);

    void goToManagerEditing(int accountId, int groupId, Manager manager);

    void showUserProfile(int accountId, User user);

    void startSelectProfilesActivity(int accountId, int groupId);

    void startAddingUsersToManagers(int accountId, int groupId, ArrayList<User> users);

    void notifyItemRemoved(int index);

    void notifyItemChanged(int index);

    void notifyItemAdded(int index);
}
