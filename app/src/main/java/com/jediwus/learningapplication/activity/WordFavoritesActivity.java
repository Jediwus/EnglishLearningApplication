package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.jediwus.learningapplication.R;

public class WordFavoritesActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_favorites);

        windowExplode();


        ImageView imageBack = findViewById(R.id.favorites_img_back);
        imageBack.setOnClickListener(view -> {
            supportFinishAfterTransition();
            finish();
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}