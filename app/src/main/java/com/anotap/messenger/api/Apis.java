package com.anotap.messenger.api;

import com.anotap.messenger.Injection;
import com.anotap.messenger.api.interfaces.INetworker;

/**
 * Created by ruslan.kolbasa on 29.12.2016.
 * phoenix
 */
public class Apis {

    public static INetworker get(){
        return Injection.provideNetworkInterfaces();
    }

}
