package com.anotap.messenger.domain.impl;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.api.interfaces.INetworker;
import com.anotap.messenger.api.model.feedback.VkApiBaseFeedback;
import com.anotap.messenger.db.interfaces.IStores;
import com.anotap.messenger.db.model.IdPairEntity;
import com.anotap.messenger.db.model.entity.OwnerEntities;
import com.anotap.messenger.db.model.entity.feedback.CopyEntity;
import com.anotap.messenger.db.model.entity.feedback.FeedbackEntity;
import com.anotap.messenger.db.model.entity.feedback.LikeCommentEntity;
import com.anotap.messenger.db.model.entity.feedback.LikeEntity;
import com.anotap.messenger.db.model.entity.feedback.MentionCommentEntity;
import com.anotap.messenger.db.model.entity.feedback.MentionEntity;
import com.anotap.messenger.db.model.entity.feedback.NewCommentEntity;
import com.anotap.messenger.db.model.entity.feedback.PostFeedbackEntity;
import com.anotap.messenger.db.model.entity.feedback.ReplyCommentEntity;
import com.anotap.messenger.db.model.entity.feedback.UsersEntity;
import com.anotap.messenger.domain.IFeedbackInteractor;
import com.anotap.messenger.domain.IOwnersInteractor;
import com.anotap.messenger.domain.mappers.Dto2Entity;
import com.anotap.messenger.domain.mappers.Dto2Model;
import com.anotap.messenger.domain.mappers.Entity2Model;
import com.anotap.messenger.domain.mappers.FeedbackEntity2Model;
import com.anotap.messenger.model.Owner;
import com.anotap.messenger.model.criteria.NotificationsCriteria;
import com.anotap.messenger.model.feedback.Feedback;
import com.anotap.messenger.util.Pair;
import com.anotap.messenger.util.Utils;
import com.anotap.messenger.util.VKOwnIds;
import io.reactivex.Completable;
import io.reactivex.Single;

import static com.anotap.messenger.domain.mappers.Entity2Model.fillCommentOwnerIds;
import static com.anotap.messenger.util.Utils.isEmpty;

/**
 * Created by Ruslan Kolbasa on 11.09.2017.
 * phoenix
 */
public class FeedbackInteractor implements IFeedbackInteractor {

    private final IStores cache;
    private final INetworker networker;
    private final IOwnersInteractor ownersInteractor;

    public FeedbackInteractor(IStores cache, INetworker networker) {
        this.cache = cache;
        this.networker = networker;
        this.ownersInteractor = new OwnersInteractor(networker, cache.owners());
    }

    @Override
    public Single<List<Feedback>> getCachedFeedbacks(int accountId) {
        final NotificationsCriteria criteria = new NotificationsCriteria(accountId);
        return getCachedFeedbacksByCriteria(criteria);
    }

    @Override
    public Single<Pair<List<Feedback>, String>> getActualFeedbacks(int accountId, int count, String startFrom) {
        return networker.vkDefault(accountId)
                .notifications()
                .get(count, startFrom, null, null, null)
                .flatMap(response -> {
                    final List<VkApiBaseFeedback> dtos = Utils.listEmptyIfNull(response.notifications);
                    final List<FeedbackEntity> dbos = new ArrayList<>(dtos.size());

                    final VKOwnIds ownIds = new VKOwnIds();

                    for (VkApiBaseFeedback dto : dtos) {
                        FeedbackEntity dbo = Dto2Entity.buildFeedbackDbo(dto);
                        populateOwnerIds(ownIds, dbo);
                        dbos.add(dbo);
                    }

                    final OwnerEntities ownerEntities = Dto2Entity.buildOwnerDbos(response.profiles, response.groups);
                    final List<Owner> owners = Dto2Model.transformOwners(response.profiles, response.groups);

                    return cache.notifications()
                            .insert(accountId, dbos, ownerEntities, isEmpty(startFrom))
                            .flatMap(ints -> ownersInteractor
                                    .findBaseOwnersDataAsBundle(accountId, ownIds.getAll(), IOwnersInteractor.MODE_ANY, owners)
                                    .map(ownersBundle -> {
                                        final List<Feedback> feedbacks = new ArrayList<>(dbos.size());

                                        for (FeedbackEntity dbo : dbos) {
                                            feedbacks.add(FeedbackEntity2Model.buildFeedback(dbo, ownersBundle));
                                        }

                                        return Pair.create(feedbacks, response.nextFrom);
                                    }));
                });
    }

