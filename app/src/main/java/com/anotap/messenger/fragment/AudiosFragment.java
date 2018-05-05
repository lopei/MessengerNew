package com.anotap.messenger.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.activity.ActivityFeatures;
import com.anotap.messenger.activity.ActivityUtils;
import com.anotap.messenger.adapter.AudioRecyclerAdapter;
import com.anotap.messenger.fragment.base.BasePresenterFragment;
import com.anotap.messenger.listener.EndlessRecyclerOnScrollListener;
import com.anotap.messenger.listener.OnSectionResumeCallback;
import com.anotap.messenger.model.Audio;
import com.anotap.messenger.mvp.presenter.AudiosPresenter;
import com.anotap.messenger.mvp.view.IAudiosView;
import com.anotap.messenger.place.Place;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.settings.Settings;
import com.anotap.mvp.core.IPresenterFactory;

import static com.anotap.messenger.util.Objects.nonNull;

/**
 * Audio is not supported :-(
 */
public class AudiosFragment extends BasePresenterFragment<AudiosPresenter, IAudiosView>
        implements IAudiosView {

    public static AudiosFragment newInstance(int accountId, int ownerId) {
        Bundle args = new Bundle();
        args.putInt(Extra.OWNER_ID, ownerId);
        args.putInt(Extra.ACCOUNT_ID, accountId);
        AudiosFragment fragment = new AudiosFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private View mBlockedRoot;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AudioRecyclerAdapter mAudioRecyclerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_music, container, false);
        ((AppCompatActivity)getActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));

        mBlockedRoot = root.findViewById(R.id.blocked_root);
        mSwipeRefreshLayout = root.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> getPresenter().fireRefresh());

        root.findViewById(R.id.button_details).setOnClickListener(v -> openPost());

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener() {
            @Override
            public void onScrollToLastElement() {
                getPresenter().fireScrollToEnd();
            }
        });

        mAudioRecyclerAdapter = new AudioRecyclerAdapter(getActivity(), Collections.emptyList());
        mAudioRecyclerAdapter.setClickListener((position, audio) -> getPresenter().playAudio(getActivity(), position));
        recyclerView.setAdapter(mAudioRecyclerAdapter);
        return root;
    }

    private void openPost(){
        PlaceFactory.getPostPreviewPlace(getArguments().getInt(Extra.ACCOUNT_ID), 7927, -72124992).tryOpenWith(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Settings.get().ui().notifyPlaceResumed(Place.AUDIOS);

        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (actionBar != null) {
            actionBar.setTitle(R.string.music);
            actionBar.setSubtitle(null);
        }

        if (getActivity() instanceof OnSectionResumeCallback) {
            ((OnSectionResumeCallback) getActivity()).onSectionResume(NavigationFragment.SECTION_ITEM_AUDIOS);
        }

        new ActivityFeatures.Builder()
                .begin()
                .setBlockNavigationDrawer(false)
                .setStatusBarColored(getActivity(),true)
                .build()
                .apply(getActivity());
    }

    @Override
    protected String tag() {
        return AudiosFragment.class.getSimpleName();
    }

    @Override
    public IPresenterFactory<AudiosPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new AudiosPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getInt(Extra.OWNER_ID),
                saveInstanceState
        );
    }

    @Override
    public void displayList(List<Audio> audios) {
        if(nonNull(mAudioRecyclerAdapter)){
            mAudioRecyclerAdapter.setData(audios);
        }
    }

    @Override
    public void notifyListChanged() {
        if(nonNull(mAudioRecyclerAdapter)){
            mAudioRecyclerAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void displayRefreshing(boolean refresing) {
        if(nonNull(mSwipeRefreshLayout)){
            mSwipeRefreshLayout.setRefreshing(refresing);
        }
    }

    @Override
    public void setBlockedScreen(boolean visible) {
        if(nonNull(mBlockedRoot)){
            mBlockedRoot.setVisibility(visible ? View.VISIBLE : View.GONE);
        }

        if(nonNull(mSwipeRefreshLayout)){
            mSwipeRefreshLayout.setVisibility(visible ? View.GONE : View.VISIBLE);
        }
    }
}