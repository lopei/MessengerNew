package com.anotap.messenger.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.ActivityFeatures;
import com.anotap.messenger.activity.ActivityUtils;
import com.anotap.messenger.activity.SelectProfilesActivity;
import com.anotap.messenger.adapter.PeopleAdapter;
import com.anotap.messenger.fragment.base.BasePresenterFragment;
import com.anotap.messenger.fragment.friends.FriendsTabsFragment;
import com.anotap.messenger.listener.EndlessRecyclerOnScrollListener;
import com.anotap.messenger.listener.OnSectionResumeCallback;
import com.anotap.messenger.model.Owner;
import com.anotap.messenger.model.SelectProfileCriteria;
import com.anotap.messenger.model.User;
import com.anotap.messenger.mvp.presenter.UserBannedPresenter;
import com.anotap.messenger.mvp.view.IUserBannedView;
import com.anotap.messenger.place.Place;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.mvp.core.IPresenterFactory;

import static com.anotap.messenger.util.Objects.nonNull;

/**
 * Created by admin on 09.07.2017.
 * phoenix
 */
public class UserBannedFragment extends BasePresenterFragment<UserBannedPresenter, IUserBannedView> implements IUserBannedView, PeopleAdapter.LongClickListener {

    private static final int REQUEST_SELECT = 13;

    public static UserBannedFragment newInstance(int accountId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        UserBannedFragment fragment = new UserBannedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;

    private RecyclerView mRecyclerView;
    private PeopleAdapter mPeopleAdapter;
    private TextView mEmptyText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_banned, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().fireRefresh());

        mRecyclerView = root.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onScrollToLastElement() {
                getPresenter().fireScrollToEnd();
            }
        });

        mPeopleAdapter = new PeopleAdapter(getActivity(), Collections.emptyList());
        mPeopleAdapter.setLongClickListener(this);
        mPeopleAdapter.setClickListener(owner -> getPresenter().fireUserClick((User) owner));
        mRecyclerView.setAdapter(mPeopleAdapter);

        mEmptyText = root.findViewById(R.id.empty_text);

        root.findViewById(R.id.button_add).setOnClickListener(v -> getPresenter().fireButtonAddClick());

        resolveEmptyTextVisibility();
        return root;
    }

    private void resolveEmptyTextVisibility() {
        if (nonNull(mPeopleAdapter) && nonNull(mEmptyText)) {
            mEmptyText.setVisibility(mPeopleAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void displayUserList(List<User> users) {
        if (nonNull(mPeopleAdapter)) {
            mPeopleAdapter.setItems(users);
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void notifyItemsAdded(int position, int count) {
        if (nonNull(mPeopleAdapter)) {
            mPeopleAdapter.notifyItemRangeInserted(position, count);
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if (nonNull(mPeopleAdapter)) {
            mPeopleAdapter.notifyDataSetChanged();
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void notifyItemRemoved(int position) {
        if (nonNull(mPeopleAdapter)) {
            mPeopleAdapter.notifyItemRemoved(position);
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getActivity() instanceof OnSectionResumeCallback) {
            ((OnSectionResumeCallback) getActivity()).onClearSelection();
        }

        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (actionBar != null) {
            actionBar.setTitle(R.string.user_blacklist_title);
            actionBar.setSubtitle(null);
        }

        new ActivityFeatures.Builder()
                .begin()
                .setBlockNavigationDrawer(false)
                .setStatusBarColored(getActivity(),true)
                .build()
                .apply(getActivity());
    }

    @Override
    public void displayRefreshing(boolean refreshing) {
        if (nonNull(mSwipeRefreshLayout)) {
            mSwipeRefreshLayout.setRefreshing(refreshing);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT && resultCode == Activity.RESULT_OK) {
            ArrayList<User> users = data.getParcelableArrayListExtra(Extra.USERS);
            postPrenseterReceive(presenter -> presenter.fireUsersSelected(users));
        }
    }

    @Override
    public void startUserSelection(int accountId) {
        Place place = PlaceFactory.getFriendsFollowersPlace(accountId, accountId, FriendsTabsFragment.TAB_ALL_FRIENDS, null);
        SelectProfileCriteria criteria = new SelectProfileCriteria();
        Intent intent = SelectProfilesActivity.createIntent(getActivity(), place, criteria);
        startActivityForResult(intent, REQUEST_SELECT);
    }

    @Override
    public void showSuccessToast() {
        Toast.makeText(getActivity(), R.string.success, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void scrollToPosition(int position) {
        if(nonNull(mRecyclerView)){
            mRecyclerView.smoothScrollToPosition(position);
        }
    }

    @Override
    public void showUserProfile(int accountId, User user) {
        PlaceFactory.getOwnerWallPlace(accountId, user).tryOpenWith(getActivity());
    }

    @Override
    public IPresenterFactory<UserBannedPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new UserBannedPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                saveInstanceState
        );
    }

    @Override
    protected String tag() {
        return UserBannedFragment.class.getSimpleName();
    }

    @Override
    public boolean onOwnerLongClick(Owner owner) {
        new AlertDialog.Builder(getActivity())
                .setTitle(owner.getFullName())
                .setItems(new String[]{getString(R.string.delete)}, (dialog, which) -> getPresenter().fireRemoveClick((User) owner))
                .setNegativeButton(R.string.button_cancel, null)
                .show();
        return true;
    }
}