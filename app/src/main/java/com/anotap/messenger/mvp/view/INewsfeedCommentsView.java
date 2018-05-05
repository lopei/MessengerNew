package com.anotap.messenger.mvp.view;

import java.util.List;

import com.anotap.messenger.model.NewsfeedComment;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 08.05.2017.
 * phoenix
 */
public interface INewsfeedCommentsView extends IAccountDependencyView, IAttachmentsPlacesView, IMvpView, IErrorView {
    void displayData(List<NewsfeedComment> data);
    void notifyDataAdded(int position, int count);
    void notifyDataSetChanged();
    void showLoading(boolean loading);
}
