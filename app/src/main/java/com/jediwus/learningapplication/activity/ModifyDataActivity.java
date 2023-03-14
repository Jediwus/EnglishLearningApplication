package com.jediwus.learningapplication.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Spinner;

import com.jediwus.learningapplication.R;

public class ModifyDataActivity extends BaseActivity {

    public static final String WORD_ID_NAME = "modifyWordId";

    public static final String MODE_NAME = "modifyName";

    public static final String MODE_MEANS = "modifyMeans";

    public static final String MODE_SENTENCES = "modifySentences";

    public static final String MODE_REMARKS = "modifyRemarks";

    private String currentMode;

    private int currentWordId;

    private Spinner spinnerMeansType;

    private int currentMeansType = 0;

    private String[] meansTypes = {
            "名词 [n]",
            "代词 [pron]",
            "形容词 [adj]",
            "副词 [adv]",
            "动词 [v]",
            "及物动词 [vt]",
            "不及物动词 [vi]",
            "数词 [num]",
            "冠词 [art]",
            "介词 [prep]",
            "连词 [conj]",
            "感叹词 [interj]"
    };

    private String[] types = {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_data);
    }
}