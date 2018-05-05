package com.anotap.messenger.domain.mappers;

import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.anotap.messenger.db.model.entity.AudioEntity;
import com.anotap.messenger.db.model.entity.DocumentEntity;
import com.anotap.messenger.db.model.entity.Entity;
import com.anotap.messenger.db.model.entity.LinkEntity;
import com.anotap.messenger.db.model.entity.MessageEntity;
import com.anotap.messenger.db.model.entity.PageEntity;
import com.anotap.messenger.db.model.entity.PhotoEntity;
import com.anotap.messenger.db.model.entity.PhotoSizeEntity;
import com.anotap.messenger.db.model.entity.PollEntity;
import com.anotap.messenger.db.model.entity.PostEntity;
import com.anotap.messenger.db.model.entity.PrivacyEntity;
import com.anotap.messenger.db.model.entity.StickerEntity;
import com.anotap.messenger.db.model.entity.VideoEntity;
import com.anotap.messenger.model.AbsModel;
import com.anotap.messenger.model.Attachments;
import com.anotap.messenger.model.Audio;
import com.anotap.messenger.model.CryptStatus;
import com.anotap.messenger.model.Document;
import com.anotap.messenger.model.Link;
import com.anotap.messenger.model.Message;
import com.anotap.messenger.model.Photo;
import com.anotap.messenger.model.PhotoSizes;
import com.anotap.messenger.model.Poll;
import com.anotap.messenger.model.Post;
import com.anotap.messenger.model.PostSource;
import com.anotap.messenger.model.SimplePrivacy;
import com.anotap.messenger.model.Sticker;
import com.anotap.messenger.model.Video;
import com.anotap.messenger.model.VoiceMessage;
import com.anotap.messenger.model.WikiPage;

import static com.anotap.messenger.domain.mappers.MapUtil.mapAll;
import static com.anotap.messenger.domain.mappers.MapUtil.mapAndAdd;
import static com.anotap.messenger.util.Objects.isNull;
import static com.anotap.messenger.util.Objects.nonNull;
import static com.anotap.messenger.util.Utils.safeCountOf;

/**
 * Created by Ruslan Kolbasa on 05.09.2017.
 * phoenix
 */
public class Model2Entity {

    public static MessageEntity buildMessageEntity(Message message) {
        return new MessageEntity(message.getId(), message.getPeerId(), message.getSenderId())
                .setDate(message.getDate())
                .setRead(message.isRead())
                .setOut(message.isOut())
                .setTitle(message.getTitle())
                .setBody(message.getBody())
                .setEncrypted(message.getCryptStatus() != CryptStatus.NO_ENCRYPTION)
                .setImportant(message.isImportant())
                .setDeleted(message.isDeleted())
                .setForwardCount(message.getForwardMessagesCount())
                .setHasAttachmens(message.isHasAttachments())
                .setStatus(message.getStatus())
                .setOriginalId(message.getOriginalId())
                .setChatActive(message.getChatActive())
                .setUsersCount(message.getUsersCount())
                .setAdminId(message.getAdminId())
                .setAction(message.getAction())
                .setActionMemberId(message.getActionMid())
                .setActionEmail(message.getActionEmail())
                .setActionText(message.getActionText())
                .setPhoto50(message.getPhoto50())
                .setPhoto100(message.getPhoto100())
                .setPhoto200(message.getPhoto200())
                .setRandomId(message.getRandomId())
                .setExtras(message.getExtras())
                .setAttachments(nonNull(message.getAttachments()) ? buildEntityAttachments(message.getAttachments()) : null)
                .setForwardMessages(mapAll(message.getFwd(), Model2Entity::buildMessageEntity, false));
    }

