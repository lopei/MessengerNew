package com.anotap.messenger.fragment.search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.adapter.AttachmentsViewBinder;
import com.anotap.messenger.adapter.DialogPreviewAdapter;
import com.anotap.messenger.adapter.MessagesAdapter;
import com.anotap.messenger.fragment.search.criteria.DialogsSearchCriteria;
import com.anotap.messenger.model.Audio;
import com.anotap.messenger.model.Document;
import com.anotap.messenger.model.Link;
import com.anotap.messenger.model.Message;
import com.anotap.messenger.model.Photo;
import com.anotap.messenger.model.Poll;
import com.anotap.messenger.model.Post;
import com.anotap.messenger.model.Video;
import com.anotap.messenger.model.WikiPage;
import com.anotap.messenger.mvp.presenter.search.DialogsSearchPresenter;
import com.anotap.messenger.mvp.view.search.IDialogsSearchView;
import com.anotap.mvp.core.IPresenterFactory;

/**
 * Created by admin on 02.05.2017.
 * phoenix
 */
public class DialogsSearchFragment extends AbsSearchFragment<DialogsSearchPresenter, IDialogsSearchView, Object>
        implements IDialogsSearchView, DialogPreviewAdapter.ActionListener {

    public static DialogsSearchFragment newInstance(int accountId, DialogsSearchCriteria criteria) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        args.putParcelable(Extra.CRITERIA, criteria);
        DialogsSearchFragment fragment = new DialogsSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public IPresenterFactory<DialogsSearchPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> {
            int accountId = getArguments().getInt(Extra.ACCOUNT_ID);
            DialogsSearchCriteria criteria = getArguments().getParcelable(Extra.CRITERIA);
            return new DialogsSearchPresenter(accountId, criteria, saveInstanceState);
        };
    }

    @Override
    public void notifyDataSetChanged() {
        getPresenter().firePublishData();
        super.notifyDataSetChanged();
    }

    @Override
    protected String tag() {
        return DialogsSearchFragment.class.getSimpleName();
    }

    @Override
    void setAdapterData(RecyclerView.Adapter adapter, List<Object> data) {
        ((MessagesAdapter) adapter).clear();
        List<Message> newData = new ArrayList<Message>();
        for (Object d : data) {
            newData.add((Message) d);
        }
        ((MessagesAdapter) adapter).addAll(newData);
    }

    @Override
    RecyclerView.Adapter createAdapter(List<Object> data) {
        List<Message> newData = new ArrayList<Message>();
        for (Object d : data) {
            newData.add((Message) d);
        }
        return new MessagesAdapter(getActivity(), newData, null);
        //return new DialogPreviewAdapter(getActivity(), data, this);
    }

    @Override
    RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    @Override
    public void onEntryClick(Object o) {
        getPresenter().fireEntryClick(o);
    }
}