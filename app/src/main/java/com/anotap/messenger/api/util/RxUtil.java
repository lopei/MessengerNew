package com.anotap.messenger.api.util;

import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by alan on 17.10.17.
 */

public class RxUtil {
    public static <S> void mainThreadAction(S object, Action1<S> action) {
        mainThreadAction(object, action, null);
    }

    public static <S> void mainThreadAction(S object, Action1<S> action, @Nullable Action1<Throwable> errorAction) {
        Observable
                .just(object)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action, errorAction == null ? new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                } : errorAction);
    }

    public static void delayedAction(long delay, Action1<Long> action) {
        delayedAction(delay, action, null);
    }

    public static void delayedAction(long delay, Action1<Long> action, @Nullable Action1<Throwable> errorAction) {
        Observable
                .timer(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action, errorAction == null ? new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                } : errorAction);
    }

    public static <S> void networkAction(Observable<S> observable, Action1<S> action, @Nullable Action1<Throwable> errorAction) {
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action, errorAction == null ? new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                } : errorAction);
    }

    public static <S> void networkAction(Observable<S> observable, Action1<S> action) {
        networkAction(observable, action, null);
    }

    public static <S> void asyncAction(Observable<S> observable, Action1<S> action, @Nullable Action1<Throwable> errorAction) {
        observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(action, errorAction == null ? new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                } : errorAction);
    }

    public static <S> void asyncAction(Observable<S> observable, Action1<S> action) {
        asyncAction(observable, action, null);
    }
}
