package com.anotap.messenger.api.model.feedback;

import com.anotap.messenger.api.model.VKApiComment;

/**
 * Created by ruslan.kolbasa on 09.12.2016.
 * phoenix
 */
public abstract class VkApiBaseFeedback {

    public String type;
    public long date;

    public VKApiComment reply;


}
