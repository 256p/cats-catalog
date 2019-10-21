package com.example.cats_catalog.di;

import com.example.cats_catalog.App;
import com.example.cats_catalog.BuildConfig;
import com.example.cats_catalog.MockInterceptor;
import com.example.cats_catalog.data.ApiService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class TestAppModule extends AppModule {

    public TestAppModule(App app) {
        super(app);
    }

    @Override
    ApiService provideApiService(OkHttpClient client) {
        OkHttpClient mockClient = new OkHttpClient.Builder()
                .addInterceptor(new MockInterceptor())
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_URL)
                .client(mockClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(ApiService.class);
    }



}
