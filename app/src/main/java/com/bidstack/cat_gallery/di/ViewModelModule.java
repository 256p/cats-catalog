package com.bidstack.cat_gallery.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bidstack.cat_gallery.viewmodel.CatDetailsViewModel;
import com.bidstack.cat_gallery.viewmodel.MainViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);


    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    abstract ViewModel bindMainViewModel(MainViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CatDetailsViewModel.class)
    abstract ViewModel bindCatDetailsViewModel(CatDetailsViewModel viewModel);

}