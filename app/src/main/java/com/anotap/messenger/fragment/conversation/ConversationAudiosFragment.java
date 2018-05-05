package com.anotap.messenger.fragment.conversation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.adapter.AudioRecyclerAdapter;
import com.anotap.messenger.model.Audio;
import com.anotap.messenger.mvp.presenter.history.ChatAttachmentAudioPresenter;
import com.anotap.messenger.mvp.view.IChatAttachmentAudiosView;
import com.anotap.mvp.core.IPresenterFactory;

public class ConversationAudiosFragment extends AbsChatAttachmentsFragment<Audio, ChatAttachmentAudioPresenter, IChatAttachmentAudiosView>
        implements AudioRecyclerAdapter.ClickListener, IChatAttachmentAudiosView {

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public RecyclerView.Adapter createAdapter() {
        AudioRecyclerAdapter audioRecyclerAdapter = new AudioRecyclerAdapter(getActivity(), Collections.emptyList());
        audioRecyclerAdapter.setClickListener(this);
        return audioRecyclerAdapter;
    }

    @Override
    public void onClick(int position, Audio audio) {
        getPresenter().fireAudioPlayClick(position, audio);
    }

    @Override
    public void displayAttachments(List<Audio> data) {
        ((AudioRecyclerAdapter)getAdapter()).setData(data);
    }

    @Override
    public IPresenterFactory<ChatAttachmentAudioPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new ChatAttachmentAudioPresenter(
                getArguments().getInt(Extra.PEER_ID),
                getArguments().getInt(Extra.ACCOUNT_ID),
                saveInstanceState
        );
    }

    @Override
    protected String tag() {
        return ConversationAudiosFragment.class.getSimpleName();
    }
}
