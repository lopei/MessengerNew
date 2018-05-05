package com.anotap.messenger.db.impl;

import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import com.anotap.messenger.db.MapFunction;
import com.anotap.messenger.db.interfaces.Cancelable;
import com.anotap.messenger.db.interfaces.IStore;
import com.anotap.messenger.db.interfaces.IStores;
import com.anotap.messenger.db.model.entity.AttachmentsEntity;
import com.anotap.messenger.db.model.entity.EntityWrapper;
import com.anotap.messenger.db.serialize.AttachmentsDboAdapter;
import com.anotap.messenger.db.serialize.DboWrapperAdapter;
import com.anotap.messenger.db.serialize.UriSerializer;

import static com.anotap.messenger.util.Objects.nonNull;
import static com.anotap.messenger.util.Utils.safeCountOf;

public class AbsStore implements IStore {

    static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Uri.class, new UriSerializer())
            .registerTypeAdapter(AttachmentsEntity.class, new AttachmentsDboAdapter())
            .registerTypeAdapter(EntityWrapper.class, new DboWrapperAdapter())
            .serializeSpecialFloatingPointValues() // for test
            .create();

    private final AppStores mRepositoryContext;

    public AbsStore(@NonNull AppStores base) {
        this.mRepositoryContext = base;
    }

    @Override
    public IStores getStores() {
        return mRepositoryContext;
    }

    @NonNull
    public Context getContext() {
        return mRepositoryContext.getApplicationContext();
    }


    static <T> List<T> mapAll(Cursor cursor, MapFunction<T> function, boolean close){
        List<T> data = new ArrayList<>(safeCountOf(cursor));
        if (nonNull(cursor)) {
            while (cursor.moveToNext()) {
                data.add(function.map(cursor));
            }

            if(close){
                cursor.close();
            }
        }

        return data;
    }

    static <T> List<T> mapAll(Cancelable cancelable, Cursor cursor, MapFunction<T> function, boolean close){
        List<T> data = new ArrayList<>(safeCountOf(cursor));
        if (nonNull(cursor)) {
            while (cursor.moveToNext()) {
                if (cancelable.isOperationCancelled()) {
                    break;
                }

                data.add(function.map(cursor));
            }

            if(close){
                cursor.close();
            }
        }

        return data;
    }

    static int extractId(ContentProviderResult result) {
        return Integer.parseInt(result.uri.getPathSegments().get(1));
    }

    protected ContentResolver getContentResolver(){
        return mRepositoryContext.getContentResolver();
    }

    static <T> int addToListAndReturnIndex(@NonNull List<T> target, @NonNull T item) {
        target.add(item);
        return target.size() - 1;
    }
}