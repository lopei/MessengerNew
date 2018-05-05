package com.anotap.messenger.domain.mappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.anotap.messenger.api.model.Commentable;
import com.anotap.messenger.api.model.Likeable;
import com.anotap.messenger.api.model.PhotoSizeDto;
import com.anotap.messenger.api.model.VKApiAttachment;
import com.anotap.messenger.api.model.VKApiAudio;
import com.anotap.messenger.api.model.VKApiCareer;
import com.anotap.messenger.api.model.VKApiCity;
import com.anotap.messenger.api.model.VKApiComment;
import com.anotap.messenger.api.model.VKApiCommunity;
import com.anotap.messenger.api.model.VKApiCountry;
import com.anotap.messenger.api.model.VKApiLink;
import com.anotap.messenger.api.model.VKApiMessage;
import com.anotap.messenger.api.model.VKApiMilitary;
import com.anotap.messenger.api.model.VKApiNews;
import com.anotap.messenger.api.model.VKApiPhoto;
import com.anotap.messenger.api.model.VKApiPhotoAlbum;
import com.anotap.messenger.api.model.VKApiPoll;
import com.anotap.messenger.api.model.VKApiPost;
import com.anotap.messenger.api.model.VKApiSchool;
import com.anotap.messenger.api.model.VKApiSticker;
import com.anotap.messenger.api.model.VKApiTopic;
import com.anotap.messenger.api.model.VKApiUniversity;
import com.anotap.messenger.api.model.VKApiUser;
import com.anotap.messenger.api.model.VKApiVideo;
import com.anotap.messenger.api.model.VKApiVideoAlbum;
import com.anotap.messenger.api.model.VKApiWikiPage;
import com.anotap.messenger.api.model.VkApiAttachments;
import com.anotap.messenger.api.model.VkApiDialog;
import com.anotap.messenger.api.model.VkApiDoc;
import com.anotap.messenger.api.model.VkApiPostSource;
import com.anotap.messenger.api.model.VkApiPrivacy;
import com.anotap.messenger.api.model.feedback.Copies;
import com.anotap.messenger.api.model.feedback.VkApiBaseFeedback;
import com.anotap.messenger.api.model.feedback.VkApiCommentFeedback;
import com.anotap.messenger.api.model.feedback.VkApiCopyFeedback;
import com.anotap.messenger.api.model.feedback.VkApiLikeCommentFeedback;
import com.anotap.messenger.api.model.feedback.VkApiLikeFeedback;
import com.anotap.messenger.api.model.feedback.VkApiMentionCommentFeedback;
import com.anotap.messenger.api.model.feedback.VkApiMentionWallFeedback;
import com.anotap.messenger.api.model.feedback.VkApiReplyCommentFeedback;
import com.anotap.messenger.api.model.feedback.VkApiUsersFeedback;
import com.anotap.messenger.api.model.feedback.VkApiWallFeedback;
import com.anotap.messenger.crypt.CryptHelper;
import com.anotap.messenger.crypt.MessageType;
import com.anotap.messenger.db.model.IdPairEntity;
import com.anotap.messenger.db.model.entity.AudioEntity;
import com.anotap.messenger.db.model.entity.CareerEntity;
import com.anotap.messenger.db.model.entity.CityEntity;
import com.anotap.messenger.db.model.entity.CommentEntity;
import com.anotap.messenger.db.model.entity.CommunityEntity;
import com.anotap.messenger.db.model.entity.CopiesEntity;
import com.anotap.messenger.db.model.entity.CountryEntity;
import com.anotap.messenger.db.model.entity.DialogEntity;
import com.anotap.messenger.db.model.entity.DocumentEntity;
import com.anotap.messenger.db.model.entity.Entity;
import com.anotap.messenger.db.model.entity.LinkEntity;
import com.anotap.messenger.db.model.entity.MessageEntity;
import com.anotap.messenger.db.model.entity.MilitaryEntity;
import com.anotap.messenger.db.model.entity.NewsEntity;
import com.anotap.messenger.db.model.entity.OwnerEntities;
import com.anotap.messenger.db.model.entity.PageEntity;
import com.anotap.messenger.db.model.entity.PhotoAlbumEntity;
import com.anotap.messenger.db.model.entity.PhotoEntity;
import com.anotap.messenger.db.model.entity.PhotoSizeEntity;
import com.anotap.messenger.db.model.entity.PollEntity;
import com.anotap.messenger.db.model.entity.PostEntity;
import com.anotap.messenger.db.model.entity.PrivacyEntity;
import com.anotap.messenger.db.model.entity.SchoolEntity;
import com.anotap.messenger.db.model.entity.StickerEntity;
import com.anotap.messenger.db.model.entity.TopicEntity;
import com.anotap.messenger.db.model.entity.UniversityEntity;
import com.anotap.messenger.db.model.entity.UserDetailsEntity;
import com.anotap.messenger.db.model.entity.UserEntity;
import com.anotap.messenger.db.model.entity.VideoAlbumEntity;
import com.anotap.messenger.db.model.entity.VideoEntity;
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
import com.anotap.messenger.model.CommentedType;
import com.anotap.messenger.model.Message;
import com.anotap.messenger.model.MessageStatus;
import com.anotap.messenger.model.feedback.FeedbackType;
import com.anotap.messenger.util.Utils;

