package com.anotap.messenger.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.anotap.messenger.api.model.anotap.ProxyResponse;
import com.anotap.messenger.util.ProxyUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.anotap.messenger.model.ProxyConfig;
import com.anotap.messenger.util.Objects;
import com.anotap.messenger.util.Optional;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static com.anotap.messenger.util.Utils.nonEmpty;

/**
 * Created by admin on 10.07.2017.
 * phoenix
 */
public class ProxySettingsImpl implements IProxySettings {


    private static final Gson GSON = new Gson();

    private final SharedPreferences preferences;
    private final PublishSubject<ProxyConfig> addPublisher;
    private final PublishSubject<ProxyConfig> deletePublisher;
    private final PublishSubject<Optional<ProxyConfig>> activePublisher;
    private ProxyConfig activeProxy;

    public ProxySettingsImpl(Context context) {
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.addPublisher = PublishSubject.create();
        this.deletePublisher = PublishSubject.create();
        this.activePublisher = PublishSubject.create();
    }


    @Override
    public void put(String address, int port) {
        int id = generateNextId();

        ProxyConfig config = new ProxyConfig(id, address, port);
        put(config);
    }

    private void put(ProxyConfig config) {
        Set<String> set = new HashSet<>(preferences.getStringSet(KEY_LIST, new HashSet<>(0)));
        set.add(GSON.toJson(config));

        preferences.edit()
                .putStringSet(KEY_LIST, set)
                .apply();

        addPublisher.onNext(config);
    }

    @Override
    public void put(String address, int port, String username, String pass) {
        int id = generateNextId();

        ProxyConfig config = new ProxyConfig(id, address, port).setAuth(username, pass);
        put(config);
    }

    @Override
    public Observable<ProxyConfig> observeAdding() {
        return addPublisher;
    }

    @Override
    public Observable<ProxyConfig> observeRemoving() {
        return deletePublisher;
    }

    @Override
    public Observable<Optional<ProxyConfig>> observeActive() {
        return activePublisher;
    }

    @Override
    public List<ProxyConfig> getAll() {
        Set<String> set = preferences.getStringSet(KEY_LIST, new HashSet<>(0));
        List<ProxyConfig> configs = new ArrayList<>(set.size());
        for (String s : set) {
            configs.add(GSON.fromJson(s, ProxyConfig.class));
        }

        return configs;
    }

    @Override
    public ProxyConfig getActiveProxy() {
        boolean active = preferences.getBoolean(KEY_ACTIVE, false);
        if (activeProxy == null) {
            ProxyResponse proxyResponse = ProxyUtil.getProxyFromApiSync();
            activeProxy = new ProxyConfig(0, proxyResponse.getProxy().getIp(), proxyResponse.getProxy().getPort());
//            activeProxy = new ProxyConfig(0, "95.213.236.188", 3128);
        }
        return active ? activeProxy : null;
    }

    @Override
    public void setActive(ProxyConfig config) {
        Log.e("SET PROXY", config.getAddress());
        activeProxy = config;
//        preferences.edit()
//                .putString(KEY_ACTIVE, Objects.isNull(config) ? null : GSON.toJson(config))
//                .apply();
//
//        activePublisher.onNext(Optional.wrap(config));
    }

    @Override
    public void delete(ProxyConfig config) {
        Set<String> set = new HashSet<>(preferences.getStringSet(KEY_LIST, new HashSet<>(0)));
        set.remove(GSON.toJson(config));

        preferences.edit()
                .putStringSet(KEY_LIST, set)
                .apply();

        deletePublisher.onNext(config);
    }

    private int generateNextId() {
        int next = preferences.getInt(KEY_NEXT_ID, 1);

        preferences.edit()
                .putInt(KEY_NEXT_ID, next + 1)
                .apply();
        return next;
    }
}