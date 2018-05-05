package com.anotap.messenger.fragment.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.adapter.PeopleAdapter;
import com.anotap.messenger.fragment.search.criteria.GroupSearchCriteria;
import com.anotap.messenger.model.Community;
import com.anotap.messenger.model.Owner;
import com.anotap.messenger.mvp.presenter.search.CommunitiesSearchPresenter;
import com.anotap.messenger.mvp.view.search.ICommunitiesSearchView;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.mvp.core.IPresenterFactory;

public class GroupsSearchFragment extends AbsSearchFragment<CommunitiesSearchPresenter, ICommunitiesSearchView, Community>
        implements ICommunitiesSearchView, PeopleAdapter.ClickListener {

    public static GroupsSearchFragment newInstance(int accountId, @Nullable GroupSearchCriteria initialCriteria) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putParcelable(Extra.CRITERIA, initialCriteria);
        GroupsSearchFragment fragment = new GroupsSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    void setAdapterData(RecyclerView.Adapter adapter, List<Community> data) {
        ((PeopleAdapter) adapter).setItems(data);
    }

    @Override
    RecyclerView.Adapter createAdapter(List<Community> data) {
        PeopleAdapter adapter = new PeopleAdapter(getActivity(), data);
        adapter.setClickListener(this);
        return adapter;
    }

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public IPresenterFactory<CommunitiesSearchPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new CommunitiesSearchPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getParcelable(Extra.CRITERIA),
                saveInstanceState
        );
    }

    @Override
    protected String tag() {
        return GroupsSearchFragment.class.getSimpleName();
    }

    @Override
    public void onOwnerClick(Owner owner) {
        getPresenter().fireCommunityClick((Community) owner);
    }

    @Override
    public void openCommunityWall(int accountId, Community community) {
        PlaceFactory.getOwnerWallPlace(accountId, community).tryOpenWith(getActivity());
    }
}