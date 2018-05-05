package com.anotap.messenger.domain.impl;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.Constants;
import com.anotap.messenger.api.interfaces.INetworker;
import com.anotap.messenger.api.model.VKApiTopic;
import com.anotap.messenger.db.interfaces.IStores;
import com.anotap.messenger.db.model.entity.OwnerEntities;
import com.anotap.messenger.db.model.entity.TopicEntity;
import com.anotap.messenger.domain.IBoardInteractor;
import com.anotap.messenger.domain.IOwnersInteractor;
import com.anotap.messenger.domain.mappers.Dto2Entity;
import com.anotap.messenger.domain.mappers.Dto2Model;
import com.anotap.messenger.domain.mappers.Entity2Model;
import com.anotap.messenger.model.Owner;
import com.anotap.messenger.model.Topic;
import com.anotap.messenger.model.criteria.TopicsCriteria;
import com.anotap.messenger.util.Utils;
import com.anotap.messenger.util.VKOwnIds;
import io.reactivex.Single;

/**
 * Created by Ruslan Kolbasa on 19.09.2017.
 * phoenix
 */
public class BoardInteractor implements IBoardInteractor {

    private final INetworker networker;
    private final IStores stores;
    private final IOwnersInteractor ownersInteractor;

    public BoardInteractor(INetworker networker, IStores stores) {
        this.networker = networker;
        this.stores = stores;
        this.ownersInteractor = new OwnersInteractor(networker, stores.owners());
    }

    @Override
    public Single<List<Topic>> getCachedTopics(int accountId, int ownerId) {
        TopicsCriteria criteria = new TopicsCriteria(accountId, ownerId);
        return stores.topics()
                .getByCriteria(criteria)
                .flatMap(dbos -> {
                    VKOwnIds ids = new VKOwnIds();
                    for (TopicEntity dbo : dbos) {
                        ids.append(dbo.getCreatorId());
                        ids.append(dbo.getUpdatedBy());
                    }

                    return ownersInteractor.findBaseOwnersDataAsBundle(accountId, ids.getAll(), IOwnersInteractor.MODE_ANY)
                            .map(owners -> {
                                List<Topic> topics = new ArrayList<>(dbos.size());
                                for (TopicEntity dbo : dbos) {
                                    topics.add(Entity2Model.buildTopicFromDbo(dbo, owners));
                                }
                                return topics;
                            });
                });
    }

    //public static final int ORDER_DESCENDING_UPDATE_TIME = 1;
    //public static final int ORDER_DESCENDING_CREATE_TIME = 2;
    //public static final int ORDER_ASCENDING_UPDATE_TIME = -1;
    //public static final int ORDER_ASCENDING_CREATE_TIME = -2;

    @Override
    public Single<List<Topic>> getActualTopics(int accountId, int ownerId, int count, int offset) {
        return networker.vkDefault(accountId)
                .board()
                .getTopics(Math.abs(ownerId), null, null, offset, count, true, null, null, Constants.MAIN_OWNER_FIELDS)
                .flatMap(response -> {
                    List<VKApiTopic> dtos = Utils.listEmptyIfNull(response.items);
                    List<TopicEntity> dbos = new ArrayList<>(dtos.size());

                    for (VKApiTopic dto : dtos) {
                        dbos.add(Dto2Entity.buildTopicDbo(dto));
                    }

                    final OwnerEntities ownerEntities = Dto2Entity.buildOwnerDbos(response.profiles, response.groups);

                    VKOwnIds ownerIds = new VKOwnIds();
                    for (TopicEntity dbo : dbos) {
                        ownerIds.append(dbo.getCreatorId());
                        ownerIds.append(dbo.getUpdatedBy());
                    }

                    final List<Owner> owners = Dto2Model.transformOwners(response.profiles, response.groups);

                    return stores.topics()
                            .store(accountId, ownerId, dbos, ownerEntities, response.canAddTopics == 1, response.defaultOrder, offset == 0)
                            .andThen(ownersInteractor.findBaseOwnersDataAsBundle(accountId, ownerIds.getAll(), IOwnersInteractor.MODE_ANY, owners)
                                    .map(ownersBundle -> {
                                        List<Topic> topics = new ArrayList<>(dbos.size());
                                        for(TopicEntity dbo : dbos){
                                            topics.add(Entity2Model.buildTopicFromDbo(dbo, ownersBundle));
                                        }
                                        return topics;
                                    }));
                });
    }
}