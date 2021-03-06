package com.anotap.messenger.fragment.fave;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.anotap.messenger.Constants;
import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.adapter.fave.FavePhotosAdapter;
import com.anotap.messenger.fragment.base.BasePresenterFragment;
import com.anotap.messenger.listener.EndlessRecyclerOnScrollListener;
import com.anotap.messenger.listener.PicassoPauseOnScrollListener;
import com.anotap.messenger.model.Photo;
import com.anotap.messenger.mvp.presenter.FavePhotosPresenter;
import com.anotap.messenger.mvp.view.IFavePhotosView;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.util.ViewUtils;
import com.anotap.mvp.core.IPresenterFactory;

import static com.anotap.messenger.util.Objects.nonNull;

public class FavePhotosFragment extends BasePresenterFragment<FavePhotosPresenter, IFavePhotosView>
        implements SwipeRefreshLayout.OnRefreshListener, IFavePhotosView, FavePhotosAdapter.PhotoSelectionListener {

    public static FavePhotosFragment newInstance(int accountId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        FavePhotosFragment favePhotosFragment = new FavePhotosFragment();
        favePhotosFragment.setArguments(args);
        return favePhotosFragment;
    }

    private TextView mEmpty;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FavePhotosAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_photos, container, false);
        root.findViewById(R.id.toolbar).setVisibility(View.GONE);
        RecyclerView recyclerView = root.findViewById(android.R.id.list);
        mEmpty = root.findViewById(R.id.empty);

        int columns = getContext().getResources().getInteger(R.integer.photos_column_count);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), columns);
        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.addOnScrollListener(new PicassoPauseOnScrollListener(Constants.PICASSO_TAG));
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onScrollToLastElement() {
                getPresenter().fireScrollToEnd();
            }
        });

        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        ViewUtils.setupSwipeRefreshLayoutWithCurrentTheme(getActivity(), mSwipeRefreshLayout);

        mAdapter = new FavePhotosAdapter(getActivity(), Collections.emptyList());
        mAdapter.setPhotoSelectionListener(this);
        recyclerView.setAdapter(mAdapter);

        resolveEmptyTextVisibility();
        return root;
    }

    @Override
    protected String tag() {
        return FavePhotosFragment.class.getSimpleName();
    }

    @Override
    public void onRefresh() {
        getPresenter().fireRefresh();
    }

    @Override
    public void onPhotoClicked(int position, Photo photo) {
        getPresenter().firePhotoClick(position, photo);
    }

    @Override
    public void displayData(List<Photo> photos) {
        if(nonNull(mAdapter)){
            mAdapter.setData(photos);
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
    public void goToGallery(int accountId, ArrayList<Photo> photos, int position) {
        PlaceFactory.getFavePhotosGallery(accountId, photos, position)
                .tryOpenWith(getActivity());
    }

    @Override
    public IPresenterFactory<FavePhotosPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new FavePhotosPresenter(getArguments().getInt(Extra.ACCOUNT_ID), saveInstanceState);
    }
}
