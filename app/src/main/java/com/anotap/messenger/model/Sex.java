package com.anotap.messenger.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by admin on 08.10.2016.
 * phoenix
 */
@IntDef({Sex.MAN, Sex.WOMAN, Sex.UNKNOWN})
@Retention(RetentionPolicy.SOURCE)
public @interface Sex {
    int MAN = 2;
    int WOMAN = 1;
    int UNKNOWN = 0;
}