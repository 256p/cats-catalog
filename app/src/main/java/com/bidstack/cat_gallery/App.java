package com.bidstack.cat_gallery;

import androidx.databinding.DataBindingUtil;
import androidx.multidex.MultiDexApplication;

import com.amazon.device.ads.AdRegistration;
import com.bidstack.cat_gallery.di.AppComponent;
import com.bidstack.cat_gallery.di.BindingComponent;
import com.bidstack.cat_gallery.di.DaggerAppComponent;
import com.bidstack.cat_gallery.di.DaggerBindingComponent;

import javax.inject.Inject;

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
    }
}
