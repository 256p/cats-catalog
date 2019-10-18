package com.example.cats_catalog.di;

import com.example.cats_catalog.App;
import com.example.cats_catalog.ui.activities.CatDetailsActivity;
import com.example.cats_catalog.viewmodel.MainViewModel;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AppModule.class, AndroidSupportInjectionModule.class})
public interface AppComponent extends AndroidInjector<App> {

    Picasso picasso();
    void inject(MainViewModel viewModel);
    void inject(CatDetailsActivity activity);
}
