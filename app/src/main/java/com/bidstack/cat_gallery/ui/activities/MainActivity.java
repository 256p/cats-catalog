package com.bidstack.cat_gallery.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bidstack.cat_gallery.R;
import com.bidstack.cat_gallery.adapters.CatsAdapter;
import com.bidstack.cat_gallery.databinding.ActivityMainBinding;
import com.bidstack.cat_gallery.ui.GetVotesDialog;
import com.bidstack.cat_gallery.viewmodel.MainViewModel;
import com.google.android.material.snackbar.Snackbar;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class MainActivity extends DaggerAppCompatActivity {

    private static final int STATE_PROGRESS = 1;
    private static final int STATE_ERROR = 2;
    private static final int STATE_SUCCESS = 3;

    @Inject
    ViewModelProvider.Factory viewModelProvider;
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private CatsAdapter adapter;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this, viewModelProvider).get(MainViewModel.class);

        binding.swipeRefresh.setOnRefreshListener(() -> viewModel.fetchCats());

        setOnClickListeners();
        configureRV();
        configureLiveDataObservers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setMenuVotesCount();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        setMenuVotesCount();
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_votes_left:
                if (viewModel.getVotesCount() <= 0) {
                    Dialog dialog = new GetVotesDialog(this);
                    dialog.setOnCancelListener(d -> {
                        setMenuVotesCount();
                    });
                    dialog.show();
                }
                break;
        }
        return true;
    }

    private void setMenuVotesCount() {
        if  (mMenu != null) {
            MenuItem item = mMenu.findItem(R.id.action_votes_left);
            int votesCount = viewModel.getVotesCount();
            if (votesCount <= 0) {
                item.setTitle(getString(R.string.get_votes));
            } else {
                item.setTitle(getString(R.string.votes_left, votesCount));
            }
        }
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
