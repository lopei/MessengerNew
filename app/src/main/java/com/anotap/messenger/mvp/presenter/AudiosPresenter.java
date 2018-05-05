package com.anotap.messenger.mvp.presenter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.domain.IAudioInteractor;
import com.anotap.messenger.domain.InteractorFactory;
import com.anotap.messenger.model.Audio;
import com.anotap.messenger.mvp.presenter.base.AccountDependencyPresenter;
import com.anotap.messenger.mvp.view.IAudiosView;
import com.anotap.messenger.place.PlaceFactory;
import com.anotap.messenger.player.MusicPlaybackService;
import com.anotap.messenger.util.RxUtils;
import com.anotap.messenger.util.Utils;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by admin on 1/4/2018.
 * Phoenix-for-VK
 */
public class AudiosPresenter extends AccountDependencyPresenter<IAudiosView> {

    private final IAudioInteractor audioInteractor;
    private final ArrayList<Audio> audios;
    private final int ownerId;
    private final boolean audioAvailable;
    private boolean actualReceived;

    public AudiosPresenter(int accountId, int ownerId, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);
        this.audioInteractor = InteractorFactory.createAudioInteractor();
        this.audioAvailable = audioInteractor.isAudioPluginAvailable();
        this.audios = new ArrayList<>();
        this.ownerId = ownerId;

        if (audioAvailable) {
            requestList();
        }
    }

    private CompositeDisposable audioListDisposable = new CompositeDisposable();

    private boolean loadingNow;

    public void setLoadingNow(boolean loadingNow) {
        this.loadingNow = loadingNow;
        resolveRefreshingView();
    }

    @Override
    public void onGuiResumed() {
        super.onGuiResumed();
        resolveRefreshingView();
    }

    private void resolveRefreshingView() {
        if (isGuiResumed()) {
            getView().displayRefreshing(loadingNow);
        }
    }

    private boolean endOfContent;

    private void requestNext() {
        setLoadingNow(true);
        final int offset = audios.size();
        audioListDisposable.add(audioInteractor.get(ownerId, offset)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onNextListReceived, this::onListGetError));
    }

    private void requestList() {
        setLoadingNow(true);
        audioListDisposable.add(audioInteractor.get(ownerId, 0)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onListReceived, this::onListGetError));
    }

    private void onNextListReceived(List<Audio> next) {
        next.removeAll(audios);
        audios.addAll(next);
        endOfContent = next.isEmpty();
        setLoadingNow(false);
        callView(IAudiosView::notifyListChanged);
    }

    private void onListReceived(List<Audio> data) {
        audios.clear();
        audios.addAll(data);
        endOfContent = data.isEmpty();
        actualReceived = true;
        setLoadingNow(false);
        callView(IAudiosView::notifyListChanged);
    }

    public void playAudio(Context context, int position) {
        MusicPlaybackService.startForPlayList(context, audios, position, false);
        PlaceFactory.getPlayerPlace(getAccountId()).tryOpenWith(context);
    }

    public void fireRefresh() {
        if (audioAvailable) {
            audioListDisposable.clear();
            requestList();
        }
    }

    @Override
    public void onDestroyed() {
        audioListDisposable.dispose();
        super.onDestroyed();
    }

    private void onListGetError(Throwable t) {
        setLoadingNow(false);
        showError(getView(), Utils.getCauseIfRuntime(t));
    }

    @Override
    public void onGuiCreated(@NonNull IAudiosView view) {
        super.onGuiCreated(view);
        view.displayList(audios);
        view.setBlockedScreen(!audioAvailable);
    }

    @Override
    protected String tag() {
        return AudiosPresenter.class.getSimpleName();
    }

    public void fireScrollToEnd() {
        if (actualReceived && !endOfContent) {
            requestNext();
        }
    }
}