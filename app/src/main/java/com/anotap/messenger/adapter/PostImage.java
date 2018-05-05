package com.anotap.messenger.adapter;

import com.anotap.messenger.model.AbsModel;
import com.anotap.messenger.model.Document;
import com.anotap.messenger.model.Photo;
import com.anotap.messenger.model.PhotoSize;
import com.anotap.messenger.model.PhotoSizes;
import com.anotap.messenger.model.Video;
import com.anotap.messenger.view.mozaik.PostImagePosition;

import static com.anotap.messenger.util.Utils.firstNonEmptyString;

public class PostImage {

    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VIDEO = 2;
    public static final int TYPE_GIF = 3;

    private final int type;
    private final AbsModel attachment;
    private PostImagePosition position;

    public PostImage(AbsModel model, int type){
        this.attachment = model;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public AbsModel getAttachment() {
        return attachment;
    }

    public PostImage setPosition(PostImagePosition position) {
        this.position = position;
        return this;
    }

    public PostImagePosition getPosition() {
        return position;
    }

    public int getWidth(){
        switch (type){
            case TYPE_IMAGE:
                Photo photo = (Photo) attachment;
                return photo.getWidth() == 0 ? 100 : photo.getWidth();
            case TYPE_VIDEO:
                return 640;
            case TYPE_GIF:
                Document document = (Document) attachment;
                PhotoSizes.Size max = document.getMaxPreviewSize(false);
                return max == null ? 640 : max.getW();
            default:
                throw new UnsupportedOperationException();
        }
    }

    public String getPreviewUrl(@PhotoSize int photoPreviewSize) {
        switch (type) {
            case PostImage.TYPE_IMAGE:
                Photo photo = (Photo) attachment;
                PhotoSizes.Size size = photo.getSizes().getSize(photoPreviewSize, true);
                return size == null ? null : size.getUrl();
            case PostImage.TYPE_VIDEO:
                Video video = (Video) attachment;
                return firstNonEmptyString(video.getPhoto800(), video.getPhoto320());
            case PostImage.TYPE_GIF:
                Document document = (Document) attachment;
                return document.getPreviewWithSize(PhotoSize.Q, false);
        }

        throw new UnsupportedOperationException();
    }

    public int getHeight(){
        switch (type){
            case TYPE_IMAGE:
                Photo photo = (Photo) attachment;
                return photo.getHeight() == 0 ? 100 : photo.getHeight();
            case TYPE_VIDEO:
                return 360;
            case TYPE_GIF:
                Document document = (Document) attachment;
                PhotoSizes.Size max = document.getMaxPreviewSize(false);
                return max == null ? 480 : max.getH();
            default:
                throw new UnsupportedOperationException();
        }
    }

    public float getAspectRatio(){
        return (float) getWidth() / (float) getHeight();
    }
}