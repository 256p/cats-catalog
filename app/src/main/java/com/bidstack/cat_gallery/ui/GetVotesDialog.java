package com.bidstack.cat_gallery.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bidstack.cat_gallery.R;

public class GetVotesDialog extends Dialog {
    public GetVotesDialog(@NonNull Context context) {
        super(context);
    }

    public GetVotesDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected GetVotesDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.get_votes_dialog);
        View getVotesBtn = findViewById(R.id.getVotesBtn);

        getVotesBtn.setOnClickListener(v -> {

            dismiss();
        });

        super.onCreate(savedInstanceState);
    }
}
