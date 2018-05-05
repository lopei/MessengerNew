package com.anotap.messenger.fragment.fave;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import com.anotap.messenger.Constants;
import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.adapter.VideosAdapter;
import com.anotap.messenger.fragment.base.BasePresenterFragment;
import com.anotap.messenger.listener.EndlessRecyclerOnScrollListener;
import com.anotap.messenger.listener.PicassoPauseOnScrollListener;
import com.anotap.messenger.model.Video;
import com.anotap.messenger.mvp.presenter.FaveVideosPresenter;
import com.anotap.messenger.mvp.view.IFaveVideosView;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.util.ViewUtils;
import com.anotap.mvp.core.IPresenterFactory;

import static com.anotap.messenger.util.Objects.nonNull;

public class FaveVideosFragment extends BasePresenterFragment<FaveVideosPresenter, IFaveVideosView>
        implements IFaveVideosView, SwipeRefreshLayout.OnRefreshListener, VideosAdapter.VideoOnClickListener {

    public static FaveVideosFragment newInstance(int accountId){
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        FaveVideosFragment fragment = new FaveVideosFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private VideosAdapter mAdapter;
    private TextView mEmpty;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fave_videos, container, false);
        RecyclerView recyclerView = root.findViewById(android.R.id.list);

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(getActivity(), mSwipeRefreshLayout);

        mEmpty = root.findViewById(R.id.empty);

        int columns = getContext().getResources().getInteger(R.integer.videos_column_count);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.addOnScrollListener(new PicassoPauseOnScrollListener(Constants.PICASSO_TAG));
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onScrollToLastElement() {
                getPresenter().fireScrollToEnd();
            }
        });

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mAdapter = new VideosAdapter(getActivity(), Collections.emptyList());
        mAdapter.setVideoOnClickListener(this);
        recyclerView.setAdapter(mAdapter);

        resolveEmptyTextVisibility();
        return root;
    }

    @Override
    protected String tag() {
        return FaveVideosFragment.class.getSimpleName();
    }

    @Override
    public void onRefresh() {
        getPresenter().fireRefresh();
    }

    @Override
    public void onVideoClick(int position, Video video) {
        getPresenter().fireVideoClick(video);
    }

    @Override
    public void displayData(List<Video> videos) {
        if(nonNull(mAdapter)){
            mAdapter.setData(videos);
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void notifyDataSetChanged() {
        if(nonNull(mAdapter)){
            mAdapter.notifyDataSetChanged();
            resolveEmptyTextVisibility();
        }
    }

    @Override
    public void notifyDataAdded(int position, int count) {
        if(nonNull(mAdapter)){
            mAdapter.notifyItemRangeInserted(position, count);
            resolveEmptyTextVisibility();
        }
    }

    private void resolveEmptyTextVisibility() {
        if(nonNull(mEmpty) && nonNull(mAdapter)){
            mEmpty.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void showRefreshing(boolean refreshing) {
        if(nonNull(mSwipeRefreshLayout)){
            mSwipeRefreshLayout.post(() -> mSwipeRefreshLayout.setRefreshing(refreshing));
        }
    }

    @Override
    public void goToPreview(int accountId, Video video) {
        PlaceFactory.getVideoPreviewPlace(accountId, video)
                .tryOpenWith(getActivity());
    }

    @Override
    public IPresenterFactory<FaveVideosPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new FaveVideosPresenter(getArguments().getInt(Extra.ACCOUNT_ID), saveInstanceState);
    }
}
