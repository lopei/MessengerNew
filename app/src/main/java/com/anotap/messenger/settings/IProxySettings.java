package com.anotap.messenger.settings;

import android.content.Context;

import java.util.List;

import com.anotap.messenger.model.ProxyConfig;
import com.anotap.messenger.util.Optional;

import io.reactivex.Observable;

/**
 * Created by admin on 10.07.2017.
 * phoenix
 */
public interface IProxySettings {
    String PREF_NAME = "proxy_settings";
    String KEY_NEXT_ID = "next_id";
    String KEY_LIST = "list";
    String KEY_ACTIVE = "active_proxy";
    static void setProxyActive(boolean active, Context context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().putBoolean(KEY_ACTIVE, active).apply();
    }

    static boolean getProxyActive(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getBoolean(KEY_ACTIVE, false);
    }

    void put(String address, int port);

    void put(String address, int port, String username, String pass);

    Observable<ProxyConfig> observeAdding();

    Observable<ProxyConfig> observeRemoving();

    Observable<Optional<ProxyConfig>> observeActive();

    List<ProxyConfig> getAll();

    ProxyConfig getActiveProxy();

    void setActive(ProxyConfig config);

    void delete(ProxyConfig config);
}