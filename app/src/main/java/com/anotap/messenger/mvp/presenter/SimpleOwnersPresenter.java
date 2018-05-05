package com.anotap.messenger.mvp.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.model.Owner;
import com.anotap.messenger.mvp.presenter.base.AccountDependencyPresenter;
import com.anotap.messenger.mvp.view.ISimpleOwnersView;

/**
 * Created by Ruslan Kolbasa on 08.09.2017.
 * phoenix
 */
public abstract class SimpleOwnersPresenter<V extends ISimpleOwnersView> extends AccountDependencyPresenter<V> {

    List<Owner> data;

    public SimpleOwnersPresenter(int accountId, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);
        this.data = new ArrayList<>();
    }

    @Override
    public void onGuiCreated(@NonNull V view) {
        super.onGuiCreated(view);
        view.displayOwnerList(data);
    }

    public final void fireRefresh(){
        this.onUserRefreshed();
    }

    void onUserRefreshed(){

    }

    public final void fireScrollToEnd() {
        this.onUserScrolledToEnd();
    }

    void onUserScrolledToEnd(){

    }

    void onUserOwnerClicked(Owner owner){
        getView().showOwnerWall(getAccountId(), owner);
    }

    public final void fireOwnerClick(Owner owner) {
        this.onUserOwnerClicked(owner);
    }
}