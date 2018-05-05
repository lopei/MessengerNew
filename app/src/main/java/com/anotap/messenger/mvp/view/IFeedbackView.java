package com.anotap.messenger.mvp.view;

import android.support.annotation.NonNull;

import java.util.List;

import com.anotap.messenger.model.LoadMoreState;
import com.anotap.messenger.model.feedback.Feedback;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 11.12.2016.
 * phoenix
 */
public interface IFeedbackView extends IAccountDependencyView, IMvpView, IAttachmentsPlacesView, IErrorView {
    void displayData(List<Feedback> data);
    void showLoading(boolean loading);
    void notifyDataAdding(int position, int count);
    void notifyDataSetChanged();
    void configLoadMore(@LoadMoreState int loadmoreState);
    void showLinksDialog(int accountId, @NonNull Feedback notification);
}
