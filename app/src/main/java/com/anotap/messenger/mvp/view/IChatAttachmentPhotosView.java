package com.anotap.messenger.mvp.view;

import android.support.annotation.NonNull;

import com.anotap.messenger.model.Photo;
import com.anotap.messenger.model.TmpSource;

/**
 * Created by admin on 29.03.2017.
 * phoenix
 */
public interface IChatAttachmentPhotosView extends IBaseChatAttachmentsView<Photo> {
    void goToTempPhotosGallery(int accountId, @NonNull TmpSource source, int index);
}