package com.anotap.messenger.api.interfaces;

import java.util.Map;

import com.anotap.messenger.util.Optional;
import io.reactivex.Single;

/**
 * Created by Ruslan Kolbasa on 31.07.2017.
 * phoenix
 */
public interface IOtherApi {
    Single<Optional<String>> rawRequest(String method, Map<String, String> postParams);
}