import static com.anotap.messenger.domain.mappers.MapUtil.mapAll;
import static com.anotap.messenger.util.Objects.isNull;
import static com.anotap.messenger.util.Objects.nonNull;
import static com.anotap.messenger.util.Utils.listEmptyIfNull;
import static com.anotap.messenger.util.Utils.nonEmpty;
import static com.anotap.messenger.util.Utils.safeCountOf;

/**
 * Created by Ruslan Kolbasa on 04.09.2017.
 * phoenix
 */
public class Dto2Entity {

    public static FeedbackEntity buildFeedbackDbo(VkApiBaseFeedback feedback) {
        @FeedbackType
        int type = FeedbackEntity2Model.transformType(feedback.type);

        switch (type) {
            case FeedbackType.FOLLOW:
            case FeedbackType.FRIEND_ACCEPTED:
                VkApiUsersFeedback usersNotifcation = (VkApiUsersFeedback) feedback;

                final UsersEntity usersDbo = new UsersEntity(type);
                usersDbo.setOwners(usersNotifcation.users.ids);
                usersDbo.setDate(feedback.date);
                return usersDbo;

            case FeedbackType.MENTION:
                VkApiMentionWallFeedback mentionWallFeedback = (VkApiMentionWallFeedback) feedback;

                final MentionEntity mentionDbo = new MentionEntity(type);
                PostEntity post = buildPostEntity(mentionWallFeedback.post);
                mentionDbo.setWhere(post);

                if (nonNull(feedback.reply)) {
                    mentionDbo.setReply(buildCommentDbo(post.getId(), post.getOwnerId(), CommentedType.POST, null, feedback.reply));
                }

                mentionDbo.setDate(feedback.date);
                return mentionDbo;

            case FeedbackType.MENTION_COMMENT_POST:
            case FeedbackType.MENTION_COMMENT_PHOTO:
            case FeedbackType.MENTION_COMMENT_VIDEO:
                VkApiMentionCommentFeedback mentionCommentFeedback = (VkApiMentionCommentFeedback) feedback;
                CEntity entity = createFromCommentable(mentionCommentFeedback.comment_of);

                final MentionCommentEntity mentionCommentDbo = new MentionCommentEntity(type);
                mentionCommentDbo.setDate(feedback.date);
                mentionCommentDbo.setCommented(entity.entity);
                mentionCommentDbo.setWhere(buildCommentDbo(entity.id, entity.ownerId, entity.type, entity.accessKey, mentionCommentFeedback.where));

                if (nonNull(feedback.reply)) {
                    mentionCommentDbo.setReply(buildCommentDbo(entity.id, entity.ownerId, entity.type, entity.accessKey, feedback.reply));
                }

                return mentionCommentDbo;

            case FeedbackType.WALL:
            case FeedbackType.WALL_PUBLISH:
                VkApiWallFeedback wallFeedback = (VkApiWallFeedback) feedback;
                PostEntity postEntity = buildPostEntity(wallFeedback.post);

                final PostFeedbackEntity postFeedbackEntity = new PostFeedbackEntity(type);
                postFeedbackEntity.setDate(feedback.date);
                postFeedbackEntity.setPost(postEntity);

                if (nonNull(feedback.reply)) {
                    postFeedbackEntity.setReply(buildCommentDbo(postEntity.getId(), postEntity.getOwnerId(), CommentedType.POST, null, feedback.reply));
                }

                return postFeedbackEntity;

            case FeedbackType.COMMENT_POST:
            case FeedbackType.COMMENT_PHOTO:
            case FeedbackType.COMMENT_VIDEO:
                VkApiCommentFeedback commentFeedback = (VkApiCommentFeedback) feedback;
                CEntity commented = createFromCommentable(commentFeedback.comment_of);

                final NewCommentEntity commentEntity = new NewCommentEntity(type);
                commentEntity.setComment(buildCommentDbo(commented.id, commented.ownerId, commented.type, commented.accessKey, commentFeedback.comment));
                commentEntity.setCommented(commented.entity);
                commentEntity.setDate(feedback.date);

                if (nonNull(feedback.reply)) {
                    commentEntity.setReply(buildCommentDbo(commented.id, commented.ownerId, commented.type, commented.accessKey, feedback.reply));
                }

                return commentEntity;

            case FeedbackType.REPLY_COMMENT:
            case FeedbackType.REPLY_COMMENT_PHOTO:
            case FeedbackType.REPLY_COMMENT_VIDEO:
            case FeedbackType.REPLY_TOPIC:
                VkApiReplyCommentFeedback replyCommentFeedback = (VkApiReplyCommentFeedback) feedback;
                CEntity c = createFromCommentable(replyCommentFeedback.comments_of);

                final ReplyCommentEntity replyCommentEntity = new ReplyCommentEntity(type);
                replyCommentEntity.setDate(feedback.date);
                replyCommentEntity.setCommented(c.entity);
                replyCommentEntity.setFeedbackComment(buildCommentDbo(c.id, c.ownerId, c.type, c.accessKey, replyCommentFeedback.feedback_comment));

                if (nonNull(replyCommentFeedback.own_comment)) {
                    replyCommentEntity.setOwnComment(buildCommentDbo(c.id, c.ownerId, c.type, c.accessKey, replyCommentFeedback.own_comment));
                }

                if (nonNull(feedback.reply)) {
                    replyCommentEntity.setReply(buildCommentDbo(c.id, c.ownerId, c.type, c.accessKey, feedback.reply));
                }

                return replyCommentEntity;

            case FeedbackType.LIKE_POST:
            case FeedbackType.LIKE_PHOTO:
            case FeedbackType.LIKE_VIDEO:
                VkApiLikeFeedback likeFeedback = (VkApiLikeFeedback) feedback;

                final LikeEntity likeEntity = new LikeEntity(type);
                likeEntity.setLiked(createFromLikeable(likeFeedback.liked));
                likeEntity.setLikesOwnerIds(likeFeedback.users.ids);
                likeEntity.setDate(feedback.date);
                return likeEntity;

            case FeedbackType.LIKE_COMMENT_POST:
            case FeedbackType.LIKE_COMMENT_PHOTO:
            case FeedbackType.LIKE_COMMENT_VIDEO:
            case FeedbackType.LIKE_COMMENT_TOPIC:
                VkApiLikeCommentFeedback likeCommentFeedback = (VkApiLikeCommentFeedback) feedback;
                CEntity ce = createFromCommentable(likeCommentFeedback.commented);

                final LikeCommentEntity likeCommentEntity = new LikeCommentEntity(type);
                likeCommentEntity.setCommented(ce.entity);
                likeCommentEntity.setLiked(buildCommentDbo(ce.id, ce.ownerId, ce.type, ce.accessKey, likeCommentFeedback.comment));
                likeCommentEntity.setDate(feedback.date);
                likeCommentEntity.setLikesOwnerIds(likeCommentFeedback.users.ids);
                return likeCommentEntity;

            case FeedbackType.COPY_POST:
            case FeedbackType.COPY_PHOTO:
            case FeedbackType.COPY_VIDEO:
                VkApiCopyFeedback copyFeedback = (VkApiCopyFeedback) feedback;

                final CopyEntity copyEntity = new CopyEntity(type);
                copyEntity.setDate(feedback.date);

                if (type == FeedbackType.COPY_POST) {
                    copyEntity.setCopied(buildPostEntity((VKApiPost) copyFeedback.what));
                } else if (type == FeedbackType.COPY_PHOTO) {
                    copyEntity.setCopied(buildPhotoEntity((VKApiPhoto) copyFeedback.what));
                } else {
                    copyEntity.setCopied(buildVideoEntity((VKApiVideo) copyFeedback.what));
                }

                List<Copies.IdPair> copyPairs = listEmptyIfNull(copyFeedback.copies.pairs);

                CopiesEntity copiesEntity = new CopiesEntity();
                copiesEntity.setPairDbos(new ArrayList<>(copyPairs.size()));

                for(Copies.IdPair idPair : copyPairs){
                    copiesEntity.getPairDbos().add(new IdPairEntity(idPair.id, idPair.owner_id));
                }

                copyEntity.setCopies(copiesEntity);
                return copyEntity;
        }

        throw new UnsupportedOperationException("Unsupported feedback type: " + feedback.type);
    }

