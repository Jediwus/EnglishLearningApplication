package com.jediwus.learningapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.WordDetailActivity;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.myUtil.MyApplication;
import com.jediwus.learningapplication.pojo.ItemDisplay;

import java.util.List;

public class DisplayAdapter extends RecyclerView.Adapter<DisplayAdapter.MyViewHolder> {

    private final List<ItemDisplay> mItemDisplayList;

    public DisplayAdapter(List<ItemDisplay> mItemDisplayList) {
        this.mItemDisplayList = mItemDisplayList;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout_word;
        TextView tv_word;
        TextView tv_mean;
        ImageView img_star;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            layout_word = itemView.findViewById(R.id.layout_item_display_word);
            tv_word = itemView.findViewById(R.id.text_item_display_name);
            tv_mean = itemView.findViewById(R.id.text_item_display_mean);
            img_star = itemView.findViewById(R.id.img_item_display_star);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_display, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        holder.layout_word.setOnClickListener(view_word -> {
            int position = holder.getAdapterPosition();
            ItemDisplay itemDisplay = mItemDisplayList.get(position);
            WordDetailActivity.wordId = itemDisplay.getWordId();
            Intent intent = new Intent(MyApplication.getContext(), WordDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(WordDetailActivity.TYPE, WordDetailActivity.TYPE_CHECK);
            MyApplication.getContext().startActivity(intent);
        });

        holder.img_star.setOnClickListener(view_star -> {
            int position = holder.getAdapterPosition();
            ItemDisplay itemDisplay = mItemDisplayList.get(position);
            if (itemDisplay.isStar()) {
                itemDisplay.setStar(false);
                Word word = new Word();
                word.setToDefault("isCollected");
                word.updateAll("wordId = ?", itemDisplay.getWordId() + "");
            } else {
                itemDisplay.setStar(true);
                Word word = new Word();
                word.setIsCollected(1);
                word.updateAll("wordId = ?", itemDisplay.getWordId() + "");
            }
            notifyDataSetChanged();
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemDisplay itemDisplay = mItemDisplayList.get(position);
        holder.tv_word.setText(itemDisplay.getWord());
        holder.tv_mean.setText(itemDisplay.getWordMean());
        if (itemDisplay.isStar()) {
            Glide.with(MyApplication.getContext())
                    .load(R.drawable.icon_star_selected)
                    .into(holder.img_star);
        } else {
            Glide.with(MyApplication.getContext())
                    .load(R.drawable.icon_star)
                    .into(holder.img_star);
        }
    }

    @Override
    public int getItemCount() {
        return mItemDisplayList.size();
    }

}
