package com.anotap.messenger.domain;

import java.util.List;

import com.anotap.messenger.fragment.search.criteria.VideoSearchCriteria;
import com.anotap.messenger.model.Video;
import com.anotap.messenger.model.VideoAlbum;
import com.anotap.messenger.util.Pair;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by admin on 11.06.2017.
 * phoenix
 */
public interface IVideosInteractor {
    Single<List<Video>> get(int accountId, int ownerId, int albumId, int count, int offset);
    Single<List<Video>> getCachedVideos(int accountId, int ownerId, int albumId);

    Single<Video> getById(int accountId, int ownerId, int videoId, String accessKey, boolean cache);
    Completable addToMy(int accountId, int targetOwnerId, int videoOwnerId, int videoId);

    Single<Pair<Integer, Boolean>> likeOrDislike(int accountId, int ownerId, int videoId, String accessKey, boolean like);

    Single<List<VideoAlbum>> getCachedAlbums(int accoutnId, int ownerId);
    Single<List<VideoAlbum>> getActualAlbums(int accoutnId, int ownerId, int count, int offset);

    Single<List<Video>> seacrh(int accountId, VideoSearchCriteria criteria, int count, int offset);
}