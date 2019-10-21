package com.example.cats_catalog.di;

import com.example.cats_catalog.espresso.SuccessfulDataRefreshTest;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AppModule.class, AndroidSupportInjectionModule.class})
public interface TestAppComponent extends AppComponent {

    void inject(SuccessfulDataRefreshTest successfulDataRefreshTest);

}
