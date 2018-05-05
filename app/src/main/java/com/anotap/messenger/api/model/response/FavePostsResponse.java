package com.anotap.messenger.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.anotap.messenger.api.model.VKApiCommunity;
import com.anotap.messenger.api.model.VKApiPost;
import com.anotap.messenger.api.model.VKApiUser;

public class FavePostsResponse {

    @SerializedName("count")
    public int count;

    @SerializedName("items")
    public List<VKApiPost> posts;

    @SerializedName("profiles")
    public List<VKApiUser> profiles;

    @SerializedName("groups")
    public List<VKApiCommunity> groups;
}
