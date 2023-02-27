package com.jediwus.learningapplication.activity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.database.Word;

import java.util.ArrayList;
import java.util.List;

import me.grantland.widget.AutofitTextView;

public class WordDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "WordDetailActivity";

    // 操作栏
    private RelativeLayout layoutContinue, layoutVoice, layoutDelete;
    private RelativeLayout layoutStar, layoutMore, layoutPicCustom, layoutFolder;
    private ImageView imgStar, imgDelete, imgPicCustom;
    private CardView cardPicCustom;
    private TextView textContinue;

    // 单词
    private AutofitTextView textWordName;

    // 单词发音
    private LinearLayout layoutPhoneUk, layoutPhoneUs;
    private TextView textPhoneUk, textPhoneUs;

    // 单词释义
    private TextView textInterpretation;

    // 巧记
    private CardView cardRemMind;
    private TextView textRemMind;

    // 例句
    private CardView cardSentence;
    private RecyclerView recyclerSentence;
//    private DetailSentenceAdapter detailSentenceAdapter;
//    private List<ItemSentence> itemSentenceList = new ArrayList<>();

    // 英文释义
    private CardView cardEnglish;
    private TextView textEnglish;

    // 词组
    private CardView cardPhrase;
    private RecyclerView recyclerPhrase;
//    private DetailPhraseAdapter detailPhraseAdapter;
//    private List<ItemPhrase> itemPhraseList = new ArrayList<>();

    // 图片记忆
    private CardView cardPic;
    private ImageView imgPic;

    // 备注
    private CardView cardRemark;
    private TextView textRemark;

    // 单词
    List<Word> words;

    // 传入的单词ID
    public static int wordId;
    private Word currentWord;

    public static final String TYPE_NAME = "typeName";
    public static final int TYPE_LEARN = 1;
    public static final int TYPE_GENERAL = 2;

    private int currentType;

    private String[] mores = {"更改释义", "更改例句", "修改备注", "增加/修改自定义图片"};

    private final int IMAGE_REQUEST_CODE = 1;

    private ProgressDialog progressDialog;

    private byte[] imgByte;
    private Bitmap bitmap;

    private final int FINISH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);
    }

    @Override
    public void onClick(View view) {

    }
}