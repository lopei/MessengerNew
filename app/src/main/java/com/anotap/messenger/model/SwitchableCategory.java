package com.anotap.messenger.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by admin on 08.10.2016.
 * phoenix
 */

@IntDef({SwitchableCategory.FEED,
        SwitchableCategory.DIALOGS,
        SwitchableCategory.FEEDBACK,
        SwitchableCategory.FRIENDS,
        SwitchableCategory.GROUPS,
        SwitchableCategory.PHOTOS,
        SwitchableCategory.VIDEOS,
        SwitchableCategory.DOCS,
        SwitchableCategory.BOOKMARKS,
        SwitchableCategory.SEARCH,
        SwitchableCategory.NEWSFEED_COMMENTS/*,
        SwitchableCategory.MUSIC*/})
@Retention(RetentionPolicy.SOURCE)
public @interface SwitchableCategory {
    int FEED = 1;
    int DIALOGS = 2;
    int FEEDBACK = 3;
    int FRIENDS = 4;
    int GROUPS = 5;
    int PHOTOS = 6;
    int VIDEOS = 7;
    int DOCS = 8;
    int BOOKMARKS = 9;
    int SEARCH = 10;
    int NEWSFEED_COMMENTS = 11;
    /*int MUSIC = 12;*/
}
