package com.anotap.messenger.api.interfaces;

import android.support.annotation.CheckResult;

import com.anotap.messenger.api.model.Items;
import com.anotap.messenger.api.model.VKApiStickerSet;
import io.reactivex.Single;

/**
 * Created by admin on 08.01.2017.
 * phoenix
 */
public interface IStoreApi {
    @CheckResult
    Single<Items<VKApiStickerSet>> getStickers();
    @CheckResult
    Single<Items<VKApiStickerSet.Product>> getProducts(Boolean extended, String filters, String type);
}