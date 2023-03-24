package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.adapter.FavoritesWordListAdapter;
import com.jediwus.learningapplication.database.Favorites;
import com.jediwus.learningapplication.database.FavoritesLinkWord;
import com.jediwus.learningapplication.database.Translation;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.myUtil.MyApplication;
import com.jediwus.learningapplication.pojo.ItemWordList;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class FavoritesDetailActivity extends BaseActivity {

    public static int currentFavoritesId;

    private final List<ItemWordList> itemWordListList = new ArrayList<>();

    private final String[] editOption = {"修改名称", "修改备注"};

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites_detail);

        // 界面初始化
        TextView textTitle = findViewById(R.id.favorites_detail_title);
        ImageView imgModify = findViewById(R.id.favorites_detail_modify);
        ImageView imgPlay = findViewById(R.id.favorites_detail_img_play);
        ImageView imgBack = findViewById(R.id.favorites_detail_img_back);
        RecyclerView recyclerView = findViewById(R.id.favorites_detail_recycle_view);

        // 布局适配器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        FavoritesWordListAdapter favoritesWordListAdapter = new FavoritesWordListAdapter(
                FavoritesDetailActivity.this, itemWordListList, recyclerView);
        recyclerView.setAdapter(favoritesWordListAdapter);

        // 操作数据库
        List<Favorites> favoritesList = LitePal.where("id = ?", currentFavoritesId + "").find(Favorites.class);
        // 设置标题
        textTitle.setText(favoritesList.get(0).getName());

        List<FavoritesLinkWord> favoritesLinkWordList = LitePal
                .where("favoritesId = ?", currentFavoritesId + "")
                .find(FavoritesLinkWord.class);
        itemWordListList.clear();
        // 设置词条内容
        for (FavoritesLinkWord favoritesLinkWord : favoritesLinkWordList) {
            List<Word> wordList = LitePal.where("wordId = ?", favoritesLinkWord.getWordId() + "")
                    .select("wordId", "word")
                    .find(Word.class);
            Word word = wordList.get(0);
            itemWordListList.add(
                    new ItemWordList(
                            word.getWordId(),
                            word.getWord(),
                            getMeans(word.getWordId()),
                            false,
                            false
                    )
            );
        }
        favoritesWordListAdapter.notifyDataSetChanged();


        imgModify.setOnClickListener(viewModify -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(FavoritesDetailActivity.this);
            builder.setTitle("重定义单词夹");
            builder.setSingleChoiceItems(editOption, -1, (dialog, which) -> {
                final int type = which;
                // 延迟 200 毫秒取消对话框
                new Handler().postDelayed(() -> {
                    dialog.dismiss();
                    final View dialogView = LayoutInflater.from(FavoritesDetailActivity.this)
                            .inflate(R.layout.item_edit, null);
                    final EditText editText = dialogView.findViewById(R.id.edit_text);
                    if (type == 0) {
                        editText.setText(textTitle.getText().toString());
                    } else {
                        List<Favorites> favoritesList1 = LitePal
                                .where("id = ?", currentFavoritesId + "")
                                .find(Favorites.class);
                        editText.setText(favoritesList1.get(0).getRemark());
                    }
                    MaterialAlertDialogBuilder inputDialog = new MaterialAlertDialogBuilder(FavoritesDetailActivity.this);
                    if (type == 0) {
                        inputDialog.setTitle("编辑名称");
                    } else {
                        inputDialog.setTitle("编辑备注");
                    }
                    inputDialog.setView(dialogView)
                            .setPositiveButton("确定", (dialog1, which1) -> {
                                if (!TextUtils.isEmpty(editText.getText().toString().trim())) {
                                    Favorites favorites = new Favorites();
                                    if (type == 0) {
                                        favorites.setName(editText.getText().toString().trim());
                                        favorites.updateAll("id = ?", currentFavoritesId + "");
                                        dialog1.dismiss();
                                        textTitle.setText(editText.getText().toString().trim());
                                        Toast.makeText(FavoritesDetailActivity.this, "名称已更新", Toast.LENGTH_SHORT).show();
                                    } else {
                                        favorites.setRemark(editText.getText().toString().trim());
                                        favorites.updateAll("id = ?", currentFavoritesId + "");
                                        Toast.makeText(FavoritesDetailActivity.this, "备注已更新", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    if (type == 0) {
                                        Toast.makeText(FavoritesDetailActivity.this, "默认使用原名", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Favorites favorites = new Favorites();
                                        favorites.setToDefault("remark");
                                        favorites.updateAll("id = ?", currentFavoritesId + "");
                                        Toast.makeText(FavoritesDetailActivity.this, "备注已清空", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("取消", null).show();
                }, 200);
            }).show();
        });

        // 播放按钮点击事件
        imgPlay.setOnClickListener(viewPlay -> {

//            List<FavoritesLinkWord> folderLinkWords = LitePal
//                    .where("favoritesId = ?", currentFavoritesId + "")
//                    .find(FavoritesLinkWord.class);
//            if (!folderLinkWords.isEmpty()) {
//                WordController.needLearnWords.clear();
//                for (ItemWordList itemWordList : wordLists) {
//                    WordController.needLearnWords.add(itemWordList.getWordId());
//                }
//                WordController.justLearnedWords.clear();
//                WordController.needReviewWords.clear();
//                LearnWordActivity.lastWordMean = "";
//                LearnWordActivity.lastWord = "";
//                if (WordController.needLearnWords.size() != 0) {
//                    Intent intent = new Intent(FavoritesDetailActivity.this, LearnWordActivity.class);
//                    intent.putExtra(LearnWordActivity.MODE_NAME, LearnWordActivity.MODE_ONCE);
//                    startActivity(intent);
//                    Toast.makeText(FavoritesDetailActivity.this, "开始背单词", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(FavoritesDetailActivity.this, "请输入合法单词", Toast.LENGTH_SHORT).show();
//                }
//            }

        });

        imgBack.setOnClickListener(viewBack -> onBackPressed());

    }

    // 获取释义
    private String getMeans(int id) {
        List<Translation> translationList = LitePal.where("wordId = ?", id + "").find(Translation.class);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < translationList.size(); ++i) {
            stringBuilder.append(translationList.get(i).getWordType())
                    .append(". ")
                    .append(translationList.get(i).getCnMeaning());
            if (i != translationList.size() - 1)
                stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 动画效果
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_left);
    }
}