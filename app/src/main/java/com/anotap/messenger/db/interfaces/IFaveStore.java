package com.anotap.messenger.db.interfaces;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import java.util.List;

import com.anotap.messenger.db.model.entity.FaveLinkEntity;
import com.anotap.messenger.db.model.entity.OwnerEntities;
import com.anotap.messenger.db.model.entity.PhotoEntity;
import com.anotap.messenger.db.model.entity.PostEntity;
import com.anotap.messenger.db.model.entity.UserEntity;
import com.anotap.messenger.db.model.entity.VideoEntity;
import com.anotap.messenger.model.criteria.FavePhotosCriteria;
import com.anotap.messenger.model.criteria.FavePostsCriteria;
import com.anotap.messenger.model.criteria.FaveVideosCriteria;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by hp-dv6 on 28.05.2016.
 * VKMessenger
 */
public interface IFaveStore extends IStore {

    @CheckResult
    Single<List<PostEntity>> getFavePosts(@NonNull FavePostsCriteria criteria);

    @CheckResult
    Completable storePosts(int accountId, List<PostEntity> posts, OwnerEntities owners, boolean clearBeforeStore);

    @CheckResult
    Single<List<FaveLinkEntity>> getFaveLinks(int accountId);

    Completable removeLink(int accountId, String id);

    Completable storeLinks(int accountId, List<FaveLinkEntity> entities, boolean clearBefore);

    @CheckResult
    Completable storeUsers(int accountId, List<UserEntity> users, boolean clearBeforeStore);

    Single<List<UserEntity>> getFaveUsers(int accountId);

    Completable removeUser(int accountId, int userId);

    @CheckResult
    Single<int[]> storePhotos(int accountId, List<PhotoEntity> photos, boolean clearBeforeStore);

    @CheckResult
    Single<List<PhotoEntity>> getPhotos(FavePhotosCriteria criteria);

    @CheckResult
    Single<List<VideoEntity>> getVideos(FaveVideosCriteria criteria);

    @CheckResult
    Single<int[]> storeVideos(int accountId, List<VideoEntity> videos, boolean clearBeforeStore);
}