    private static final class CEntity {

        final int id;
        final int ownerId;
        final int type;
        final String accessKey;
        final Entity entity;

        private CEntity(int id, int ownerId, int type, String accessKey, Entity entity) {
            this.id = id;
            this.ownerId = ownerId;
            this.type = type;
            this.accessKey = accessKey;
            this.entity = entity;
        }
    }

    private static Entity createFromLikeable(Likeable likeable) {
        if (likeable instanceof VKApiPost) {
            return buildPostEntity((VKApiPost) likeable);
        }

        if (likeable instanceof VKApiPhoto) {
            return buildPhotoEntity((VKApiPhoto) likeable);
        }

        if (likeable instanceof VKApiVideo) {
            return buildVideoEntity((VKApiVideo) likeable);
        }

        throw new UnsupportedOperationException("Unsupported commentable type: " + likeable);
    }

    private static CEntity createFromCommentable(Commentable commentable) {
        if (commentable instanceof VKApiPost) {
            PostEntity entity = buildPostEntity((VKApiPost) commentable);
            return new CEntity(entity.getId(), entity.getOwnerId(), CommentedType.POST, null, entity);
        }

        if (commentable instanceof VKApiPhoto) {
            PhotoEntity entity = buildPhotoEntity((VKApiPhoto) commentable);
            return new CEntity(entity.getId(), entity.getOwnerId(), CommentedType.PHOTO, entity.getAccessKey(), entity);
        }

        if (commentable instanceof VKApiVideo) {
            VideoEntity entity = buildVideoEntity((VKApiVideo) commentable);
            return new CEntity(entity.getId(), entity.getOwnerId(), CommentedType.VIDEO, entity.getAccessKey(), entity);
        }

        if (commentable instanceof VKApiTopic) {
            TopicEntity entity = buildTopicDbo((VKApiTopic) commentable);
            return new CEntity(entity.getId(), entity.getOwnerId(), CommentedType.TOPIC, null, entity);
        }

        throw new UnsupportedOperationException("Unsupported commentable type: " + commentable);
    }

