package com.anotap.messenger.api.model.feedback;

import com.anotap.messenger.api.model.Commentable;
import com.anotap.messenger.api.model.VKApiComment;

/**
 * Created by ruslan.kolbasa on 09.12.2016.
 * phoenix
 */
public class VkApiLikeCommentFeedback extends VkApiBaseFeedback {

    public UserArray users;

    public VKApiComment comment;

    public Commentable commented;
}
