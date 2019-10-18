package com.example.cats_catalog.adapters;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.example.cats_catalog.R;
import com.squareup.picasso.Picasso;

public class ImageBindingAdapter {

    private Picasso picasso;

    public ImageBindingAdapter(Picasso picasso) {
        this.picasso = picasso;
    }

    @BindingAdapter("android:src")
    public void loadImage(ImageView view, String url) {
        picasso.load(url).fit().centerCrop().placeholder(R.drawable.cat).into(view);
    }
}
