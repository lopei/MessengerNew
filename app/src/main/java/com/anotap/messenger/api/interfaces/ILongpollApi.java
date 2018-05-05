package com.anotap.messenger.api.interfaces;

import com.anotap.messenger.api.model.longpoll.VkApiLongpollUpdates;
import io.reactivex.Single;

/**
 * Created by Ruslan Kolbasa on 31.07.2017.
 * phoenix
 */
public interface ILongpollApi {
    Single<VkApiLongpollUpdates> getUpdates(String server, String key, long ts, int wait, int mode, int version);
}