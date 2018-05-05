package com.anotap.messenger.domain;

import android.support.annotation.NonNull;

import java.util.List;

import com.anotap.messenger.model.AbsModel;
import com.anotap.messenger.model.Comment;
import com.anotap.messenger.model.CommentIntent;
import com.anotap.messenger.model.Commented;
import com.anotap.messenger.model.CommentsBundle;
import com.anotap.messenger.model.DraftComment;
import com.anotap.messenger.model.Owner;
import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by Ruslan Kolbasa on 07.06.2017.
 * phoenix
 */
public interface ICommentsInteractor {

    Single<List<Comment>> getAllCachedData(int accounrId, @NonNull Commented commented);

    Single<CommentsBundle> getCommentsPortion(int accountId, @NonNull Commented commented, int offset,
                                              int count, Integer startCommentId, boolean invalidateCache, String sort);

    Maybe<DraftComment> restoreDraftComment(int accountId, @NonNull Commented commented);

    Single<Integer> safeDraftComment(int accountId, @NonNull Commented commented, String body, int replyToCommentId, int replyToUserId);

    Completable like(int accountId, Commented commented, int commentId, boolean add);

    Completable deleteRestore(int accountId, Commented commented, int commentId, boolean delete);

    Single<Comment> send(int accountId, Commented commented, CommentIntent intent);

    Single<List<Comment>> getAllCommentsRange(int accountId, Commented commented, int startFromCommentId, int continueToCommentId);

    Single<List<Owner>> getAvailableAuthors(int accountId);

    Single<Comment> edit(int accountId, Commented commented, int commentId, String body, List<AbsModel> attachments);
}