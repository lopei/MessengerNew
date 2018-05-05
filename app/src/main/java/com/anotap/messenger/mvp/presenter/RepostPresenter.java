package com.anotap.messenger.mvp.presenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.anotap.messenger.Injection;
import com.anotap.messenger.R;
import com.anotap.messenger.domain.IWalls;
import com.anotap.messenger.model.AttachmenEntry;
import com.anotap.messenger.model.Post;
import com.anotap.messenger.mvp.view.IRepostView;
import com.anotap.messenger.util.RxUtils;
import com.anotap.mvp.reflect.OnGuiCreated;

/**
 * Created by admin on 15.05.2017.
 * phoenix
 */
public class RepostPresenter extends AbsAttachmentsEditPresenter<IRepostView> {

    private static final String TAG = RepostPresenter.class.getSimpleName();

    private final Post post;
    private final Integer targetGroupId;
    private boolean publishingNow;

    private final IWalls walls;

    public RepostPresenter(int accountId, Post post, Integer targetGroupId, @Nullable Bundle savedInstanceState) {
        super(accountId, savedInstanceState);
        this.walls = Injection.provideWalls();
        this.post = post;
        this.targetGroupId = targetGroupId;

        getData().add(new AttachmenEntry(false, post));
    }

    @Override
    public void onGuiCreated(@NonNull IRepostView viewHost) {
        super.onGuiCreated(viewHost);
        viewHost.setSupportedButtons(false, false, false, false, false, false);
    }

    @OnGuiCreated
    private void resolveProgressDialog() {
        if (isGuiReady()) {
            if (publishingNow) {
                getView().displayProgressDialog(R.string.please_wait, R.string.publication, false);
            } else {
                getView().dismissProgressDialog();
            }
        }
    }

    private void setPublishingNow(boolean publishingNow) {
        this.publishingNow = publishingNow;
        resolveProgressDialog();
    }

    private void onPublishError(Throwable throwable) {
        setPublishingNow(false);
        showError(getView(), throwable);
    }

    @SuppressWarnings("unused")
    private void onPublishComplete(Post post) {
        setPublishingNow(false);
        getView().goBack();
    }

    public final void fireReadyClick() {
        setPublishingNow(true);

        final int accountId = super.getAccountId();
        final String body = getTextBody();

        appendDisposable(walls.repost(accountId, post.getVkid(), post.getOwnerId(), targetGroupId, body)
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(this::onPublishComplete, this::onPublishError));
    }

    @Override
    protected String tag() {
        return TAG;
    }
}