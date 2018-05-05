package com.anotap.messenger.mvp.view;

import android.support.annotation.NonNull;

import java.util.List;

import com.anotap.messenger.model.Poll;
import com.anotap.messenger.mvp.view.base.IAccountDependencyView;
import com.anotap.mvp.core.IMvpView;

/**
 * Created by admin on 19.12.2016.
 * phoenix
 */
public interface IPollView extends IAccountDependencyView, IMvpView, IErrorView {

    void displayQuestion(String title);

    void displayType(boolean anonymous);

    void displayCreationTime(long unixtime);

    void displayVoteCount(int count);

    void displayVotesList(List<Poll.Answer> answers, boolean canCheck, Integer myVoteId);

    void displayLoading(boolean loading);

    void setupButton(boolean voted);

    void sendDataToParent(@NonNull Poll poll);
}
