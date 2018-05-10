package com.anotap.messenger.mvp.presenter.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.anotap.messenger.App;
import com.anotap.messenger.Injection;
import com.anotap.messenger.db.Stores;
import com.anotap.messenger.mvp.view.IErrorView;
import com.anotap.messenger.mvp.view.IToastView;
import com.anotap.messenger.service.ErrorLocalizer;
import com.anotap.messenger.util.Analytics;
import com.anotap.messenger.util.InstancesCounter;
import com.anotap.messenger.util.Utils;
import com.anotap.mvp.core.AbsPresenter;
import com.anotap.mvp.core.IMvpView;

import java.net.ConnectException;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.anotap.messenger.util.Objects.isNull;
import static com.anotap.messenger.util.Objects.nonNull;

/**
 * Created by admin on 24.09.2016.
 * phoenix
 */
public abstract class RxSupportPresenter<V extends IMvpView> extends AbsPresenter<V> {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private static InstancesCounter instancesCounter = new InstancesCounter();

    private static final String SAVE_INSTANCE_ID = "save_instance_id";
    private static final String SAVE_TEMP_DATA_USAGE = "save_temp_data_usage";

    private final int instanceId;

    public RxSupportPresenter(@Nullable Bundle savedInstanceState) {
        super(savedInstanceState);

        if (nonNull(savedInstanceState)) {
            instanceId = savedInstanceState.getInt(SAVE_INSTANCE_ID);
            instancesCounter.fireExists(getClass(), instanceId);
            tempDataUsage = savedInstanceState.getBoolean(SAVE_TEMP_DATA_USAGE);
        } else {
            instanceId = instancesCounter.incrementAndGet(getClass());
        }
    }

    private boolean tempDataUsage;

    protected void fireTempDataUsage() {
        this.tempDataUsage = true;
    }

    private int viewCreationCounter;

    @Override
    public void onGuiCreated(@NonNull V view) {
        viewCreationCounter++;
        super.onGuiCreated(view);
    }

    public int getViewCreationCount() {
        return viewCreationCounter;
    }

    @Override
    public void saveState(@NonNull Bundle outState) {
        super.saveState(outState);
        outState.putInt(SAVE_INSTANCE_ID, instanceId);
        outState.putBoolean(SAVE_TEMP_DATA_USAGE, tempDataUsage);
    }

    protected int getInstanceId() {
        return instanceId;
    }

    @Override
    public void onDestroyed() {
        compositeDisposable.dispose();

        if (tempDataUsage) {
            Stores.getInstance()
                    .tempStore()
                    .delete(getInstanceId())
                    .subscribeOn(Schedulers.io())
                    .subscribe(() -> {}, Analytics::logUnexpectedError);
            tempDataUsage = false;
        }

        super.onDestroyed();
    }

    protected void appendDisposable(@NonNull Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    protected void showError(IErrorView view, Throwable throwable) {
        if(throwable instanceof ConnectException) {
            return;
        }
        if (isNull(view)) {
            return;
        }

        throwable = Utils.getCauseIfRuntime(throwable);

        view.showError(ErrorLocalizer.localizeThrowable(getApplicationContext(), throwable));
    }

    protected void safeShowError(IErrorView view, @StringRes int text, Object... params) {
        if (isGuiReady()) {
            view.showError(text, params);
        }
    }

    protected void safeShowLongToast(IToastView view, @StringRes int text, Object... params) {
        safeShowToast(view, text, true, params);
    }


    protected void safeShowToast(IToastView view, @StringRes int text, boolean isLong, Object... params) {
        if (nonNull(view)) {
            view.showToast(text, isLong, params);
        }
    }

    protected void safeShowError(IErrorView view, String text) {
        if (nonNull(view)) {
            view.showError(text);
        }
    }

    protected Context getApplicationContext() {
        return Injection.provideApplicationContext();
    }

    protected String getString(@StringRes int res) {
        return App.getInstance().getString(res);
    }

    protected String getString(@StringRes int res, Object... params) {
        return App.getInstance().getString(res, params);
    }
}