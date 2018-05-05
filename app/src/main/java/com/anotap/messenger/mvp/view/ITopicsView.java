package com.anotap.messenger.mvp.view;

import android.support.annotation.NonNull;

import java.util.List;

import com.anotap.messenger.model.LoadMoreState;
import com.anotap.messenger.model.Topic;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 13.12.2016.
 * phoenix
 */
public interface ITopicsView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(@NonNull List<Topic> topics);
    void notifyDataSetChanged();
    void notifyDataAdd(int position, int count);
    void showRefreshing(boolean refreshing);
    void setupLoadMore(@LoadMoreState int state);

    void goToComments(int accountId, @NonNull Topic topic);
    void setButtonCreateVisible(boolean visible);
}