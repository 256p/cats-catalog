package com.bidstack.cat_gallery.ui.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.bidstack.cat_gallery.R;
import com.bidstack.cat_gallery.data.models.Cat;
import com.bidstack.cat_gallery.data.models.VoteResponse;
import com.bidstack.cat_gallery.data.models.VoteType;
import com.bidstack.cat_gallery.databinding.ActivityCatDetailsBinding;
import com.bidstack.cat_gallery.ui.GetVotesDialog;
import com.bidstack.cat_gallery.viewmodel.CatDetailsViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.InterstitialListener;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

public class CatDetailsActivity extends DaggerAppCompatActivity implements View.OnClickListener {

    private static final String EXTRA_CAT = "CAT";
    private static final String EXTRA_IMAGE_TRANSITION_NAME = "IMAGE_TRANSITION_NAME";

    @Inject
    Picasso picasso;
    @Inject
    ViewModelProvider.Factory viewModelProvider;

    private ActivityCatDetailsBinding binding;
    private CatDetailsViewModel viewModel;
    private Cat cat;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportPostponeEnterTransition();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cat_details);
        viewModel = ViewModelProviders.of(this, viewModelProvider).get(CatDetailsViewModel.class);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
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

        IronSource.setInterstitialListener(new InterstitialListener() {
            @Override
            public void onInterstitialAdReady() {
                Log.d("IronSource", "onInterstitialAdReady Ad Ready");
            }

            @Override
            public void onInterstitialAdLoadFailed(IronSourceError ironSourceError) {
                Log.d("IronSource", "onInterstitialAdLoadFailed Load Error: " + ironSourceError.toString());
                finish();
            }

            @Override
            public void onInterstitialAdOpened() {
                Log.d("IronSource", "onInterstitialAdOpened Ad Opened");
            }

            @Override
            public void onInterstitialAdClosed() {
                Log.d("IronSource", "onInterstitialAdClosed Closed");
                finish();
            }

            @Override
            public void onInterstitialAdShowSucceeded() {
                Log.d("IronSource", "onInterstitialAdShowSucceeded Ad Show Success");
            }

            @Override
            public void onInterstitialAdShowFailed(IronSourceError ironSourceError) {
                Log.d("IronSource", "onInterstitialAdShowFailed Show Error: " + ironSourceError.toString());
                finish();
            }

            @Override
            public void onInterstitialAdClicked() {
                Log.d("IronSource", "onInterstitialAdClicked Ad Clicked");
            }
        });

        IronSource.init(
                this,
                "d0e265e5",
                IronSource.AD_UNIT.REWARDED_VIDEO,
                IronSource.AD_UNIT.INTERSTITIAL,
                IronSource.AD_UNIT.BANNER
        );

        IronSource.loadInterstitial();

        IronSourceBannerLayout banner = IronSource.createBanner(this, ISBannerSize.BANNER);
        banner.setId(View.generateViewId());
        binding.rootContainer.addView(banner);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(binding.rootContainer);
        constraintSet.connect(binding.catImage.getId(), ConstraintSet.BOTTOM, banner.getId(), ConstraintSet.TOP);
        constraintSet.connect(banner.getId(), ConstraintSet.TOP, binding.catImage.getId(), ConstraintSet.BOTTOM);
        constraintSet.connect(banner.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        constraintSet.connect(banner.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraintSet.connect(banner.getId(), ConstraintSet.BOTTOM, binding.buttonsContainer.getId(), ConstraintSet.TOP);
        constraintSet.connect(binding.buttonsContainer.getId(), ConstraintSet.TOP, banner.getId(), ConstraintSet.BOTTOM);
        constraintSet.applyTo(binding.rootContainer);
        IronSource.loadBanner(banner);

        IntegrationHelper.validateIntegration(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        setMenuVotesCount();
        return super.onPrepareOptionsMenu(menu);
    }

    private void setOnClickListeners() {
        binding.downVote.setOnClickListener(this);
        binding.upVote.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        if (IronSource.isInterstitialReady()) {
            IronSource.showInterstitial();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        VoteType voteType = null;

        if (viewModel.getVotesCount() <= 0) {
            Snackbar.make(binding.getRoot(), getString(R.string.you_are_out_of_votes), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.get_votes), v -> {
                        new GetVotesDialog(this).show();
                    })
                    .show();
        } else {
            switch (view.getId()) {
                case R.id.down_vote:
                    voteType = VoteType.DOWN_VOTE;
                    break;
                case R.id.up_vote:
                    voteType = VoteType.UP_VOTE;
                    break;
            }
        }
        if (voteType != null) {
            setButtonsClickable(false);
            enableGrayOutOnButtons(true);
            viewModel.sendVote(voteType, cat);
            viewModel.reduceVotesCount();
            setMenuVotesCount();
        }
    }

    private void setMenuVotesTitle(String title) {
        if (mMenu != null) {
            MenuItem item = mMenu.findItem(R.id.action_votes_left);
            item.setTitle(title);
        }
    }

    private void setMenuVotesCount() {
        int votesCount = viewModel.getVotesCount();
        if (votesCount <= 0) {
            setMenuVotesTitle(getString(R.string.get_votes));
        } else {
            setMenuVotesTitle(getString(R.string.votes_left, votesCount));
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                picasso.load(cat.getUrl())
                        .fit()
                        .centerInside()
                        .placeholder(R.drawable.cat)
                        .into(binding.catImage);
            } else {
                picasso.load(cat.getUrl())
                        .fit()
                        .centerInside()
                        .into(binding.catImage);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
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
