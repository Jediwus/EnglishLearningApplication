package com.jediwus.learningapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.WordDetailActivity;
import com.jediwus.learningapplication.myUtil.MediaHelper;
import com.jediwus.learningapplication.myUtil.MyApplication;
import com.jediwus.learningapplication.pojo.ItemWordList;

import java.util.List;

public class WordListAdapter extends RecyclerView.Adapter<WordListAdapter.MyViewHolder> {

    private final List<ItemWordList> mItemWordLists;

    public WordListAdapter(List<ItemWordList> mItemWordLists) {
        this.mItemWordLists = mItemWordLists;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imgSearch;
        TextView textWord;
        TextView textMean;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            textWord = itemView.findViewById(R.id.text_word_list_name);
            textMean = itemView.findViewById(R.id.text_word_list_translation);
            imgSearch = itemView.findViewById(R.id.img_word_list_search);
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_list, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        // 点击单词本体发音
        holder.textWord.setOnClickListener(viewWord -> {
            int position = holder.getAdapterPosition();
            ItemWordList itemWordList = mItemWordLists.get(position);
            MediaHelper.play(itemWordList.getWordName());
        });

        // 点击释义事件
        holder.textMean.setOnClickListener(viewMean -> {
            int position = holder.getAdapterPosition();
            ItemWordList itemWordList = mItemWordLists.get(position);
            itemWordList.setOnClick(!itemWordList.isOnClick());
            notifyDataSetChanged();
        });

        // 跳转至单词释义的页面
        holder.imgSearch.setOnClickListener(viewSearch -> {
            int position = holder.getAdapterPosition();
            ItemWordList itemWordList = mItemWordLists.get(position);
            WordDetailActivity.wordId = itemWordList.getWordId();
            Intent intent = new Intent(MyApplication.getContext(), WordDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(WordDetailActivity.TYPE, WordDetailActivity.TYPE_CHECK);
            MyApplication.getContext().startActivity(intent);
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemWordList itemWordList = mItemWordLists.get(position);
        holder.textWord.setText(itemWordList.getWordName());
        holder.textMean.setText(itemWordList.getWordMean());
        if (itemWordList.isOnClick()) {
            holder.textMean.setBackgroundColor(
                    MyApplication.getContext().getResources().getColor(
                            R.color.colorBackground, MyApplication.getContext().getTheme()));
        } else {
            holder.textMean.setBackgroundColor(
                    MyApplication.getContext().getResources().getColor(
                            R.color.colorOnBackground, MyApplication.getContext().getTheme()));
        }
    }

    @Override
    public int getItemCount() {
        return mItemWordLists.size();
    }
}
