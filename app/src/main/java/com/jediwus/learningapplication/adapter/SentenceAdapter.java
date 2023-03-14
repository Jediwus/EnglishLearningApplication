package com.jediwus.learningapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.myUtil.MediaHelper;
import com.jediwus.learningapplication.pojo.ItemSentence;

import java.util.List;

public class SentenceAdapter extends RecyclerView.Adapter<SentenceAdapter.ViewHolder> {

    private final List<ItemSentence> itemSentenceList;

    public SentenceAdapter(List<ItemSentence> itemSentenceList) {
        this.itemSentenceList = itemSentenceList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgPlay;
        TextView textEnglish, textChinese;

        public ViewHolder(View itemView) {
            super(itemView);
            imgPlay = itemView.findViewById(R.id.img_word_detail_item_sentence_voice);
            textEnglish = itemView.findViewById(R.id.text_word_detail_item_sen_en);
            textChinese = itemView.findViewById(R.id.text_word_detail_item_sen_cn);
        }
    }


    @NonNull
    @Override
    public SentenceAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_word_detail_sentence, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.imgPlay.setOnClickListener(view1 -> {
            int position = viewHolder.getAdapterPosition();
            final ItemSentence itemSentence = itemSentenceList.get(position);
            // 开启播放
            new Thread(() -> MediaHelper.play(itemSentence.getEnglish().replace('’', '\''))).start();
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SentenceAdapter.ViewHolder holder, int position) {
        ItemSentence itemSentence = itemSentenceList.get(position);
        holder.textChinese.setText(itemSentence.getChinese());
        holder.textEnglish.setText(itemSentence.getEnglish());
    }

    @Override
    public int getItemCount() {
        return itemSentenceList.size();
    }
}
