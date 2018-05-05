package com.anotap.messenger.view;

import android.support.annotation.DrawableRes;

import com.anotap.messenger.R;
import com.anotap.messenger.model.VideoPlatform;

/**
 * Created by admin on 08.05.2017.
 * phoenix
 */
public class VideoServiceIcons {

    @DrawableRes
    public static Integer getIconByType(String platfrom) {
        if(platfrom == null){
            return null;
        }

        switch (platfrom) {
            default:
                return null;
            case VideoPlatform.COUB:
                return R.drawable.logo_coub;
            case VideoPlatform.VIMEO:
                return R.drawable.logo_vimeo;
            case VideoPlatform.YOUTUBE:
                return R.drawable.logo_youtube_trans;
            case VideoPlatform.RUTUBE:
                return R.drawable.logo_rutube;
        }
    }
}