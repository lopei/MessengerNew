package com.anotap.messenger.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.ActivityFeatures;
import com.anotap.messenger.activity.ActivityUtils;
import com.anotap.messenger.adapter.NewsfeedCommentsAdapter;
import com.anotap.messenger.fragment.base.PlaceSupportPresenterFragment;
import com.anotap.messenger.listener.EndlessRecyclerOnScrollListener;
import com.anotap.messenger.listener.OnSectionResumeCallback;
import com.anotap.messenger.model.NewsfeedComment;
import com.anotap.messenger.model.Post;
import com.anotap.messenger.mvp.presenter.NewsfeedCommentsPresenter;
import com.anotap.messenger.mvp.view.INewsfeedCommentsView;
import com.anotap.messenger.place.Place;
import com.anotap.messenger.settings.Settings;
import com.anotap.messenger.util.Utils;
import com.anotap.mvp.core.IPresenterFactory;

import static com.anotap.messenger.util.Objects.nonNull;
import static com.anotap.messenger.util.Utils.isLandscape;

/**
 * Created by admin on 08.05.2017.
 * phoenix
 */
public class NewsfeedCommentsFragment extends PlaceSupportPresenterFragment<NewsfeedCommentsPresenter, INewsfeedCommentsView>
        implements INewsfeedCommentsView, NewsfeedCommentsAdapter.ActionListener {

    public static NewsfeedCommentsFragment newInstance(int accountId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        NewsfeedCommentsFragment fragment = new NewsfeedCommentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NewsfeedCommentsAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_newsfeed_comments, container, false);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().fireRefresh());

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager manager;
        if (Utils.is600dp(requireActivity())) {
            manager = new StaggeredGridLayoutManager(isLandscape(requireActivity()) ? 2 : 1, StaggeredGridLayoutManager.VERTICAL);
        } else {
            manager = new LinearLayoutManager(requireActivity());
        }

        recyclerView.setLayoutManager(manager);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onScrollToLastElement() {
                getPresenter().fireScrollToEnd();
            }
        });

        mAdapter = new NewsfeedCommentsAdapter(getActivity(), Collections.emptyList(), this);
        mAdapter.setActionListener(this);
        mAdapter.setOwnerClickListener(this);

        recyclerView.setAdapter(mAdapter);
        return root;
    }

    @Override
    public IPresenterFactory<NewsfeedCommentsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            int accountId = getArguments().getInt(Extra.ACCOUNT_ID);
            return new NewsfeedCommentsPresenter(accountId, saveInstanceState);
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        Settings.get().ui().notifyPlaceResumed(Place.NEWSFEED_COMMENTS);

        ActivityUtils.setToolbarTitle(this, R.string.drawer_newsfeed_comments);
        ActivityUtils.setToolbarSubtitle(this, null);

        if (getActivity() instanceof OnSectionResumeCallback) {
            ((OnSectionResumeCallback) getActivity()).onSectionResume(NavigationFragment.SECTION_ITEM_NEWSFEED_COMMENTS);
        }

        new ActivityFeatures.Builder()
                .begin()
                .setBlockNavigationDrawer(false)
                .setStatusBarColored(getActivity(),true)
                .build()
                .apply(requireActivity());
    }

    @Override
    protected String tag() {
        return NewsfeedCommentsFragment.class.getSimpleName();
    }

    @Override
    public void displayData(List<NewsfeedComment> data) {
        if (nonNull(mAdapter)) {
            mAdapter.setData(data);
        }
    }

    @Override
    public void notifyDataAdded(int position, int count) {
        if (nonNull(mAdapter)) {
            mAdapter.notifyItemRangeInserted(position, count);
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if (nonNull(mAdapter)) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showLoading(boolean loading) {
        if (nonNull(mSwipeRefreshLayout)) {
            mSwipeRefreshLayout.setRefreshing(loading);
        }
    }

    @Override
    public void onPostBodyClick(NewsfeedComment comment) {
        getPresenter().firePostClick((Post) comment.getModel());
    }

    @Override
    public void onCommentBodyClick(NewsfeedComment comment) {
        getPresenter().fireCommentBodyClick(comment);
    }
}