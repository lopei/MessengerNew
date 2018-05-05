package com.anotap.messenger.fragment.friends;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.anotap.messenger.Extra;
import com.anotap.messenger.fragment.AbsOwnersListFragment;
import com.anotap.messenger.mvp.presenter.MutualFriendsPresenter;
import com.anotap.messenger.mvp.view.ISimpleOwnersView;
import com.anotap.mvp.core.IPresenterFactory;

public class MutualFriendsFragment extends AbsOwnersListFragment<MutualFriendsPresenter, ISimpleOwnersView> implements ISimpleOwnersView {

    private static final String EXTRA_TARGET_ID = "targetId";

    public static MutualFriendsFragment newInstance(int accountId, int targetId){
        Bundle bundle = new Bundle();
        bundle.putInt(EXTRA_TARGET_ID, targetId);
        bundle.putInt(Extra.ACCOUNT_ID, accountId);
        MutualFriendsFragment friendsFragment = new MutualFriendsFragment();
        friendsFragment.setArguments(bundle);
        return friendsFragment;
    }

    @Override
    protected String tag() {
        return MutualFriendsFragment.class.getSimpleName();
    }

    @Override
    public IPresenterFactory<MutualFriendsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new MutualFriendsPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getInt(EXTRA_TARGET_ID),
                saveInstanceState
        );
    }
}