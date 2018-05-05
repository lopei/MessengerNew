package com.anotap.messenger.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.anotap.messenger.api.model.VKApiComment;
import com.anotap.messenger.api.model.VKApiCommunity;
import com.anotap.messenger.api.model.VKApiPoll;
import com.anotap.messenger.api.model.VKApiUser;

/**
 * Created by admin on 28.12.2016.
 * phoenix
 */
public class CustomCommentsResponse {

    // Parse manually in CustomCommentsResponseAdapter

    public Main main;

    public Integer firstId;

    public Integer lastId;

    public Integer admin_level;

    public static class Main {

        @SerializedName("count")
        public int count;

        @SerializedName("items")
        public List<VKApiComment> comments;

        @SerializedName("profiles")
        public List<VKApiUser> profiles;

        @SerializedName("groups")
        public List<VKApiCommunity> groups;

        @SerializedName("poll")
        public VKApiPoll poll;
    }

}
