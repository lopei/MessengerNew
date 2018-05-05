package com.anotap.messenger.db.interfaces;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import java.util.List;

import com.anotap.messenger.db.model.entity.OwnerEntities;
import com.anotap.messenger.db.model.entity.PollEntity;
import com.anotap.messenger.db.model.entity.TopicEntity;
import com.anotap.messenger.model.criteria.TopicsCriteria;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by admin on 13.12.2016.
 * phoenix
 */
public interface ITopicsStore {

    @CheckResult
    Single<List<TopicEntity>> getByCriteria(@NonNull TopicsCriteria criteria);

    @CheckResult
    Completable store(int accountId, int ownerId, List<TopicEntity> topics, OwnerEntities owners, boolean canAddTopic, int defaultOrder, boolean clearBefore);

    @CheckResult
    Completable attachPoll(int accountId, int ownerId, int topicId, PollEntity pollDbo);
}