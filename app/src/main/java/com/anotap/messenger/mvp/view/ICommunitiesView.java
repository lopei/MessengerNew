package com.anotap.messenger.mvp.view;

import com.anotap.messenger.model.Community;
import com.anotap.messenger.model.DataWrapper;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by Ruslan Kolbasa on 19.09.2017.
 * phoenix
 */
public interface ICommunitiesView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(DataWrapper<Community> own, DataWrapper<Community> filtered, DataWrapper<Community> seacrh);
    void notifyDataSetChanged();
    void notifyOwnDataAdded(int position, int count);
    void displayRefreshing(boolean refreshing);

    void showCommunityWall(int accountId, Community community);

    void notifySeacrhDataAdded(int position, int count);
}