package com.jediwus.learningapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jediwus.learningapplication.R;

public class FavoritesDetailActivity extends BaseActivity {

    public static int currentFavoritesId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_detail);
    }
}