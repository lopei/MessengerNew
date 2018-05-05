package com.anotap.messenger.db.interfaces;

import android.graphics.Bitmap;

import java.util.List;

import com.anotap.messenger.model.LocalImageAlbum;
import com.anotap.messenger.model.LocalPhoto;
import io.reactivex.Single;

/**
 * Created by admin on 03.10.2016.
 * phoenix
 */
public interface ILocalMediaStore extends IStore {

    Single<List<LocalPhoto>> getPhotos(long albumId);

    Single<List<LocalImageAlbum>> getImageAlbums();

    Bitmap getImageThumbnail(long imageId);

    //Single<List<LocalVideo>> getVideos();

    //Bitmap getVideoThumbnail(long videoId);
}
