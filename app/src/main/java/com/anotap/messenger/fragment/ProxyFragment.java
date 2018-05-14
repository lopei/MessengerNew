package com.anotap.messenger.fragment;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anotap.messenger.Injection;
import com.anotap.messenger.R;
import com.anotap.messenger.api.model.anotap.ProxyResponse;
import com.anotap.messenger.api.util.RxUtil;
import com.anotap.messenger.fragment.base.BaseFragment;
import com.anotap.messenger.model.ProxyConfig;
import com.anotap.messenger.settings.IProxySettings;
import com.anotap.messenger.util.ProxyUtil;
import com.anotap.messenger.util.Utils;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ProxyFragment extends BaseFragment {
    private static final int SPLASH_DELAY = 3000;
    private static final int PROXY_DELAY = 1000;
    private static final int MINOR_DELAY = 250;

    private View.OnClickListener listener;
    private SwipeRefreshLayout refresh;

    private ImageView iconWorld, iconEagle;
    private TextView textConnection;
    private View containerButtons;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proxy, container, false);
        refresh = view.findViewById(R.id.refresh);
        refresh.setEnabled(false);

        iconEagle = view.findViewById(R.id.iconEagle);
        iconWorld = view.findViewById(R.id.iconWorld);
        containerButtons = view.findViewById(R.id.containerButtons);
        textConnection = view.findViewById(R.id.textConnection);

        ViewGroup mainContainer = view.findViewById(R.id.mainContainer);


        LayoutTransition layoutTransition = mainContainer.getLayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);

        selectProxy();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IProxySettings.setProxyActive(true, getContext());
                refresh.setRefreshing(true);
                updateProxyFromApi();


            }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IProxySettings.setProxyActive(false, getContext());
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        });

        return view;
    }

    private void updateProxyFromApi() {
        ProxyUtil.updateProxyFromApi(new ProxyUtil.UpdateProxyListener() {
            @Override
            public void onSuccess(ProxyResponse proxyResponse) {
                refresh.setRefreshing(false);
                if (listener != null) {
                    listener.onClick(null);
                }
            }

            @Override
            public void onError(Throwable throwable) {
                refresh.setRefreshing(false);
                Utils.showRedTopToast(getActivity(), R.string.error_retrieving_proxy);
            }
        });
    }

    private void selectProxy() {

        if (!IProxySettings.getProxyActive(getContext())) {
            animateViews();
        } else {
            RxUtil.delayedConsumer(PROXY_DELAY, new Consumer<Long>() {
                @Override
                public void accept(Long aLong) throws Exception {
                    updateProxyFromApi();
                }
            });
        }

    }

    private void animateViews() {

        Observable.timer(SPLASH_DELAY, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.newThread())
                .observeOn(Injection.provideMainThreadScheduler())
                .flatMap(new Function<Long, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(Long aLong) throws Exception {
                        animateVisibility(iconEagle, View.GONE);
                        return Observable.timer(MINOR_DELAY, TimeUnit.MILLISECONDS);
                    }
                })
                .flatMap(new Function<Long, ObservableSource<Long>>() {
                    @Override
                    public ObservableSource<Long> apply(Long aLong) throws Exception {
                        animateVisibility(iconWorld, View.VISIBLE);
                        animateVisibility(textConnection, View.VISIBLE);
                        return Observable.timer(MINOR_DELAY, TimeUnit.MILLISECONDS);
                    }
                })
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        animateVisibility(containerButtons, View.VISIBLE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });

    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    private void animateVisibility(View view, int visibility) {
        RxUtil.mainThreadConsumer(view, new Consumer<View>() {
            @Override
            public void accept(View view) throws Exception {
                view.setVisibility(visibility);
            }
        });
    }
}
