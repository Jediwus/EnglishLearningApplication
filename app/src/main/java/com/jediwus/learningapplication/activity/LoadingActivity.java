package com.jediwus.learningapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.jediwus.learningapplication.R;

import org.jetbrains.annotations.Nullable;

public class LoadingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        ImageView img_loading = findViewById(R.id.img_loading);
        Glide.with(this)
                .load(R.drawable.gif_loading_1)
                .into(img_loading);

    }
}