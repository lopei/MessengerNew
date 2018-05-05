package com.anotap.messenger.mvp.presenter.search;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import com.anotap.messenger.Injection;
import com.anotap.messenger.db.model.PostUpdate;
import com.anotap.messenger.domain.ILikesInteractor;
import com.anotap.messenger.domain.IWalls;
import com.anotap.messenger.fragment.search.criteria.WallSearchCriteria;
import com.anotap.messenger.fragment.search.nextfrom.IntNextFrom;
import com.anotap.messenger.model.Post;
import com.anotap.messenger.mvp.view.search.IBaseSearchView;
import com.anotap.messenger.mvp.view.search.IWallSearchView;
import com.anotap.messenger.util.Pair;
import com.anotap.messenger.util.RxUtils;
import com.anotap.messenger.util.Utils;
import io.reactivex.Single;

import static com.anotap.messenger.util.Objects.isNull;
import static com.anotap.messenger.util.Objects.nonNull;
import static com.anotap.messenger.util.Utils.getCauseIfRuntime;

/**
 * Created by admin on 02.05.2017.
 * phoenix
 */
public class WallSearchPresenter extends AbsSearchPresenter<IWallSearchView, WallSearchCriteria, Post, IntNextFrom> {

    private static final String TAG = WallSearchPresenter.class.getSimpleName();

    private final IWalls walls;

    public WallSearchPresenter(int accountId, @Nullable WallSearchCriteria criteria, @Nullable Bundle savedInstanceState) {
        super(accountId, criteria, savedInstanceState);
        this.walls = Injection.provideWalls();

        appendDisposable(walls.observeMinorChanges()
                .observeOn(Injection.provideMainThreadScheduler())
                .subscribe(this::onPostMinorUpdates));
    }

    private void onPostMinorUpdates(PostUpdate update) {
        for(int i = 0; i < super.data.size(); i++){
            final Post post = super.data.get(i);

            if(post.getVkid() == update.getPostId() && post.getOwnerId() == update.getOwnerId()){
                if (nonNull(update.getLikeUpdate())) {
                    post.setLikesCount(update.getLikeUpdate().getCount());
                    post.setUserLikes(update.getLikeUpdate().isLiked());
                }

                if (nonNull(update.getDeleteUpdate())) {
                    post.setDeleted(update.getDeleteUpdate().isDeleted());
                }

                boolean pinStateChanged = false;

                if (nonNull(update.getPinUpdate())) {
                    pinStateChanged = true;

                    for (Post p : super.data) {
                        p.setPinned(false);
                    }

                    post.setPinned(update.getPinUpdate().isPinned());
                }

                if(pinStateChanged){
                    callView(IBaseSearchView::notifyDataSetChanged);
                } else {
                    int finalI = i;
                    callView(view -> view.notifyItemChanged(finalI));
                }

                break;
            }
        }
    }

    @Override
    IntNextFrom getInitialNextFrom() {
        return new IntNextFrom(0);
    }

    @Override
    boolean isAtLast(IntNextFrom startFrom) {
        return startFrom.getOffset() == 0;
    }

    @Override
    protected String tag() {
        return TAG;
    }

    private static final int COUNT = 30;

    @Override
    Single<Pair<List<Post>, IntNextFrom>> doSearch(int accountId, WallSearchCriteria criteria, IntNextFrom startFrom) {
        final int offset = isNull(startFrom) ? 0 : startFrom.getOffset();
        final IntNextFrom nextFrom = new IntNextFrom(offset + COUNT);

        return walls.search(accountId, criteria.getOwnerId(), criteria.getQuery(), true, COUNT, offset)
                .map(pair -> Pair.create(pair.getFirst(), nextFrom));
    }

    @Override
    WallSearchCriteria instantiateEmptyCriteria() {
        // not supported
        throw new UnsupportedOperationException();
    }

    @Override
    boolean canSearch(WallSearchCriteria criteria) {
        return Utils.trimmedNonEmpty(criteria.getQuery());
    }

    public final void fireShowCopiesClick(Post post) {
        fireCopiesLikesClick("post", post.getOwnerId(), post.getVkid(), ILikesInteractor.FILTER_COPIES);
    }

    public final void fireShowLikesClick(Post post) {
        fireCopiesLikesClick("post", post.getOwnerId(), post.getVkid(), ILikesInteractor.FILTER_LIKES);
    }

    public void fireLikeClick(Post post) {
        final int accountId = super.getAccountId();

        appendDisposable(walls.like(accountId, post.getOwnerId(), post.getVkid(), !post.isUserLikes())
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(ignored -> {
                }, t -> showError(getView(), getCauseIfRuntime(t))));
    }
}