package com.example.cats_catalog.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.example.cats_catalog.App;
import com.example.cats_catalog.R;
import com.example.cats_catalog.data.models.Cat;
import com.example.cats_catalog.databinding.ActivityCatDetailsBinding;
import com.example.cats_catalog.viewmodel.CatDetailsViewModel;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

public class CatDetailsActivity extends AppCompatActivity {

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
        }

        configureImage();
    }

    private void configureImage() {
        if (cat != null) {
            picasso.load(cat.getUrl())
                    .fit()
                    .centerInside()
                    .noFade()
                    .placeholder(R.drawable.cat)
                    .into(binding.catImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            supportStartPostponedEnterTransition();
                        }

                        @Override
                        public void onError(Exception e) {
                            supportStartPostponedEnterTransition();
                        }
                    });
            binding.catImage.setScaleType(ImageView.ScaleType.FIT_START);
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
