package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.fragment.BottomSheetDialogFragmentCustom;
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
import com.jediwus.learningapplication.myUtil.FileUtil;
import com.jediwus.learningapplication.myUtil.LearningController;
import com.jediwus.learningapplication.myUtil.MediaHelper;
import com.jediwus.learningapplication.myUtil.MyPopupWindow;
import com.jediwus.learningapplication.pojo.ItemPhrase;
import com.jediwus.learningapplication.pojo.ItemSentence;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import me.grantland.widget.AutofitTextView;
import uk.co.senab.photoview.PhotoView;

public class WordDetailActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "WordDetailActivity";

    private ImageView imgStar;
    private ImageView imgDelete;
    private CardView cardPicCustom;

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
    private SentenceAdapter sentenceAdapter;
    private final List<ItemSentence> itemSentenceList = new ArrayList<>();

    // 英文释义
    private CardView cardEnglishInterpretation;
    private TextView textEnglishInterpretation;

    // 词组
    private CardView cardPhrase;
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

    // 单词List
    List<Word> words;

    // 当前单词ID
    public static int wordId;
    public static Word currentWord;

    // 自定义图片数据
    private byte[] imgByte;
    private Bitmap bitmap;

    private int currentType;

    private AlertDialog dialog;
//    private ProgressDialog progressDialog;

    public static final String TYPE = "LearningOrCheck";
    public static final int TYPE_LEARNING = 1;
    public static final int TYPE_CHECK = 2;

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
                    dialog.dismiss();
                    cardPicCustom.setVisibility(View.VISIBLE);
                    Toast.makeText(WordDetailActivity.this,
                            "自定义图片设置成功！",
                            Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    // 处理类型为2的消息
                    break;
                // 可以根据实际需求添加更多的case分支
                default:
                    break;
            }
        }
    };

    public static ActivityResultLauncher<Intent> launcher;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_detail);

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                        // 获取系统返回的照片的Uri
                        Uri selectedImage = result.getData().getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        // 从系统表中查询指定 Uri 对应的照片
                        Cursor cursor = getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                        // 获取照片路径
                        String path = cursor.getString(columnIndex);
                        cursor.close();
                        bitmap = BitmapFactory.decodeFile(path);
                        showProgressDialog();

                        // handler处理消息
                        imgByte = FileUtil.bitmapCompress(bitmap, 1000);
                        Message message = new Message();
                        message.what = FINISH;
                        handler.sendMessage(message);
                    }
                });


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

        RelativeLayout layoutPicCustom = findViewById(R.id.layout_word_detail_picture);
        layoutPicCustom.setOnClickListener(this);

        cardPicCustom = findViewById(R.id.img_detail_picture_custom);

        RelativeLayout layoutFolder = findViewById(R.id.layout_word_detail_favorites);
        layoutFolder.setOnClickListener(this);

        RelativeLayout layoutStar = findViewById(R.id.layout_word_detail_star);
        layoutStar.setOnClickListener(this);
        imgStar = findViewById(R.id.img_word_detail_star);

        RelativeLayout layoutMore = findViewById(R.id.layout_word_detail_more);
        layoutMore.setOnClickListener(this);

        textTranslation = findViewById(R.id.text_word_detail_interpretation);

        cardPicture = findViewById(R.id.card_word_detail_pic);
        imgPicture = findViewById(R.id.img_word_detail_pic);

        cardRemMind = findViewById(R.id.card_word_detail_remMethod);

        textRemMind = findViewById(R.id.text_word_detail_remMethod);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setSmoothScrollbarEnabled(true);

        RecyclerView recyclerSentence = findViewById(R.id.recycler_word_detail_sentence);
        recyclerSentence.setLayoutManager(linearLayoutManager);
        recyclerSentence.setHasFixedSize(false);
        recyclerSentence.setNestedScrollingEnabled(false);
        recyclerSentence.setFocusable(false);

        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(this);
        linearLayoutManager1.setSmoothScrollbarEnabled(true);

        RecyclerView recyclerPhrase = findViewById(R.id.recycler_word_detail_phrase);
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

        RelativeLayout layoutDelete = findViewById(R.id.layout_word_detail_delete);
        layoutDelete.setOnClickListener(this);
        imgDelete = findViewById(R.id.img_word_detail_delete);

        RelativeLayout layoutVoice = findViewById(R.id.layout_word_detail_voice);
        layoutVoice.setOnClickListener(this);

        // 操作栏
        RelativeLayout layoutContinue = findViewById(R.id.layout_word_detail_continue);
        layoutContinue.setOnClickListener(this);
        TextView textContinue = findViewById(R.id.text_word_detail_continue);

        //------------------------------------界面初始化完毕--------------------------------------


        currentType = getIntent().getIntExtra(TYPE, 0);
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


    @SuppressLint({"NonConstantResourceId", "ResourceAsColor"})
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
                    final MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(WordDetailActivity.this);
                    builder.setTitle("保存至单词夹")
                            .setSingleChoiceItems(favoritesNames, -1, (dialog, which) -> {
                                // 延迟400毫秒取消对话框
                                new Handler().postDelayed(() -> {
                                    dialog.dismiss();
                                    List<FavoritesLinkWord> favoritesLinkWordList = LitePal
                                            .where("wordId = ? and favoritesId = ?",
                                                    currentWord.getWordId() + "",
                                                    favoritesList.get(which).getId() + "")
                                            .find(FavoritesLinkWord.class);
                                    if (favoritesLinkWordList.isEmpty()) {
                                        FavoritesLinkWord folderLinkWord = new FavoritesLinkWord();
                                        folderLinkWord.setFavoritesId(favoritesList.get(which).getId());
                                        folderLinkWord.setWordId(currentWord.getWordId());
                                        folderLinkWord.save();
                                        Toast.makeText(WordDetailActivity.this, "已存入 " + favoritesNames[which], Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(WordDetailActivity.this, "该词已在 " + favoritesNames[which] + " 中了哦", Toast.LENGTH_SHORT).show();
                                    }
                                }, 400);
                            }).show();
                }
                break;

            // 自定义图片按钮点击事件处理
            case R.id.layout_word_detail_picture:
                // 创建一个新的 PopupWindow
                MyPopupWindow popupWindow = new MyPopupWindow(WordDetailActivity.this);
                popupWindow.setContentView(R.layout.floating_layout);
                PhotoView imageView = popupWindow.findViewById(R.id.floating_layout_imageview);
                TextView titleView = popupWindow.findViewById(R.id.floating_layout_title_textview);
                // 搜索图片数据
                Word word1 = LitePal.where("wordId = ?", currentWord.getWordId() + "")
                        .select("wordId", "word", "picCustom")
                        .find(Word.class).get(0);
                // 添加标题
                titleView.setText(word1.getWord());
                // 添加图片
                Glide.with(WordDetailActivity.this).load(word1.getPicCustom()).into(imageView);

                popupWindow.setOutSideDismiss(true)
                        .setPopupGravity(Gravity.CENTER)
                        .showPopupWindow();
                break;

            // 收藏按钮点击事件处理
            case R.id.layout_word_detail_star:
                if (currentWord.getIsCollected() == 1) {
                    Glide.with(this).load(R.drawable.icon_star).into(imgStar);
                    Word word = new Word();
                    word.setToDefault("isCollected");
                    word.updateAll("wordId = ?", wordId + "");
                    Toast.makeText(this, "取消生词标记", Toast.LENGTH_SHORT).show();
                } else {
                    Glide.with(this).load(R.drawable.icon_star_selected).into(imgStar);
                    Word word = new Word();
                    word.setIsCollected(1);
                    word.updateAll("wordId = ?", wordId + "");
                    Toast.makeText(this, "已加入生词本", Toast.LENGTH_SHORT).show();
                }
                currentWord = LitePal.where("wordId = ?", wordId + "").find(Word.class).get(0);
                break;

            // 修改数据按钮点击事件处理
            case R.id.layout_word_detail_more:

                BottomSheetDialogFragmentCustom bottomSheet = new BottomSheetDialogFragmentCustom();
                bottomSheet.show(getSupportFragmentManager(), "tag");

                break;

            // 简单词——降低重视度 按钮点击事件处理
            case R.id.layout_word_detail_delete:
                if (currentWord.getIsEasy() == 1) {
                    Glide.with(this).load(R.drawable.icon_delete).into(imgDelete);
                    Word word = new Word();
                    word.setToDefault("isEasy");
                    word.updateAll("wordId = ?", wordId + "");
                    Toast.makeText(this, "已取消熟知词标记", Toast.LENGTH_SHORT).show();
                } else {
                    Glide.with(this).load(R.drawable.icon_delete_easy).into(imgDelete);
                    Word word = new Word();
                    word.setIsEasy(1);
                    word.updateAll("wordId = ?", wordId + "");
                    Toast.makeText(this, "已标记为熟知词", Toast.LENGTH_SHORT).show();
                }
                currentWord = LitePal.where("wordId = ?", wordId + "").find(Word.class).get(0);
                if (currentType == TYPE_LEARNING) {
                    LearningController.removeSelectedWord(wordId);
                    LearningActivity.flagNeedRefresh = true;
                    ActivityCollector.startOtherActivity(WordDetailActivity.this, LearningActivity.class);
                }
                break;

            // 发音按钮点击事件处理（默认为英式发音）
            case R.id.layout_word_detail_voice:
                new Thread(() -> MediaHelper.play(words.get(0).getWord())).start();
                break;

            // 继续/返回按钮点击事件处理
            case R.id.layout_word_detail_continue:
                onBackPressed();
                break;

            default:
                break;
        }

    }

    private void showProgressDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(WordDetailActivity.this);
        builder.setTitle("稍候");
        builder.setMessage("图片压缩中...");
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // 设置退出的动画效果
        overridePendingTransition(android.R.anim.fade_in, R.anim.slide_out_bottom);

        LearningActivity.flagNeedRefresh = currentType != TYPE_CHECK;
        /* // 下面是简化之前的 if 语句
        if (currentType == TYPE_CHECK) {
            LearningActivity.flagNeedRefresh = false;
        } else {
            LearningActivity.flagNeedRefresh = true;
        }*/
        MediaHelper.releaseMediaPlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}