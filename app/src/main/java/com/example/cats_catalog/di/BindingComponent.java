package com.example.cats_catalog.di;

import dagger.Component;

@DataBinding
@Component(dependencies = AppComponent.class, modules = BindingModule.class)
public interface BindingComponent extends androidx.databinding.DataBindingComponent {
}
