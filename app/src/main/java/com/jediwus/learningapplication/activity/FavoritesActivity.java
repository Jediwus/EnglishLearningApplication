package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.fragment.BottomSheetDialogFragmentAddF;
import com.jediwus.learningapplication.adapter.FavoritesAdapter;
import com.jediwus.learningapplication.database.Favorites;
import com.jediwus.learningapplication.database.FavoritesLinkWord;
import com.jediwus.learningapplication.pojo.ItemFavorites;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends BaseActivity implements BottomSheetDialogFragmentAddF.OnDismissListener {

    private final List<ItemFavorites> itemFavoritesList = new ArrayList<>();
    private FavoritesAdapter favoritesAdapter;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        // Find views
        RecyclerView recyclerView = findViewById(R.id.recycler_wordFavorites);
        ImageView imageBack = findViewById(R.id.favorites_img_back);
        imageBack.setOnClickListener(view -> onBackPressed());
        FloatingActionButton fab_add = findViewById(R.id.favorites_fab_add);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        favoritesAdapter = new FavoritesAdapter(this, itemFavoritesList, recyclerView);
        recyclerView.setAdapter(favoritesAdapter);

        fab_add.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = (int) view.getX();
                        initialY = (int) view.getY();
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int newX = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                        int newY = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                        int maxX = getWindow().getDecorView().getWidth() - view.getWidth();
                        int maxY = getWindow().getDecorView().getHeight() - view.getHeight();
                        newX = Math.min(Math.max(0, newX), maxX);
                        newY = Math.min(Math.max(0, newY), maxY);
                        view.setX(newX);
                        view.setY(newY);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if ((motionEvent.getRawX() - initialTouchX) == 0 || (motionEvent.getRawY() - initialTouchY) == 0) {

                            BottomSheetDialogFragmentAddF bottomSheet = new BottomSheetDialogFragmentAddF();
                            bottomSheet.show(getSupportFragmentManager(), "tagAdd");

                        }
                        return true;
                }
                return false;
            }
        });

        if (savedInstanceState != null) {
            int fabX = savedInstanceState.getInt("fabX");
            int fabY = savedInstanceState.getInt("fabY");
            fab_add.setX(fabX);
            fab_add.setY(fabY);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onStart() {
        super.onStart();
        List<Favorites> favoritesList = LitePal.findAll(Favorites.class);
        if (!favoritesList.isEmpty()) {
            itemFavoritesList.clear();
            for (Favorites favorites : favoritesList) {
                List<FavoritesLinkWord> favoritesLinkWordList = LitePal.where("favoritesId = ?", favorites.getId() + "").find(FavoritesLinkWord.class);
                itemFavoritesList.add(new ItemFavorites(favorites.getId(), favoritesLinkWordList.size(), favorites.getName(), favorites.getRemark()));
            }
            favoritesAdapter.notifyDataSetChanged();
        }
    }

    // BottomSheetDialogFragmentAddF 内定义的接口，执行 bottomSheet 关闭后的操作
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDismiss() {
        List<Favorites> favoritesList = LitePal.findAll(Favorites.class);
        if (!favoritesList.isEmpty()) {
            itemFavoritesList.clear();
            for (Favorites favorites : favoritesList) {
                List<FavoritesLinkWord> favoritesLinkWordList = LitePal.where("favoritesId = ?", favorites.getId() + "").find(FavoritesLinkWord.class);
                itemFavoritesList.add(new ItemFavorites(favorites.getId(), favoritesLinkWordList.size(), favorites.getName(), favorites.getRemark()));
            }
            favoritesAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 动画效果
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_left);
    }

}