    public static VideoAlbumEntity buildVideoAlbumDbo(VKApiVideoAlbum dto) {
        return new VideoAlbumEntity(dto.id, dto.owner_id)
                .setUpdateTime(dto.updated_time)
                .setCount(dto.count)
                .setPhoto160(dto.photo_160)
                .setPhoto320(dto.photo_320)
                .setTitle(dto.title)
                .setPrivacy(nonNull(dto.privacy) ? buildPrivacyDbo(dto.privacy) : null);
    }

    public static TopicEntity buildTopicDbo(VKApiTopic dto) {
        return new TopicEntity(dto.id, dto.owner_id)
                .setTitle(dto.title)
                .setCreatedTime(dto.created)
                .setCreatorId(dto.created_by)
                .setLastUpdateTime(dto.updated)
                .setUpdatedBy(dto.updated_by)
                .setClosed(dto.is_closed)
                .setFixed(dto.is_fixed)
                .setCommentsCount(nonNull(dto.comments) ? dto.comments.count : 0)
                .setFirstComment(dto.first_comment)
                .setLastComment(dto.last_comment)
                .setPoll(null);
    }

    public static PhotoAlbumEntity buildPhotoAlbumDbo(VKApiPhotoAlbum dto) {
        return new PhotoAlbumEntity(dto.id, dto.owner_id)
                .setTitle(dto.title)
                .setSize(dto.size)
                .setDescription(dto.description)
                .setCanUpload(dto.can_upload)
                .setUpdatedTime(dto.updated)
                .setCreatedTime(dto.created)
                .setSizes(nonNull(dto.photo) ? buildPhotoSizeDbo(dto.photo) : null)
                .setCommentsDisabled(dto.comments_disabled)
                .setUploadByAdminsOnly(dto.upload_by_admins_only)
                .setPrivacyView(nonNull(dto.privacy_view) ? buildPrivacyDbo(dto.privacy_view) : null)
                .setPrivacyComment(nonNull(dto.privacy_comment) ? buildPrivacyDbo(dto.privacy_comment) : null);
    }

    public static OwnerEntities buildOwnerDbos(List<VKApiUser> users, List<VKApiCommunity> communities) {
        return new OwnerEntities(buildUserDbos(users), buildCommunityDbos(communities));
    }

    public static List<CommunityEntity> buildCommunityDbos(List<VKApiCommunity> communities) {
        return mapAll(communities, Dto2Entity::buildCommunityDbo);
    }

    public static List<UserEntity> buildUserDbos(List<VKApiUser> users) {
        return mapAll(users, Dto2Entity::buildUserDbo, true);
    }

    public static CommunityEntity buildCommunityDbo(VKApiCommunity community) {
        return new CommunityEntity(community.id)
                .setName(community.name)
                .setScreenName(community.screen_name)
                .setClosed(community.is_closed)
                .setAdmin(community.is_admin)
                .setAdminLevel(community.admin_level)
                .setMember(community.is_member)
                .setMemberStatus(community.member_status)
                .setType(community.type)
                .setPhoto50(community.photo_50)
                .setPhoto100(community.photo_100)
                .setPhoto200(community.photo_200);
    }

    public static UserEntity buildUserDbo(VKApiUser user) {
        return new UserEntity(user.id)
                .setFirstName(user.first_name)
                .setLastName(user.last_name)
                .setOnline(user.online)
                .setOnlineMobile(user.online_mobile)
                .setOnlineApp(user.online_app)
                .setPhoto50(user.photo_50)
                .setPhoto100(user.photo_100)
                .setPhoto200(user.photo_200)
                .setLastSeen(user.last_seen)
                .setPlatform(user.platform)
                .setStatus(user.status)
                .setSex(user.sex)
                .setDomain(user.domain)
                .setFriend(user.is_friend)
                .setFriendStatus(user.friend_status);
    }

