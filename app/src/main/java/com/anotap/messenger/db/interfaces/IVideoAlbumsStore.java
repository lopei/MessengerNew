package com.anotap.messenger.db.interfaces;

import android.support.annotation.NonNull;

import java.util.List;

import com.anotap.messenger.db.model.entity.VideoAlbumEntity;
import com.anotap.messenger.model.VideoAlbumCriteria;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by admin on 21.11.2016.
 * phoenix
 */
public interface IVideoAlbumsStore extends IStore {
    Single<List<VideoAlbumEntity>> findByCriteria(@NonNull VideoAlbumCriteria criteria);
    Completable insertData(int accountId, int ownerId, @NonNull List<VideoAlbumEntity> data, boolean invalidateBefore);
}