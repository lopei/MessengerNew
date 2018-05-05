package com.anotap.messenger.mvp.view;

import java.util.List;

import com.anotap.messenger.model.LogEventType;
import com.anotap.messenger.model.LogEventWrapper;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by Ruslan Kolbasa on 26.04.2017.
 * phoenix
 */
public interface ILogsView extends IMvpView, IErrorView {

    void displayTypes(List<LogEventType> types);

    void displayData(List<LogEventWrapper> events);

    void showRefreshing(boolean refreshing);

    void notifyEventDataChanged();

    void notifyTypesDataChanged();

    void setEmptyTextVisible(boolean visible);
}
