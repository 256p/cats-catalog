package com.bidstack.cat_gallery.di;

import com.bidstack.cat_gallery.adapters.ImageBindingAdapter;
import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;

@Module
public class BindingModule {

    @Provides
    @DataBinding
    ImageBindingAdapter provideImageBindingAdapter(Picasso picasso) {
        return new ImageBindingAdapter(picasso);
    }

}