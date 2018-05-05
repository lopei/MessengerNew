package com.anotap.messenger.api.impl;

import com.anotap.messenger.api.IServiceProvider;
import com.anotap.messenger.api.interfaces.ICommentsApi;
import com.anotap.messenger.api.model.response.CustomCommentsResponse;
import com.anotap.messenger.api.services.ICommentsService;
import io.reactivex.Single;

/**
 * Created by admin on 03.01.2017.
 * phoenix
 */
class CommentsApi extends AbsApi implements ICommentsApi {

    CommentsApi(int accountId, IServiceProvider provider) {
        super(accountId, provider);
    }

    @Override
    public Single<CustomCommentsResponse> get(String sourceType, int ownerId, int sourceId, Integer offset,
                                              Integer count, String sort, Integer startCommentId, String accessKey, String fields) {
        return provideService(ICommentsService.class)
                .flatMap(service -> service
                        .get(sourceType, ownerId, sourceId, offset, count, sort, startCommentId, accessKey, fields)
                        .map(handleExecuteErrors("execute.getComments"))
                        .map(extractResponseWithErrorHandling()));
    }
}