package com.anotap.messenger.fragment.conversation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.adapter.DocsAdapter;
import com.anotap.messenger.model.Document;
import com.anotap.messenger.mvp.presenter.history.ChatAttachmentDocsPresenter;
import com.anotap.messenger.mvp.view.IChatAttachmentDocsView;
import com.anotap.mvp.core.IPresenterFactory;

public class ConversationDocsFragment extends AbsChatAttachmentsFragment<Document, ChatAttachmentDocsPresenter, IChatAttachmentDocsView>
        implements DocsAdapter.ActionListener, IChatAttachmentDocsView {

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public RecyclerView.Adapter createAdapter() {
        DocsAdapter simpleDocRecycleAdapter = new DocsAdapter(Collections.emptyList());
        simpleDocRecycleAdapter.setActionListner(this);
        return simpleDocRecycleAdapter;
    }

    @Override
    public void displayAttachments(List<Document> data) {
        if(getAdapter() instanceof DocsAdapter){
            ((DocsAdapter) getAdapter()).setItems(data);
        }
    }

    @Override
    public IPresenterFactory<ChatAttachmentDocsPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new ChatAttachmentDocsPresenter(
                getArguments().getInt(Extra.PEER_ID),
                getArguments().getInt(Extra.ACCOUNT_ID),
                saveInstanceState
        );
    }

    @Override
    protected String tag() {
        return ConversationDocsFragment.class.getSimpleName();
    }

    @Override
    public void onDocClick(int index, @NonNull Document doc) {
        getPresenter().fireDocClick(doc);
    }

    @Override
    public boolean onDocLongClick(int index, @NonNull Document doc) {
        return false;
    }
}
