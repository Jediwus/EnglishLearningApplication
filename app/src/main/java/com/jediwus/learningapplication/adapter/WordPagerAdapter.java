package com.jediwus.learningapplication.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.jediwus.learningapplication.activity.fragment.AllWordsFragment;
import com.jediwus.learningapplication.activity.fragment.KnownWordsFragment;
import com.jediwus.learningapplication.activity.fragment.StarWordsFragment;

public class WordPagerAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 3;

    public WordPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new AllWordsFragment();
            case 1:
                return new StarWordsFragment();
            case 2:
                return new KnownWordsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        // 返回选项卡数量
        return NUM_PAGES;
    }

}
