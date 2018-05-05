package com.anotap.messenger.mvp.view;

import java.util.List;

import com.anotap.messenger.model.Video;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 09.01.2017.
 * phoenix
 */
public interface IFaveVideosView extends IAccountDependencyView, IMvpView, IErrorView {
    void displayData(List<Video> videos);
    void notifyDataSetChanged();
    void notifyDataAdded(int position, int count);
    void showRefreshing(boolean refreshing);
    void goToPreview(int accountId, Video video);
}
