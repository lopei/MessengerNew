package com.anotap.messenger.fragment.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.widget.Toast;

import com.anotap.messenger.mvp.view.IErrorView;
import com.anotap.messenger.mvp.view.IToastView;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.messenger.util.Utils;
import com.anotap.mvp.core.AbsPresenter;
import com.anotap.mvp.core.IMvpView;
import com.anotap.mvp.ui.AbsPresenterDialogFragment;

/**
 * Created by ruslan.kolbasa on 11.10.2016.
 * phoenix
 */
public abstract class BasePresenterDialogFragment<P extends AbsPresenter<V>, V extends IMvpView>
        extends AbsPresenterDialogFragment<P, V> implements IMvpView, IAccountDependencyView, IErrorView, IToastView {

    @Override
    public void showToast(@StringRes int titleTes, boolean isLong, Object... params) {
        if(isAdded()){
            Toast.makeText(getActivity(), getString(titleTes), isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showError(String text) {
        if(isAdded()){
            Utils.showRedTopToast(getActivity(), text);
        }
    }

    @Override
    public void showError(@StringRes int titleTes, Object... params) {
        if(isAdded()){
            showError(getString(titleTes, params));
        }
    }

    @Override
    public void savePresenterState(@NonNull P presenter, @NonNull Bundle outState) {
        presenter.saveState(outState);
    }

    @Override
    public void displayAccountNotSupported() {
        // TODO: 18.12.2017
    }

    @Override
    public void displayAccountSupported() {
        // TODO: 18.12.2017
    }
}
