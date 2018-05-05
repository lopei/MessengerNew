package com.anotap.messenger.mvp.view;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.model.Banned;
import com.anotap.messenger.model.User;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 13.06.2017.
 * phoenix
 */
public interface ICommunityBlacklistView extends IAccountDependencyView, IErrorView, IMvpView, IToastView {

    void displayRefreshing(boolean loadingNow);

    void notifyDataSetChanged();

    void diplayData(List<Banned> data);

    void notifyItemRemoved(int index);

    void openBanEditor(int accountId, int groupId, Banned banned);

    void startSelectProfilesActivity(int accountId, int groupId);

    void addUsersToBan(int accountId, int groupId, ArrayList<User> users);

    void notifyItemsAdded(int position, int size);
}