    public static UserDetailsEntity buildUserDetailsDbo(VKApiUser user) {
        UserDetailsEntity dbo = new UserDetailsEntity();

        try {
            if (nonEmpty(user.photo_id)) {
                int dividerIndex = user.photo_id.indexOf("_");
                if (dividerIndex != -1) {
                    int photoId = Integer.parseInt(user.photo_id.substring(dividerIndex + 1));
                    dbo.setPhotoId(new IdPairEntity(photoId, user.id));
                }
            }
        } catch (Exception ignored) {

        }

        dbo.setStatusAudio(nonNull(user.status_audio) ? buildAudioEntity(user.status_audio) : null);
        dbo.setBdate(user.bdate);
        dbo.setCity(isNull(user.city) ? null : dto2entity(user.city));
        dbo.setCountry(isNull(user.country) ? null : dto2entity(user.country));
        dbo.setHomeTown(user.home_town);
        dbo.setPhone(user.mobile_phone);
        dbo.setHomePhone(user.home_phone);
        dbo.setSkype(user.skype);
        dbo.setInstagram(user.instagram);
        dbo.setFacebook(user.facebook);
        dbo.setTwitter(user.twitter);

        VKApiUser.Counters counters = user.counters;

        if (nonNull(counters)) {
            dbo.setFriendsCount(counters.friends)
                    .setOnlineFriendsCount(counters.online_friends)
                    .setMutualFriendsCount(counters.mutual_friends)
                    .setFollowersCount(counters.followers)
                    .setGroupsCount(counters.groups)
                    .setPhotosCount(counters.photos)
                    .setAudiosCount(counters.audios)
                    .setVideosCount(counters.videos)
                    .setAllWallCount(counters.all_wall)
                    .setOwnWallCount(counters.owner_wall)
                    .setPostponedWallCount(counters.postponed_wall);
        }

        dbo.setMilitaries(mapAll(user.militaries, Dto2Entity::dto2entity));
        dbo.setCareers(mapAll(user.careers, Dto2Entity::dto2entity));
        dbo.setUniversities(mapAll(user.universities, Dto2Entity::dto2entity));
        dbo.setSchools(mapAll(user.schools, Dto2Entity::dto2entity));
        dbo.setRelatives(mapAll(user.relatives, Dto2Entity::dto2entity));

        dbo.setRelation(user.relation);
        dbo.setRelationPartnerId(user.relation_partner == null ? 0 : user.relation_partner.id);
        dbo.setLanguages(user.langs);

        dbo.setPolitical(user.political);
        dbo.setPeopleMain(user.people_main);
        dbo.setLifeMain(user.life_main);
        dbo.setSmoking(user.smoking);
        dbo.setAlcohol(user.alcohol);
        dbo.setInspiredBy(user.inspired_by);
        dbo.setReligion(user.religion);
        dbo.setSite(user.site);
        dbo.setInterests(user.interests);
        dbo.setMusic(user.music);
        dbo.setActivities(user.activities);
        dbo.setMovies(user.movies);
        dbo.setTv(user.tv);
        dbo.setGames(user.games);
        dbo.setQuotes(user.quotes);
        dbo.setAbout(user.about);
        dbo.setBooks(user.books);
        return dbo;
    }

    public static UserDetailsEntity.RelativeEntity dto2entity(VKApiUser.Relative relative) {
        return new UserDetailsEntity.RelativeEntity()
                .setId(relative.id)
                .setType(relative.type)
                .setName(relative.name);
    }

    public static SchoolEntity dto2entity(VKApiSchool dto) {
        return new SchoolEntity()
                .setCityId(dto.city_id)
                .setClazz(dto.clazz)
                .setCountryId(dto.country_id)
                .setFrom(dto.year_from)
                .setTo(dto.year_to)
                .setYearGraduated(dto.year_graduated)
                .setId(dto.id)
                .setName(dto.name);
    }

    public static UniversityEntity dto2entity(VKApiUniversity dto) {
        return new UniversityEntity()
                .setId(dto.id)
                .setCityId(dto.city_id)
                .setCountryId(dto.country_id)
                .setName(dto.name)
                .setStatus(dto.education_status)
                .setForm(dto.education_form)
                .setFacultyId(dto.faculty)
                .setFacultyName(dto.faculty_name)
                .setChairId(dto.chair)
                .setChairName(dto.chair_name)
                .setGraduationYear(dto.graduation);
    }

    public static MilitaryEntity dto2entity(VKApiMilitary dto) {
        return new MilitaryEntity()
                .setCountryId(dto.country_id)
                .setFrom(dto.from)
                .setUnit(dto.unit)
                .setUnitId(dto.unit_id)
                .setUntil(dto.until);
    }

    public static CareerEntity dto2entity(VKApiCareer dto) {
        return new CareerEntity()
                .setCityId(dto.city_id)
                .setCompany(dto.company)
                .setCountryId(dto.country_id)
                .setFrom(dto.from)
                .setUntil(dto.until)
                .setPosition(dto.position)
                .setGroupId(dto.group_id);
    }

    public static CountryEntity dto2entity(VKApiCountry dto) {
        return new CountryEntity(dto.id, dto.title);
    }

    public static CityEntity dto2entity(VKApiCity dto) {
        return new CityEntity()
                .setArea(dto.area)
                .setId(dto.id)
                .setImportant(dto.important)
                .setTitle(dto.title)
                .setRegion(dto.region);
    }

    public static NewsEntity buildNewsEntity(VKApiNews news) {
        NewsEntity entity = new NewsEntity()
                .setType(news.type)
                .setSourceId(news.source_id)
                .setDate(news.date)
                .setPostId(news.post_id)
                .setPostType(news.post_type)
                .setFinalPost(news.final_post)
                .setCopyOwnerId(news.copy_owner_id)
                .setCopyPostId(news.copy_post_id)
                .setCopyPostDate(news.copy_post_date)
                .setText(news.text)
                .setCanEdit(news.can_edit)
                .setCanDelete(news.can_delete)
                .setCommentCount(news.comment_count)
                .setCanPostComment(news.comment_can_post)
                .setLikesCount(news.like_count)
                .setUserLikes(news.user_like)
                .setCanLike(news.can_like)
                .setCanPublish(news.can_publish)
                .setRepostCount(news.reposts_count)
                .setUserReposted(news.user_reposted)
                .setGeoId(nonNull(news.geo) ? news.geo.id : 0)
                .setFriendsTags(news.friends)
                .setViews(news.views);

        if (news.hasAttachments()) {
            entity.setAttachments(buildAttachmentsEntities(news.attachments));
        } else {
            entity.setAttachments(Collections.emptyList());
        }

        entity.setCopyHistory(mapAll(news.copy_history, Dto2Entity::buildPostEntity, false));
        return entity;
    }

