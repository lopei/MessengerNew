package com.anotap.messenger.api.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import com.anotap.messenger.api.model.VKApiMessage;

/**
 * Created by ruslan.kolbasa on 28.12.2016.
 * phoenix
 */
public class MessageHistoryResponse {

    @SerializedName("items")
    public List<VKApiMessage> messages;

    @SerializedName("count")
    public int count;

    @SerializedName("unread")
    public int unread;

}
