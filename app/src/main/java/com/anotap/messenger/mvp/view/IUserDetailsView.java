package com.anotap.messenger.mvp.view;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import com.anotap.messenger.model.Owner;
import com.anotap.messenger.model.menu.AdvancedItem;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 3/19/2018.
 * Phoenix-for-VK
 */
public interface IUserDetailsView extends IMvpView, IAccountDependencyView, IErrorView {
    void displayData(@NonNull List<AdvancedItem> items);

    void displayToolbarTitle(String title);

    void openOwnerProfile(int accountId, int ownerId, @Nullable Owner owner);
}