    public static List<Entity> buildEntityAttachments(Attachments attachments) {
        List<Entity> entities = new ArrayList<>(attachments.size());
        mapAndAdd(attachments.getAudios(), Model2Entity::buildAudioEntity, entities);
        mapAndAdd(attachments.getStickers(), Model2Entity::buildStickerEntity, entities);
        mapAndAdd(attachments.getPhotos(), Model2Entity::buildPhotoEntity, entities);
        mapAndAdd(attachments.getDocs(), Model2Entity::buildDocumentDbo, entities);
        mapAndAdd(attachments.getVoiceMessages(), Model2Entity::buildDocumentDbo, entities);
        mapAndAdd(attachments.getVideos(), Model2Entity::buildVideoDbo, entities);
        mapAndAdd(attachments.getPosts(), Model2Entity::buildPostDbo, entities);
        mapAndAdd(attachments.getLinks(), Model2Entity::buildLinkDbo, entities);
        mapAndAdd(attachments.getPolls(), Model2Entity::buildPollDbo, entities);
        mapAndAdd(attachments.getPages(), Model2Entity::buildPageEntity, entities);
        return entities;
    }

    public static List<Entity> buildDboAttachments(List<? extends AbsModel> models){
        List<Entity> entities = new ArrayList<>(models.size());

        for(AbsModel model : models){
            if(model instanceof Audio){
                entities.add(buildAudioEntity((Audio) model));
            } else if(model instanceof Sticker){
                entities.add(buildStickerEntity((Sticker) model));
            } else if(model instanceof Photo){
                entities.add(buildPhotoEntity((Photo) model));
            } else if(model instanceof Document){
                entities.add(buildDocumentDbo((Document) model));
            } else if(model instanceof Video){
                entities.add(buildVideoDbo((Video) model));
            } else if(model instanceof Post){
                entities.add(buildPostDbo((Post) model));
            } else if(model instanceof Link){
                entities.add(buildLinkDbo((Link) model));
            } else if(model instanceof Poll){
                entities.add(buildPollDbo((Poll) model));
            } else if(model instanceof WikiPage){
                entities.add(buildPageEntity((WikiPage) model));
            } else {
                throw new UnsupportedOperationException("Unsupported model");
            }
        }

        return entities;
    }

    public static PageEntity buildPageEntity(WikiPage page) {
        return new PageEntity(page.getId(), page.getOwnerId())
                .setViewUrl(page.getViewUrl())
                .setViews(page.getViews())
                .setParent2(page.getParent2())
                .setParent(page.getParent())
                .setCreationTime(page.getCreationTime())
                .setEditionTime(page.getEditionTime())
                .setCreatorId(page.getCreatorId())
                .setSource(page.getSource());
    }

    public static PollEntity.AnswerDbo mapAnswer(Poll.Answer answer) {
        return new PollEntity.AnswerDbo(answer.getId(), answer.getText(), answer.getVoteCount(), answer.getRate());
    }

    public static PollEntity buildPollDbo(Poll poll){
        return new PollEntity(poll.getId(), poll.getOwnerId())
                .setAnswers(mapAll(poll.getAnswers(), Model2Entity::mapAnswer, false))
                .setQuestion(poll.getQuestion())
                .setVoteCount(poll.getVoteCount())
                .setMyAnswerId(poll.getMyAnswerId())
                .setCreationTime(poll.getCreationTime())
                .setAnonymous(poll.isAnonymous())
                .setBoard(poll.isBoard());
    }

    public static LinkEntity buildLinkDbo(Link link){
        return new LinkEntity(link.getUrl())
                .setPhoto(isNull(link.getPhoto()) ? null : buildPhotoEntity(link.getPhoto()))
                .setTitle(link.getTitle())
                .setDescription(link.getDescription())
                .setCaption(link.getCaption());
    }

