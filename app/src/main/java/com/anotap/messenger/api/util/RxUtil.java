package com.anotap.messenger.api.util;

import android.support.annotation.Nullable;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by alan on 17.10.17.
 */

public class RxUtil {
    public static <S> void mainThreadConsumer(S object, Consumer<S> consumer) {
        mainThreadConsumer(object, consumer, null);
    }

    public static <S> void mainThreadConsumer(S object, Consumer<? super S> consumer, @Nullable Consumer<Throwable> errorConsumer) {
        Observable
                .just(object)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer, errorConsumer == null ? new io.reactivex.functions.Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {

                    }
                } : errorConsumer);
    }

    public static void delayedConsumer(long delay, Consumer<Long> consumer) {
        delayedConsumer(delay, consumer, null);
    }

    public static void delayedConsumer(long delay, Consumer<Long> consumer, @Nullable Consumer<Throwable> errorConsumer) {
        Observable
                .timer(delay, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer, errorConsumer == null ? new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                } : errorConsumer);
    }

    public static <S> void networkConsumer(Observable<S> observable, Consumer<S> consumer, @Nullable Consumer<Throwable> errorConsumer) {
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer, errorConsumer == null ? new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                } : errorConsumer);
    }

    public static <S> void networkConsumer(Observable<S> observable, Consumer<S> consumer) {
        networkConsumer(observable, consumer, null);
    }

    public static <S> void asyncConsumer(Observable<S> observable, final Consumer<S> consumer, @Nullable Consumer<Throwable> errorConsumer) {
        observable
                .flatMap(new Function<S, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(S s) throws Exception {
                        consumer.accept(s);
                        return Observable.just(0L);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                    }
                }, errorConsumer == null ? new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) {
                        throwable.printStackTrace();
                    }
                } : errorConsumer);
    }

    public static <S> void asyncConsumer(Observable<S> observable, Consumer<S> consumer) {
        asyncConsumer(observable, consumer, null);
    }
}