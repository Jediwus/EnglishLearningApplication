package com.jediwus.learningapplication.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.WordDetailActivity;
import com.jediwus.learningapplication.myUtil.MyApplication;
import com.jediwus.learningapplication.pojo.ItemSearch;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final List<ItemSearch> mItemSearchLists;

    public SearchAdapter(List<ItemSearch> mItemSearchLists) {
        this.mItemSearchLists = mItemSearchLists;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView textWord, textMean, textSound;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textWord = itemView.findViewById(R.id.text_search_word);
            textMean = itemView.findViewById(R.id.text_search_means);
            textSound = itemView.findViewById(R.id.text_search_pronounce);
        }

    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.view.setOnClickListener(view1 -> {
            int position = holder.getAdapterPosition();
            ItemSearch itemSearch = mItemSearchLists.get(position);
            WordDetailActivity.wordId = itemSearch.getWordId();
            Intent intent = new Intent(MyApplication.getContext(), WordDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(WordDetailActivity.TYPE, WordDetailActivity.TYPE_CHECK);
            MyApplication.getContext().startActivity(intent);
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        ItemSearch itemSearch = mItemSearchLists.get(position);
        holder.textWord.setText(itemSearch.getWordName());
        holder.textSound.setText(itemSearch.getWordSound());
        holder.textMean.setText(itemSearch.getWordMeans());
    }

    @Override
    public int getItemCount() {
        return mItemSearchLists.size();
    }

}
