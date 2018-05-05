package com.anotap.messenger.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.anotap.messenger.fragment.AddProxyFragment;
import com.anotap.messenger.fragment.ProxyManagerFrgament;
import com.anotap.messenger.place.Place;
import com.anotap.messenger.place.PlaceProvider;
import com.anotap.messenger.util.Objects;

/**
 * Created by admin on 10.07.2017.
 * phoenix
 */
public class ProxyManagerActivity extends NoMainActivity implements PlaceProvider {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Objects.isNull(savedInstanceState)){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(getMainContainerViewId(), ProxyManagerFrgament.newInstance())
                    .addToBackStack("proxy-manager")
                    .commit();
        }
    }

    @Override
    public void openPlace(Place place) {
        if(place.type == Place.PROXY_ADD){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(getMainContainerViewId(), AddProxyFragment.newInstance())
                    .addToBackStack("proxy-add")
                    .commit();
        }
    }
}