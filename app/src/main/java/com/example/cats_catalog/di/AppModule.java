package com.example.cats_catalog.di;

import android.content.Context;

import com.example.cats_catalog.App;
import com.example.cats_catalog.BuildConfig;
import com.example.cats_catalog.Constants;
import com.example.cats_catalog.data.ApiService;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.ConnectionSpec;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.TlsVersion;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class AppModule {

    private App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides
    @Singleton
    Context provideContext() {
        return app.getApplicationContext();
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        List<ConnectionSpec> connectionSpecs = new LinkedList<>();
        ConnectionSpec strict = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2)
                .build();
        connectionSpecs.add(strict);

        Interceptor headerInterceptor = chain -> {
            Request request = chain.request();
            Request newRequest;

            newRequest = request.newBuilder()
                    .addHeader(Constants.HEADER_X_CLIENT_ID, BuildConfig.API_KEY)
                    .build();
            return chain.proceed(newRequest);
        };

        return new OkHttpClient.Builder()
                .connectionSpecs(connectionSpecs)
                .addInterceptor(loggingInterceptor)
                .addInterceptor(headerInterceptor)
                .build();
    }

    @Provides
    @Singleton
    ApiService provideApiService(OkHttpClient client) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(ApiService.class);
    }

    @Provides
    @Singleton
    Picasso providePicasso(Context context, OkHttpClient client) {
        return new Picasso.Builder(context)
                .downloader(new OkHttp3Downloader(client))
                .build();
    }
}
