package com.anotap.messenger.plugins;

import java.util.List;

import com.anotap.messenger.model.Audio;
import io.reactivex.Single;

/**
 * Created by admin on 2/3/2018.
 * Phoenix-for-VK
 */
public interface IAudioPluginConnector {
    Single<List<Audio>> get(int ownerId, int offset);
    Single<String> findAudioUrl(int audioId, int ownerId);
    boolean isPluginAvailable();
}