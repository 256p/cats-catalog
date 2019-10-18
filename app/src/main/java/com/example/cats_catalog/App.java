package com.example.cats_catalog;

import android.app.Application;

import androidx.databinding.DataBindingUtil;

import com.example.cats_catalog.di.AppComponent;
import com.example.cats_catalog.di.AppModule;
import com.example.cats_catalog.di.BindingComponent;
import com.example.cats_catalog.di.DaggerAppComponent;
import com.example.cats_catalog.di.DaggerBindingComponent;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

public class App extends DaggerApplication {

    private static AppComponent component;

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();

        BindingComponent bindingComponent = DaggerBindingComponent.builder()
                .appComponent(component)
                .build();
        DataBindingUtil.setDefaultComponent(bindingComponent);

        return component;
    }

    public static AppComponent getComponent() {
        return component;
    }
}
