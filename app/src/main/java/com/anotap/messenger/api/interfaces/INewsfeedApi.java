package com.anotap.messenger.api.interfaces;

import android.support.annotation.CheckResult;

import java.util.Collection;

import com.anotap.messenger.api.model.Items;
import com.anotap.messenger.api.model.VkApiFeedList;
import com.anotap.messenger.api.model.response.NewsfeedCommentsResponse;
import com.anotap.messenger.api.model.response.NewsfeedResponse;
import com.anotap.messenger.api.model.response.NewsfeedSearchResponse;
import io.reactivex.Single;

/**
 * Created by admin on 03.01.2017.
 * phoenix
 */
public interface INewsfeedApi {

    @CheckResult
    Single<Items<VkApiFeedList>> getLists(Collection<Integer> listIds);

    @CheckResult
    Single<NewsfeedSearchResponse> search(String query, Boolean extended, Integer count,
                                          Double latitude, Double longitude, Long startTime,
                                          Long endTime, String startFrom, String fields);
    @CheckResult
    Single<NewsfeedCommentsResponse> getComments(Integer count, String filters, String reposts,
                                                 Long startTime, Long endTime, Integer lastCommentsCount,
                                                 String startFrom, String fields);

    @CheckResult
    Single<NewsfeedResponse> get(String filters, Boolean returnBanned, Long startTime, Long endTime,
                                 Integer maxPhotoCount, String sourceIds, String startFrom, Integer count, String fields);

}
