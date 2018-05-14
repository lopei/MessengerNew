package com.anotap.messenger.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.ArrayMap;
import android.util.Log;
import android.webkit.WebView;

import com.anotap.messenger.Injection;
import com.anotap.messenger.R;
import com.anotap.messenger.api.AnotapWebService;
import com.anotap.messenger.api.model.anotap.ProxyResponse;
import com.anotap.messenger.api.util.RxUtil;
import com.anotap.messenger.model.ProxyConfig;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.reactivex.functions.Consumer;

/**
 * Created by alan on 27.06.17.
 */

public class ProxyUtil {
    private static final String LOG_TAG = "logs proxy";

    @SuppressWarnings("all")
    private static boolean setProxyICS(WebView webview, String host, int port) {
        try {
            Log.d(LOG_TAG, "Setting proxy with 4.0 API.");

            Class jwcjb = Class.forName("android.webkit.JWebCoreJavaBridge");
            Class params[] = new Class[1];
            params[0] = Class.forName("android.net.ProxyProperties");
            Method updateProxyInstance = jwcjb.getDeclaredMethod("updateProxy", params);

            Class wv = Class.forName("android.webkit.WebView");
            Field mWebViewCoreField = wv.getDeclaredField("mWebViewCore");
            Object mWebViewCoreFieldInstance = getFieldValueSafely(mWebViewCoreField, webview);

            Class wvc = Class.forName("android.webkit.WebViewCore");
            Field mBrowserFrameField = wvc.getDeclaredField("mBrowserFrame");
            Object mBrowserFrame = getFieldValueSafely(mBrowserFrameField, mWebViewCoreFieldInstance);

            Class bf = Class.forName("android.webkit.BrowserFrame");
            Field sJavaBridgeField = bf.getDeclaredField("sJavaBridge");
            Object sJavaBridge = getFieldValueSafely(sJavaBridgeField, mBrowserFrame);

            Class ppclass = Class.forName("android.net.ProxyProperties");
            Class pparams[] = new Class[3];
            pparams[0] = String.class;
            pparams[1] = int.class;
            pparams[2] = String.class;
            Constructor ppcont = ppclass.getConstructor(pparams);

            updateProxyInstance.invoke(sJavaBridge, ppcont.newInstance(host, port, null));

            Log.d(LOG_TAG, "Setting proxy with 4.0 API successful!");
            return true;
        } catch (Exception ex) {
            Log.e(LOG_TAG, "failed to set HTTP proxy: " + ex);
            return false;
        }
    }


    @SuppressWarnings("all")
    private static boolean setProxyJB(WebView webview, String host, int port) {
        Log.d(LOG_TAG, "Setting proxy with 4.1 - 4.3 API.");

        try {
            Class wvcClass = Class.forName("android.webkit.WebViewClassic");
            Class wvParams[] = new Class[1];
            wvParams[0] = Class.forName("android.webkit.WebView");
            Method fromWebView = wvcClass.getDeclaredMethod("fromWebView", wvParams);
            Object webViewClassic = fromWebView.invoke(null, webview);

            Class wv = Class.forName("android.webkit.WebViewClassic");
            Field mWebViewCoreField = wv.getDeclaredField("mWebViewCore");
            Object mWebViewCoreFieldInstance = getFieldValueSafely(mWebViewCoreField, webViewClassic);

            Class wvc = Class.forName("android.webkit.WebViewCore");
            Field mBrowserFrameField = wvc.getDeclaredField("mBrowserFrame");
            Object mBrowserFrame = getFieldValueSafely(mBrowserFrameField, mWebViewCoreFieldInstance);

            Class bf = Class.forName("android.webkit.BrowserFrame");
            Field sJavaBridgeField = bf.getDeclaredField("sJavaBridge");
            Object sJavaBridge = getFieldValueSafely(sJavaBridgeField, mBrowserFrame);

            Class ppclass = Class.forName("android.net.ProxyProperties");
            Class pparams[] = new Class[3];
            pparams[0] = String.class;
            pparams[1] = int.class;
            pparams[2] = String.class;
            Constructor ppcont = ppclass.getConstructor(pparams);

            Class jwcjb = Class.forName("android.webkit.JWebCoreJavaBridge");
            Class params[] = new Class[1];
            params[0] = Class.forName("android.net.ProxyProperties");
            Method updateProxyInstance = jwcjb.getDeclaredMethod("updateProxy", params);

            updateProxyInstance.invoke(sJavaBridge, ppcont.newInstance(host, port, null));
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Setting proxy with >= 4.1 API failed with error: " + ex.getMessage());
            return false;
        }

        Log.d(LOG_TAG, "Setting proxy with 4.1 - 4.3 API successful!");
        return true;
    }