    public static PostEntity buildPostDbo(Post post){
        PostEntity dbo = new PostEntity(post.getVkid(), post.getOwnerId())
                .setFromId(post.getAuthorId())
                .setDate(post.getDate())
                .setText(post.getText())
                .setReplyOwnerId(post.getReplyOwnerId())
                .setReplyPostId(post.getReplyPostId())
                .setFriendsOnly(post.isFriendsOnly())
                .setCommentsCount(post.getCommentsCount())
                .setCanPostComment(post.isCanPostComment())
                .setLikesCount(post.getLikesCount())
                .setUserLikes(post.isUserLikes())
                .setCanLike(post.isCanLike())
                .setCanEdit(post.isCanEdit())
                .setCanPublish(post.isCanRepost())
                .setRepostCount(post.getRepostCount())
                .setUserReposted(post.isUserReposted())
                .setPostType(post.getPostType())
                .setAttachmentsCount(nonNull(post.getAttachments()) ? post.getAttachments().size() : 0)
                .setSignedId(post.getSignerId())
                .setCreatedBy(post.getCreatorId())
                .setCanPin(post.isCanPin())
                .setPinned(post.isPinned())
                .setDeleted(post.isDeleted())
                .setViews(post.getViewCount())
                .setDbid(post.getDbid());

        PostSource source = post.getSource();
        if(nonNull(source)){
            dbo.setSource(new PostEntity.SourceDbo(source.getType(), source.getPlatform(), source.getData(), source.getUrl()));
        }

        if(nonNull(post.getAttachments())){
            dbo.setAttachments(buildEntityAttachments(post.getAttachments()));
        } else {
            dbo.setAttachments(Collections.emptyList());
        }

        dbo.setCopyHierarchy(mapAll(post.getCopyHierarchy(), Model2Entity::buildPostDbo, false));
        return dbo;
    }

    public static VideoEntity buildVideoDbo(Video video){
        return new VideoEntity(video.getId(), video.getOwnerId())
                .setAlbumId(video.getAlbumId())
                .setTitle(video.getTitle())
                .setDescription(video.getDescription())
                .setLink(video.getLink())
                .setDate(video.getDate())
                .setAddingDate(video.getAddingDate())
                .setViews(video.getViews())
                .setPlayer(video.getPlayer())
                .setPhoto130(video.getPhoto130())
                .setPhoto320(video.getPhoto320())
                .setPhoto800(video.getPhoto800())
                .setAccessKey(video.getAccessKey())
                .setCommentsCount(video.getCommentsCount())
                .setUserLikes(video.isUserLikes())
                .setLikesCount(video.getLikesCount())
                .setMp4link240(video.getMp4link240())
                .setMp4link360(video.getMp4link360())
                .setMp4link480(video.getMp4link480())
                .setMp4link720(video.getMp4link720())
                .setMp4link1080(video.getMp4link1080())
                .setExternalLink(video.getExternalLink())
                .setPlatform(video.getPlatform())
                .setRepeat(video.isRepeat())
                .setDuration(video.getDuration())
                .setPrivacyView(isNull(video.getPrivacyView()) ? null : buildPrivacyDbo(video.getPrivacyView()))
                .setPrivacyComment(isNull(video.getPrivacyComment()) ? null : buildPrivacyDbo(video.getPrivacyComment()))
                .setCanEdit(video.isCanEdit())
                .setCanAdd(video.isCanAdd())
                .setCanComment(video.isCanComment())
                .setCanRepost(video.isCanRepost());
    }

    public static PrivacyEntity buildPrivacyDbo(SimplePrivacy privacy){
        List<SimplePrivacy.Entry> entries = privacy.getEntries();
        PrivacyEntity.Entry[] entryDbos = new PrivacyEntity.Entry[(safeCountOf(entries))];

        if(nonNull(entries)){
            for(int i = 0; i < entries.size(); i++){
                SimplePrivacy.Entry entry = entries.get(i);
                entryDbos[i] = new PrivacyEntity.Entry(entry.getType(), entry.getId(), entry.isAllowed());
            }
        }

        return new PrivacyEntity(privacy.getType(), entryDbos);
    }

