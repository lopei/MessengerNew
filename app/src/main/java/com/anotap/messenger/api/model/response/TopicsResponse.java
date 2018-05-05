package com.anotap.messenger.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.anotap.messenger.api.model.VKApiCommunity;
import com.anotap.messenger.api.model.VKApiTopic;
import com.anotap.messenger.api.model.VKApiUser;

public class TopicsResponse {

    @SerializedName("count")
    public int count;

    @SerializedName("items")
    public List<VKApiTopic> items;

    @SerializedName("default_order")
    public int defaultOrder;

    @SerializedName("can_add_topics")
    public int canAddTopics;

    @SerializedName("profiles")
    public List<VKApiUser> profiles;

    @SerializedName("groups")
    public List<VKApiCommunity> groups;
}