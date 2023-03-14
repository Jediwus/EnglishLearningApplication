package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.adapter.PhraseAdapter;
import com.jediwus.learningapplication.adapter.SentenceAdapter;
import com.jediwus.learningapplication.database.Conjugation;
import com.jediwus.learningapplication.database.ConjugationItems;
import com.jediwus.learningapplication.database.Favorites;
import com.jediwus.learningapplication.database.FavoritesLinkWord;
import com.jediwus.learningapplication.database.Phrase;
import com.jediwus.learningapplication.database.Sentence;
import com.jediwus.learningapplication.database.Synonym;
import com.jediwus.learningapplication.database.SynonymItems;
import com.jediwus.learningapplication.database.Translation;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.myUtil.ActivityCollector;
import com.jediwus.learningapplication.myUtil.MediaHelper;
import com.jediwus.learningapplication.pojo.ItemPhrase;
import com.jediwus.learningapplication.pojo.ItemSentence;

import org.litepal.LitePal;

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
    private TextView textTranslation;

    // 巧记
    private CardView cardRemMind;
    private TextView textRemMind;

    // 例句
    private CardView cardSentence;
    private RecyclerView recyclerSentence;
    private SentenceAdapter sentenceAdapter;
    private final List<ItemSentence> itemSentenceList = new ArrayList<>();

    // 英文释义
    private CardView cardEnglishInterpretation;
    private TextView textEnglishInterpretation;

    // 词组
    private CardView cardPhrase;
    private RecyclerView recyclerPhrase;
    private PhraseAdapter phraseAdapter;
    private final List<ItemPhrase> itemPhraseList = new ArrayList<>();

    // 图片记忆
    private CardView cardPicture;
    private ImageView imgPicture;

    // 同根词
    private CardView cardConjugation;
    private TextView textConjugation;


    // 同近义词
    private CardView cardSynonym;
    private TextView textSynonym;

    // 备注
    private CardView cardRemark;
    private TextView textRemark;

    // 单词
    List<Word> words;

    // 传入的单词ID
    public static int wordId;
    private Word currentWord;

    public static final String TYPE_NAME = "typeName";
    public static final int TYPE_LEARNING = 1;
    public static final int TYPE_CHECK = 2;

    private int currentType;

    private final String[] modificationItem = {"更替释义", "更替例句", "修改备注", "修改自定义图片"};

    private final int IMAGE_REQUEST_CODE = 1;

    private ProgressDialog progressDialog;

    private byte[] imgByte;
    private Bitmap bitmap;

    private final int FINISH = 1;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case FINISH:
                    Word word = new Word();
                    word.setPicCustom(imgByte);
                    word.updateAll("wordId = ?", currentWord.getWordId() + "");
                    progressDialog.dismiss();
                    cardPicCustom.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    // 处理类型为2的消息
                    break;
                // 可以根据实际需求添加更多的case分支
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

