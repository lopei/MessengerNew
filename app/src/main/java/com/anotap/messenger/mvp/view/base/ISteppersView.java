package com.anotap.messenger.mvp.view.base;

import android.support.annotation.NonNull;

import com.anotap.messenger.view.steppers.base.AbsStepsHost;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by ruslan.kolbasa on 30.11.2016.
 * phoenix
 */
public interface ISteppersView<H extends AbsStepsHost> extends IMvpView {
    void updateStepView(int step);
    void moveSteppers(int from, int to);
    void goBack();
    void hideKeyboard();
    void updateStepButtonsAvailability(int step);
    void attachSteppersHost(@NonNull H mHost);
}
