package com.anotap.messenger.db.interfaces;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import com.anotap.messenger.db.model.entity.FeedListEntity;
import com.anotap.messenger.db.model.entity.NewsEntity;
import com.anotap.messenger.db.model.entity.OwnerEntities;
import com.anotap.messenger.model.FeedSourceCriteria;
import com.anotap.messenger.model.criteria.FeedCriteria;
import io.reactivex.Completable;
import io.reactivex.Single;

public interface IFeedStore extends IStore {

    Single<List<NewsEntity>> findByCriteria(@NonNull FeedCriteria criteria);

    Single<int[]> store(int accountId, @NonNull List<NewsEntity> data, @Nullable OwnerEntities owners, boolean clearBeforeStore);

    Completable storeLists(int accountid, @NonNull List<FeedListEntity> entities);

    Single<List<FeedListEntity>> getAllLists(@NonNull FeedSourceCriteria criteria);
}