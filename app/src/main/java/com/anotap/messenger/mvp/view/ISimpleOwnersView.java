package com.anotap.messenger.mvp.view;

import java.util.List;

import com.anotap.messenger.model.Owner;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by Ruslan Kolbasa on 08.09.2017.
 * phoenix
 */
public interface ISimpleOwnersView extends IMvpView, IErrorView, IAccountDependencyView {
    void displayOwnerList(List<Owner> owners);
    void notifyDataSetChanged();
    void notifyDataAdded(int position, int count);

    void displayRefreshing(boolean refreshing);
    void showOwnerWall(int accoutnId, Owner owner);
}