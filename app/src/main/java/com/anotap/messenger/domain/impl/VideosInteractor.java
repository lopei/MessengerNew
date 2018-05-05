package com.anotap.messenger.domain.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.anotap.messenger.api.interfaces.INetworker;
import com.anotap.messenger.api.model.AccessIdPair;
import com.anotap.messenger.api.model.VKApiVideo;
import com.anotap.messenger.api.model.VKApiVideoAlbum;
import com.anotap.messenger.db.interfaces.IStores;
import com.anotap.messenger.db.model.entity.VideoAlbumEntity;
import com.anotap.messenger.db.model.entity.VideoEntity;
import com.anotap.messenger.domain.IVideosInteractor;
import com.anotap.messenger.domain.mappers.Dto2Entity;
import com.anotap.messenger.domain.mappers.Dto2Model;
import com.anotap.messenger.domain.mappers.Entity2Model;
import com.anotap.messenger.exception.NotFoundException;
import com.anotap.messenger.fragment.search.criteria.VideoSearchCriteria;
import com.anotap.messenger.fragment.search.options.SpinnerOption;
import com.anotap.messenger.model.Video;
import com.anotap.messenger.model.VideoAlbum;
import com.anotap.messenger.model.VideoAlbumCriteria;
import com.anotap.messenger.model.VideoCriteria;
import com.anotap.messenger.util.Pair;
import com.anotap.messenger.util.Utils;
import io.reactivex.Completable;
import io.reactivex.Single;

import static com.anotap.messenger.util.Utils.join;
import static com.anotap.messenger.util.Utils.listEmptyIfNull;

/**
 * Created by admin on 11.06.2017.
 * phoenix
 */
public class VideosInteractor implements IVideosInteractor {

    private final INetworker networker;

    private final IStores cache;

    public VideosInteractor(INetworker networker, IStores cache) {
        this.networker = networker;
        this.cache = cache;
    }

    @Override
    public Single<List<Video>> get(int accountId, int ownerId, int albumId, int count, int offset) {
        return networker.vkDefault(accountId)
                .video()
                .get(ownerId, null, albumId, count, offset, true)
                .flatMap(items -> {
                    List<VKApiVideo> dtos = Utils.listEmptyIfNull(items.getItems());
                    List<VideoEntity> dbos = new ArrayList<>(dtos.size());
                    List<Video> videos = new ArrayList<>(dbos.size());

                    for(VKApiVideo dto : dtos){
                        dbos.add(Dto2Entity.buildVideoEntity(dto));
                        videos.add(Dto2Model.transform(dto));
                    }

                    return cache.videos()
                            .insertData(accountId, ownerId, albumId, dbos, offset == 0)
                            .andThen(Single.just(videos));
                });
    }

    @Override
    public Single<List<Video>> getCachedVideos(int accountId, int ownerId, int albumId) {
        VideoCriteria criteria = new VideoCriteria(accountId, ownerId, albumId);
        return cache.videos()
                .findByCriteria(criteria)
                .map(dbos -> {
                    List<Video> videos = new ArrayList<>(dbos.size());
                    for(VideoEntity dbo : dbos){
                        videos.add(Entity2Model.buildVideoFromDbo(dbo));
                    }
                    return videos;
                });
    }

    @Override
    public Single<Video> getById(int accountId, int ownerId, int videoId, String accessKey, boolean cacheData) {
        Collection<AccessIdPair> ids = Collections.singletonList(new AccessIdPair(videoId, ownerId, accessKey));
        return networker.vkDefault(accountId)
                .video()
                .get(null, ids, null, null, null, true)
                .map(items -> {
                    if(Utils.nonEmpty(items.getItems())){
                        return items.getItems().get(0);
                    }

                    throw new NotFoundException();
                })
                .flatMap(dto -> {
                    if(cacheData){
                        VideoEntity dbo = Dto2Entity.buildVideoEntity(dto);

                        return cache.videos()
                                .insertData(accountId, ownerId, dto.album_id, Collections.singletonList(dbo), false)
                                .andThen(Single.just(dto));
                    }

                    return Single.just(dto);
                })
                .map(Dto2Model::transform);
    }

    @Override
    public Completable addToMy(int accountId, int targetOwnerId, int videoOwnerId, int videoId) {
        return networker.vkDefault(accountId)
                .video()
                .addVideo(targetOwnerId, videoId, videoOwnerId)
                .toCompletable();
    }

