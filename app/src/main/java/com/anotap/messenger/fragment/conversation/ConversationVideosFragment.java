package com.anotap.messenger.fragment.conversation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.Collections;
import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.R;
import com.anotap.messenger.adapter.VideosAdapter;
import com.anotap.messenger.model.Video;
import com.anotap.messenger.mvp.presenter.history.ChatAttachmentVideoPresenter;
import com.anotap.messenger.mvp.view.IChatAttachmentVideoView;
import com.anotap.mvp.core.IPresenterFactory;

public class ConversationVideosFragment extends AbsChatAttachmentsFragment<Video, ChatAttachmentVideoPresenter, IChatAttachmentVideoView>
        implements VideosAdapter.VideoOnClickListener, IChatAttachmentVideoView {

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        int columns = getContext().getResources().getInteger(R.integer.videos_column_count);
        return new StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL);
    }

    @Override
    public RecyclerView.Adapter createAdapter() {
        VideosAdapter adapter = new VideosAdapter(getActivity(), Collections.emptyList());
        adapter.setVideoOnClickListener(this);
        return adapter;
    }

    @Override
    public void onVideoClick(int position, Video video) {
        getPresenter().fireVideoClick(video);
    }

    @Override
    public void displayAttachments(List<Video> data) {
        VideosAdapter adapter = (VideosAdapter) getAdapter();
        adapter.setData(data);
    }

    @Override
    public IPresenterFactory<ChatAttachmentVideoPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            int accountId = getArguments().getInt(Extra.ACCOUNT_ID);
            int peerId = getArguments().getInt(Extra.PEER_ID);
            return new ChatAttachmentVideoPresenter(peerId, accountId, saveInstanceState);
        };
    }

    @Override
    protected String tag() {
        return ConversationVideosFragment.class.getSimpleName();
    }
}
