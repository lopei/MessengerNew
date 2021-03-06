package com.anotap.messenger.fragment.conversation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.anotap.messenger.Constants;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.ActivityFeatures;
import com.anotap.messenger.activity.ActivityUtils;
import com.anotap.messenger.fragment.base.PlaceSupportPresenterFragment;
import com.anotap.messenger.listener.EndlessRecyclerOnScrollListener;
import com.anotap.messenger.listener.PicassoPauseOnScrollListener;
import com.anotap.messenger.mvp.presenter.history.BaseChatAttachmentsPresenter;
import com.anotap.messenger.mvp.view.IBaseChatAttachmentsView;
import com.anotap.messenger.util.ViewUtils;

import static com.anotap.messenger.util.Objects.nonNull;

public abstract class AbsChatAttachmentsFragment<T, P extends BaseChatAttachmentsPresenter<T, V>, V extends IBaseChatAttachmentsView<T>>
        extends PlaceSupportPresenterFragment<P, V> implements IBaseChatAttachmentsView<T> {

    public static final String TAG = ConversationPhotosFragment.class.getSimpleName();

    protected View root;
    protected RecyclerView mRecyclerView;
    protected TextView mEmpty;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView.Adapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_photos, container, false);

        ((AppCompatActivity) requireActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));

        mRecyclerView = root.findViewById(android.R.id.list);
        mEmpty = root.findViewById(R.id.empty);

        RecyclerView.LayoutManager manager = createLayoutManager();
        mRecyclerView.setLayoutManager(manager);

        mRecyclerView.addOnScrollListener(new PicassoPauseOnScrollListener(Constants.PICASSO_TAG));
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onScrollToLastElement() {
                getPresenter().fireScrollToEnd();
            }
        });

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(getPresenter()::fireRefresh);

        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(getActivity(), mSwipeRefreshLayout);

        this.mAdapter = createAdapter();
        mRecyclerView.setAdapter(mAdapter);
        return root;
    }

    protected RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    protected abstract RecyclerView.LayoutManager createLayoutManager();

    public abstract RecyclerView.Adapter createAdapter();

    @Override
    public void notifyDataAdded(int position, int count) {
        if(nonNull(mAdapter)){
            mAdapter.notifyItemRangeInserted(position, count);
        }
    }

    @Override
    public void notifyDatasetChanged() {
        if(nonNull(mAdapter)){
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void showLoading(boolean loading) {
        if(nonNull(mSwipeRefreshLayout)){
            mSwipeRefreshLayout.setRefreshing(loading);
        }
    }

    @Override
    public void setEmptyTextVisible(boolean visible) {
        if(nonNull(mEmpty)){
            mEmpty.setVisibility(visible ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void setToolbarTitle(String title) {
        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if(nonNull(actionBar)){
            actionBar.setTitle(title);
        }
    }

    @Override
    public void setToolbarSubtitle(String subtitle) {
        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if(nonNull(actionBar)){
            actionBar.setSubtitle(subtitle);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        new ActivityFeatures.Builder()
                .begin()
                .setBlockNavigationDrawer(false)
                .setStatusBarColored(getActivity(),true)
                .build()
                .apply(requireActivity());
    }
}
