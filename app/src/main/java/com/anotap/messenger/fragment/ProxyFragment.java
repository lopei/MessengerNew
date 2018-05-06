package com.anotap.messenger.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anotap.messenger.R;
import com.anotap.messenger.fragment.base.BaseFragment;
import com.anotap.messenger.settings.ProxySettingsImpl;

public class ProxyFragment extends BaseFragment {
    private View.OnClickListener listener;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_proxy, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProxySettingsImpl.setProxyActive(true, getContext());
                if(listener != null) {
                    listener.onClick(v);
                }
            }
        });
        getView().findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProxySettingsImpl.setProxyActive(false, getContext());
                if(listener != null) {
                    listener.onClick(v);
                }
            }
        });
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
