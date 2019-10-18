package com.example.cats_catalog.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.cats_catalog.R;
import com.example.cats_catalog.adapters.CatsAdapter;
import com.example.cats_catalog.databinding.ActivityMainBinding;
import com.example.cats_catalog.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private CatsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.fetchCats());

        configureRV();
        configureLiveDataObservers();
    }

    private void configureRV() {
        binding.catsRv.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new CatsAdapter(null, (cat, imageView) -> CatDetailsActivity.startActivity(this, cat, imageView));
        binding.catsRv.setAdapter(adapter);
    }

    private void configureLiveDataObservers() {
        viewModel.getCatsLiveData().observe(this, cats -> {
            adapter.updateData(cats);
            binding.swipeRefresh.setRefreshing(false);
        });
    }
}
