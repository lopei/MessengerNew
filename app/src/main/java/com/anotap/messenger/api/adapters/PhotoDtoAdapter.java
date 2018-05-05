package com.anotap.messenger.api.adapters;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.anotap.messenger.api.model.CommentsDto;
import com.anotap.messenger.api.model.PhotoSizeDto;
import com.anotap.messenger.api.model.VKApiPhoto;

import static com.anotap.messenger.util.Utils.nonEmpty;

/**
 * Created by ruslan.kolbasa on 26.12.2016.
 * phoenix
 */
public class PhotoDtoAdapter extends AbsAdapter implements JsonDeserializer<VKApiPhoto> {

    @Override
    public VKApiPhoto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject root = json.getAsJsonObject();

        VKApiPhoto photo = new VKApiPhoto();
        photo.id = optInt(root, "id");
        photo.album_id = optInt(root, "album_id");
        photo.date = optLong(root, "date");
        photo.height = optInt(root, "height");
        photo.width = optInt(root, "width");
        photo.owner_id = optInt(root, "owner_id");
        photo.text = optString(root, "text");
        photo.access_key = optString(root, "access_key");

        String photo_75 = optString(root, "photo_75");
        String photo_130 = optString(root, "photo_130");
        String photo_604 = optString(root, "photo_604");
        String photo_807 = optString(root, "photo_807");
        String photo_1280 = optString(root, "photo_1280");
        String photo_2560 = optString(root, "photo_2560");

        if (root.has("likes")) {
            JsonObject likesRoot = root.get("likes").getAsJsonObject();
            photo.likes = optInt(likesRoot, "count");
            photo.user_likes = optInt(likesRoot, "user_likes") == 1;
        }

        if (root.has("comments")) {
            JsonElement commentsRoot = root.get("comments").getAsJsonObject();
            photo.comments = context.deserialize(commentsRoot, CommentsDto.class);
        }

        if (root.has("tags")) {
            JsonObject tagsRoot = root.get("tags").getAsJsonObject();
            photo.tags = optInt(tagsRoot, "count");
        }

        photo.can_comment = optInt(root, "can_comment") == 1;
        photo.post_id = optInt(root, "post_id");

        if (root.has("sizes")) {
            JsonArray sizesArray = root.getAsJsonArray("sizes");
            photo.sizes = new ArrayList<>(sizesArray.size());

            for (int i = 0; i < sizesArray.size(); i++) {
                PhotoSizeDto photoSizeDto = context.deserialize(sizesArray.get(i).getAsJsonObject(), PhotoSizeDto.class);
                photo.sizes.add(photoSizeDto);

                if (photo.width != 0 && photo.height != 0) {
                    continue;
                }

                switch (photoSizeDto.type) {
                    case PhotoSizeDto.Type.O:
                    case PhotoSizeDto.Type.P:
                    case PhotoSizeDto.Type.Q:
                    case PhotoSizeDto.Type.R:
                        continue;

                    default:
                        photo.width = photoSizeDto.width;
                        photo.height = photoSizeDto.height;
                        break;
                }
            }
        } else {
            photo.sizes = new ArrayList<>();

            int w = photo.width;
            int h = photo.height;

            if (nonEmpty(photo_75)) {
                photo.sizes.add(manualCreate(PhotoSizeDto.Type.S, photo_75, w, h, 75));
            }

            if (nonEmpty(photo_130)) {
                photo.sizes.add(manualCreate(PhotoSizeDto.Type.M, photo_130, w, h, 130));
            }

            if (nonEmpty(photo_604)) {
                photo.sizes.add(manualCreate(PhotoSizeDto.Type.X, photo_604, w, h, 604));
            }

            if (nonEmpty(photo_807)) {
                photo.sizes.add(manualCreate(PhotoSizeDto.Type.Y, photo_807, w, h, 807));
            }

            if (nonEmpty(photo_1280)) {
                photo.sizes.add(manualCreate(PhotoSizeDto.Type.Z, photo_1280, w, h, 1280));
            }

            if (nonEmpty(photo_2560)) {
                photo.sizes.add(manualCreate(PhotoSizeDto.Type.W, photo_2560, w, h, 2560));
            }
        }

        return photo;
    }

    private static PhotoSizeDto manualCreate(char type, String url, int origw, int origh, int trimToSize) {
        PhotoSizeDto dto = new PhotoSizeDto();
        dto.src = url;
        dto.type = type;

        WH trimmed = trimSizeTo(origw, origh, trimToSize);
        dto.width = trimmed.w;
        dto.height = trimmed.h;
        return dto;
    }

    private static WH trimSizeTo(int origw, int origh, float max) {
        if (origh < max && origw < max) {
            return new WH(origw, origh);
        }

        float ratio = Math.min(max / origw, max / origh);
        int width = Math.round(ratio * origw);
        int height = Math.round(ratio * origh);
        return new WH(width, height);
    }

    private static final class WH {

        final int w;
        final int h;

        WH(int w, int h) {
            this.w = w;
            this.h = h;
        }
    }
}