    // from https://stackoverflow.com/questions/19979578/android-webview-set-proxy-programatically-kitkat
    @SuppressLint("NewApi")
    @SuppressWarnings("all")
    private static boolean setProxyKKPlus(WebView webView, String host, int port, String applicationClassName) {
        Log.d(LOG_TAG, "Setting proxy with >= 4.4 API.");

        Context appContext = webView.getContext().getApplicationContext();
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", port + "");
        System.setProperty("https.proxyHost", host);
        System.setProperty("https.proxyPort", port + "");
        try {
            Class applictionCls = Class.forName(applicationClassName);
            Field loadedApkField = applictionCls.getField("mLoadedApk");
            loadedApkField.setAccessible(true);
            Object loadedApk = loadedApkField.get(appContext);
            Class loadedApkCls = Class.forName("android.app.LoadedApk");
            Field receiversField = loadedApkCls.getDeclaredField("mReceivers");
            receiversField.setAccessible(true);
            ArrayMap receivers = (ArrayMap) receiversField.get(loadedApk);
            for (Object receiverMap : receivers.values()) {
                for (Object rec : ((ArrayMap) receiverMap).keySet()) {
                    Class clazz = rec.getClass();
                    if (clazz.getName().contains("ProxyChangeListener")) {
                        Method onReceiveMethod = clazz.getDeclaredMethod("onReceive", Context.class, Intent.class);
                        Intent intent = new Intent("android.intent.action.PROXY_CHANGE");

                        onReceiveMethod.invoke(rec, appContext, intent);
                    }
                }
            }

            Log.d(LOG_TAG, "Setting proxy with >= 4.4 API successful!");
            return true;
        } catch (ClassNotFoundException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        } catch (NoSuchFieldException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        } catch (IllegalAccessException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        } catch (IllegalArgumentException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        } catch (NoSuchMethodException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        } catch (InvocationTargetException e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            Log.v(LOG_TAG, e.getMessage());
            Log.v(LOG_TAG, exceptionAsString);
        }
        return false;
    }

    private static Object getFieldValueSafely(Field field, Object classInstance) throws IllegalArgumentException, IllegalAccessException {
        boolean oldAccessibleValue = field.isAccessible();
        field.setAccessible(true);
        Object result = field.get(classInstance);
        field.setAccessible(oldAccessibleValue);
        return result;
    }

    public static boolean setProxy(WebView webview, String host, int port) {
        //webview.getSettings().setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.115 Safari/537.36");
        if (Build.VERSION.SDK_INT <= 15) {
            return setProxyICS(webview, host, port);
        } else if (Build.VERSION.SDK_INT <= 18) {
            return setProxyJB(webview, host, port);
        } else {
            return setProxyKKPlus(webview, host, port, "android.app.Application");
        }
        //return true;
    }

    public static void updateProxyFromApi(UpdateProxyListener listener) {
        RxUtil.networkConsumer(AnotapWebService.service.getProxy(), new Consumer<ProxyResponse>() {
            @Override
            public void accept(ProxyResponse proxyResponse) {
                Injection.provideProxySettings().setActive(new ProxyConfig(0, proxyResponse.getProxy().getIp(), proxyResponse.getProxy().getPort()));
                if (listener != null) {
                    listener.onSuccess(proxyResponse);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                throwable.printStackTrace();
                if (listener != null) {
                    listener.onError(throwable);
                }
            }
        });
    }

    public static ProxyResponse getProxyFromApiSync() {
        return AnotapWebService.service.getProxy().blockingSingle();
    }


    public interface UpdateProxyListener {
        void onSuccess(ProxyResponse proxyResponse);

        void onError(Throwable throwable);

    }
}
