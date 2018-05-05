package com.anotap.messenger.db.interfaces;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import java.util.List;

import com.anotap.messenger.db.model.entity.PhotoAlbumEntity;
import com.anotap.messenger.model.criteria.PhotoAlbumsCriteria;
import com.anotap.messenger.util.Optional;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by ruslan.kolbasa on 29.11.2016.
 * phoenix
 */
public interface IPhotoAlbumsStore extends IStore {

    @CheckResult
    Single<Optional<PhotoAlbumEntity>> findAlbumById(int accountId, int ownerId, int albumId);

    @CheckResult
    Single<List<PhotoAlbumEntity>> findAlbumsByCriteria(@NonNull PhotoAlbumsCriteria criteria);

    @CheckResult
    Completable store(int accountId, int ownerId, @NonNull List<PhotoAlbumEntity> albums, boolean clearBefore);

    @CheckResult
    Completable removeAlbumById(int accountId, int ownerId, int albumId);
}