package com.anotap.messenger.api.impl;

import com.anotap.messenger.api.IServiceProvider;
import com.anotap.messenger.api.TokenType;
import com.anotap.messenger.api.interfaces.ILikesApi;
import com.anotap.messenger.api.model.response.LikesListResponse;
import com.anotap.messenger.api.services.ILikesService;
import io.reactivex.Single;

/**
 * Created by admin on 08.01.2017.
 * phoenix
 */
class LikesApi extends AbsApi implements ILikesApi {

    LikesApi(int accountId, IServiceProvider provider) {
        super(accountId, provider);
    }

    @Override
    public Single<LikesListResponse> getList(String type, Integer ownerId, Integer itemId, String pageUrl,
                                             String filter, Boolean friendsOnly, Integer offset,
                                             Integer count, Boolean skipOwn, String fields) {
        return provideService(ILikesService.class, TokenType.USER)
                .flatMap(service -> service
                        .getList(type, ownerId, itemId, pageUrl, filter, integerFromBoolean(friendsOnly),
                                1, offset, count, integerFromBoolean(skipOwn), fields)
                        .map(extractResponseWithErrorHandling()));
    }

    @Override
    public Single<Integer> delete(String type, Integer ownerId, int itemId) {
        return provideService(ILikesService.class, TokenType.USER)
                .flatMap(service -> service.delete(type, ownerId, itemId)
                        .map(extractResponseWithErrorHandling())
                        .map(response -> response.likes));
    }

    @Override
    public Single<Integer> add(String type, Integer ownerId, int itemId, String accessKey) {
        return provideService(ILikesService.class, TokenType.USER)
                .flatMap(service -> service.add(type, ownerId, itemId, accessKey)
                        .map(extractResponseWithErrorHandling())
                        .map(respose -> respose.likes));
    }
}
