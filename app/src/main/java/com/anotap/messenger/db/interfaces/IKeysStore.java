package com.anotap.messenger.db.interfaces;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import java.util.List;

import com.anotap.messenger.crypt.AesKeyPair;
import com.anotap.messenger.util.Optional;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by ruslan.kolbasa on 20.10.2016.
 * phoenix
 */
public interface IKeysStore extends IStore {
    @CheckResult
    Completable saveKeyPair(@NonNull AesKeyPair pair);

    @CheckResult
    Single<List<AesKeyPair>> getAll(int accountId);

    @CheckResult
    Single<List<AesKeyPair>> getKeys(int accountId, int peerId);

    @CheckResult
    Single<Optional<AesKeyPair>> findLastKeyPair(int accountId, int peerId);

    @CheckResult
    Maybe<AesKeyPair> findKeyPairFor(int accountId, long sessionId);

    @CheckResult
    Completable deleteAll(int accountId);
}