    @Override
    public Single<Pair<Integer, Boolean>> likeOrDislike(int accountId, int ownerId, int videoId, String accessKey, boolean like) {
        if(like){
            return networker.vkDefault(accountId)
                    .likes()
                    .add("video", ownerId, videoId, accessKey)
                    .map(integer -> Pair.create(integer, true));
        } else {
            return networker.vkDefault(accountId)
                    .likes()
                    .delete("video", ownerId, videoId)
                    .map(integer -> Pair.create(integer, false));
        }
    }

    @Override
    public Single<List<VideoAlbum>> getCachedAlbums(int accoutnId, int ownerId) {
        VideoAlbumCriteria criteria = new VideoAlbumCriteria(accoutnId, ownerId);
        return cache.videoAlbums()
                .findByCriteria(criteria)
                .map(dbos -> {
                    List<VideoAlbum> albums = new ArrayList<>(dbos.size());
                    for(VideoAlbumEntity dbo : dbos){
                        albums.add(Entity2Model.buildVideoAlbumFromDbo(dbo));
                    }
                    return albums;
                });
    }

    @Override
    public Single<List<VideoAlbum>> getActualAlbums(int accoutnId, int ownerId, int count, int offset) {
        return networker.vkDefault(accoutnId)
                .video()
                .getAlbums(ownerId, offset, count, false)
                .flatMap(items -> {
                    List<VKApiVideoAlbum> dtos = listEmptyIfNull(items.getItems());
                    List<VideoAlbumEntity> dbos = new ArrayList<>(dtos.size());
                    List<VideoAlbum> albums = new ArrayList<>(dbos.size());

                    for(VKApiVideoAlbum dto : dtos){
                        VideoAlbumEntity dbo = Dto2Entity.buildVideoAlbumDbo(dto);
                        dbos.add(dbo);
                        albums.add(Entity2Model.buildVideoAlbumFromDbo(dbo));
                    }

                    return cache.videoAlbums()
                            .insertData(accoutnId, ownerId, dbos, offset == 0)
                            .andThen(Single.just(albums));
                });
    }

    @Override
    public Single<List<Video>> seacrh(int accountId, VideoSearchCriteria criteria, int count, int offset) {
        SpinnerOption sortOption = criteria.findOptionByKey(VideoSearchCriteria.KEY_SORT);
        Integer sort = (sortOption == null || sortOption.value == null) ? null : sortOption.value.id;

        Boolean hd = criteria.extractBoleanValueFromOption(VideoSearchCriteria.KEY_HD);
        Boolean adult = criteria.extractBoleanValueFromOption(VideoSearchCriteria.KEY_ADULT);
        String filters = buildFiltersByCriteria(criteria);
        Boolean searchOwn = criteria.extractBoleanValueFromOption(VideoSearchCriteria.KEY_SEARCH_OWN);
        Integer longer = criteria.extractNumberValueFromOption(VideoSearchCriteria.KEY_DURATION_FROM);
        Integer shoter = criteria.extractNumberValueFromOption(VideoSearchCriteria.KEY_DURATION_TO);

        return networker.vkDefault(accountId)
                .video()
                .search(criteria.getQuery(), sort, hd, adult, filters, searchOwn, offset, longer, shoter, count, false)
                .map(response -> {
                    List<VKApiVideo> dtos = Utils.listEmptyIfNull(response.items);

                    List<Video> videos = new ArrayList<>(dtos.size());
                    for (VKApiVideo dto : dtos){
                        videos.add(Dto2Model.transform(dto));
                    }

                    return videos;
                });
    }

    private static String buildFiltersByCriteria(VideoSearchCriteria criteria) {
        Boolean youtube = criteria.extractBoleanValueFromOption(VideoSearchCriteria.KEY_YOUTUBE);
        Boolean vimeo = criteria.extractBoleanValueFromOption(VideoSearchCriteria.KEY_VIMEO);
        Boolean shortVideos = criteria.extractBoleanValueFromOption(VideoSearchCriteria.KEY_SHORT);
        Boolean longVideos = criteria.extractBoleanValueFromOption(VideoSearchCriteria.KEY_LONG);

        ArrayList<String> list = new ArrayList<>();
        if (youtube != null && youtube) {
            list.add("youtube");
        }

        if (vimeo != null && vimeo) {
            list.add("vimeo");
        }

        if (shortVideos != null && shortVideos) {
            list.add("short");
        }

        if (longVideos != null && longVideos) {
            list.add("long");
        }

        return list.isEmpty() ? null : join(",", list);
    }
}