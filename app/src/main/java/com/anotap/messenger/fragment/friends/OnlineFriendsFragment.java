package com.anotap.messenger.fragment.friends;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.anotap.messenger.Extra;
import com.anotap.messenger.fragment.AbsOwnersListFragment;
import com.anotap.messenger.mvp.presenter.OnlineFriendsPresenter;
import com.anotap.messenger.mvp.view.ISimpleOwnersView;
import com.anotap.mvp.core.IPresenterFactory;

public class OnlineFriendsFragment extends AbsOwnersListFragment<OnlineFriendsPresenter, ISimpleOwnersView> implements ISimpleOwnersView {

    public static OnlineFriendsFragment newInstance(int accoutnId, int userId) {
        Bundle bundle = new Bundle();
        bundle.putInt(Extra.USER_ID, userId);
        bundle.putInt(Extra.ACCOUNT_ID, accoutnId);
        OnlineFriendsFragment friendsFragment = new OnlineFriendsFragment();
        friendsFragment.setArguments(bundle);
        return friendsFragment;
    }

    @Override
    protected String tag() {
        return OnlineFriendsFragment.class.getSimpleName();
    }

    @Override
    public IPresenterFactory<OnlineFriendsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new OnlineFriendsPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getInt(Extra.USER_ID),
                saveInstanceState);
    }
}