package com.anotap.messenger.api.impl;

import com.anotap.messenger.api.IOtherVkRetrofitProvider;
import com.anotap.messenger.api.interfaces.ILongpollApi;
import com.anotap.messenger.api.model.longpoll.VkApiLongpollUpdates;
import com.anotap.messenger.api.services.ILongpollUpdatesService;
import io.reactivex.Single;

/**
 * Created by Ruslan Kolbasa on 31.07.2017.
 * phoenix
 */
public class LongpollApi implements ILongpollApi {

    private final IOtherVkRetrofitProvider provider;

    public LongpollApi(IOtherVkRetrofitProvider provider) {
        this.provider = provider;
    }

    @Override
    public Single<VkApiLongpollUpdates> getUpdates(String server, String key, long ts, int wait, int mode, int version) {
        return provider.provideLongpollRetrofit()
                .flatMap(wrapper -> wrapper.create(ILongpollUpdatesService.class)
                        .getUpdates(server, "a_check", key, ts, wait, mode, version));
    }
}