package com.bidstack.cat_gallery.adapters;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.bidstack.cat_gallery.R;
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
