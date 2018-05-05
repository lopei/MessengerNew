package com.anotap.messenger.mvp.view;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.anotap.messenger.model.Poll;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 20.12.2016.
 * phoenix
 */
public interface ICreatePollView extends IAccountDependencyView, IMvpView, IProgressView, IErrorView {
    void displayQuestion(String question);

    void setAnonymous(boolean anomymous);

    void displayOptions(String[] options);

    void showQuestionError(@StringRes int message);

    void showOptionError(int index, @StringRes int message);

    void sendResultAndGoBack(@NonNull Poll poll);
}
