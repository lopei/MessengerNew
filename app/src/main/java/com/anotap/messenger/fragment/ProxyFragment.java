package com.anotap.messenger.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anotap.messenger.Injection;
import com.anotap.messenger.R;
import com.anotap.messenger.api.AnotapWebService;
import com.anotap.messenger.api.model.anotap.ProxyResponse;
import com.anotap.messenger.api.util.RxUtil;
import com.anotap.messenger.fragment.base.BaseFragment;
import com.anotap.messenger.model.ProxyConfig;
import com.anotap.messenger.settings.ProxySettingsImpl;
import com.anotap.messenger.util.Utils;

import rx.functions.Action1;

public class ProxyFragment extends BaseFragment {
    private View.OnClickListener listener;
    private SwipeRefreshLayout refresh;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_proxy, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refresh = getView().findViewById(R.id.refresh);
        refresh.setEnabled(false);

        getView().findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProxySettingsImpl.setProxyActive(true, getContext());
                refresh.setRefreshing(true);
                RxUtil.networkAction(AnotapWebService.service.getProxy(), new Action1<ProxyResponse>() {
                    @Override
                    public void call(ProxyResponse proxyResponse) {
                        Injection.provideProxySettings().setActive(new ProxyConfig(0, proxyResponse.getProxy().getIp(), proxyResponse.getProxy().getPort()));
                        refresh.setRefreshing(false);
                        if (listener != null) {
                            listener.onClick(v);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        refresh.setRefreshing(false);
                        Utils.showRedTopToast(getActivity(), R.string.error_retrieving_proxy);
                    }
                });



            }
        });
        getView().findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProxySettingsImpl.setProxyActive(false, getContext());
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        });
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
