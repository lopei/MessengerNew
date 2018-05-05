package com.anotap.messenger.api.impl;

import android.support.annotation.NonNull;

import java.io.InputStream;

import com.anotap.messenger.api.IUploadRetrofitProvider;
import com.anotap.messenger.api.PercentageListener;
import com.anotap.messenger.api.interfaces.IUploadApi;
import com.anotap.messenger.api.model.upload.UploadDocDto;
import com.anotap.messenger.api.model.upload.UploadOwnerPhotoDto;
import com.anotap.messenger.api.model.upload.UploadPhotoToAlbumDto;
import com.anotap.messenger.api.model.upload.UploadPhotoToMessageDto;
import com.anotap.messenger.api.model.upload.UploadPhotoToWallDto;
import com.anotap.messenger.api.services.IUploadService;
import com.anotap.messenger.api.util.ProgressRequestBody;
import com.anotap.messenger.util.Objects;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import retrofit2.Call;

/**
 * Created by Ruslan Kolbasa on 31.07.2017.
 * phoenix
 */
public class UploadApi implements IUploadApi {

    private final IUploadRetrofitProvider provider;

    public UploadApi(IUploadRetrofitProvider provider) {
        this.provider = provider;
    }

    private IUploadService service(){
        return provider.provideUploadRetrofit().blockingGet().create(IUploadService.class);
    }

    private static ProgressRequestBody.UploadCallbacks wrapPercentageListener(final PercentageListener listener){
        return percentage -> {
            if(Objects.nonNull(listener)){
                listener.onProgressChanged(percentage);
            }
        };
    }

    @Override
    public Call<UploadDocDto> uploadDocument(String server, String filename, @NonNull InputStream is, PercentageListener listener) {
        ProgressRequestBody body = new ProgressRequestBody(is, wrapPercentageListener(listener), MediaType.parse("*/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", filename, body);
        return service().uploadDocument(server, part);
    }

    @Override
    public Call<UploadOwnerPhotoDto> uploadOwnerPhoto(String server, @NonNull InputStream is, PercentageListener listener) {
        ProgressRequestBody body = new ProgressRequestBody(is, wrapPercentageListener(listener), MediaType.parse("image/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("photo", "photo.jpg", body);
        return service().uploadOwnerPhoto(server, part);
    }

    @Override
    public Call<UploadPhotoToWallDto> uploadPhotoToWall(String server, @NonNull InputStream is, PercentageListener listener) {
        ProgressRequestBody body = new ProgressRequestBody(is, wrapPercentageListener(listener), MediaType.parse("image/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("photo", "photo.jpg", body);
        return service().uploadPhotoToWall(server, part);
    }

    @Override
    public Call<UploadPhotoToMessageDto> uploadPhotoToMessage(String server, @NonNull InputStream is, PercentageListener listener) {
        ProgressRequestBody body = new ProgressRequestBody(is, wrapPercentageListener(listener), MediaType.parse("image/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("photo", "photo.jpg", body);
        return service().uploadPhotoToMessage(server, part);
    }

    @Override
    public Call<UploadPhotoToAlbumDto> uploadPhotoToAlbum(String server, @NonNull InputStream is, PercentageListener listener) {
        ProgressRequestBody body = new ProgressRequestBody(is, wrapPercentageListener(listener), MediaType.parse("image/*"));
        MultipartBody.Part part = MultipartBody.Part.createFormData("file1", "photo.jpg", body);
        return service().uploadPhotoToAlbum(server, part);
    }
}