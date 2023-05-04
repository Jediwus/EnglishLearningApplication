package com.jediwus.learningapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.DisplayActivity;
import com.jediwus.learningapplication.config.ExternalData;
import com.jediwus.learningapplication.myUtil.MediaHelper;
import com.jediwus.learningapplication.myUtil.MyApplication;
import com.jediwus.learningapplication.pojo.ItemMatching;
import com.jediwus.learningapplication.pojo.MatchQueue;

import java.util.ArrayList;
import java.util.List;

public class MatchingAdapter extends RecyclerView.Adapter<MatchingAdapter.MyViewHolder> {

    private static final String TAG = "MatchingAdapter";
    /**
     * 所有选项 List
     */
    private final List<ItemMatching> mItemMatchingList;

    /**
     * 需进行匹配的队列
     */
    private final List<MatchQueue> matchQueueList = new ArrayList<>();

    public MatchingAdapter(List<ItemMatching> itemMatchingList) {
        this.mItemMatchingList = itemMatchingList;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        View view;
        CardView cardView;
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            cardView = itemView.findViewById(R.id.card_matching_item);
            textView = itemView.findViewById(R.id.text_matching_item_content);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matching, parent, false);
        MyViewHolder holder = new MyViewHolder(view);

        holder.view.setOnClickListener(view1 -> {
            int position = holder.getAdapterPosition();
            ItemMatching itemMatching = mItemMatchingList.get(position);
            // 当匹配队列为空时，加入用户点击的选项
            if (matchQueueList.isEmpty()) {
                matchQueueList.add(new MatchQueue(itemMatching.getWordId(), position));
                itemMatching.setSelected(true);
                notifyDataSetChanged();
            } else if (matchQueueList.size() == 1) {
                // 用户已选择一个选项，接下来需要进行正确与否的比对;
                // 只需要比对两个选项是否有相同 itemId 和 处在不同位置
                if (matchQueueList.get(0).getWordId() == itemMatching.getWordId() &&
                        matchQueueList.get(0).getPosition() != position) {
                    // 加入匹配队列
                    matchQueueList.add(new MatchQueue(itemMatching.getWordId(), position));
                    // 将两个选项放入临时 List<ItemMatching>
                    List<ItemMatching> matchingList = new ArrayList<>();
                    matchingList.add(mItemMatchingList.get(matchQueueList.get(0).getPosition()));
                    matchingList.add(mItemMatchingList.get(position));
                    // 将两个选项从 mItemMatchingList 移除
                    mItemMatchingList.removeAll(matchingList);

                    // 更新当前 position 的变化
                    notifyItemRemoved(position);
                    notifyItemChanged(position, mItemMatchingList.size());

                    // 更新第一个入队的 position 的变化
                    if (matchQueueList.get(0).getPosition() < position) {
                        notifyItemRemoved(matchQueueList.get(0).getPosition());
                        notifyItemChanged(matchQueueList.get(0).getPosition(), mItemMatchingList.size());
                    } else if (matchQueueList.get(0).getPosition() > position) {
                        notifyItemRemoved(matchQueueList.get(0).getPosition() - 1);
                        notifyItemChanged(matchQueueList.get(0).getPosition() - 1, mItemMatchingList.size());
                    }
                    // 清空匹配队列
                    matchQueueList.clear();
                    // 播放声音
                    MediaHelper.playLocalSource(ExternalData.REMOVE_TONE);

                    // 结算跳转
                    if (mItemMatchingList.isEmpty()) {
                        Log.d(TAG, "onCreateViewHolder: 跳转到结算界面");
                        Toast.makeText(MyApplication.getContext(), "Excellent!!!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MyApplication.getContext(), DisplayActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra(DisplayActivity.DISPLAY_TYPE, DisplayActivity.TYPE_MATCHING);
                        MyApplication.getContext().startActivity(intent);
                    }
                } else if (matchQueueList.get(0).getPosition() == position) {
                    // 选中同一个，清除选中状态
                    matchQueueList.clear();
                    itemMatching.setSelected(false);
                    notifyDataSetChanged();
                } else {
                    // 匹配失败
                    ItemMatching itemMatching1 = mItemMatchingList.get(matchQueueList.get(0).getPosition());
                    itemMatching1.setSelected(false);
                    ItemMatching itemMatching2 = mItemMatchingList.get(position);
                    itemMatching2.setSelected(false);
                    notifyDataSetChanged();
                    // 清空匹配队列
                    matchQueueList.clear();
                    // 播放声音
                    MediaHelper.playLocalSource(ExternalData.WRONG_TONE);
                    Toast.makeText(MyApplication.getContext(), "Nope~", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ItemMatching itemMatching = mItemMatchingList.get(position);
        // 设置文本内容
        holder.textView.setText(itemMatching.getContent());
        // 选中状态的颜色变化
        if (itemMatching.isSelected()) {
            holder.cardView.setCardBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.colorTertiaryContainer, MyApplication.getContext().getTheme()));
            holder.textView.setTextColor(MyApplication.getContext().getResources().getColor(R.color.colorOnTertiaryContainer, MyApplication.getContext().getTheme()));
        } else {
            holder.cardView.setCardBackgroundColor(MyApplication.getContext().getResources().getColor(R.color.colorCardWordDetail, MyApplication.getContext().getTheme()));
            holder.textView.setTextColor(MyApplication.getContext().getResources().getColor(R.color.colorOnSecondaryContainer, MyApplication.getContext().getTheme()));
        }
    }

    @Override
    public int getItemCount() {
        return mItemMatchingList.size();
    }

}
