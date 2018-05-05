package com.anotap.messenger.api.impl;

import com.anotap.messenger.api.IOtherVkRetrofitProvider;
import com.anotap.messenger.api.IUploadRetrofitProvider;
import com.anotap.messenger.api.IVkRetrofitProvider;
import com.anotap.messenger.api.OtherVkRetrofitProvider;
import com.anotap.messenger.api.UploadRetrofitProvider;
import com.anotap.messenger.api.VkMethodHttpClientFactory;
import com.anotap.messenger.api.VkRetrofitProvider;
import com.anotap.messenger.api.interfaces.IAccountApis;
import com.anotap.messenger.api.interfaces.IAuthApi;
import com.anotap.messenger.api.interfaces.ILongpollApi;
import com.anotap.messenger.api.interfaces.INetworker;
import com.anotap.messenger.api.interfaces.IUploadApi;
import com.anotap.messenger.api.services.IAuthService;
import com.anotap.messenger.settings.IProxySettings;

/**
 * Created by ruslan.kolbasa on 30.12.2016.
 * phoenix
 */
public class Networker implements INetworker {

    private final IOtherVkRetrofitProvider otherVkRetrofitProvider;
    private final IVkRetrofitProvider vkRetrofitProvider;
    private final IUploadRetrofitProvider uploadRetrofitProvider;

    public Networker(IProxySettings settings) {
        this.otherVkRetrofitProvider = new OtherVkRetrofitProvider(settings);
        this.vkRetrofitProvider = new VkRetrofitProvider(settings, new VkMethodHttpClientFactory());
        this.uploadRetrofitProvider = new UploadRetrofitProvider(settings);
    }

    @Override
    public IAccountApis vkDefault(int accountId) {
        return VkApies.get(accountId, vkRetrofitProvider);
    }

    @Override
    public IAccountApis vkManual(int accountId, String accessToken) {
        return VkApies.create(accountId, accessToken, vkRetrofitProvider);
    }

    @Override
    public IAuthApi vkDirectAuth() {
        return new AuthApi(() -> otherVkRetrofitProvider.provideAuthRetrofit().map(wrapper -> wrapper.create(IAuthService.class)));
    }

    @Override
    public ILongpollApi longpoll() {
        return new LongpollApi(otherVkRetrofitProvider);
    }

    @Override
    public IUploadApi uploads() {
        return new UploadApi(uploadRetrofitProvider);
    }
}