    public static DocumentEntity buildDocumentDbo(Document document){
        DocumentEntity dbo = new DocumentEntity(document.getId(), document.getOwnerId())
                .setTitle(document.getTitle())
                .setSize(document.getSize())
                .setExt(document.getExt())
                .setUrl(document.getUrl())
                .setDate(document.getDate())
                .setType(document.getType())
                .setAccessKey(document.getAccessKey());

        if(document instanceof VoiceMessage){
            VoiceMessage message = (VoiceMessage) document;
            dbo.setAudio(new DocumentEntity.AudioMessageDbo(message.getDuration(), message.getWaveform(), message.getLinkOgg(), message.getLinkMp3()));
        }

        if(nonNull(document.getGraffiti())){
            Document.Graffiti graffiti = document.getGraffiti();
            dbo.setGraffiti(new DocumentEntity.GraffitiDbo(graffiti.getSrc(), graffiti.getWidth(), graffiti.getHeight()));
        }

        if(nonNull(document.getVideoPreview())){
            Document.VideoPreview video = document.getVideoPreview();
            dbo.setVideo(new DocumentEntity.VideoPreviewDbo(video.getSrc(), video.getWidth(), video.getHeight(), video.getFileSize()));
        }

        return dbo;
    }

    public static StickerEntity buildStickerEntity(Sticker sticker) {
        return new StickerEntity(sticker.getId())
                .setWidth(sticker.getWidth())
                .setHeight(sticker.getHeight());
    }

    public static AudioEntity buildAudioEntity(Audio audio) {
        return new AudioEntity(audio.getId(), audio.getOwnerId())
                .setArtist(audio.getArtist())
                .setTitle(audio.getTitle())
                .setDuration(audio.getDuration())
                .setUrl(audio.getUrl())
                .setLyricsId(audio.getLyricsId())
                .setAlbumId(audio.getAlbumId())
                .setGenre(audio.getGenre())
                .setAccessKey(audio.getAccessKey())
                .setDeleted(audio.isDeleted());
    }

    public static PhotoEntity buildPhotoEntity(Photo photo) {
        return new PhotoEntity(photo.getId(), photo.getOwnerId())
                .setAlbumId(photo.getAlbumId())
                .setWidth(photo.getWidth())
                .setHeight(photo.getHeight())
                .setText(photo.getText())
                .setDate(photo.getDate())
                .setUserLikes(photo.isUserLikes())
                .setCanComment(photo.isCanComment())
                .setLikesCount(photo.getLikesCount())
                .setCommentsCount(photo.getCommentsCount())
                .setTagsCount(photo.getTagsCount())
                .setAccessKey(photo.getAccessKey())
                .setPostId(photo.getPostId())
                .setDeleted(photo.isDeleted())
                .setSizes(isNull(photo.getSizes()) ? null : buildPhotoSizeEntity(photo.getSizes()));
    }

    private static PhotoSizeEntity.Size model2entityNullable(@Nullable PhotoSizes.Size size) {
        if (nonNull(size)) {
            return new PhotoSizeEntity.Size()
                    .setUrl(size.getUrl())
                    .setW(size.getW())
                    .setH(size.getH());
        }
        return null;
    }

    public static PhotoSizeEntity buildPhotoSizeEntity(PhotoSizes sizes) {
        return new PhotoSizeEntity()
                .setS(model2entityNullable(sizes.getS()))
                .setM(model2entityNullable(sizes.getM()))
                .setX(model2entityNullable(sizes.getX()))
                .setO(model2entityNullable(sizes.getO()))
                .setP(model2entityNullable(sizes.getP()))
                .setQ(model2entityNullable(sizes.getQ()))
                .setR(model2entityNullable(sizes.getR()))
                .setY(model2entityNullable(sizes.getY()))
                .setZ(model2entityNullable(sizes.getZ()))
                .setW(model2entityNullable(sizes.getW()));
    }
}