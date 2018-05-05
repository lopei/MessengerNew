package com.anotap.messenger.mvp.view;

import java.util.List;

import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 29.03.2017.
 * phoenix
 */
public interface IBaseChatAttachmentsView<T> extends IMvpView, IAccountDependencyView,
        IAttachmentsPlacesView, IErrorView {

    void displayAttachments(List<T> data);

    void notifyDataAdded(int position, int count);

    void notifyDatasetChanged();

    void showLoading(boolean loading);

    void setEmptyTextVisible(boolean visible);

    void setToolbarTitle(String title);

    void setToolbarSubtitle(String subtitle);
}
