package com.anotap.messenger.api.model.feedback;

import com.anotap.messenger.api.model.Likeable;

/**
 * Created by ruslan.kolbasa on 09.12.2016.
 * phoenix
 */
public class VkApiLikeFeedback extends VkApiBaseFeedback {

    public UserArray users;
    public Likeable liked;

}