    public static CommentEntity buildCommentDbo(int sourceId, int sourceOwnerId, int sourceType, String sourceAccessKey, VKApiComment comment) {
        List<Entity> attachmentsEntities;

        if(nonNull(comment.attachments)){
            attachmentsEntities = buildAttachmentsEntities(comment.attachments);
        } else {
            attachmentsEntities = Collections.emptyList();
        }

        return new CommentEntity(sourceId, sourceOwnerId, sourceType, sourceAccessKey, comment.id)
                .setFromId(comment.from_id)
                .setDate(comment.date)
                .setText(comment.text)
                .setReplyToUserId(comment.reply_to_user)
                .setReplyToComment(comment.reply_to_comment)
                .setLikesCount(comment.likes)
                .setUserLikes(comment.user_likes)
                .setCanLike(comment.can_like)
                .setCanEdit(comment.can_edit)
                .setDeleted(false)
                .setAttachmentsCount(comment.getAttachmentsCount())
                .setAttachments(attachmentsEntities);
    }

    public static DialogEntity buildDialogDbo(VkApiDialog dto) {
        MessageEntity messageDbo = buildMessageDbo(dto.message);

        return new DialogEntity(messageDbo.getPeerId())
                .setLastMessageId(messageDbo.getId())
                .setAdminId(messageDbo.getAdminId())
                .setMessage(messageDbo)
                .setPhoto50(messageDbo.getPhoto50())
                .setPhoto100(messageDbo.getPhoto100())
                .setPhoto200(messageDbo.getPhoto200())
                .setTitle(messageDbo.getTitle())
                .setUnreadCount(dto.unread);
    }

    public static List<Entity> buildAttachmentsEntities(VkApiAttachments attachments) {
        List<VkApiAttachments.Entry> entries = attachments.entryList();

        if (entries.isEmpty()) {
            return Collections.emptyList();
        }

        if (entries.size() == 1) {
            return Collections.singletonList(buildAttachmentEntity(entries.get(0).attachment));
        }

        List<Entity> entities = new ArrayList<>(entries.size());

        for (VkApiAttachments.Entry entry : entries) {
            if(isNull(entry)){
                // TODO: 04.10.2017
                continue;
            }

            entities.add(buildAttachmentEntity(entry.attachment));
        }

        return entities;
    }

    public static Entity buildAttachmentEntity(VKApiAttachment dto) {
        if (dto instanceof VKApiPhoto) {
            return buildPhotoEntity((VKApiPhoto) dto);
        }

        if (dto instanceof VKApiVideo) {
            return buildVideoEntity((VKApiVideo) dto);
        }

        if (dto instanceof VkApiDoc) {
            return buildDocumentEntity((VkApiDoc) dto);
        }

        if (dto instanceof VKApiLink) {
            return buildLinkEntity((VKApiLink) dto);
        }

        if (dto instanceof VKApiWikiPage) {
            return buildPageEntity((VKApiWikiPage) dto);
        }

        if (dto instanceof VKApiSticker) {
            return buildStickerEntity((VKApiSticker) dto);
        }

        if (dto instanceof VKApiPost) {
            return buildPostEntity((VKApiPost) dto);
        }

        if (dto instanceof VKApiPoll) {
            return buildPollEntity((VKApiPoll) dto);
        }

        if (dto instanceof VKApiAudio) {
            return buildAudioEntity((VKApiAudio) dto);
        }

        throw new UnsupportedOperationException("Unsupported attachment, class: " + dto.getClass());
    }

    public static AudioEntity buildAudioEntity(VKApiAudio dto) {
        return new AudioEntity(dto.id, dto.owner_id)
                .setArtist(dto.artist)
                .setTitle(dto.title)
                .setDuration(dto.duration)
                .setUrl(dto.url)
                .setLyricsId(dto.lyrics_id)
                .setAlbumId(dto.album_id)
                .setGenre(dto.genre)
                .setAccessKey(dto.access_key);
    }

    public static PollEntity buildPollEntity(VKApiPoll dto) {
        List<PollEntity.AnswerDbo> answerDbos = new ArrayList<>(safeCountOf(dto.answers));

        if (nonNull(dto.answers)) {
            for (VKApiPoll.Answer answer : dto.answers) {
                answerDbos.add(new PollEntity.AnswerDbo(answer.id, answer.text, answer.votes, answer.rate));
            }
        }

        return new PollEntity(dto.id, dto.owner_id)
                .setAnonymous(dto.anonymous)
                .setAnswers(answerDbos)
                .setBoard(dto.isBoard)
                .setCreationTime(dto.created)
                .setMyAnswerId(dto.answer_id)
                .setVoteCount(dto.votes)
                .setQuestion(dto.question);
    }

