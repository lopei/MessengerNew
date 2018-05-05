package com.anotap.messenger.db.interfaces;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import java.util.List;

import com.anotap.messenger.db.model.entity.CommentEntity;
import com.anotap.messenger.db.model.entity.OwnerEntities;
import com.anotap.messenger.model.CommentUpdate;
import com.anotap.messenger.model.Commented;
import com.anotap.messenger.model.DraftComment;
import com.anotap.messenger.model.criteria.CommentsCriteria;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by ruslan.kolbasa on 28.11.2016.
 * phoenix
 */
public interface ICommentsStore extends IStore {

    Single<int[]> insert(int accountId, int sourceId, int sourceOwnerId, int sourceType, List<CommentEntity> dbos, OwnerEntities owners, boolean clearBefore);

    Single<List<CommentEntity>> getDbosByCriteria(@NonNull CommentsCriteria criteria);

    @CheckResult
    Maybe<DraftComment> findEditingComment(int accountId, @NonNull Commented commented);

    @CheckResult
    Single<Integer> saveDraftComment(int accountId, Commented commented, String text, int replyToUser, int replyToComment);

    Completable commitMinorUpdate(CommentUpdate update);

    Observable<CommentUpdate> observeMinorUpdates();

    Completable deleteByDbid(int accountId, Integer dbid);
}
