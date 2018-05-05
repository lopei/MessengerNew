package com.anotap.messenger.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import com.anotap.messenger.activity.ActivityFeatures;
import com.anotap.messenger.activity.ActivityUtils;
import com.anotap.messenger.adapter.AttachmentsViewBinder;
import com.anotap.messenger.adapter.MessagesAdapter;
import com.anotap.messenger.fragment.base.PlaceSupportPresenterFragment;
import com.anotap.messenger.listener.OnSectionResumeCallback;
import com.anotap.messenger.model.Message;
import com.anotap.messenger.model.VoiceMessage;
import com.anotap.messenger.mvp.presenter.FwdsPresenter;
import com.anotap.messenger.mvp.view.IFwdsView;
import com.anotap.messenger.util.Objects;
import com.anotap.mvp.core.IPresenterFactory;

import static com.anotap.messenger.util.Objects.nonNull;

public class FwdsFragment extends PlaceSupportPresenterFragment<FwdsPresenter, IFwdsView>
        implements MessagesAdapter.OnMessageActionListener, IFwdsView, AttachmentsViewBinder.VoiceActionListener {

    public static Bundle buildArgs(int accountId, ArrayList<Message> messages){
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putParcelableArrayList(Extra.MESSAGES, messages);
        return args;
    }

    public static FwdsFragment newInstance(Bundle args){
        FwdsFragment fwdsFragment = new FwdsFragment();
        fwdsFragment.setArguments(args);
        return fwdsFragment;
    }

    private MessagesAdapter mAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_fwds, container, false);
        ((AppCompatActivity)getActivity()).setSupportActionBar(root.findViewById(R.id.toolbar));

        RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new MessagesAdapter(getActivity(), Collections.emptyList(), this);
        mAdapter.setOnMessageActionListener(this);
        mAdapter.setVoiceActionListener(this);
        recyclerView.setAdapter(mAdapter);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(getActivity() instanceof OnSectionResumeCallback){
            ((OnSectionResumeCallback)getActivity()).onClearSelection();
        }

        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if(actionBar != null){
            actionBar.setSubtitle(null);
            actionBar.setTitle(R.string.title_mssages);
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
        return FwdsFragment.class.getSimpleName();
    }

    @Override
    public void onAvatarClick(@NonNull Message message, int userId) {
        super.onOpenOwner(userId);
    }

    @Override
    public void onRestoreClick(@NonNull Message message, int position) {
        // not supported
    }

    @Override
    public boolean onMessageLongClick(@NonNull Message message) {
        // not supported
        return false;
    }

    @Override
    public void onMessageClicked(@NonNull Message message) {
        // not supported
    }

    @Override
    public void displayMessages(@NonNull List<Message> messages) {
        if(Objects.nonNull(mAdapter)){
            mAdapter.setItems(messages);
        }
    }

    @Override
    public void notifyMessagesUpAdded(int position, int count) {
        // not supported
    }

    @Override
    public void notifyDataChanged() {
        if(Objects.nonNull(mAdapter)){
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void notifyMessagesDownAdded(int count) {
        // not supported
    }

    @Override
    public void configNowVoiceMessagePlaying(int voiceId, float progress, boolean paused, boolean amin) {
        if (nonNull(mAdapter)) {
            mAdapter.configNowVoiceMessagePlaying(voiceId, progress, paused, amin);
        }
    }

    @Override
    public void bindVoiceHolderById(int holderId, boolean play, boolean paused, float progress, boolean amin) {
        if (nonNull(mAdapter)) {
            mAdapter.bindVoiceHolderById(holderId, play, paused, progress, amin);
        }
    }

    @Override
    public void disableVoicePlaying() {
        if (nonNull(mAdapter)) {
            mAdapter.disableVoiceMessagePlaying();
        }
    }

    @Override
    public void showActionMode(String title) {
        // not supported
    }

    @Override
    public void finishActionMode() {
        // not supported
    }

    @Override
    public IPresenterFactory<FwdsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            ArrayList<Message> messages = getArguments().getParcelableArrayList(Extra.MESSAGES);
            int accountId = getArguments().getInt(Extra.ACCOUNT_ID);
            return new FwdsPresenter(accountId, messages, saveInstanceState);
        };
    }

    @Override
    public void onVoiceHolderBinded(int voiceMessageId, int voiceHolderId) {
        getPresenter().fireVoiceHolderCreated(voiceMessageId, voiceHolderId);
    }

    @Override
    public void onVoicePlayButtonClick(int voiceHolderId, int voiceMessageId, @NonNull VoiceMessage voiceMessage) {
        getPresenter().fireVoicePlayButtonClick(voiceHolderId, voiceMessageId, voiceMessage);
    }
}