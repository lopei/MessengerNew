package com.anotap.messenger.push;

import android.content.Context;

import java.io.IOException;

import com.anotap.messenger.Constants;
import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Created by admin on 07.10.2017.
 * Phoenix-for-VK
 */
public class GcmTokenProvider implements IGcmTokenProvider {

    private final Context app;

    public GcmTokenProvider(Context context) {
        this.app = context.getApplicationContext();
    }

    @Override
    public String getToken() throws IOException {
        return FirebaseInstanceId.getInstance().getToken();
    }
}