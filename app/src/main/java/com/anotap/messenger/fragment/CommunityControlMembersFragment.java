package com.anotap.messenger.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.anotap.messenger.Extra;
import com.anotap.messenger.fragment.base.BasePresenterFragment;
import com.anotap.messenger.mvp.presenter.CommunityMembersPresenter;
import com.anotap.messenger.mvp.view.ICommunityMembersView;
import com.anotap.mvp.core.IPresenterFactory;

/**
 * Created by admin on 13.06.2017.
 * phoenix
 */
public class CommunityControlMembersFragment extends BasePresenterFragment<CommunityMembersPresenter, ICommunityMembersView>
        implements ICommunityMembersView {

    public static CommunityControlMembersFragment newInstance(int accountId, int groupId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putInt(Extra.GROUP_ID, groupId);
        CommunityControlMembersFragment fragment = new CommunityControlMembersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public IPresenterFactory<CommunityMembersPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new CommunityMembersPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getInt(Extra.GROUP_ID),
                saveInstanceState
        );
    }

    @Override
    protected String tag() {
        return CommunityControlMembersFragment.class.getSimpleName();
    }
}
