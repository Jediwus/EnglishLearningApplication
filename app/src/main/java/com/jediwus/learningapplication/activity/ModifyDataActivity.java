package com.jediwus.learningapplication.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.adapter.ModifyPhraseAdapter;
import com.jediwus.learningapplication.adapter.ModifySentenceAdapter;
import com.jediwus.learningapplication.database.Phrase;
import com.jediwus.learningapplication.database.Sentence;
import com.jediwus.learningapplication.database.Translation;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.pojo.ItemModifyPhrase;
import com.jediwus.learningapplication.pojo.ItemModifySentence;
import com.jediwus.learningapplication.pojo.ModifyTranslation;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ModifyDataActivity extends BaseActivity {

    public static final String WORD_ID = "modifyWordId";

    public static final String MODE_NAME = "modifyName";

    public static final String MODE_MEANS = "modifyMeans";

    public static final String MODE_PHRASE = "modifySentences";

    public static final String MODE_MEMO = "modifyMemo";

    public static final String MODE_SENTENCE = "modifySentence";

    private String currentMode;

    private int currentWordId;

    private int currentMeansType = 0;

    private final Map<Integer, ModifyTranslation> translationMap = new HashMap<>();

    private final List<ItemModifyPhrase> itemModifyPhraseList = new ArrayList<>();

    private final List<ItemModifySentence> itemModifySentenceList = new ArrayList<>();

    private ModifyPhraseAdapter modifyPhraseAdapter;

    private ModifySentenceAdapter modifySentenceAdapter;

    private final String[] meansTypes = {
            "名词 - n.",
            "代词 - pron.",
            "形容词 - adj.",
            "副词 - adv.",
            "动词 - v.",
            "及物动词 - vt.",
            "不及物动词 - vi.",
            "数词 - num.",
            "冠词 - art.",
            "介词 - prep.",
            "连词 - conj.",
            "叹词 - interj."
    };

    private final String[] types = {
            "n",
            "pron",
            "adj",
            "adv",
            "v",
            "vt",
            "vi",
            "num",
            "art",
            "prep",
            "conj",
            "interj"
    };
    private TextInputEditText editMeansCn;
    private TextInputEditText editMeansEn;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_data);

        currentMode = getIntent().getStringExtra(MODE_NAME);
        currentWordId = getIntent().getIntExtra(WORD_ID, 0);

        ImageView imgBack = findViewById(R.id.modify_data_img_back);
        LinearLayout layoutTopHelp = findViewById(R.id.modify_data_title);
        TextView textTitle = findViewById(R.id.text_modify_data_title);
        TextView textSave = findViewById(R.id.text_modify_data_save);

        CardView cardMeans = findViewById(R.id.card_modify_data_translation);
        Spinner spinnerMeansType = findViewById(R.id.spinner_modify_data_type);
        editMeansCn = findViewById(R.id.edit_text_modify_data_cn);
        editMeansEn = findViewById(R.id.edit_text_modify_data_en);

        RecyclerView recyclerPhrase = findViewById(R.id.recycler_modify_data_phrase);

        CardView cardMemo = findViewById(R.id.card_modify_data_memo);
        TextInputEditText editMemo = findViewById(R.id.edit_text_modify_data_memo);

        FloatingActionButton fabAdd = findViewById(R.id.fab_modify_data_add);

        imgBack.setOnClickListener(view -> onBackPressed());

        layoutTopHelp.setOnClickListener(view -> {
            String tip = "";
            switch (currentMode) {
                case MODE_MEANS:
                    tip = "1. 原数据一经修改便无法恢复，三思而后行\n" +
                            "2. 仅供丰富单词释义栏，例如添加当前词书中没有的词性、释义\n" +
                            "3. 英文释义没有的话可以不用填\n" +
                            "4. 卡片左滑可以删除，点击保存才会生效，不用害怕误删";
                    break;
                case MODE_MEMO:
                    tip = "1. 备注内容不限，可添加对单词的理解、助记方式或者自行编制的小故事\n" +
                            "2. 卡片左滑可以删除，点击保存才会生效，不用害怕误删";
                    break;
                case MODE_PHRASE:
                    tip = "1. 原数据一经修改便无法恢复，三思而后行\n" +
                            "2. 仅供丰富单词词组栏，例如添加当前词书中没有的常用词组\n" +
                            "3. 中英文要么都填，要么都不填\n" +
                            "4. 卡片左滑可以删除，点击保存才会生效，不用害怕误删";
                    break;
                case MODE_SENTENCE:
                    tip = "1. 原数据一经修改便无法恢复，三思而后行\n" +
                            "2. 仅供丰富单词例句栏，例如添加当前词书中没有的常用例句\n" +
                            "3. 中英文要么都填，要么都不填\n" +
                            "4. 卡片左滑可以删除，点击保存才会生效，不用害怕误删";
                    break;
                default:
                    break;
            }
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ModifyDataActivity.this);
            builder.setTitle("提示")
                    .setMessage(tip)
                    .setPositiveButton("确定", null)
                    .show();
        });

        // 判断传入的键值对符合三者中的哪项
        switch (currentMode) {
            // 释义视图
            case MODE_MEANS:
                textTitle.setText("编辑释义");
                cardMeans.setVisibility(View.VISIBLE);
                cardMemo.setVisibility(View.GONE);
                recyclerPhrase.setVisibility(View.GONE);
                fabAdd.setVisibility(View.GONE);

                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, meansTypes);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMeansType.setAdapter(arrayAdapter);

                // 初始化卡片数据
                setMeansInitialData();
                setMeansEditText(0);

                // 下拉框数据
                spinnerMeansType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        currentMeansType = i;
                        setMeansEditText(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                // 设置文本变化的监听器，以实时保存输入的中文文本
                editMeansCn.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        ModifyTranslation modifyTranslation = translationMap.get(currentMeansType);
                        if (!TextUtils.isEmpty(charSequence.toString().trim())) {
                            assert modifyTranslation != null;
                            modifyTranslation.setTranslationCn(charSequence.toString().trim());
                        } else {
                            assert modifyTranslation != null;
                            modifyTranslation.setTranslationCn("@null");
                        }
                        translationMap.put(currentMeansType, modifyTranslation);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                // 设置文本变化的监听器，以实时保存输入的英文文本
                editMeansEn.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        ModifyTranslation modifyTranslation = translationMap.get(currentMeansType);
                        if (!TextUtils.isEmpty(charSequence.toString().trim())) {
                            assert modifyTranslation != null;
                            modifyTranslation.setTranslationEn(charSequence.toString().trim());
                        } else {
                            assert modifyTranslation != null;
                            modifyTranslation.setTranslationEn("@null");
                        }
                        translationMap.put(currentMeansType, modifyTranslation);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                break;

            // 备注视图
            case MODE_MEMO:
                textTitle.setText("编辑备注");
                cardMemo.setVisibility(View.VISIBLE);
                recyclerPhrase.setVisibility(View.GONE);
                fabAdd.setVisibility(View.GONE);
                cardMeans.setVisibility(View.GONE);
                List<Word> words = LitePal
                        .where("wordId = ?", currentWordId + "")
                        .select("wordId", "word", "remark")
                        .find(Word.class);
                if (words.get(0).getRemark() != null) {
                    editMemo.setText(words.get(0).getRemark());
                }
                break;

            // 修改词组视图
            case MODE_PHRASE:
                textTitle.setText("编辑词组");
                itemModifyPhraseList.clear();
                fabAdd.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManagerPhrase = new LinearLayoutManager(this);
                recyclerPhrase.setLayoutManager(linearLayoutManagerPhrase);
                List<Phrase> phraseList = LitePal
                        .where("wordId = ?", currentWordId + "")
                        .find(Phrase.class);
                for (Phrase phrase : phraseList) {
                    itemModifyPhraseList.add(new ItemModifyPhrase(phrase.getEnPhrase(), phrase.getCnPhrase()));
                }
                modifyPhraseAdapter = new ModifyPhraseAdapter(ModifyDataActivity.this, itemModifyPhraseList, recyclerPhrase);
                recyclerPhrase.setAdapter(modifyPhraseAdapter);
                break;

            // 修改例句视图
            case MODE_SENTENCE:
                textTitle.setText("编辑例句");
                itemModifySentenceList.clear();
                fabAdd.setVisibility(View.VISIBLE);
                LinearLayoutManager linearLayoutManagerSentence = new LinearLayoutManager(this);
                recyclerPhrase.setLayoutManager(linearLayoutManagerSentence);
                List<Sentence> sentenceList = LitePal
                        .where("wordId = ?", currentWordId + "")
                        .find(Sentence.class);
                for (Sentence sentence : sentenceList) {
                    itemModifySentenceList.add(new ItemModifySentence(sentence.getEnSentence(), sentence.getCnSentence()));
                }
                modifySentenceAdapter = new ModifySentenceAdapter(ModifyDataActivity.this, itemModifySentenceList, recyclerPhrase);
                recyclerPhrase.setAdapter(modifySentenceAdapter);
                break;
            default:
                break;
        }

        fabAdd.setOnTouchListener(new View.OnTouchListener() {

            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = (int) view.getX();
                        initialY = (int) view.getY();
                        initialTouchX = motionEvent.getRawX();
                        initialTouchY = motionEvent.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int newX = initialX + (int) (motionEvent.getRawX() - initialTouchX);
                        int newY = initialY + (int) (motionEvent.getRawY() - initialTouchY);
                        int maxX = getWindow().getDecorView().getWidth() - view.getWidth();
                        int maxY = getWindow().getDecorView().getHeight() - view.getHeight();
                        newX = Math.min(Math.max(0, newX), maxX);
                        newY = Math.min(Math.max(0, newY), maxY);
                        view.setX(newX);
                        view.setY(newY);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if ((motionEvent.getRawX() - initialTouchX) == 0 || (motionEvent.getRawY() - initialTouchY) == 0) {
                            switch (currentMode) {
                                case MODE_PHRASE:
                                    itemModifyPhraseList.add(new ItemModifyPhrase("", ""));
                                    modifyPhraseAdapter.notifyDataSetChanged();
                                    break;

                                case MODE_SENTENCE:
                                    itemModifySentenceList.add(new ItemModifySentence("", ""));
                                    modifySentenceAdapter.notifyDataSetChanged();
                                    break;

                                default:
                                    break;
                            }
                        }
                        return true;
                }
                return false;
            }
        });

        if (savedInstanceState != null) {
            int fabX = savedInstanceState.getInt("fabX");
            int fabY = savedInstanceState.getInt("fabY");
            fabAdd.setX(fabX);
            fabAdd.setY(fabY);
        }

        textSave.setOnClickListener(view -> {
            switch (currentMode) {
                // 保存释义视图
                case MODE_MEANS:
                    for (int i = 0; i < types.length; i++) {
                        ModifyTranslation modifyTranslation = translationMap.get(i);
                        assert modifyTranslation != null;
                        Translation translation = new Translation();
                        if (!modifyTranslation.getTranslationCn().equals("@null")) {
                            translation.setCnMeaning(modifyTranslation.getTranslationCn());
                        } else {
                            translation.setToDefault("cnMeaning");
                        }
                        if (!modifyTranslation.getTranslationEn().equals("@null")) {
                            translation.setEnMeaning(modifyTranslation.getTranslationEn());
                        } else {
                            translation.setToDefault("enMeaning");
                        }
                        // 若已存在数据
                        if (!LitePal.where("wordId = ? and wordType = ?", currentWordId + "", types[i])
                                .find(Translation.class)
                                .isEmpty()) {
                            // 两行都不为空
                            if (!modifyTranslation.getTranslationEn().equals("@null") &&
                                    !modifyTranslation.getTranslationCn().equals("@null")) {
                                // 更新数据库
                                translation.updateAll("wordId = ? and wordType = ?", currentWordId + "", types[i]);
                            } else {
                                // 词义当中有一行被清空，就删除这两个释义
                                LitePal.deleteAll(Translation.class, "wordId = ? and wordType = ?", currentWordId + "", types[i]);
                            }
                        } else { // 不存在数据，新建时
                            // 只要两个文本框不是都空着的时候，就能加入数据库
                            if (!(modifyTranslation.getTranslationEn().equals("@null") && modifyTranslation.getTranslationCn().equals("@null"))) {
                                translation.setWordType(types[i]);
                                translation.setWordId(currentWordId);
                                translation.save();
                            }
                        }
                    }
                    Toast.makeText(ModifyDataActivity.this, "释义已更新", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    break;

                // 保存词组视图
                case MODE_PHRASE:
                    LitePal.deleteAll(Phrase.class, "wordId = ?", currentWordId + "");
                    for (ItemModifyPhrase itemModifyPhrase : itemModifyPhraseList) {
                        if (!TextUtils.isEmpty(itemModifyPhrase.getPhraseCn()) && !TextUtils.isEmpty(itemModifyPhrase.getPhraseEn())) {
                            Phrase phrase = new Phrase();
                            phrase.setWordId(currentWordId);
                            phrase.setCnPhrase(itemModifyPhrase.getPhraseCn());
                            phrase.setEnPhrase(itemModifyPhrase.getPhraseEn());
                            phrase.save();
                        }
                    }
                    Toast.makeText(ModifyDataActivity.this, "词组已更新", Toast.LENGTH_SHORT).show();
                    onBackPressed();

                    break;

                // 保存备注视图
                case MODE_MEMO:
                    Word word = new Word();
                    if (TextUtils.isEmpty(Objects.requireNonNull(editMemo.getText()).toString())) {
                        word.setToDefault("remark");
                    } else {
                        word.setRemark(editMemo.getText().toString());
                    }
                    word.updateAll("wordId = ?", currentWordId + "");
                    Toast.makeText(ModifyDataActivity.this, "备注已更新", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    break;

                // 保存例句视图
                case MODE_SENTENCE:
                    LitePal.deleteAll(Sentence.class, "wordId = ?", currentWordId + "");
                    for (ItemModifySentence itemModifySentence : itemModifySentenceList) {
                        if (!TextUtils.isEmpty(itemModifySentence.getSentenceCn()) &&
                                !TextUtils.isEmpty(itemModifySentence.getSentenceEn())) {
                            Sentence sentence = new Sentence();
                            sentence.setWordId(currentWordId);
                            sentence.setCnSentence(itemModifySentence.getSentenceCn());
                            sentence.setEnSentence(itemModifySentence.getSentenceEn());
                            sentence.save();
                        }
                    }
                    Toast.makeText(ModifyDataActivity.this, "例句已更新", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    break;

                default:
                    break;
            }
        });


    }

    private void setMeansEditText(int position) {
        editMeansCn.setText(
                Objects.requireNonNull(translationMap.get(position)).getTranslationCn().equals("@null") ?
                        "" : Objects.requireNonNull(translationMap.get(position)).getTranslationCn()
        );
        editMeansEn.setText(
                Objects.requireNonNull(translationMap.get(position)).getTranslationEn().equals("@null") ?
                        "" : Objects.requireNonNull(translationMap.get(position)).getTranslationEn()
        );
    }

    private void setMeansInitialData() {
        translationMap.clear();
        for (int i = 0; i < types.length; ++i) {
            // 查询 词性 和 wordID
            List<Translation> translationList = LitePal
                    .where("wordType = ? and wordId = ?", types[i], currentWordId + "")
                    .find(Translation.class);
            ModifyTranslation modifyTranslation = new ModifyTranslation();
            if (!translationList.isEmpty()) {
                if (translationList.get(0).getCnMeaning() != null) {
                    modifyTranslation.setTranslationCn(translationList.get(0).getCnMeaning());
                } else {
                    modifyTranslation.setTranslationCn("@null");
                }
                if (translationList.get(0).getEnMeaning() != null) {
                    modifyTranslation.setTranslationEn(translationList.get(0).getEnMeaning());
                } else {
                    modifyTranslation.setTranslationEn("@null");
                }
            } else {
                modifyTranslation.setTranslationCn("@null");
                modifyTranslation.setTranslationEn("@null");
            }
            translationMap.put(i, modifyTranslation);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}