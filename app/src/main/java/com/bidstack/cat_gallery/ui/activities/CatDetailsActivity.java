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
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.DefaultAdListener;
import com.amazon.device.ads.InterstitialAd;
import com.bidstack.cat_gallery.R;
import com.bidstack.cat_gallery.data.models.Cat;
import com.bidstack.cat_gallery.data.models.VoteResponse;
import com.bidstack.cat_gallery.data.models.VoteType;
import com.bidstack.cat_gallery.databinding.ActivityCatDetailsBinding;
import com.bidstack.cat_gallery.ui.GetVotesDialog;
import com.bidstack.cat_gallery.viewmodel.CatDetailsViewModel;
import com.google.android.material.snackbar.Snackbar;
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
    private InterstitialAd interstitialAd;

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

        interstitialAd = new InterstitialAd(this);
        interstitialAd.loadAd();
        interstitialAd.setListener(new DefaultAdListener() {
            @Override
            public void onAdFailedToLoad(Ad ad, AdError error) {
                Log.e("Cat_Gallery", "Ad failed to load. Code: " + error.getCode() + ", Message: " + error.getMessage());
                super.onAdFailedToLoad(ad, error);
            }

            @Override
            public void onAdDismissed(Ad ad) {
                super.onAdDismissed(ad);
                finish();
            }

            @Override
            public void onAdCollapsed(Ad ad) {
                super.onAdCollapsed(ad);
                finish();
            }
        });
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
        if (!interstitialAd.showAd()) {
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
            picasso.load(cat.getUrl())
                    .fit()
                    .centerInside()
                    .placeholder(R.drawable.cat)
                    .into(binding.catImage);
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
