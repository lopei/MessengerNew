package com.anotap.messenger.mvp.presenter.search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import com.anotap.messenger.Injection;
import com.anotap.messenger.api.model.VKApiPost;
import com.anotap.messenger.db.model.PostUpdate;
import com.anotap.messenger.domain.IFeedInteractor;
import com.anotap.messenger.domain.IWalls;
import com.anotap.messenger.domain.InteractorFactory;
import com.anotap.messenger.fragment.search.criteria.NewsFeedCriteria;
import com.anotap.messenger.fragment.search.nextfrom.StringNextFrom;
import com.anotap.messenger.model.Commented;
import com.anotap.messenger.model.Post;
import com.anotap.messenger.mvp.view.search.INewsFeedSearchView;
import com.anotap.messenger.util.Pair;
import com.anotap.messenger.util.RxUtils;
import com.anotap.messenger.util.Utils;
import io.reactivex.Single;

/**
 * Created by admin on 03.10.2017.
 * phoenix
 */
public class NewsFeedSearchPresenter extends AbsSearchPresenter<INewsFeedSearchView, NewsFeedCriteria, Post, StringNextFrom> {

    private final IFeedInteractor feedInteractor;

    private final IWalls walls;

    public NewsFeedSearchPresenter(int accountId, @Nullable NewsFeedCriteria criteria, @Nullable Bundle savedInstanceState) {
        super(accountId, criteria, savedInstanceState);
        this.feedInteractor = InteractorFactory.createFeedInteractor();
        this.walls = Injection.provideWalls();

        this.walls.observeMinorChanges()
                .observeOn(Injection.provideMainThreadScheduler())
                .subscribe(this::onPostUpdate);
    }

    private void onPostUpdate(PostUpdate update){
        // TODO: 03.10.2017
    }

    @Override
    StringNextFrom getInitialNextFrom() {
        return new StringNextFrom(null);
    }

    @Override
    boolean isAtLast(StringNextFrom startFrom) {
        return Utils.isEmpty(startFrom.getNextFrom());
    }

    @Override
    Single<Pair<List<Post>, StringNextFrom>> doSearch(int accountId, NewsFeedCriteria criteria, StringNextFrom startFrom) {
        return feedInteractor.search(accountId, criteria, 50, startFrom.getNextFrom())
                .map(pair -> Pair.create(pair.getFirst(), new StringNextFrom(pair.getSecond())));
    }

    @Override
    NewsFeedCriteria instantiateEmptyCriteria() {
        return new NewsFeedCriteria("");
    }

    @Override
    public void firePostClick(@NonNull Post post) {
        if (post.getPostType() == VKApiPost.Type.REPLY) {
            getView().openComments(getAccountId(), Commented.from(post), post.getVkid());
        } else {
            getView().openPost(getAccountId(), post);
        }
    }

    @Override
    boolean canSearch(NewsFeedCriteria criteria) {
        return Utils.nonEmpty(criteria.getQuery());
    }

    @Override
    protected String tag() {
        return NewsFeedSearchPresenter.class.getSimpleName();
    }

    public void fireLikeClick(Post post) {
        final int accountId = super.getAccountId();

        appendDisposable(walls.like(accountId, post.getOwnerId(), post.getVkid(), !post.isUserLikes())
                .compose(RxUtils.applySingleIOToMainSchedulers())
                .subscribe(integer -> {/*ignore*/}, t -> showError(getView(), Utils.getCauseIfRuntime(t))));
    }
}