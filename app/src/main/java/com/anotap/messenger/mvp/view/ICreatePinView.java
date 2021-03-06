package com.anotap.messenger.mvp.view;

import android.support.annotation.StringRes;

import com.anotap.mvp.core.IMvpView;

/**
 * Created by ruslan.kolbasa on 10-Jun-16.
 * mobilebankingandroid
 */
public interface ICreatePinView extends IMvpView, IErrorView {
    void displayTitle(@StringRes int titleRes);
    void displayErrorAnimation();
    void displayPin(int[] value, int noValue);
    void sendSkipAndClose();
    void sendSuccessAndClose(int[] values);
}