//        overridePendingTransition(R.anim.slide_in_bottom, android.R.anim.fade_out);
//        overridePendingTransition(R.anim.slide_in_bottom, android.R.anim.fade_out);

        //------------------------------------界面初始化开始--------------------------------------
        // 单词本体
        textWordName = findViewById(R.id.text_word_detail_name);

        // 英式发音布局
        layoutPhoneUk = findViewById(R.id.layout_word_pronounce_uk);
        layoutPhoneUk.setOnClickListener(this);

        // 美式发音布局
        layoutPhoneUs = findViewById(R.id.layout_word_pronounce_us);
        layoutPhoneUs.setOnClickListener(this);

        // 英式发音文本
        textPhoneUk = findViewById(R.id.text_word_pronounce_uk);

        // 美式发音文本
        textPhoneUs = findViewById(R.id.text_word_pronounce_us);

        layoutPicCustom = findViewById(R.id.layout_word_detail_picture);
        layoutPicCustom.setOnClickListener(this);
        imgPicCustom = findViewById(R.id.img_word_detail_picture_custom);

        cardPicCustom = findViewById(R.id.img_detail_picture_custom);

        layoutFolder = findViewById(R.id.layout_word_detail_favorites);
        layoutFolder.setOnClickListener(this);

        layoutStar = findViewById(R.id.layout_word_detail_star);
        layoutStar.setOnClickListener(this);
        imgStar = findViewById(R.id.img_word_detail_star);

        layoutMore = findViewById(R.id.layout_word_detail_more);
        layoutMore.setOnClickListener(this);

        textTranslation = findViewById(R.id.text_word_detail_interpretation);

        cardPicture = findViewById(R.id.card_word_detail_pic);
        imgPicture = findViewById(R.id.img_word_detail_pic);

        cardRemMind = findViewById(R.id.card_word_detail_remMethod);

        textRemMind = findViewById(R.id.text_word_detail_remMethod);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setSmoothScrollbarEnabled(true);

        recyclerSentence = findViewById(R.id.recycler_word_detail_sentence);
        recyclerSentence.setLayoutManager(linearLayoutManager);
        recyclerSentence.setHasFixedSize(false);
        recyclerSentence.setNestedScrollingEnabled(false);
        recyclerSentence.setFocusable(false);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setSmoothScrollbarEnabled(true);

        recyclerPhrase = findViewById(R.id.recycler_word_detail_phrase);
        recyclerPhrase.setLayoutManager(linearLayoutManager1);
        recyclerPhrase.setHasFixedSize(false);
        recyclerPhrase.setNestedScrollingEnabled(false);
        recyclerPhrase.setFocusable(false);

        phraseAdapter = new PhraseAdapter(itemPhraseList);
        recyclerPhrase.setAdapter(phraseAdapter);

        sentenceAdapter = new SentenceAdapter(itemSentenceList);
        recyclerSentence.setAdapter(sentenceAdapter);

        cardSentence = findViewById(R.id.card_word_detail_sentence);
        cardEnglishInterpretation = findViewById(R.id.card_word_detail_english_interpretation);
        textEnglishInterpretation = findViewById(R.id.text_word_detail_english_interpretation);
        cardPhrase = findViewById(R.id.card_word_detail_phrase);

        cardConjugation = findViewById(R.id.card_word_detail_rel_word);
        textConjugation = findViewById(R.id.text_word_detail_rel_word);

        cardSynonym = findViewById(R.id.card_word_detail_synonym);
        textSynonym = findViewById(R.id.text_word_detail_synonym);

        cardRemark = findViewById(R.id.card_word_detail_remark);
        textRemark = findViewById(R.id.text_word_detail_remark);

        layoutDelete = findViewById(R.id.layout_word_detail_delete);
        layoutDelete.setOnClickListener(this);
        imgDelete = findViewById(R.id.img_word_detail_delete);

        layoutVoice = findViewById(R.id.layout_word_detail_voice);
        layoutVoice.setOnClickListener(this);

        layoutContinue = findViewById(R.id.layout_word_detail_continue);
        layoutContinue.setOnClickListener(this);
        textContinue = findViewById(R.id.text_word_detail_continue);

        //------------------------------------界面初始化完毕--------------------------------------


        currentType = getIntent().getIntExtra(TYPE_NAME, 0);
        if (currentType == TYPE_CHECK) {
            textContinue.setText("返 回");
        } else {
            textContinue.setText("继 续");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        InitializeData();
    }

    private void InitializeData() {
        words = LitePal.where("wordId = ?", wordId + "").find(Word.class);
        currentWord = words.get(0);

        // 设置名称
        textWordName.setText(currentWord.getWord());

        // 设置英音
        if (currentWord.getUkPhone() != null) {
            layoutPhoneUk.setVisibility(View.VISIBLE);
            textPhoneUk.setText(currentWord.getUkPhone());
        } else {
            layoutPhoneUk.setVisibility(View.GONE);
        }

        // 设置美音
        if (currentWord.getUsPhone() != null) {
            layoutPhoneUs.setVisibility(View.VISIBLE);
            textPhoneUs.setText(currentWord.getUsPhone());
        } else {
            layoutPhoneUs.setVisibility(View.GONE);
        }

        // 自定义图片
        if (currentWord.getPicCustom() != null) {
            cardPicCustom.setVisibility(View.VISIBLE);
        } else {
            cardPicCustom.setVisibility(View.GONE);
        }

        // 设置收藏
        if (currentWord.getIsCollected() == 1) {
            Glide.with(this).load(R.drawable.icon_star_selected).into(imgStar);
        } else {
            Glide.with(this).load(R.drawable.icon_star).into(imgStar);
        }

        // 设置中英文释义
        List<Translation> translationList = LitePal.where("wordId = ?", wordId + "").find(Translation.class);
        StringBuilder chinese = new StringBuilder();
        StringBuilder english = new StringBuilder();
        ArrayList<String> chineseMeaning = new ArrayList<>();
        ArrayList<String> englishMeaning = new ArrayList<>();
        for (int i = 0; i < translationList.size(); ++i) {
            chineseMeaning.add(translationList.get(i).getWordType() + ". " + translationList.get(i).getCnMeaning());
            if (translationList.get(i).getEnMeaning() != null) {
                englishMeaning.add("\n" + translationList.get(i).getWordType() + ". " + translationList.get(i).getEnMeaning());
            }
        }
        for (int i = 0; i < chineseMeaning.size(); ++i) {
            if (i != chineseMeaning.size() - 1) {
                chinese.append(chineseMeaning.get(i))
                        .append("\n");
            } else {
                chinese.append(chineseMeaning.get(i));
            }
        }
        textTranslation.setText(chinese.toString());
        if (englishMeaning.size() > 0) {
            cardEnglishInterpretation.setVisibility(View.VISIBLE);
            for (int i = 0; i < englishMeaning.size(); ++i) {
                if (i == 0) {
                    english.append(englishMeaning.get(i).replace("\n", ""))
                            .append("\n");
                } else if (i != englishMeaning.size() - 1) {
                    english.append(englishMeaning.get(i))
                            .append("\n");
                } else {
                    english.append(englishMeaning.get(i));
                }
            }
            textEnglishInterpretation.setText(english.toString());
        } else {
            cardEnglishInterpretation.setVisibility(View.GONE);
        }

        // 设置图片记忆
        if (currentWord.getPicAddress() != null) {
            Glide.with(this)
                    .load(currentWord.getPicAddress())
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imgPicture);
            Log.d(TAG, currentWord.getPicAddress());
            cardPicture.setVisibility(View.VISIBLE);
        } else {
            cardPicture.setVisibility(View.GONE);
        }

        // 设置巧记
        if (currentWord.getRemMethod() != null) {
            textRemMind.setText(currentWord.getRemMethod());
            cardRemMind.setVisibility(View.VISIBLE);
        } else {
            cardRemMind.setVisibility(View.GONE);
        }

        // 设置例句
        List<Sentence> sentenceList = LitePal.where("wordId = ?", wordId + "").find(Sentence.class);
        if (!sentenceList.isEmpty()) {
            cardSentence.setVisibility(View.VISIBLE);
            setSentenceData(sentenceList);
        } else {
            cardSentence.setVisibility(View.GONE);
        }

        // 设置词组
        List<Phrase> phraseList = LitePal.where("wordId = ?", wordId + "").find(Phrase.class);
        if (!phraseList.isEmpty()) {
            cardPhrase.setVisibility(View.VISIBLE);
            setPhraseData(phraseList);
        } else {
            cardPhrase.setVisibility(View.GONE);
        }

        // 设置同根词
        List<Conjugation> conjugationList = LitePal.where("wordId = ?", wordId + "").find(Conjugation.class);
        List<ConjugationItems> conjugationItemsList = LitePal.findAll(ConjugationItems.class);
        StringBuilder conjugationBuilder = new StringBuilder();
        ArrayList<String> conjugationContent = new ArrayList<>();
        if (!conjugationList.isEmpty()) {
            cardConjugation.setVisibility(View.VISIBLE);
            for (int i = 0; i < conjugationList.size(); i++) {
                conjugationContent.add("\n" + conjugationList.get(i).getPos() + ". ");
                for (int j = 0; j < conjugationItemsList.size(); j++) {
                    if (conjugationItemsList.get(j).getConjugationId()
                            > conjugationList.get(i).getId()) {
                        break;
                    } else if (conjugationItemsList.get(j).getConjugationId()
                            == conjugationList.get(i).getId()) {
                        conjugationContent.add("▷ " + conjugationItemsList.get(j).getEnConjugation() +
                                "\t" + conjugationItemsList.get(j).getCnConjugation());
                    }
                }
            }
            for (int i = 0; i < conjugationContent.size(); i++) {
                if (i == 0) {
                    conjugationBuilder.append(conjugationContent.get(i).replace("\n", ""))
                            .append("\n");
                } else if (i != conjugationContent.size() - 1) {
                    conjugationBuilder.append(conjugationContent.get(i))
                            .append("\n");
                } else {
                    conjugationBuilder.append(conjugationContent.get(i));
                }
            }
            textConjugation.setText(conjugationBuilder.toString());

        } else {
            cardConjugation.setVisibility(View.GONE);
        }

        // 设置同近义词
        List<Synonym> synonymList = LitePal.where("wordId = ?", wordId + "").find(Synonym.class);
        List<SynonymItems> synonymItemsList = LitePal.findAll(SynonymItems.class);
        StringBuilder synonymBuilder = new StringBuilder();
        ArrayList<String> synonymContent = new ArrayList<>();
        if (!synonymList.isEmpty()) {
            cardSynonym.setVisibility(View.VISIBLE);
            for (int i = 0; i < synonymList.size(); i++) {
                synonymContent.add("\n" + synonymList.get(i).getPos() + ". " + synonymList.get(i).getTran());
                for (int j = 0; j < synonymItemsList.size(); j++) {
                    if (synonymItemsList.get(j).getSynonymId() > synonymList.get(i).getId()) {
                        break;
                    } else if (synonymItemsList.get(j).getSynonymId() == synonymList.get(i).getId()) {
                        synonymContent.add("◇ " + synonymItemsList.get(j).getItemWords());
                    }
                }
            }
            for (int i = 0; i < synonymContent.size(); i++) {
                if (i == 0) {
                    synonymBuilder.append(synonymContent.get(i).replace("\n", ""))
                            .append("\n");
                } else if (i != synonymContent.size() - 1) {
                    synonymBuilder.append(synonymContent.get(i))
                            .append("\n");
                } else {
                    synonymBuilder.append(synonymContent.get(i));
                }
            }
            textSynonym.setText(synonymBuilder.toString());

        } else {
            cardSynonym.setVisibility(View.GONE);
        }

        // 设置备注
        if (currentWord.getRemark() != null) {
            cardRemark.setVisibility(View.VISIBLE);
            textRemark.setText(currentWord.getRemark());
        } else {
            cardRemark.setVisibility(View.GONE);
        }

        // 垃圾桶按钮，设置目的在于将该单词标记为简单
        if (currentWord.getIsEasy() == 1) {
            Glide.with(this).load(R.drawable.icon_delete_easy).into(imgDelete);
        } else {
            Glide.with(this).load(R.drawable.icon_delete).into(imgDelete);
        }

    }


    @SuppressLint("NotifyDataSetChanged")
    private void setSentenceData(List<Sentence> sentenceList) {
        itemSentenceList.clear();
        for (Sentence sentence : sentenceList) {
            itemSentenceList.add(new ItemSentence(sentence.getCnSentence(), sentence.getEnSentence()));
        }
        sentenceAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setPhraseData(List<Phrase> phraseList) {
        itemPhraseList.clear();
        for (Phrase phrase : phraseList) {
            itemPhraseList.add(new ItemPhrase(phrase.getCnPhrase(), phrase.getEnPhrase()));
        }
        phraseAdapter.notifyDataSetChanged();
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            // 英式发音按钮点击事件处理
            case R.id.layout_word_pronounce_uk:
                MediaHelper.play(MediaHelper.ENGLISH_VOICE, words.get(0).getWord());
                break;

            // 美式发音按钮点击事件处理
            case R.id.layout_word_pronounce_us:
                MediaHelper.play(MediaHelper.AMERICA_VOICE, words.get(0).getWord());
                break;

            // 收藏夹按钮点击事件处理
            case R.id.layout_word_detail_favorites:
                final List<Favorites> favoritesList = LitePal.findAll(Favorites.class);
                if (favoritesList.isEmpty()) {
                    Toast.makeText(this, "好像没找到单词夹...先去创建一个吧", Toast.LENGTH_SHORT).show();
                } else {
                    String[] favoritesNames = new String[favoritesList.size()];
                    for (int i = 0; i < favoritesList.size(); ++i) {
                        favoritesNames[i] = favoritesList.get(i).getName();
                    }
                    final AlertDialog.Builder builder = new AlertDialog.Builder(WordDetailActivity.this);
                    builder.setTitle("选择单词夹进行保存")
                            .setSingleChoiceItems(favoritesNames, -1, (dialog, which) -> {
                                // 延迟500毫秒取消对话框
                                new Handler().postDelayed(() -> {
                                    dialog.dismiss();
                                    List<FavoritesLinkWord> favoritesLinkWordList = LitePal
                                            .where("wordId = ? and folderId = ?",
                                                    currentWord.getWordId() + "",
                                                    favoritesList.get(which).getId() + "")
                                            .find(FavoritesLinkWord.class);
                                    if (favoritesLinkWordList.isEmpty()) {
                                        FavoritesLinkWord folderLinkWord = new FavoritesLinkWord();
                                        folderLinkWord.setFolderId(favoritesList.get(which).getId());
                                        folderLinkWord.setWordId(currentWord.getWordId());
                                        folderLinkWord.save();
                                        Toast.makeText(WordDetailActivity.this, "单词已成功保存！", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(WordDetailActivity.this, "该单词已经在此单词夹中了哦", Toast.LENGTH_SHORT).show();
                                    }
                                }, 200);
                            }).show();
                }
                break;

            // 自定义图片按钮点击事件处理
            case R.id.layout_word_detail_picture:
                Intent intent = new Intent(WordDetailActivity.this, CustomPictureActivity.class);
                intent.putExtra(CustomPictureActivity.TYPE_WORD_ID, currentWord.getWordId());
                startActivity(intent);
                break;

            // 收藏按钮点击事件处理
            case R.id.layout_word_detail_star:
                if (currentWord.getIsCollected() == 1) {
                    Glide.with(this).load(R.drawable.icon_star).into(imgStar);
                    Word word = new Word();
                    word.setToDefault("isCollected");
                    word.updateAll("wordId = ?", wordId + "");
                    Toast.makeText(this, "取消收藏", Toast.LENGTH_SHORT).show();
                } else {
                    Glide.with(this).load(R.drawable.icon_star_selected).into(imgStar);
                    Word word = new Word();
                    word.setIsCollected(1);
                    word.updateAll("wordId = ?", wordId + "");
                    Toast.makeText(this, "收藏成功", Toast.LENGTH_SHORT).show();
                }
                currentWord = LitePal.where("wordId = ?", wordId + "").find(Word.class).get(0);
                break;

            // 修改数据按钮点击事件处理
            case R.id.layout_word_detail_more:
                final AlertDialog.Builder builder = new AlertDialog.Builder(WordDetailActivity.this);
                builder.setTitle("选择您要进行的操作")
                        .setSingleChoiceItems(modificationItem, -1, (dialogInterface, i) -> {
                            // 延迟500毫秒取消对话框
                            new Handler().postDelayed(() -> {
                                dialogInterface.dismiss();
                                Intent intent1 = new Intent(WordDetailActivity.this, ModifyDataActivity.class);
                                switch (i) {
                                    // 更改释义
                                    case 0:
                                        intent1.putExtra(ModifyDataActivity.MODE_NAME, ModifyDataActivity.MODE_MEANS);
                                        intent1.putExtra(ModifyDataActivity.WORD_ID_NAME, currentWord.getWordId());
                                        startActivity(intent1);
                                        break;
                                    // 增加例句
                                    case 1:
                                        intent1.putExtra(ModifyDataActivity.MODE_NAME, ModifyDataActivity.MODE_SENTENCES);
                                        intent1.putExtra(ModifyDataActivity.WORD_ID_NAME, currentWord.getWordId());
                                        startActivity(intent1);
                                        break;
                                    // 增加备注
                                    case 2:
                                        intent1.putExtra(ModifyDataActivity.MODE_NAME, ModifyDataActivity.MODE_REMARKS);
                                        intent1.putExtra(ModifyDataActivity.WORD_ID_NAME, currentWord.getWordId());
                                        startActivity(intent1);
                                        break;
                                    // 自定义图片
                                    case 3:
                                        AlertDialog.Builder builder2 = new AlertDialog.Builder(WordDetailActivity.this);
                                        builder2.setTitle("警告")
                                                .setMessage("该操作会覆盖原图片,确定要继续吗")
                                                .setPositiveButton("是的没错！", (dialogInterface1, i1) -> {
                                                    //在这里跳转到手机系统相册里面
                                                    Intent intent11 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                                    startActivityForResult(intent11, IMAGE_REQUEST_CODE);
                                                })
                                                .setNegativeButton("算了...", null)
                                                .show();
                                }

                            }, 200);
                        })
                        .setNegativeButton("取消", (dialogInterface, i) -> {
                            // 用户点击了“取消”按钮，不执行任何操作
                        })
                        .show();
                break;

            // 修改数据按钮点击事件处理
            case R.id.layout_word_detail_delete:
                if (currentWord.getIsEasy() == 1) {
                    Glide.with(this).load(R.drawable.icon_delete).into(imgDelete);
                    Word word = new Word();
                    word.setToDefault("isEasy");
                    word.updateAll("wordId = ?", wordId + "");
                    Toast.makeText(this, "已取消简单词标记", Toast.LENGTH_SHORT).show();
                } else {
                    Glide.with(this).load(R.drawable.icon_delete_easy).into(imgDelete);
                    Word word = new Word();
                    word.setIsEasy(1);
                    word.updateAll("wordId = ?", wordId + "");
                    Toast.makeText(this, "已标记为简单词", Toast.LENGTH_SHORT).show();
                }
                currentWord = LitePal.where("wordId = ?", wordId + "").find(Word.class).get(0);
                if (currentType == TYPE_LEARNING) {
//                    LearningController.removeOneWord(wordId);
                    ActivityCollector.startOtherActivity(WordDetailActivity.this, LearningActivity.class);
                }
                break;

            case R.id.layout_word_detail_voice:
                new Thread(() -> MediaHelper.play(words.get(0).getWord())).start();
                break;

            case R.id.layout_word_detail_continue:
                if (currentType == TYPE_CHECK) {
                    onBackPressed();
                } else {
                    LearningActivity.needUpdate = true;
                    onBackPressed();
                }
                break;

            default:
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 设置退出的动画效果
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_bottom);

//        ListActivity.isUpdate = true;
//        LearnWordActivity.needUpdate = true;
        MediaHelper.releaseMediaPlayer();
    }

}