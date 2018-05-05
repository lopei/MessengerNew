package com.anotap.messenger.db.model;

import com.anotap.messenger.db.model.entity.AudioEntity;
import com.anotap.messenger.db.model.entity.DocumentEntity;
import com.anotap.messenger.db.model.entity.Entity;
import com.anotap.messenger.db.model.entity.LinkEntity;
import com.anotap.messenger.db.model.entity.PageEntity;
import com.anotap.messenger.db.model.entity.PhotoEntity;
import com.anotap.messenger.db.model.entity.PollEntity;
import com.anotap.messenger.db.model.entity.PostEntity;
import com.anotap.messenger.db.model.entity.StickerEntity;
import com.anotap.messenger.db.model.entity.TopicEntity;
import com.anotap.messenger.db.model.entity.VideoEntity;

/**
 * Created by Ruslan Kolbasa on 04.09.2017.
 * phoenix
 */
public final class AttachmentsTypes {

    private AttachmentsTypes(){}

    public static final int PHOTO = 1;
    public static final int VIDEO = 2;
    public static final int AUDIO = 4;
    public static final int DOC = 8;
    public static final int POST = 16;
    public static final int LINK = 64;
    public static final int POLL = 512;
    public static final int PAGE = 1024;
    public static final int STICKER = 4096;
    public static final int TOPIC = 8192;

    public static int typeForInstance(Entity entity) {
        if (entity instanceof PhotoEntity) {
            return PHOTO;
        } else if (entity instanceof VideoEntity) {
            return VIDEO;
        } else if (entity instanceof PostEntity) {
            return POST;
        } else if(entity instanceof DocumentEntity){
            return DOC;
        } else if(entity instanceof PollEntity){
            return POLL;
        } else if(entity instanceof AudioEntity){
            return AUDIO;
        } else if(entity instanceof LinkEntity){
            return LINK;
        } else if(entity instanceof StickerEntity){
            return STICKER;
        } else if(entity instanceof PageEntity){
            return PAGE;
        } else if(entity instanceof TopicEntity){
            return TOPIC;
        }

        throw new UnsupportedOperationException("Unsupported type: " + entity.getClass());
    }

    public static Class<? extends Entity> classForType(int type) {
        switch (type) {
            case PHOTO:
                return PhotoEntity.class;
            case VIDEO:
                return VideoEntity.class;
            case POST:
                return PostEntity.class;
            case DOC:
                return DocumentEntity.class;
            case POLL:
                return PollEntity.class;
            case AUDIO:
                return AudioEntity.class;
            case LINK:
                return LinkEntity.class;
            case STICKER:
                return StickerEntity.class;
            case PAGE:
                return PageEntity.class;
            case TOPIC:
                return TopicEntity.class;
            default:
                throw new UnsupportedOperationException("Unsupported type: " + type);
        }
    }
}