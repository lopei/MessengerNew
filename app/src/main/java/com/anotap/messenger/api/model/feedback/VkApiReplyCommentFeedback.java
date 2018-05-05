package com.anotap.messenger.api.model.feedback;

import com.anotap.messenger.api.model.Commentable;
import com.anotap.messenger.api.model.VKApiComment;

/**
 * Created by ruslan.kolbasa on 09.12.2016.
 * phoenix
 */
public class VkApiReplyCommentFeedback extends VkApiBaseFeedback {

    public Commentable comments_of;

    public VKApiComment own_comment;

    public VKApiComment feedback_comment;
}
