package com.anotap.messenger.media.voice;

import android.content.Context;
import android.support.annotation.NonNull;

import com.anotap.messenger.model.ProxyConfig;
import com.anotap.messenger.settings.IProxySettings;
import com.anotap.messenger.settings.ISettings;

import static com.anotap.messenger.util.Objects.isNull;

/**
 * Created by r.kolbasa on 27.11.2017.
 * Phoenix-for-VK
 */
public class VoicePlayerFactory implements IVoicePlayerFactory {

    private final Context app;
    private final IProxySettings proxySettings;
    private final ISettings.IOtherSettings otherSettings;

    public VoicePlayerFactory(Context context, IProxySettings proxySettings, ISettings.IOtherSettings otherSettings) {
        this.app = context.getApplicationContext();
        this.proxySettings = proxySettings;
        this.otherSettings = otherSettings;
    }

    @NonNull
    @Override
    public IVoicePlayer createPlayer() {
        ProxyConfig config = proxySettings.getActiveProxy();

        if (isNull(config) && !otherSettings.isForceExoplayer()) {
            return new DefaultVoicePlayer();
        } else {
            return new ExoVoicePlayer(app, config);
        }
    }
}