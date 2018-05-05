package com.anotap.messenger.settings;

import java.util.List;

import com.anotap.messenger.model.ProxyConfig;
import com.anotap.messenger.util.Optional;
import io.reactivex.Observable;

/**
 * Created by admin on 10.07.2017.
 * phoenix
 */
public interface IProxySettings {
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