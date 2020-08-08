package com.bidstack.cat_gallery.di;

import com.bidstack.cat_gallery.espresso.SuccessfulDataRefreshTest;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AppModule.class, AndroidSupportInjectionModule.class})
public interface TestAppComponent extends AppComponent {

    void inject(SuccessfulDataRefreshTest successfulDataRefreshTest);

}
