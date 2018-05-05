package com.anotap.messenger.settings;

import com.anotap.messenger.Injection;

/**
 * Created by admin on 01.12.2016.
 * phoenix
 */
public class Settings {

    public static ISettings get(){
        return Injection.provideSettings();
    }

}
