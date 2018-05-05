package com.anotap.messenger.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.adapter.AudioRecyclerAdapter;
import com.anotap.messenger.fragment.base.BaseFragment;
import com.anotap.messenger.listener.BackPressCallback;
import com.anotap.messenger.model.Audio;
import com.anotap.messenger.player.MusicPlaybackService;
import com.anotap.messenger.player.util.MusicUtils;

/**
 * Created by golde on 27.09.2016.
 */

public class PlaylistFragment extends BaseFragment implements AudioRecyclerAdapter.ClickListener,
        BackPressCallback {

    private RecyclerView mRecyclerView;
    private View root;
    private AudioRecyclerAdapter mAdapter;
    private ArrayList<Audio> mData;

    public static Bundle buildArgs(ArrayList<Audio> playlist) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(Extra.AUDIOS, playlist);
        return bundle;
    }

    public static PlaylistFragment newInstance(ArrayList<Audio> playlist) {
        return newInstance(buildArgs(playlist));
    }

    public static PlaylistFragment newInstance(Bundle args) {
        PlaylistFragment fragment = new PlaylistFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            restoreFromSavedInstanceState(savedInstanceState);
        }
        mData = getArguments().getParcelableArrayList(Extra.AUDIOS);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_playlist, container, false);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));

        mRecyclerView = root.findViewById(R.id.list);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(manager);
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdapter = new AudioRecyclerAdapter(getActivity(), mData);
        mAdapter.setClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void restoreFromSavedInstanceState(@NonNull Bundle state) {
        //this.mData = state.getParcelableArrayList(Extra.AUDIOS);
    }

    @Override
    public void onClick(int position, Audio audio) {
        MusicPlaybackService.startForPlayList(getActivity(), (ArrayList) MusicUtils.getQueue(), position, false);
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }
}
