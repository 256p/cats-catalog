package com.bidstack.cat_gallery;

import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.multidex.MultiDexApplication;

import com.bidstack.cat_gallery.di.AppComponent;
import com.bidstack.cat_gallery.di.BindingComponent;
import com.bidstack.cat_gallery.di.DaggerAppComponent;
import com.bidstack.cat_gallery.di.DaggerBindingComponent;
import com.bidstack.pubguard.Pubguard;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.vungle.warren.InitCallback;
import com.vungle.warren.Vungle;
import com.vungle.warren.error.VungleException;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;
import javax.net.ssl.SSLContext;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

public class App extends MultiDexApplication implements HasAndroidInjector {

    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppComponent appComponent = DaggerAppComponent.factory().create(this);
        appComponent.inject(this);
        BindingComponent bindingComponent = DaggerBindingComponent.builder()
                .appComponent(appComponent)
                .build();
        DataBindingUtil.setDefaultComponent(bindingComponent);

        Pubguard.init(this, "834db244-69fb-42c7-bfbd-7ddfb16d6d3c", BuildConfig.VERSION_NAME);

        try {
            ProviderInstaller.installIfNeeded(this);
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        Vungle.init("5fc4c47b93854280cf9cb746", this, new InitCallback() {
            @Override
            public void onSuccess() {
                Log.d("TEST", "Vungle init success");
            }

            @Override
            public void onError(VungleException exception) {
                Log.d("TEST", "Vungle init error");
                exception.printStackTrace();
            }

            @Override
            public void onAutoCacheAdAvailable(String placementId) {

            }
        });

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