    public static PostEntity buildPostEntity(VKApiPost dto) {
        PostEntity dbo = new PostEntity(dto.id, dto.owner_id)
                .setFromId(dto.from_id)
                .setDate(dto.date)
                .setText(dto.text)
                .setReplyOwnerId(dto.reply_owner_id)
                .setReplyPostId(dto.reply_post_id)
                .setFriendsOnly(dto.friends_only)
                .setCommentsCount(nonNull(dto.comments) ? dto.comments.count : 0)
                .setCanPostComment(nonNull(dto.comments) && dto.comments.canPost)
                .setLikesCount(dto.likes_count)
                .setUserLikes(dto.user_likes)
                .setCanLike(dto.can_like)
                .setCanEdit(dto.can_edit)
                .setCanPublish(dto.can_publish)
                .setRepostCount(dto.reposts_count)
                .setUserReposted(dto.user_reposted)
                .setPostType(dto.post_type)
                .setAttachmentsCount(dto.getAttachmentsCount())
                .setSignedId(dto.signer_id)
                .setCreatedBy(dto.created_by)
                .setCanPin(dto.can_pin)
                .setPinned(dto.is_pinned)
                .setDeleted(false) // cant be deleted
                .setViews(dto.views);

        VkApiPostSource source = dto.post_source;
        if (nonNull(source)) {
            dbo.setSource(new PostEntity.SourceDbo(source.type, source.platform, source.data, source.url));
        }

        if (dto.hasAttachments()) {
            dbo.setAttachments(buildAttachmentsEntities(dto.attachments));
        } else {
            dbo.setAttachments(Collections.emptyList());
        }

        if (dto.hasCopyHistory()) {
            dbo.setCopyHierarchy(mapAll(dto.copy_history, Dto2Entity::buildPostEntity));
        } else {
            dbo.setCopyHierarchy(Collections.emptyList());
        }

        return dbo;
    }

    public static StickerEntity buildStickerEntity(VKApiSticker sticker) {
        return new StickerEntity(sticker.id)
                .setHeight(sticker.height)
                .setWidth(sticker.width);
    }

    public static PageEntity buildPageEntity(VKApiWikiPage dto) {
        return new PageEntity(dto.id, dto.owner_id)
                .setCreatorId(dto.creator_id)
                .setTitle(dto.title)
                .setSource(dto.source)
                .setEditionTime(dto.edited)
                .setCreationTime(dto.created)
                .setParent(dto.parent)
                .setParent2(dto.parent2)
                .setViews(dto.views)
                .setViewUrl(dto.view_url);
    }

    public static LinkEntity buildLinkEntity(VKApiLink link) {
        return new LinkEntity(link.url)
                .setCaption(link.caption)
                .setDescription(link.description)
                .setTitle(link.title)
                .setPhoto(nonNull(link.photo) ? buildPhotoEntity(link.photo) : null);
    }

    public static DocumentEntity buildDocumentEntity(VkApiDoc dto) {
        DocumentEntity dbo = new DocumentEntity(dto.id, dto.ownerId)
                .setTitle(dto.title)
                .setSize(dto.size)
                .setExt(dto.ext)
                .setUrl(dto.url)
                .setDate(dto.date)
                .setType(dto.type)
                .setAccessKey(dto.accessKey);

        if (nonNull(dto.preview)) {
            if (nonNull(dto.preview.photo) && nonNull(dto.preview.photo.sizes)) {
                dbo.setPhoto(buildPhotoSizeDbo(dto.preview.photo.sizes));
            }

            if (nonNull(dto.preview.video)) {
                VkApiDoc.Video video = dto.preview.video;
                dbo.setVideo(new DocumentEntity.VideoPreviewDbo(video.src, video.width, video.height, video.fileSize));
            }

            if (nonNull(dto.preview.audioMsg)) {
                VkApiDoc.AudioMsg audio = dto.preview.audioMsg;
                dbo.setAudio(new DocumentEntity.AudioMessageDbo(audio.duration, audio.waveform, audio.linkOgg, audio.linkMp3));
            }

            if (nonNull(dto.preview.graffiti)) {
                VkApiDoc.Graffiti graffiti = dto.preview.graffiti;
                dbo.setGraffiti(new DocumentEntity.GraffitiDbo(graffiti.src, graffiti.width, graffiti.height));
            }
        }

        return dbo;
    }

    public static MessageEntity buildMessageDbo(VKApiMessage dto) {
        boolean encrypted = CryptHelper.analizeMessageBody(dto.body) == MessageType.CRYPTED;

        int randomId = 0;
        try {
            randomId = Integer.parseInt(dto.random_id);
        } catch (NumberFormatException ignored) {
        }

        MessageEntity entity = new MessageEntity(dto.id, dto.peer_id, dto.from_id)
                .setDate(dto.date)
                .setRead(dto.read_state)
                .setOut(dto.out)
                .setTitle(dto.title)
                .setBody(dto.body)
                .setEncrypted(encrypted)
                .setImportant(dto.important)
                .setDeleted(dto.deleted)
                .setForwardCount(Utils.safeCountOf(dto.fwd_messages))
                .setHasAttachmens(nonNull(dto.attachments) && !dto.attachments.isEmpty())
                .setStatus(MessageStatus.SENT) // only sent can be
                .setOriginalId(dto.id)
                .setChatActive(dto.chat_active)
                .setUsersCount(dto.users_count)
                .setAdminId(dto.admin_id)
                .setAction(Message.fromApiChatAction(dto.action))
                .setActionMemberId(dto.action_mid)
                .setActionEmail(dto.action_email)
                .setActionText(dto.action_text)
                .setPhoto50(dto.photo_50)
                .setPhoto100(dto.photo_100)
                .setPhoto200(dto.photo_200)
                .setRandomId(randomId);

        if (entity.isHasAttachmens()) {
            entity.setAttachments(buildAttachmentsEntities(dto.attachments));
        } else {
            entity.setAttachments(Collections.emptyList());
        }

        if (nonEmpty(dto.fwd_messages)) {
            if (dto.fwd_messages.size() == 1) {
                entity.setForwardMessages(Collections.singletonList(buildMessageDbo(dto.fwd_messages.get(0))));
            } else {
                List<MessageEntity> fwds = new ArrayList<>(dto.fwd_messages.size());

                for (VKApiMessage f : dto.fwd_messages) {
                    fwds.add(buildMessageDbo(f));
                }

                entity.setForwardMessages(fwds);
            }
        } else {
            entity.setForwardMessages(Collections.emptyList());
        }

        return entity;
    }

