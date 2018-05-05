package com.anotap.messenger.view;

import java.lang.ref.WeakReference;

import com.anotap.messenger.util.Action;

/**
 * Created by admin on 03.06.2017.
 * phoenix
 */
public class WeakRunnable<T> implements Runnable {

    private final WeakReference<T> reference;
    private final Action<T> action;

    public WeakRunnable(T reference, Action<T> action) {
        this.reference = new WeakReference<>(reference);
        this.action = action;
    }

    //protected abstract void run(T object);

    @Override
    public final void run() {
        T object = reference.get();
        if(object != null){
            //run(object);
            action.call(object);
        }
    }
}
