package com.jediwus.learningapplication.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.database.Word;

import org.litepal.LitePal;

import uk.co.senab.photoview.PhotoView;

public class CustomPictureActivity extends BaseActivity {


    public static final String TYPE_WORD_ID = "typeWordId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_picture);

        PhotoView imgBackground = findViewById(R.id.img_custom_picture);
        TextView textName = findViewById(R.id.text_custom_picture_word);

        int currentWordId = getIntent().getIntExtra(TYPE_WORD_ID, 0);
        Word word = LitePal.where("wordId = ?", currentWordId + "")
                .select("wordId", "word", "picCustom")
                .find(Word.class).get(0);
        Glide.with(this).load(word.getPicCustom()).into(imgBackground);
        textName.setText(word.getWord());

    }

}