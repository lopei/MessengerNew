package com.anotap.messenger.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.SelectProfilesActivity;
import com.anotap.messenger.adapter.CommunityManagersAdapter;
import com.anotap.messenger.fragment.base.BasePresenterFragment;
import com.anotap.messenger.fragment.search.SearchContentType;
import com.anotap.messenger.fragment.search.criteria.PeopleSearchCriteria;
import com.anotap.messenger.model.Manager;
import com.anotap.messenger.model.SelectProfileCriteria;
import com.anotap.messenger.model.User;
import com.anotap.messenger.mvp.presenter.CommunityManagersPresenter;
import com.anotap.messenger.mvp.view.ICommunityManagersView;
import com.anotap.messenger.place.Place;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.util.AssertUtils;
import com.anotap.messenger.util.Objects;
import com.anotap.mvp.core.IPresenterFactory;

/**
 * Created by admin on 13.06.2017.
 * phoenix
 */
public class CommunityManagersFragment extends BasePresenterFragment<CommunityManagersPresenter, ICommunityManagersView>
        implements ICommunityManagersView {

    private static final int REQUEST_SELECT_PROFILES = 19;

    public static CommunityManagersFragment newInstance(int accountId, int groupId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putInt(Extra.GROUP_ID, groupId);
        CommunityManagersFragment fragment = new CommunityManagersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CommunityManagersAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_community_managers, container, false);

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().fireRefresh());

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new CommunityManagersAdapter(getActivity(), Collections.emptyList());
        mAdapter.setActionListener(new CommunityManagersAdapter.ActionListener() {
            @Override
            public void onManagerClick(Manager manager) {
                getPresenter().fireManagerClick(manager);
            }

            @Override
            public void onManagerLongClick(Manager manager) {
                showManagerContextMenu(manager);
            }
        });

        recyclerView.setAdapter(mAdapter);

        root.findViewById(R.id.button_add).setOnClickListener(v -> getPresenter().fireButtonAddClick());
        return root;
    }

    private void showManagerContextMenu(Manager manager){
        String[] items = {getString(R.string.delete)};
        new AlertDialog.Builder(requireActivity())
                .setTitle(manager.getUser().getFullName())
                .setItems(items, (dialog, which) -> getPresenter().fireRemoveClick(manager))
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    @Override
    public IPresenterFactory<CommunityManagersPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new CommunityManagersPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getInt(Extra.GROUP_ID),
                saveInstanceState
        );
    }

    @Override
    protected String tag() {
        return CommunityManagersFragment.class.getSimpleName();
    }

    @Override
    public void notifyDataSetChanged() {
        if(Objects.nonNull(mAdapter)){
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void displayRefreshing(boolean loadingNow) {
        if(Objects.nonNull(mSwipeRefreshLayout)){
            mSwipeRefreshLayout.setRefreshing(loadingNow);
        }
    }

    @Override
    public void displayData(List<Manager> managers) {
        if(Objects.nonNull(mAdapter)){
            mAdapter.setData(managers);
        }
    }

    @Override
    public void goToManagerEditing(int accountId, int groupId, Manager manager) {
        PlaceFactory.getCommunityManagerEditPlace(accountId, groupId, manager).tryOpenWith(requireActivity());
    }

    @Override
    public void showUserProfile(int accountId, User user) {
        PlaceFactory.getOwnerWallPlace(accountId, user).tryOpenWith(requireActivity());
    }

    @Override
    public void startSelectProfilesActivity(int accountId, int groupId) {
        PeopleSearchCriteria criteria = new PeopleSearchCriteria("").setGroupId(groupId);

        SelectProfileCriteria c = new SelectProfileCriteria();

        Place place = PlaceFactory.getSingleTabSearchPlace(accountId, SearchContentType.PEOPLE, criteria);
        Intent intent = SelectProfilesActivity.createIntent(getActivity(), place, c);
        startActivityForResult(intent, REQUEST_SELECT_PROFILES);
    }

    @Override
    public void startAddingUsersToManagers(int accountId, int groupId, ArrayList<User> users) {
        PlaceFactory.getCommunityManagerAddPlace(accountId, groupId, users).tryOpenWith(requireActivity());
    }

    @Override
    public void notifyItemRemoved(int index) {
        if(Objects.nonNull(mAdapter)){
            mAdapter.notifyItemRemoved(index);
        }
    }

    @Override
    public void notifyItemChanged(int index) {
        if(Objects.nonNull(mAdapter)){
            mAdapter.notifyItemChanged(index);
        }
    }

    @Override
    public void notifyItemAdded(int index) {
        if(Objects.nonNull(mAdapter)){
            mAdapter.notifyItemInserted(index);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_SELECT_PROFILES && resultCode == Activity.RESULT_OK){
            ArrayList<User> users = data.getParcelableArrayListExtra(Extra.USERS);
            AssertUtils.requireNonNull(users);
            postPrenseterReceive(presenter -> presenter.fireProfilesSelected(users));
        }
    }
}