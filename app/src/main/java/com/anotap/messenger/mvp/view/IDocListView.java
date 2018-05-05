package com.anotap.messenger.mvp.view;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.model.DocFilter;
import com.anotap.messenger.model.Document;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.messenger.upload.UploadObject;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 25.12.2016.
 * phoenix
 */
public interface IDocListView extends IAccountDependencyView, IMvpView, IErrorView {

    void displayData(List<Document> documents, boolean asImages);
    void showRefreshing(boolean refreshing);

    void notifyDataSetChanged();
    void notifyDataAdd(int position, int count);

    void openDocument(int accountId, @NonNull Document document);
    void returnSelection(ArrayList<Document> docs);

    void goToGifPlayer(int accountId, @NonNull ArrayList<Document> gifs, int selected);

    void requestReadExternalStoragePermission();

    void startSelectUploadFileActivity(int accountId);

    void setUploadDataVisible(boolean visible);
    void displayUploads(List<UploadObject> data);
    void notifyUploadDataChanged();
    void notifyUploadItemsAdded(int position, int count);
    void notifyUploadItemChanged(int position);
    void notifyUploadItemRemoved(int position);
    void notifyUploadProgressChanged(int position, int progress, boolean smoothly);

    void displayFilterData(List<DocFilter> filters);

    void notifyFiltersChanged();

    void setAdapterType(boolean imagesOnly);
}
