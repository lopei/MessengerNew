package com.anotap.messenger.mvp.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.Injection;
import com.anotap.messenger.R;
import com.anotap.messenger.db.interfaces.ILogsStore;
import com.anotap.messenger.model.LogEvent;
import com.anotap.messenger.model.LogEventType;
import com.anotap.messenger.model.LogEventWrapper;
import com.anotap.messenger.mvp.presenter.base.RxSupportPresenter;
import com.anotap.messenger.mvp.view.ILogsView;
import com.anotap.messenger.util.DisposableHolder;
import com.anotap.messenger.util.RxUtils;
import com.anotap.messenger.util.Utils;
import com.anotap.mvp.reflect.OnGuiCreated;

/**
 * Created by Ruslan Kolbasa on 26.04.2017.
 * phoenix
 */
public class LogsPresenter extends RxSupportPresenter<ILogsView> {

    private final List<LogEventType> types;

    private final List<LogEventWrapper> events;

    private final ILogsStore store;

    public LogsPresenter(@Nullable Bundle savedInstanceState) {
        super(savedInstanceState);

        this.store = Injection.provideLogsStore();
        this.types = createTypes();
        this.events = new ArrayList<>();

        loadAll();
    }

    @OnGuiCreated
    private void resolveEmptyTextVisibility(){
        if(isGuiReady()){
            getView().setEmptyTextVisible(events.isEmpty());
        }
    }

    private boolean loadingNow;

    private void setLoading(boolean loading) {
        this.loadingNow = loading;
        resolveRefreshingView();
    }

    @Override
    public void onGuiCreated(@NonNull ILogsView viewHost) {
        super.onGuiCreated(viewHost);
        viewHost.displayData(events);
        viewHost.displayTypes(types);
    }

    @Override
    public void onGuiResumed() {
        super.onGuiResumed();
        resolveRefreshingView();
    }

    private void resolveRefreshingView(){
        if(isGuiResumed()){
            getView().showRefreshing(loadingNow);
        }
    }

    private void loadAll() {
        final int type = getSelectedType();

        setLoading(true);
        disposableHolder.append(store.getAll(type)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onDataReceived, throwable -> onDataReceiveError(Utils.getCauseIfRuntime(throwable))));
    }

    private void onDataReceiveError(Throwable throwable) {
        setLoading(false);
        safeShowError(getView(), throwable.getMessage());
    }

    private void onDataReceived(List<LogEvent> events) {
        setLoading(false);

        this.events.clear();

        for(LogEvent event : events){
            this.events.add(new LogEventWrapper(event));
        }

        callView(ILogsView::notifyEventDataChanged);
        resolveEmptyTextVisibility();
    }

    private int getSelectedType() {
        int type = LogEvent.Type.ERROR;
        for (LogEventType t : types) {
            if (t.isActive()) {
                type = t.getType();
            }
        }

        return type;
    }

    @Override
    public void onDestroyed() {
        disposableHolder.dispose();
        super.onDestroyed();
    }

    private DisposableHolder<Integer> disposableHolder = new DisposableHolder<>();

    private static List<LogEventType> createTypes() {
        List<LogEventType> types = new ArrayList<>();
        types.add(new LogEventType(LogEvent.Type.ERROR, R.string.log_type_error).setActive(true));
        return types;
    }

    @Override
    protected String tag() {
        return LogsPresenter.class.getSimpleName();
    }

    public void fireTypeClick(LogEventType entry) {
        if(getSelectedType() == entry.getType()){
            return;
        }

        for(LogEventType t : types){
            t.setActive(t.getType() == entry.getType());
        }

        callView(ILogsView::notifyTypesDataChanged);
        loadAll();
    }

    public void fireRefresh() {
        loadAll();
    }
}
