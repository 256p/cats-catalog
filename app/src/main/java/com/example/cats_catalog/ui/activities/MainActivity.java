package com.example.cats_catalog.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.cats_catalog.R;
import com.example.cats_catalog.adapters.CatsAdapter;
import com.example.cats_catalog.databinding.ActivityMainBinding;
import com.example.cats_catalog.viewmodel.MainViewModel;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private static final int STATE_PROGRESS = 1;
    private static final int STATE_ERROR = 2;
    private static final int STATE_SUCCESS = 3;

    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private CatsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.fetchCats());

        setOnClickListeners();
        configureRV();
        configureLiveDataObservers();
    }

    private void setOnClickListeners() {
        binding.retryBtn.setOnClickListener(v -> {
            setUpVisibility(STATE_PROGRESS);
            viewModel.fetchCats();
        });
    }

    private void setUpVisibility(int state) {
        switch (state) {
            case STATE_PROGRESS:
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.swipeRefresh.setVisibility(View.GONE);
                binding.errorContainer.setVisibility(View.GONE);
                break;
            case STATE_ERROR:
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setVisibility(View.GONE);
                binding.errorContainer.setVisibility(View.VISIBLE);
                break;
            case STATE_SUCCESS:
                binding.progressBar.setVisibility(View.GONE);
                binding.swipeRefresh.setVisibility(View.VISIBLE);
                binding.errorContainer.setVisibility(View.GONE);
        }
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
            setUpVisibility(STATE_SUCCESS);
        });
        viewModel.getErrorsLiveData().observe(this, integer -> {
            binding.swipeRefresh.setRefreshing(false);
            if (binding.swipeRefresh.getVisibility() == View.VISIBLE) {
                Snackbar.make(binding.getRoot(), getString(R.string.downloading_error), Snackbar.LENGTH_SHORT).show();
            } else {
                setUpVisibility(STATE_ERROR);
            }
        });
    }

    public static void startActivity(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        activity.startActivity(intent);
    }

}
