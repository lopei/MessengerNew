package com.anotap.messenger.api.model.response;

import com.google.gson.annotations.SerializedName;

import com.anotap.messenger.api.model.Items;
import com.anotap.messenger.api.model.VKApiUser;

/**
 * Created by admin on 09.01.2017.
 * phoenix
 */
public class FriendsWithCountersResponse {

    @SerializedName("friends")
    public Items<VKApiUser> friends;

    @SerializedName("counters")
    public VKApiUser.Counters counters;
}
