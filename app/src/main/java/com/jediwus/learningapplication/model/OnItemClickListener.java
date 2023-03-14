package com.jediwus.learningapplication.model;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.jediwus.learningapplication.pojo.ItemWordMeanChoice;

public interface OnItemClickListener {

    void onItemClick(RecyclerView parent, View view, int position, ItemWordMeanChoice itemWordMeanChoice);

}
