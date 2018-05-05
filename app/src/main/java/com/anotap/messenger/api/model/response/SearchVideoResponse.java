package com.anotap.messenger.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.anotap.messenger.api.model.VKApiCommunity;
import com.anotap.messenger.api.model.VKApiUser;
import com.anotap.messenger.api.model.VKApiVideo;

/**
 * Created by admin on 03.01.2017.
 * phoenix
 */
public class SearchVideoResponse {

    @SerializedName("count")
    public int count;

    @SerializedName("items")
    public List<VKApiVideo> items;

    @SerializedName("profiles")
    public List<VKApiUser> profiles;

    @SerializedName("groups")
    public List<VKApiCommunity> groups;
}
