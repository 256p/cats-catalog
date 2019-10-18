package com.example.cats_catalog.di;

import com.example.cats_catalog.adapters.ImageBindingAdapter;
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
