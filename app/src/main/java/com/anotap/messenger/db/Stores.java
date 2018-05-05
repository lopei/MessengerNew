package com.anotap.messenger.db;

import com.anotap.messenger.Injection;
import com.anotap.messenger.db.interfaces.IStores;

public class Stores {

    public static IStores getInstance(){
        return Injection.provideStores();
    }

}
