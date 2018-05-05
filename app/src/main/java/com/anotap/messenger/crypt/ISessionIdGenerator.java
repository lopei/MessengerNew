package com.anotap.messenger.crypt;

import io.reactivex.Single;

/**
 * Created by Ruslan Kolbasa on 12.07.2017.
 * phoenix
 */
public interface ISessionIdGenerator {
    Single<Long> generateNextId();
}