package com.anotap.messenger.api.impl;

import com.anotap.messenger.api.IServiceProvider;
import com.anotap.messenger.api.TokenType;
import com.anotap.messenger.api.interfaces.IStatusApi;
import com.anotap.messenger.api.services.IStatusService;
import io.reactivex.Single;

/**
 * Created by admin on 08.01.2017.
 * phoenix
 */
class StatusApi extends AbsApi implements IStatusApi {

    StatusApi(int accountId, IServiceProvider provider) {
        super(accountId, provider);
    }

    @Override
    public Single<Boolean> set(String text, Integer groupId) {
        return provideService(IStatusService.class, TokenType.USER)
                .flatMap(service -> service.set(text, groupId)
                        .map(extractResponseWithErrorHandling())
                        .map(response -> response == 1));
    }
}
