package com.anotap.messenger.media.gif;

import android.support.annotation.NonNull;

import com.anotap.messenger.model.ProxyConfig;
import com.anotap.messenger.settings.IProxySettings;
import com.anotap.messenger.settings.ISettings;

import static com.anotap.messenger.util.Objects.nonNull;

/**
 * Created by admin on 13.08.2017.
 * phoenix
 */
public class AppGifPlayerFactory implements IGifPlayerFactory {

    private final IProxySettings proxySettings;
    private final ISettings.IOtherSettings otherSettings;

    public AppGifPlayerFactory(IProxySettings proxySettings, ISettings.IOtherSettings otherSettings) {
        this.proxySettings = proxySettings;
        this.otherSettings = otherSettings;
    }

    @Override
    public IGifPlayer createGifPlayer(@NonNull String url) {
        ProxyConfig config = proxySettings.getActiveProxy();

        if (nonNull(config) || otherSettings.isForceExoplayer()) {
            return new ExoGifPlayer(url, config);
        } else {
            return new DefaultGifPlayer(url);
        }
    }
}