package com.anotap.messenger.fragment.friends;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.anotap.messenger.Extra;
import com.anotap.messenger.fragment.AbsOwnersListFragment;
import com.anotap.messenger.mvp.presenter.FollowersPresenter;
import com.anotap.messenger.mvp.view.ISimpleOwnersView;
import com.anotap.mvp.core.IPresenterFactory;

public class FollowersFragment extends AbsOwnersListFragment<FollowersPresenter, ISimpleOwnersView> implements ISimpleOwnersView {

    public static FollowersFragment newInstance(int accountId, int userId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putInt(Extra.USER_ID, userId);
        FollowersFragment followersFragment = new FollowersFragment();
        followersFragment.setArguments(args);
        return followersFragment;
    }

    @Override
    protected String tag() {
        return FollowersFragment.class.getSimpleName();
    }

    @Override
    public IPresenterFactory<FollowersPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new FollowersPresenter(getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getInt(Extra.USER_ID),
                saveInstanceState);
    }
}