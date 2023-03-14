package com.jediwus.learningapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.pojo.ItemPhrase;

import java.util.List;

public class PhraseAdapter extends RecyclerView.Adapter<PhraseAdapter.ViewHolder> {

    private final List<ItemPhrase> itemPhraseList;

    public PhraseAdapter(List<ItemPhrase> itemPhraseList) {
        this.itemPhraseList = itemPhraseList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textEnglish, textChinese;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textEnglish = itemView.findViewById(R.id.text_word_detail_item_phrase_en);
            textChinese = itemView.findViewById(R.id.text_word_detail_item_phrase_cn);
        }
    }

    @NonNull
    @Override
    public PhraseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_word_detail_phrase, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull PhraseAdapter.ViewHolder holder, int position) {
        ItemPhrase itemPhrase = itemPhraseList.get(position);
        holder.textChinese.setText(itemPhrase.getChinese());
        holder.textEnglish.setText(itemPhrase.getEnglish());
    }

    @Override
    public int getItemCount() {
        return itemPhraseList.size();
    }


}
