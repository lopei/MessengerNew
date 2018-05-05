package com.anotap.messenger.upload.task;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.InputStream;

import com.anotap.messenger.Injection;
import com.anotap.messenger.api.Apis;
import com.anotap.messenger.api.WeakPercentageListener;
import com.anotap.messenger.api.model.response.UploadOwnerPhotoResponse;
import com.anotap.messenger.api.model.server.UploadServer;
import com.anotap.messenger.api.model.upload.UploadOwnerPhotoDto;
import com.anotap.messenger.domain.IWalls;
import com.anotap.messenger.model.Post;
import com.anotap.messenger.upload.BaseUploadResponse;
import com.anotap.messenger.upload.UploadCallback;
import com.anotap.messenger.upload.UploadObject;
import com.anotap.messenger.util.IOUtils;
import com.anotap.messenger.util.Objects;
import retrofit2.Call;

public class OwnerPhotoUploadTask extends AbstractUploadTask<OwnerPhotoUploadTask.Response> {

    private int ownerId;
    private final IWalls walls;

    public OwnerPhotoUploadTask(@NonNull Context context, @NonNull UploadCallback callback,
                                @NonNull UploadObject uploadObject, @Nullable UploadServer initialServer) {
        super(context, callback, uploadObject, initialServer);
        this.ownerId = uploadObject.getDestination().getOwnerId();
        this.walls = Injection.provideWalls();
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
                        .getOwnerPhotoUploadServer(ownerId)
                        .blockingGet();

                result.setServer(server);
            }

            assertCancel(this);

            String serverUrl = server.getUrl();

            Call<UploadOwnerPhotoDto> call = Apis.get()
                    .uploads()
                    .uploadOwnerPhoto(serverUrl, is, new WeakPercentageListener(this));

            registerCall(call);

            UploadOwnerPhotoDto entity;

            try {
                entity = call.execute().body();
            } catch (Exception e) {
                result.setError(e);
                return result;
            } finally {
                unregisterCall(call);
            }

            assertCancel(this);

            result.uploadOwnerPhotoResponse = Apis.get()
                    .vkDefault(accountId)
                    .photos()
                    .saveOwnerPhoto(entity.server, entity.hash, entity.photo)
                    .blockingGet();

            assertCancel(this);

            if (result.uploadOwnerPhotoResponse.postId != 0) {
                result.post = loadFullPostAndLoad(uploadObject.getAccountId(), result.uploadOwnerPhotoResponse.postId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.setError(e);
        } finally {
            IOUtils.closeStreamQuietly(is);
        }

        return result;
    }

    private Post loadFullPostAndLoad(int aid, int resultId) throws Exception {
        return walls.getById(aid, ownerId, resultId).blockingGet();
    }

    public static class Response extends BaseUploadResponse {

        public UploadOwnerPhotoResponse uploadOwnerPhotoResponse;

        public Post post;

        @Override
        public boolean isSuccess() {
            return Objects.nonNull(uploadOwnerPhotoResponse);
        }
    }
}