package com.jediwus.learningapplication.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.LearningPlanActivity;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.database.UserPreference;
import com.jediwus.learningapplication.pojo.ItemWordBook;
import com.jediwus.learningapplication.myUtil.MyApplication;

import org.litepal.LitePal;

import java.util.List;

public class WordBookAdapter extends RecyclerView.Adapter<WordBookAdapter.ViewHolder> {

    private final List<ItemWordBook> mItemWordBookList;

    public WordBookAdapter(List<ItemWordBook> itemWordBookList) {
        this.mItemWordBookList = itemWordBookList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        ImageView imgBook;
        TextView textBookName, textBookSource, textBookWordNum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            imgBook = itemView.findViewById(R.id.item_img_book);
            textBookName = itemView.findViewById(R.id.item_text_book_name);
            textBookSource = itemView.findViewById(R.id.item_text_book_source);
            textBookWordNum = itemView.findViewById(R.id.item_text_book_word_num);
        }
    }


    @NonNull
    @Override
    public WordBookAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_book_list, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.view.setOnClickListener(view1 -> {

            int position = holder.getAdapterPosition();
            final ItemWordBook itemWordBook = mItemWordBookList.get(position);
            if (!itemWordBook.isFlagEnd()) {
                List<UserPreference> userPreferences = LitePal.where("userId = ?",
                        DataConfig.getWeChatNumLogged() + "").find(UserPreference.class);

                if (userPreferences.get(0).getCurrentBookId() == itemWordBook.getBookId() &&
                        userPreferences.get(0).getWordNeedReciteNum() != 0) {
                    Toast.makeText(MyApplication.getContext(), "这本书已经被你选择啦！", Toast.LENGTH_SHORT).show();
                } else {
                    // 更新用户数据
                    UserPreference userPreference = new UserPreference();
                    userPreference.setCurrentBookId(itemWordBook.getBookId());
                    userPreference.updateAll("userId = ?", DataConfig.getWeChatNumLogged() + "");

                    List<UserPreference> preferences = LitePal.findAll(UserPreference.class);
                    for (UserPreference preference : preferences) {
                        Log.d("TAG", "实际选择的书ID:" + itemWordBook.getBookId() + "，数据库中书ID：" + preference.getCurrentBookId() + "，用户ID：" + preference.getUserId());
                    }

                    Intent intent = new Intent(MyApplication.getContext(), LearningPlanActivity.class);
                    intent.putExtra(DataConfig.UPDATE_NAME, DataConfig.notUpdate);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MyApplication.getContext().startActivity(intent);
                }
            } else {
                Toast.makeText(MyApplication.getContext(), "芜湖~起飞！哎~飞~", Toast.LENGTH_SHORT).show();
            }


        });

        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WordBookAdapter.ViewHolder holder, int position) {
        ItemWordBook itemWordBook = mItemWordBookList.get(position);
        Glide.with(MyApplication.getContext()).load(itemWordBook.getBookImg()).into(holder.imgBook);
        holder.textBookName.setText(itemWordBook.getBookName());
        holder.textBookSource.setText(itemWordBook.getBookSource());
        // 这里设置成字符串
        holder.textBookWordNum.setText(itemWordBook.getBookWordNum() + "");
    }


    @Override
    public int getItemCount() {
        return mItemWordBookList.size();
    }
}
