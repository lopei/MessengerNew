package com.anotap.messenger.domain;

import java.util.List;

import com.anotap.messenger.model.EndlessData;
import com.anotap.messenger.model.FaveLink;
import com.anotap.messenger.model.Photo;
import com.anotap.messenger.model.Post;
import com.anotap.messenger.model.User;
import com.anotap.messenger.model.Video;
import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by Ruslan Kolbasa on 14.07.2017.
 * phoenix
 */
public interface IFaveInteractor {
    Single<List<Post>> getPosts(int accountId, int count, int offset);
    Single<List<Post>> getCachedPosts(int accountId);

    Single<List<Photo>> getCachedPhotos(int accountId);
    Single<List<Photo>> getPhotos(int accountId, int count, int offset);

    Single<List<Video>> getCachedVideos(int accountId);
    Single<List<Video>> getVideos(int accountId, int count, int offset);

    Single<List<User>> getCachedUsers(int accountId);
    Single<EndlessData<User>> getUsers(int accountId, int count, int offset);
    Completable removeUser(int accountId, int userId);

    Single<List<FaveLink>> getCachedLinks(int accountId);
    Single<EndlessData<FaveLink>> getLinks(int accountId, int count, int offset);
    Completable removeLink(int accountId, String id);

    Completable addUser(int accountId, int userId);
}