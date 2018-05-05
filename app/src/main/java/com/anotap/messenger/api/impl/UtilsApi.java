package com.anotap.messenger.api.impl;

import com.anotap.messenger.api.IServiceProvider;
import com.anotap.messenger.api.TokenType;
import com.anotap.messenger.api.interfaces.IUtilsApi;
import com.anotap.messenger.api.model.response.ResolveDomailResponse;
import com.anotap.messenger.api.services.IUtilsService;
import io.reactivex.Single;

/**
 * Created by admin on 09.01.2017.
 * phoenix
 */
class UtilsApi extends AbsApi implements IUtilsApi {

    UtilsApi(int accountId, IServiceProvider provider) {
        super(accountId, provider);
    }

    @Override
    public Single<ResolveDomailResponse> resolveScreenName(String screenName) {
        return provideService(IUtilsService.class, TokenType.USER)
                .flatMap(service -> service.resolveScreenName(screenName)
                .map(extractResponseWithErrorHandling()));
    }
}