package com.anotap.messenger.domain.impl;

import com.anotap.messenger.api.interfaces.INetworker;
import com.anotap.messenger.db.interfaces.IStickersStore;
import com.anotap.messenger.domain.IStickersInteractor;
import io.reactivex.Completable;

/**
 * Created by admin on 20.03.2017.
 * phoenix
 */
public class StickersInteractor implements IStickersInteractor {

    private final INetworker networker;
    private final IStickersStore repository;

    public StickersInteractor(INetworker networker, IStickersStore repository) {
        this.networker = networker;
        this.repository = repository;
    }

    @Override
    public Completable getAndStore(int accountId) {
        return networker.vkDefault(accountId)
                .store()
                .getProducts(true, "active", "stickers")
                .flatMapCompletable(items -> repository.store(accountId, items.getItems()));
    }
}