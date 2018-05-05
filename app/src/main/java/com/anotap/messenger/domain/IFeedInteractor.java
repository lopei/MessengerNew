package com.anotap.messenger.domain;

import java.util.List;

import com.anotap.messenger.fragment.search.criteria.NewsFeedCriteria;
import com.anotap.messenger.model.FeedList;
import com.anotap.messenger.model.News;
import com.anotap.messenger.model.Post;
import com.anotap.messenger.util.Pair;
import io.reactivex.Single;

/**
 * Created by Ruslan Kolbasa on 06.09.2017.
 * phoenix
 */
public interface IFeedInteractor {
    Single<List<News>> getCachedFeed(int accountId);
    Single<Pair<List<News>, String>> getActualFeed(int accountId, int count, String nextFrom, String filters, Integer maxPhotos, String sourceIds);
    Single<Pair<List<Post>, String>> search(int accountId, NewsFeedCriteria criteria, int count, String startFrom);

    Single<List<FeedList>> getCachedFeedLists(int accountId);
    Single<List<FeedList>> getActualFeedLists(int accountId);
}