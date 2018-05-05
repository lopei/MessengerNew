package com.anotap.messenger.fragment.search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

import com.anotap.messenger.Extra;
import com.anotap.messenger.adapter.DocsAdapter;
import com.anotap.messenger.fragment.search.criteria.DocumentSearchCriteria;
import com.anotap.messenger.model.Document;
import com.anotap.messenger.mvp.presenter.search.DocsSearchPresenter;
import com.anotap.messenger.mvp.view.search.IDocSearchView;
import com.anotap.mvp.core.IPresenterFactory;

public class DocsSearchFragment extends AbsSearchFragment<DocsSearchPresenter, IDocSearchView, Document>
        implements DocsAdapter.ActionListener, IDocSearchView {

    public static DocsSearchFragment newInstance(int accountId, @Nullable DocumentSearchCriteria initialCriteria){
        Bundle args = new Bundle();
        args.putParcelable(Extra.CRITERIA, initialCriteria);
        args.putInt(Extra.ACCOUNT_ID, accountId);
        DocsSearchFragment fragment = new DocsSearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    void setAdapterData(RecyclerView.Adapter adapter, List<Document> data) {
        ((DocsAdapter) adapter).setItems(data);
    }

    @Override
    RecyclerView.Adapter createAdapter(List<Document> data) {
        DocsAdapter adapter = new DocsAdapter(data);
        adapter.setActionListner(this);
        return adapter;
    }

    @Override
    protected RecyclerView.LayoutManager createLayoutManager() {
        return new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public void onDocClick(int index, @NonNull Document doc) {
        getPresenter().fireDocClick(doc);
    }

    @Override
    public boolean onDocLongClick(int index, @NonNull Document doc) {
        return false;
    }

    @Override
    public IPresenterFactory<DocsSearchPresenter> getPresenterFactory(@Nullable Bundle saveInstanceState) {
        return () -> new DocsSearchPresenter(
                getArguments().getInt(Extra.ACCOUNT_ID),
                getArguments().getParcelable(Extra.CRITERIA),
                saveInstanceState
        );
    }

    @Override
    protected String tag() {
        return DocsSearchFragment.class.getSimpleName();
    }
}