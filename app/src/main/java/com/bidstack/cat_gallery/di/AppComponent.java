package com.bidstack.cat_gallery.di;

import android.content.Context;

import com.bidstack.cat_gallery.App;
import com.bidstack.cat_gallery.ui.GetVotesDialog;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {
        AppModule.class,
        ViewModelModule.class,
        ActivityBuilder.class,
        AndroidInjectionModule.class
})
public interface AppComponent {

    Picasso picasso();
    void inject(App app);
    void inject(GetVotesDialog dialog);

    @Component.Factory
    interface Factory {
        AppComponent create(@BindsInstance Context context);
    }
}
