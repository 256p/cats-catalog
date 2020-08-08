package com.bidstack.cat_gallery.di;

import com.bidstack.cat_gallery.ui.activities.CatDetailsActivity;
import com.bidstack.cat_gallery.ui.activities.MainActivity;
import com.bidstack.cat_gallery.ui.activities.SplashActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector
    public abstract SplashActivity contributeSplashActivity();
    @ContributesAndroidInjector
    public abstract MainActivity contributeMainActivityInjector();
    @ContributesAndroidInjector
    abstract public CatDetailsActivity contributeCatsDetailsActivityInjector();
}