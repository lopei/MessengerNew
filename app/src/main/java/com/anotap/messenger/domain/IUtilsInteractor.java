package com.anotap.messenger.domain;

import android.support.annotation.NonNull;

import java.util.Map;

import com.anotap.messenger.model.Owner;
import com.anotap.messenger.model.Privacy;
import com.anotap.messenger.model.SimplePrivacy;
import com.anotap.messenger.util.Optional;
import io.reactivex.Single;

/**
 * Created by Ruslan Kolbasa on 18.09.2017.
 * phoenix
 */
public interface IUtilsInteractor {
    Single<Map<Integer, Privacy>> createFullPrivacies(int accountId, @NonNull Map<Integer, SimplePrivacy> orig);
    Single<Optional<Owner>> resolveDomain(final int accountId, String domain);
}