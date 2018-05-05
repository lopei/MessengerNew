package com.anotap.messenger.api.impl;

import com.anotap.messenger.api.IServiceProvider;
import com.anotap.messenger.api.TokenType;
import com.anotap.messenger.api.interfaces.IStoreApi;
import com.anotap.messenger.api.model.Items;
import com.anotap.messenger.api.model.VKApiStickerSet;
import com.anotap.messenger.api.services.IStoreService;
import io.reactivex.Single;

/**
 * Created by admin on 08.01.2017.
 * phoenix
 */
class StoreApi extends AbsApi implements IStoreApi {

    StoreApi(int accountId, IServiceProvider provider) {
        super(accountId, provider);
    }

    @Override
    public Single<Items<VKApiStickerSet>> getStickers() {
        return provideService(IStoreService.class, TokenType.USER)
                .flatMap(service -> service.getStockItems("stickers")
                        .map(extractResponseWithErrorHandling()));
    }

    @Override
    public Single<Items<VKApiStickerSet.Product>> getProducts(Boolean extended, String filters, String type) {
        return provideService(IStoreService.class, TokenType.USER)
                .flatMap(service -> service
                        .getProducts(integerFromBoolean(extended), filters, type)
                        .map(extractResponseWithErrorHandling()));
    }
}