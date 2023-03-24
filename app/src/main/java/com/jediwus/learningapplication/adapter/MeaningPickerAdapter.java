package com.jediwus.learningapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.myUtil.MyApplication;
import com.jediwus.learningapplication.pojo.ItemMeaningPicker;

import java.util.List;

public class MeaningPickerAdapter extends RecyclerView.Adapter<MeaningPickerAdapter.MyViewHolder> implements View.OnClickListener {

    private RecyclerView recyclerView;

    private final List<ItemMeaningPicker> mItemMeaningPickerList;

    // 判断是否是第一次点击
    public static boolean isFirstClick = true;

    // MeaningPickerAdapter 的构造函数
    public MeaningPickerAdapter(List<ItemMeaningPicker> mItemMeaningPickerList) {
        this.mItemMeaningPickerList = mItemMeaningPickerList;
    }

    // 将RecyclerView附加到Adapter上
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        this.recyclerView = null;
    }

    // 声明点击事件的接口
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView parent, View view, int position, ItemMeaningPicker itemWordMeanChoice);
    }

    @Override
    public void onClick(View view) {
        int position = recyclerView.getChildAdapterPosition(view);
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(recyclerView, view, position, mItemMeaningPickerList.get(position));
        }
    }

    // 自定义 ViewHolder 的设置
    static class MyViewHolder extends RecyclerView.ViewHolder {

        View view;
        CardView cardMeaning;
        TextView textMeaning;
        ImageView imgPicker;

        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            cardMeaning = itemView.findViewById(R.id.item_card_word_meaning_picker);
            textMeaning = itemView.findViewById(R.id.item_text_word_meaning);
            imgPicker = itemView.findViewById(R.id.item_img_word_pick_status);
        }

    }

    @NonNull
    @Override
    public MeaningPickerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word_meaning_picker, parent, false);
        view.setOnClickListener(this);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MeaningPickerAdapter.MyViewHolder holder, int position) {
        ItemMeaningPicker itemMeaningPicker = mItemMeaningPickerList.get(position);
        holder.textMeaning.setText(itemMeaningPicker.getWordMeaning());
        holder.imgPicker.setVisibility(View.GONE);

        if (itemMeaningPicker.getIfRight() == ItemMeaningPicker.WRONG) {
            // 回答错误的处理
            holder.cardMeaning.setCardBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.colorError, MyApplication.getContext().getTheme()));
            holder.textMeaning.setTextColor(MyApplication.getContext().getResources().getColor(R.color.colorOnError, MyApplication.getContext().getTheme()));
            holder.imgPicker.setVisibility(View.VISIBLE);
            Glide.with(MyApplication.getContext()).load(R.drawable.icon_cross).into(holder.imgPicker);

        } else if (itemMeaningPicker.getIfRight() == ItemMeaningPicker.RIGHT) {
            // 回答正确的处理
            holder.cardMeaning.setCardBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.colorSecondaryContainer, MyApplication.getContext().getTheme()));
            holder.textMeaning.setTextColor(MyApplication.getContext().getResources().getColor(R.color.colorOnSecondaryContainer, MyApplication.getContext().getTheme()));
            holder.imgPicker.setVisibility(View.VISIBLE);
            Glide.with(MyApplication.getContext()).load(R.drawable.icon_check).into(holder.imgPicker);
        } else if (itemMeaningPicker.getIfRight() == ItemMeaningPicker.DEFAULT) {
            // 未选择状态
            holder.cardMeaning.setCardBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.colorCardWordDetail, MyApplication.getContext().getTheme()));
            holder.textMeaning.setTextColor(MyApplication.getContext().getResources().getColor(R.color.colorOnBackground, MyApplication.getContext().getTheme()));
            holder.imgPicker.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mItemMeaningPickerList.size();
    }

}
