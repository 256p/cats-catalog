package com.bidstack.cat_gallery.di;

import com.bidstack.cat_gallery.App;
import com.bidstack.cat_gallery.BuildConfig;
import com.bidstack.cat_gallery.MockInterceptor;
import com.bidstack.cat_gallery.data.ApiService;

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
