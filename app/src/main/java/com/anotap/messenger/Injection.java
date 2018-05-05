package com.anotap.messenger;

import android.content.Context;

import com.anotap.messenger.api.CaptchaProvider;
import com.anotap.messenger.api.ICaptchaProvider;
import com.anotap.messenger.api.impl.Networker;
import com.anotap.messenger.api.interfaces.INetworker;
import com.anotap.messenger.db.impl.AppStores;
import com.anotap.messenger.db.impl.LogsStore;
import com.anotap.messenger.db.interfaces.ILogsStore;
import com.anotap.messenger.db.interfaces.IStores;
import com.anotap.messenger.domain.IAttachmentsRepository;
import com.anotap.messenger.domain.IBlacklistRepository;
import com.anotap.messenger.domain.IWalls;
import com.anotap.messenger.domain.InteractorFactory;
import com.anotap.messenger.domain.impl.AttachmentsRepository;
import com.anotap.messenger.domain.impl.BlacklistRepository;
import com.anotap.messenger.domain.impl.WallsImpl;
import com.anotap.messenger.media.gif.AppGifPlayerFactory;
import com.anotap.messenger.media.gif.IGifPlayerFactory;
import com.anotap.messenger.media.voice.IVoicePlayerFactory;
import com.anotap.messenger.media.voice.VoicePlayerFactory;
import com.anotap.messenger.push.GcmTokenProvider;
import com.anotap.messenger.push.IDevideIdProvider;
import com.anotap.messenger.push.IGcmTokenProvider;
import com.anotap.messenger.push.IPushRegistrationResolver;
import com.anotap.messenger.push.PushRegistrationResolver;
import com.anotap.messenger.settings.IProxySettings;
import com.anotap.messenger.settings.ISettings;
import com.anotap.messenger.settings.ProxySettingsImpl;
import com.anotap.messenger.settings.SettingsImpl;
import com.anotap.messenger.util.Utils;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static com.anotap.messenger.util.Objects.isNull;

/**
 * Created by ruslan.kolbasa on 01.12.2016.
 * phoenix
 */
public class Injection {

    private static volatile ICaptchaProvider captchaProvider;

    private static IProxySettings proxySettings = new ProxySettingsImpl(provideApplicationContext());

    public static IProxySettings provideProxySettings(){
        return proxySettings;
    }

    public static IGifPlayerFactory provideGifPlayerFactory(){
        return new AppGifPlayerFactory(proxySettings, provideSettings().other());
    }

    private static volatile IPushRegistrationResolver resolver;

    public static IVoicePlayerFactory provideVoicePlayerFactory(){
        return new VoicePlayerFactory(provideApplicationContext(), provideProxySettings(), provideSettings().other());
    }

    public static IPushRegistrationResolver providePushRegistrationResolver(){
        if(isNull(resolver)){
            synchronized (Injection.class){
                if(isNull(resolver)){
                    final Context context = provideApplicationContext();
                    final IDevideIdProvider devideIdProvider = () -> Utils.getDiviceId(context);
                    final IGcmTokenProvider tokenProvider = new GcmTokenProvider(context);
                    resolver = new PushRegistrationResolver(tokenProvider, devideIdProvider, provideSettings(), provideNetworkInterfaces());
                }
            }
        }

        return resolver;
    }

    public static ICaptchaProvider provideCaptchaProvider() {
        if(isNull(captchaProvider)){
            synchronized (Injection.class){
                if(isNull(captchaProvider)){
                    captchaProvider = new CaptchaProvider(provideApplicationContext(), provideMainThreadScheduler());
                }
            }
        }
        return captchaProvider;
    }

    private static volatile IAttachmentsRepository attachmentsRepository;

    public static IAttachmentsRepository provideAttachmentsRepository(){
        if(isNull(attachmentsRepository)){
            synchronized (Injection.class){
                if(isNull(attachmentsRepository)){
                    attachmentsRepository = new AttachmentsRepository(provideStores().attachments(), InteractorFactory.createOwnerInteractor());
                }
            }
        }

        return attachmentsRepository;
    }

    private static volatile IWalls walls;

    public static IWalls provideWalls(){
        if(isNull(walls)){
            synchronized (Injection.class){
                if(isNull(walls)){
                    walls = new WallsImpl(provideNetworkInterfaces(), provideStores());
                }
            }
        }

        return walls;
    }

    private static INetworker networkerInstance = new Networker(proxySettings);

    public static INetworker provideNetworkInterfaces(){
        return networkerInstance;
    }

    public static IStores provideStores(){
        return AppStores.getInstance(App.getInstance());
    }

    private static volatile IBlacklistRepository blacklistRepository;

    public static IBlacklistRepository provideBlacklistRepository() {
        if(isNull(blacklistRepository)){
            synchronized (Injection.class){
                if(isNull(blacklistRepository)){
                    blacklistRepository = new BlacklistRepository();
                }
            }
        }
        return blacklistRepository;
    }

    public static ISettings provideSettings(){
        return SettingsImpl.getInstance(App.getInstance());
    }

    private static volatile ILogsStore logsStore;

    public static ILogsStore provideLogsStore(){
        if(isNull(logsStore)){
            synchronized (Injection.class){
                if(isNull(logsStore)){
                    logsStore = new LogsStore(provideApplicationContext());
                }
            }
        }
        return logsStore;
    }

    public static Scheduler provideMainThreadScheduler(){
        return AndroidSchedulers.mainThread();
    }

    public static Context provideApplicationContext() {
        return App.getInstance();
    }
}