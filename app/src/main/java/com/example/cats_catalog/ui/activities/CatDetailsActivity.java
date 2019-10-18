package com.example.cats_catalog.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.example.cats_catalog.App;
import com.example.cats_catalog.R;
import com.example.cats_catalog.data.models.Cat;
import com.example.cats_catalog.data.models.VoteResponse;
import com.example.cats_catalog.data.models.VoteType;
import com.example.cats_catalog.databinding.ActivityCatDetailsBinding;
import com.example.cats_catalog.viewmodel.CatDetailsViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

public class CatDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String EXTRA_CAT = "CAT";
    private static final String EXTRA_IMAGE_TRANSITION_NAME = "IMAGE_TRANSITION_NAME";

    @Inject
    Picasso picasso;

    private ActivityCatDetailsBinding binding;
    private CatDetailsViewModel viewModel;
    private Cat cat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getComponent().inject(this);
        supportPostponeEnterTransition();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cat_details);
        viewModel = ViewModelProviders.of(this).get(CatDetailsViewModel.class);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        cat = getIntent().getParcelableExtra(EXTRA_CAT);
        String transitionName = getIntent().getStringExtra(EXTRA_IMAGE_TRANSITION_NAME);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && transitionName != null) {
            binding.catImage.setTransitionName(transitionName);
            supportStartPostponedEnterTransition();
        }

        configureImage();
        setOnClickListeners();
        configureLiveDataObservers();
    }

    private void setOnClickListeners() {
        binding.downVote.setOnClickListener(this);
        binding.upVote.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        VoteType voteType = null;
        switch (view.getId()) {
            case R.id.down_vote:
                voteType = VoteType.DOWN_VOTE;
                break;
            case R.id.up_vote:
                voteType = VoteType.UP_VOTE;
                break;
        }
        if (voteType != null) {
            setButtonsClickable(false);
            enableGrayOutOnButtons(true);
            viewModel.sendVote(voteType, cat);
        }
    }

    private void setButtonsClickable(boolean clickable) {
        binding.downVote.setClickable(clickable);
        binding.upVote.setClickable(clickable);
    }

    private void enableGrayOutOnButtons(boolean enable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (enable) {
                binding.buttonsContainer.setForeground(new ColorDrawable(ContextCompat.getColor(this, R.color.transparentOverlay)));
            } else {
                binding.buttonsContainer.setForeground(null);
            }
        }
    }

    private void configureLiveDataObservers() {
        viewModel.getVoteResponseLiveData().observe(this, voteResponse -> {
            String message;
            if (voteResponse.getMessage().equals(VoteResponse.MESSAGE_SUCCESS)) {
                message = getString(R.string.vote_succeed);
            } else {
                message = getString(R.string.vote_error);
            }
            Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
            setButtonsClickable(true);
            enableGrayOutOnButtons(false);
        });
    }

    private void configureImage() {
        if (cat != null) {
            picasso.load(cat.getUrl())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.cat)
                    .into(binding.catImage);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    public static void startActivity(Activity activity, Cat cat, ImageView imageView) {
        String transitionName = ViewCompat.getTransitionName(imageView);

        Intent intent = new Intent(activity, CatDetailsActivity.class);
        intent.putExtra(EXTRA_CAT, cat);
        intent.putExtra(EXTRA_IMAGE_TRANSITION_NAME, transitionName);

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                imageView,
                transitionName
        );
        activity.startActivity(intent, options.toBundle());
    }
}
