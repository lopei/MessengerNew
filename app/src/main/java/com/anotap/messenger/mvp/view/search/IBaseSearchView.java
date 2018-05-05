package com.anotap.messenger.mvp.view.search;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.fragment.search.options.BaseOption;
import com.anotap.messenger.mvp.view.IAttachmentsPlacesView;
import com.anotap.messenger.mvp.view.IErrorView;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 01.05.2017.
 * phoenix
 */
public interface IBaseSearchView<T> extends IMvpView, IErrorView, IAccountDependencyView, IAttachmentsPlacesView {
    void displayData(List<T> data);

    void setEmptyTextVisible(boolean visible);

    void notifyDataSetChanged();

    void notifyItemChanged(int index);

    void notifyDataAdded(int position, int count);

    void showLoading(boolean loading);

    void displaySearchQuery(String query);

    void displayFilter(int accountId, ArrayList<BaseOption> options);
}