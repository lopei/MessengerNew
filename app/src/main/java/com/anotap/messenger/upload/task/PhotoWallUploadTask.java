package com.anotap.messenger.upload.task;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import com.anotap.messenger.Injection;
import com.anotap.messenger.api.Apis;
import com.anotap.messenger.api.WeakPercentageListener;
import com.anotap.messenger.api.model.VKApiPhoto;
import com.anotap.messenger.api.model.server.UploadServer;
import com.anotap.messenger.api.model.upload.UploadPhotoToWallDto;
import com.anotap.messenger.db.AttachToType;
import com.anotap.messenger.domain.IAttachmentsRepository;
import com.anotap.messenger.domain.mappers.Dto2Model;
import com.anotap.messenger.model.Photo;
import com.anotap.messenger.upload.BaseUploadResponse;
import com.anotap.messenger.upload.Method;
import com.anotap.messenger.upload.UploadCallback;
import com.anotap.messenger.upload.UploadDestination;
import com.anotap.messenger.upload.UploadObject;
import com.anotap.messenger.util.IOUtils;
import com.anotap.messenger.util.Objects;
import retrofit2.Call;

import static com.anotap.messenger.util.Utils.safeCountOf;

public class PhotoWallUploadTask extends AbstractUploadTask<PhotoWallUploadTask.Response> {

    private Integer userId;
    private Integer groupId;

    public PhotoWallUploadTask(@NonNull Context context, @NonNull UploadCallback callback,
                               @NonNull UploadObject uploadObject, @Nullable UploadServer server) {
        super(context, callback, uploadObject, server);
        int subjectOwnerId = uploadObject.getDestination().getOwnerId();
        userId = subjectOwnerId > 0 ? subjectOwnerId : null;
        groupId = subjectOwnerId < 0 ? Math.abs(subjectOwnerId) : null;
    }

    @Override
    protected Response doUpload(@Nullable UploadServer server, @NonNull UploadObject uploadObject) throws CancelException {
        int accountId = uploadObject.getAccountId();

        Response result = new Response();

        InputStream is = null;
        try {
            is = openStream(getContext(), uploadObject.getFileUri(), uploadObject.getSize());

            if (server == null) {
                server = Apis.get()
                        .vkDefault(accountId)
                        .photos()
                        .getWallUploadServer(groupId)
                        .blockingGet();

                result.setServer(server);
            }

            assertCancel(this);

            String serverUrl = server.getUrl();

            Call<UploadPhotoToWallDto> call = Apis.get()
                    .uploads()
                    .uploadPhotoToWall(serverUrl, is, new WeakPercentageListener(this));

            registerCall(call);

            UploadPhotoToWallDto entity;

            try {
                entity = call.execute().body();
            } catch (Exception e) {
                result.setError(e);
                return result;
            } finally {
                unregisterCall(call);
            }

            assertCancel(this);

            List<VKApiPhoto> photos = Apis.get()
                    .vkDefault(accountId)
                    .photos()
                    .saveWallPhoto(userId, groupId, entity.photo, entity.server, entity.hash, null, null, null)
                    .blockingGet();

            assertCancel(this);

            if (safeCountOf(photos) == 1) {
                VKApiPhoto dto = photos.get(0);

                if(uploadObject.isAutoCommit()){
                    attachIntoDatabase(dto);
                }

                result.photo = Dto2Model.transform(dto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(e);
        } finally {
            IOUtils.closeStreamQuietly(is);
        }

        return result;
    }

    private void attachIntoDatabase(@NonNull VKApiPhoto dto) {
        Photo photo = Dto2Model.transform(dto);

        int aid = uploadObject.getAccountId();
        UploadDestination dest = uploadObject.getDestination();

        IAttachmentsRepository attachmentsRepository = Injection.provideAttachmentsRepository();

        switch (dest.getMethod()) {
            case Method.PHOTO_TO_COMMENT:
                attachmentsRepository
                        .attach(aid, AttachToType.COMMENT, dest.getId(), Collections.singletonList(photo))
                        .blockingAwait();
                break;

            case Method.PHOTO_TO_WALL:
                attachmentsRepository
                        .attach(aid, AttachToType.POST, dest.getId(), Collections.singletonList(photo))
                        .blockingAwait();
                break;
        }
    }

    public static class Response extends BaseUploadResponse {

        public Photo photo;

        @Override
        public boolean isSuccess() {
            return Objects.nonNull(photo);
        }
    }
}