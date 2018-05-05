package com.anotap.messenger.mvp.view;

import java.util.List;

import com.anotap.messenger.model.Post;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by Ruslan Kolbasa on 14.07.2017.
 * phoenix
 */
public interface IFavePostsView extends IAccountDependencyView, IMvpView, IErrorView, IAttachmentsPlacesView {
    void displayData(List<Post> posts);
    void notifyDataSetChanged();
    void notifyDataAdded(int position, int count);
    void showRefreshing(boolean refreshing);

    void notifyItemChanged(int index);
}