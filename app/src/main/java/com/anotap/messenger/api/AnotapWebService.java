package com.anotap.messenger.api;

import com.anotap.messenger.api.model.anotap.ProxyResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import rx.Observable;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;


/**
 * Created by alan on 01.02.17.
 */

public interface AnotapWebService {
    OkHttpClient client = new OkHttpClient
            .Builder()
            .readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true).build();

    Gson gson = new GsonBuilder().create();

    String ANOTAP_URL = "https://anotap.com/";
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(ANOTAP_URL)
            .client(client)
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    AnotapWebService service = retrofit.create(AnotapWebService.class);

    @GET("stats/select_server.php")
    Observable<ProxyResponse> getProxy();
}