    public static VideoEntity buildVideoEntity(VKApiVideo dto) {
        return new VideoEntity(dto.id, dto.owner_id)
                .setAlbumId(dto.album_id)
                .setTitle(dto.title)
                .setDescription(dto.description)
                .setDuration(dto.duration)
                .setLink(dto.link)
                .setDate(dto.date)
                .setAddingDate(dto.adding_date)
                .setViews(dto.views)
                .setPlayer(dto.player)
                .setPhoto130(dto.photo_130)
                .setPhoto320(dto.photo_320)
                .setPhoto800(dto.photo_800)
                .setAccessKey(dto.access_key)
                .setCommentsCount(isNull(dto.comments) ? 0 : dto.comments.count)
                .setCanComment(dto.can_comment)
                .setCanRepost(dto.can_repost)
                .setUserLikes(dto.user_likes)
                .setRepeat(dto.repeat)
                .setLikesCount(dto.likes)
                .setPrivacyView(nonNull(dto.privacy_view) ? buildPrivacyDbo(dto.privacy_view) : null)
                .setPrivacyComment(nonNull(dto.privacy_comment) ? buildPrivacyDbo(dto.privacy_comment) : null)
                .setMp4link240(dto.mp4_240)
                .setMp4link360(dto.mp4_360)
                .setMp4link480(dto.mp4_480)
                .setMp4link720(dto.mp4_720)
                .setMp4link1080(dto.mp4_1080)
                .setExternalLink(dto.external)
                .setHls(dto.hls)
                .setLive(dto.live)
                .setPlatform(dto.platform)
                .setCanEdit(dto.can_edit)
                .setCanAdd(dto.can_add);
    }

    public static PrivacyEntity buildPrivacyDbo(VkApiPrivacy dto) {
        List<PrivacyEntity.Entry> entries = new ArrayList<>(safeCountOf(dto.entries));

        if (nonNull(dto.entries)) {
            for (VkApiPrivacy.Entry entry : dto.entries) {
                entries.add(new PrivacyEntity.Entry(entry.type, entry.id, entry.allowed));
            }
        }

        return new PrivacyEntity(dto.type, entries.toArray(new PrivacyEntity.Entry[entries.size()]));
    }

    public static PhotoEntity buildPhotoEntity(VKApiPhoto dto) {
        return new PhotoEntity(dto.id, dto.owner_id)
                .setAlbumId(dto.album_id)
                .setWidth(dto.width)
                .setHeight(dto.height)
                .setText(dto.text)
                .setDate(dto.date)
                .setUserLikes(dto.user_likes)
                .setCanComment(dto.can_comment)
                .setLikesCount(dto.likes)
                .setCommentsCount(nonNull(dto.comments) ? dto.comments.count : 0)
                .setTagsCount(dto.tags)
                .setAccessKey(dto.access_key)
                .setPostId(dto.post_id)
                .setDeleted(false) //cant bee deleted
                .setSizes(buildPhotoSizeDbo(dto.sizes));
    }

    public static PhotoSizeEntity.Size dto2entity(PhotoSizeDto dto) {
        return new PhotoSizeEntity.Size()
                .setH(dto.height)
                .setW(dto.width)
                .setUrl(dto.src);
    }

    public static PhotoSizeEntity buildPhotoSizeDbo(List<PhotoSizeDto> dtos) {
        PhotoSizeEntity sizes = new PhotoSizeEntity();

        if (nonNull(dtos)) {
            for (PhotoSizeDto dto : dtos) {
                switch (dto.type) {
                    case PhotoSizeDto.Type.S:
                        sizes.setS(dto2entity(dto));
                        break;

                    case PhotoSizeDto.Type.M:
                        sizes.setM(dto2entity(dto));
                        break;

                    case PhotoSizeDto.Type.X:
                        sizes.setX(dto2entity(dto));
                        break;

                    case PhotoSizeDto.Type.Y:
                        sizes.setY(dto2entity(dto));
                        break;

                    case PhotoSizeDto.Type.Z:
                        sizes.setZ(dto2entity(dto));
                        break;

                    case PhotoSizeDto.Type.W:
                        sizes.setW(dto2entity(dto));
                        break;

                    case PhotoSizeDto.Type.O:
                        sizes.setO(dto2entity(dto));
                        break;

                    case PhotoSizeDto.Type.P:
                        sizes.setP(dto2entity(dto));
                        break;

                    case PhotoSizeDto.Type.Q:
                        sizes.setQ(dto2entity(dto));
                        break;

                    case PhotoSizeDto.Type.R:
                        sizes.setR(dto2entity(dto));
                        break;
                }
            }
        }

        return sizes;
    }
}