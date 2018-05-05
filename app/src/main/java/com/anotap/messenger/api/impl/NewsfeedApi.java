package com.anotap.messenger.api.impl;

import java.util.Collection;

import com.anotap.messenger.api.IServiceProvider;
import com.anotap.messenger.api.TokenType;
import com.anotap.messenger.api.interfaces.INewsfeedApi;
import com.anotap.messenger.api.model.Items;
import com.anotap.messenger.api.model.VkApiFeedList;
import com.anotap.messenger.api.model.response.NewsfeedCommentsResponse;
import com.anotap.messenger.api.model.response.NewsfeedResponse;
import com.anotap.messenger.api.model.response.NewsfeedSearchResponse;
import com.anotap.messenger.api.services.INewsfeedService;
import io.reactivex.Single;

/**
 * Created by admin on 03.01.2017.
 * phoenix
 */
class NewsfeedApi extends AbsApi implements INewsfeedApi {

    NewsfeedApi(int accountId, IServiceProvider provider) {
        super(accountId, provider);
    }

    @Override
    public Single<Items<VkApiFeedList>> getLists(Collection<Integer> listIds) {
        return provideService(INewsfeedService.class, TokenType.USER)
                .flatMap(service -> service.getLists(join(listIds, ","), 1)
                        .map(extractResponseWithErrorHandling()));
    }

    @Override
    public Single<NewsfeedSearchResponse> search(String query, Boolean extended, Integer count, Double latitude, Double longitude, Long startTime, Long endTime, String startFrom, String fields) {
        return provideService(INewsfeedService.class, TokenType.USER)
                .flatMap(service -> service
                        .search(query, integerFromBoolean(extended), count, latitude, longitude,
                                startTime, endTime, startFrom, fields)
                        .map(extractResponseWithErrorHandling()));
    }

    @Override
    public Single<NewsfeedCommentsResponse> getComments(Integer count, String filters, String reposts, Long startTime, Long endTime, Integer lastCommentsCount, String startFrom, String fields) {
        return provideService(INewsfeedService.class, TokenType.USER)
                .flatMap(service -> service
                        .getComments(count, filters, reposts, startTime, endTime, lastCommentsCount, startFrom, fields, null)
                        .map(extractResponseWithErrorHandling()));
    }

    @Override
    public Single<NewsfeedResponse> get(String filters, Boolean returnBanned, Long startTime,
                                        Long endTime, Integer maxPhotoCount, String sourceIds,
                                        String startFrom, Integer count, String fields) {
        return provideService(INewsfeedService.class, TokenType.USER)
                .flatMap(service -> service
                        .get(filters, integerFromBoolean(returnBanned), startTime, endTime,
                                maxPhotoCount, sourceIds, startFrom, count, fields)
                        .map(extractResponseWithErrorHandling()));
    }
}
