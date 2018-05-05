package com.anotap.messenger.api.services;

import com.anotap.messenger.api.model.response.BaseResponse;
import com.anotap.messenger.api.model.response.ResolveDomailResponse;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by admin on 09.01.2017.
 * phoenix
 */
public interface IUtilsService {

    @FormUrlEncoded
    @POST("utils.resolveScreenName")
    Single<BaseResponse<ResolveDomailResponse>> resolveScreenName(@Field("screen_name") String screenName);
}
