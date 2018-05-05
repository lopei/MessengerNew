package com.anotap.messenger.domain.impl;

import java.util.List;

import com.anotap.messenger.api.interfaces.INetworker;
import com.anotap.messenger.api.model.VKApiCommunity;
import com.anotap.messenger.db.column.GroupColumns;
import com.anotap.messenger.db.interfaces.IStores;
import com.anotap.messenger.db.model.entity.CommunityEntity;
import com.anotap.messenger.domain.ICommunitiesInteractor;
import com.anotap.messenger.domain.mappers.Dto2Entity;
import com.anotap.messenger.domain.mappers.Dto2Model;
import com.anotap.messenger.domain.mappers.Entity2Model;
import com.anotap.messenger.model.Community;
import com.anotap.messenger.util.Utils;
import io.reactivex.Completable;
import io.reactivex.Single;

import static com.anotap.messenger.util.Utils.listEmptyIfNull;

/**
 * Created by admin on 19.09.2017.
 * phoenix
 */
public class CommunitiesInteractor implements ICommunitiesInteractor {

    private final INetworker networker;
    private final IStores stores;

    public CommunitiesInteractor(INetworker networker, IStores repositories) {
        this.networker = networker;
        this.stores = repositories;
    }

    @Override
    public Single<List<Community>> getCachedData(int accountId, int userId) {
        return stores.relativeship()
                .getCommunities(accountId, userId)
                .map(Entity2Model::buildCommunitiesFromDbos);
    }

    @Override
    public Single<List<Community>> getActual(int accountId, int userId, int count, int offset) {
        return networker.vkDefault(accountId)
                .groups()
                .get(userId, true, null, GroupColumns.API_FIELDS, offset, count)
                .flatMap(items -> {
                    List<VKApiCommunity> dtos = listEmptyIfNull(items.getItems());
                    List<CommunityEntity> dbos = Dto2Entity.buildCommunityDbos(dtos);

                    return stores.relativeship()
                            .storeComminities(accountId, dbos, userId, offset == 0)
                            .andThen(Single.just(Entity2Model.buildCommunitiesFromDbos(dbos)));
                });
    }

    @Override
    public Single<List<Community>> search(int accountId, String q, String type, Integer countryId, Integer cityId, Boolean futureOnly, Integer sort, int count, int offset) {
        return networker.vkDefault(accountId)
                .groups()
                .search(q, type, countryId, cityId, futureOnly, null, sort, offset, count)
                .map(items -> {
                    List<VKApiCommunity> dtos = Utils.listEmptyIfNull(items.getItems());
                    return Dto2Model.transformCommunities(dtos);
                });
    }

    @Override
    public Completable join(int accountId, int groupId) {
        return networker.vkDefault(accountId)
                .groups()
                .join(groupId, null)
                .toCompletable();
    }

    @Override
    public Completable leave(int accountId, int groupId) {
        return networker.vkDefault(accountId)
                .groups()
                .leave(groupId)
                .toCompletable();
    }
}