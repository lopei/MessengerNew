package com.anotap.messenger.db.interfaces;

import com.anotap.messenger.crypt.KeyLocationPolicy;

/**
 * Created by ruslan.kolbasa on 25.11.2016.
 * phoenix
 */
public interface IStores {

    ITempDataStore tempStore();

    IVideoAlbumsStore videoAlbums();

    IVideoStore videos();

    IAttachmentsStore attachments();

    IKeysStore keys(@KeyLocationPolicy int policy);

    ILocalMediaStore localPhotos();

    IFeedbackStore notifications();

    IDialogsStore dialogs();

    IMessagesStore messages();

    IWallStore wall();

    IFaveStore fave();

    IPhotosStore photos();

    IRelativeshipStore relativeship();

    IFeedStore feed();

    IOwnersStore owners();

    ICommentsStore comments();

    IPhotoAlbumsStore photoAlbums();

    ITopicsStore topics();

    IDocsStore docs();

    IStickersStore stickers();

    IUploadQueueStore uploads();

    IDatabaseStore database();
}