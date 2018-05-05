package com.anotap.messenger.mvp.presenter.search;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import com.anotap.messenger.domain.IVideosInteractor;
import com.anotap.messenger.domain.InteractorFactory;
import com.anotap.messenger.fragment.search.criteria.VideoSearchCriteria;
import com.anotap.messenger.fragment.search.nextfrom.IntNextFrom;
import com.anotap.messenger.model.Video;
import com.anotap.messenger.mvp.view.search.IVideosSearchView;
import com.anotap.messenger.util.Pair;
import io.reactivex.Single;

import static com.anotap.messenger.util.Utils.nonEmpty;

/**
 * Created by Ruslan Kolbasa on 20.09.2017.
 * phoenix
 */
public class VideosSearchPresenter extends AbsSearchPresenter<IVideosSearchView, VideoSearchCriteria, Video, IntNextFrom> {

    private final IVideosInteractor videosInteractor;

    public VideosSearchPresenter(int accountId, @Nullable VideoSearchCriteria criteria, @Nullable Bundle savedInstanceState) {
        super(accountId, criteria, savedInstanceState);
        this.videosInteractor = InteractorFactory.createVideosInteractor();
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
    Single<Pair<List<Video>, IntNextFrom>> doSearch(int accountId, VideoSearchCriteria criteria, IntNextFrom startFrom) {
        final int offset = startFrom.getOffset();
        final IntNextFrom nextFrom = new IntNextFrom(offset + 50);
        return videosInteractor.seacrh(accountId, criteria, 50, offset)
                .map(videos -> Pair.create(videos, nextFrom));
    }

    @Override
    VideoSearchCriteria instantiateEmptyCriteria() {
        return new VideoSearchCriteria("");
    }

    @Override
    boolean canSearch(VideoSearchCriteria criteria) {
        return nonEmpty(criteria.getQuery());
    }

    @Override
    protected String tag() {
        return VideosSearchPresenter.class.getSimpleName();
    }
}