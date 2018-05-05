package com.anotap.messenger.domain.mappers;

import java.util.Collection;
import java.util.List;

import com.anotap.messenger.api.model.AttachmentsTokenCreator;
import com.anotap.messenger.api.model.IAttachmentToken;
import com.anotap.messenger.model.AbsModel;
import com.anotap.messenger.model.Audio;
import com.anotap.messenger.model.Document;
import com.anotap.messenger.model.Link;
import com.anotap.messenger.model.Photo;
import com.anotap.messenger.model.Poll;
import com.anotap.messenger.model.Post;
import com.anotap.messenger.model.Video;

/**
 * Created by Ruslan Kolbasa on 05.09.2017.
 * phoenix
 */
public class Model2Dto {

    /*public static List<IAttachmentToken> createTokens(Attachments attachments){
        List<IAttachmentToken> tokens = new ArrayList<>(nonNull(attachments) ? attachments.size() : 0);

        if(nonNull(attachments)){
            tokens.addAll(createTokens(attachments.getAudios()));
            tokens.addAll(createTokens(attachments.getPhotos()));
            tokens.addAll(createTokens(attachments.getDocs()));
            tokens.addAll(createTokens(attachments.getVideos()));
            tokens.addAll(createTokens(attachments.getPosts()));
            tokens.addAll(createTokens(attachments.getLinks()));
            tokens.addAll(createTokens(attachments.getPolls()));
            tokens.addAll(createTokens(attachments.getPages()));
            tokens.addAll(createTokens(attachments.getVoiceMessages()));
        }

        return tokens;
    }*/

    public static List<IAttachmentToken> createTokens(Collection<? extends AbsModel> models){
        return MapUtil.mapAll(models, Model2Dto::createToken);
    }

    public static IAttachmentToken createToken(AbsModel model){
        if(model instanceof Document){
            Document document = (Document) model;
            return AttachmentsTokenCreator.ofDocument(document.getId(), document.getOwnerId(), document.getAccessKey());
        }

        if(model instanceof Audio){
            Audio audio = (Audio) model;
            return AttachmentsTokenCreator.ofAudio(audio.getId(), audio.getOwnerId(), audio.getAccessKey());
        }

        if(model instanceof Link){
            return AttachmentsTokenCreator.ofLink(((Link) model).getUrl());
        }

        if(model instanceof Photo){
            Photo photo = (Photo) model;
            return AttachmentsTokenCreator.ofPhoto(photo.getId(), photo.getOwnerId(), photo.getAccessKey());
        }

        if(model instanceof Poll){
            Poll poll = (Poll) model;
            return AttachmentsTokenCreator.ofPoll(poll.getId(), poll.getOwnerId());
        }

        if(model instanceof Post){
            Post post = (Post) model;
            return AttachmentsTokenCreator.ofPost(post.getVkid(), post.getOwnerId());
        }

        if(model instanceof Video){
            Video video = (Video) model;
            return AttachmentsTokenCreator.ofVideo(video.getId(), video.getOwnerId(), video.getAccessKey());
        }

        throw new UnsupportedOperationException("Token for class " + model.getClass() + " not supported");
    }
}