    @Override
    public Completable maskAaViewed(int accountId) {
        return networker.vkDefault(accountId)
                .notifications()
                .markAsViewed()
                .toCompletable();
    }

    private Single<List<Feedback>> getCachedFeedbacksByCriteria(NotificationsCriteria criteria) {
        return cache.notifications()
                .findByCriteria(criteria)
                .flatMap(dbos -> {
                    VKOwnIds ownIds = new VKOwnIds();

                    for (FeedbackEntity dbo : dbos) {
                        populateOwnerIds(ownIds, dbo);
                    }

                    return ownersInteractor.findBaseOwnersDataAsBundle(criteria.getAccountId(), ownIds.getAll(), IOwnersInteractor.MODE_ANY)
                            .map(owners -> {
                                List<Feedback> feedbacks = new ArrayList<>(dbos.size());
                                for (FeedbackEntity dbo : dbos) {
                                    feedbacks.add(FeedbackEntity2Model.buildFeedback(dbo, owners));
                                }
                                return feedbacks;
                            });
                });
    }

    private static void populateOwnerIds(VKOwnIds ids, FeedbackEntity dbo) {
        fillCommentOwnerIds(ids, dbo.getReply());

        if (dbo instanceof CopyEntity) {
            populateOwnerIds(ids, (CopyEntity) dbo);
        } else if (dbo instanceof LikeCommentEntity) {
            populateOwnerIds(ids, (LikeCommentEntity) dbo);
        } else if (dbo instanceof LikeEntity) {
            populateOwnerIds(ids, (LikeEntity) dbo);
        } else if (dbo instanceof MentionCommentEntity) {
            populateOwnerIds(ids, (MentionCommentEntity) dbo);
        } else if (dbo instanceof MentionEntity) {
            populateOwnerIds(ids, (MentionEntity) dbo);
        } else if (dbo instanceof NewCommentEntity) {
            populateOwnerIds(ids, (NewCommentEntity) dbo);
        } else if (dbo instanceof PostFeedbackEntity) {
            populateOwnerIds(ids, (PostFeedbackEntity) dbo);
        } else if (dbo instanceof ReplyCommentEntity) {
            populateOwnerIds(ids, (ReplyCommentEntity) dbo);
        } else if (dbo instanceof UsersEntity) {
            populateOwnerIds(ids, (UsersEntity) dbo);
        }
    }

    private static void populateOwnerIds(VKOwnIds ids, UsersEntity dbo) {
        ids.appendAll(dbo.getOwners());
    }

    private static void populateOwnerIds(VKOwnIds ids, ReplyCommentEntity dbo) {
        Entity2Model.fillOwnerIds(ids, dbo.getCommented());
        Entity2Model.fillOwnerIds(ids, dbo.getFeedbackComment());
        Entity2Model.fillOwnerIds(ids, dbo.getOwnComment());
    }

    private static void populateOwnerIds(VKOwnIds ids, PostFeedbackEntity dbo) {
        Entity2Model.fillOwnerIds(ids, dbo.getPost());
    }

    private static void populateOwnerIds(VKOwnIds ids, NewCommentEntity dbo) {
        Entity2Model.fillOwnerIds(ids, dbo.getComment());
        Entity2Model.fillOwnerIds(ids, dbo.getCommented());
    }

    private static void populateOwnerIds(VKOwnIds ids, MentionEntity dbo) {
        Entity2Model.fillOwnerIds(ids, dbo.getWhere());
    }

    private static void populateOwnerIds(VKOwnIds ids, MentionCommentEntity dbo) {
        Entity2Model.fillOwnerIds(ids, dbo.getCommented());
        Entity2Model.fillOwnerIds(ids, dbo.getWhere());
    }

    private static void populateOwnerIds(VKOwnIds ids, LikeEntity dbo) {
        Entity2Model.fillOwnerIds(ids, dbo.getLiked());
        ids.appendAll(dbo.getLikesOwnerIds());
    }

    private static void populateOwnerIds(VKOwnIds ids, LikeCommentEntity dbo) {
        Entity2Model.fillOwnerIds(ids, dbo.getLiked());
        Entity2Model.fillOwnerIds(ids, dbo.getCommented());
        ids.appendAll(dbo.getLikesOwnerIds());
    }

    private static void populateOwnerIds(VKOwnIds ids, CopyEntity dbo) {
        for (IdPairEntity i : dbo.getCopies().getPairDbos()) {
            ids.append(i.getOwnerId());
        }

        Entity2Model.fillOwnerIds(ids, dbo.getCopied());
    }
}