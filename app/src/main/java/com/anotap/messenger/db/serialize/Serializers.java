package com.anotap.messenger.db.serialize;

import com.google.gson.Gson;

import com.anotap.messenger.model.Photo;

/**
 * Created by Ruslan Kolbasa on 20.06.2017.
 * phoenix
 */
public class Serializers {

    private static final Gson GSON = new Gson();

    public static final ISerializeAdapter<Photo> PHOTOS_SERIALIZER = new ISerializeAdapter<Photo>() {
        @Override
        public Photo deserialize(String raw) {
            return GSON.fromJson(raw, Photo.class);
        }

        @Override
        public String serialize(Photo data) {
            return GSON.toJson(data);
        }
    };
}