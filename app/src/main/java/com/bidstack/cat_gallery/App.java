package com.bidstack.cat_gallery;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.multidex.MultiDexApplication;

import com.amazon.device.ads.AdRegistration;
import com.bidstack.cat_gallery.di.AppComponent;
import com.bidstack.cat_gallery.di.BindingComponent;
import com.bidstack.cat_gallery.di.DaggerAppComponent;
import com.bidstack.cat_gallery.di.DaggerBindingComponent;
import com.bidstack.pubguard.Pubguard;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

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

        AdRegistration.setAppKey("4d871c533ea642798b3f4aff5a8a5b73");
        AdRegistration.enableLogging(true);

        Pubguard.init(this, "9595e51e-0adb-4dc1-b910-662e718a6c38");

        try {
            ProviderInstaller.installIfNeeded(this);
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }
}
