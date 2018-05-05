package com.anotap.messenger.domain.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.anotap.messenger.api.interfaces.INetworker;
import com.anotap.messenger.api.model.IdPair;
import com.anotap.messenger.api.model.VkApiDoc;
import com.anotap.messenger.db.interfaces.IDocsStore;
import com.anotap.messenger.db.model.entity.DocumentEntity;
import com.anotap.messenger.domain.IDocsInteractor;
import com.anotap.messenger.domain.mappers.Dto2Entity;
import com.anotap.messenger.domain.mappers.Dto2Model;
import com.anotap.messenger.domain.mappers.Entity2Model;
import com.anotap.messenger.exception.NotFoundException;
import com.anotap.messenger.fragment.search.criteria.DocumentSearchCriteria;
import com.anotap.messenger.model.Document;
import com.anotap.messenger.model.criteria.DocsCriteria;
import io.reactivex.Completable;
import io.reactivex.Single;

import static com.anotap.messenger.util.Utils.listEmptyIfNull;

/**
 * Created by Ruslan Kolbasa on 17.05.2017.
 * phoenix
 */
public class DocsInteractor implements IDocsInteractor {

    private final INetworker networker;
    private final IDocsStore cache;

    public DocsInteractor(INetworker networker, IDocsStore cache) {
        this.networker = networker;
        this.cache = cache;
    }

    @Override
    public Single<List<Document>> request(int accountId, int ownerId, int filter) {
        return networker.vkDefault(accountId)
                .docs()
                .get(ownerId, null, null, filter)
                .map(items -> listEmptyIfNull(items.getItems()))
                .flatMap(dtos -> {
                    List<Document> documents = new ArrayList<>(dtos.size());
                    List<DocumentEntity> entities = new ArrayList<>(dtos.size());

                    for(VkApiDoc dto : dtos){
                        documents.add(Dto2Model.transform(dto));
                        entities.add(Dto2Entity.buildDocumentEntity(dto));
                    }

                    return cache.store(accountId, ownerId, entities, true)
                            .andThen(Single.just(documents));
                });
    }

    @Override
    public Single<List<Document>> getCacheData(int accountId, int ownerId, int filter) {
        return cache.get(new DocsCriteria(accountId, ownerId).setFilter(filter))
                .map(entities -> {
                    List<Document> documents = new ArrayList<>(entities.size());
                    for(DocumentEntity entity : entities){
                        documents.add(Entity2Model.buildDocumentFromDbo(entity));
                    }
                    return documents;
                });
    }

    @Override
    public Single<Integer> add(int accountId, int docId, int ownerId, String accessKey) {
        return networker.vkDefault(accountId)
                .docs()
                .add(ownerId, docId, accessKey);
    }

    @Override
    public Single<Document> findById(int accountId, int ownerId, int docId) {
        return networker.vkDefault(accountId)
                .docs()
                .getById(Collections.singletonList(new IdPair(docId, ownerId)))
                .map(dtos -> {
                    if(dtos.isEmpty()){
                        throw new NotFoundException();
                    }

                    return Dto2Model.transform(dtos.get(0));
                });
    }

    @Override
    public Single<List<Document>> search(int accountId, DocumentSearchCriteria criteria, int count, int offset) {
        return networker.vkDefault(accountId)
                .docs()
                .search(criteria.getQuery(), count, offset)
                .map(items -> {
                    List<VkApiDoc> dtos = listEmptyIfNull(items.getItems());
                    List<Document> documents = new ArrayList<>();

                    for(VkApiDoc dto : dtos){
                        documents.add(Dto2Model.transform(dto));
                    }

                    return documents;
                });
    }

    @Override
    public Completable delete(int accountId, int docId, int ownerId) {
        return networker.vkDefault(accountId)
                .docs()
                .delete(ownerId, docId)
                .flatMapCompletable(ignored -> cache.delete(accountId, docId, ownerId));
    }
}