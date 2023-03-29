package com.jediwus.learningapplication.activity.fragment;

import static com.jediwus.learningapplication.activity.WordDetailActivity.launcher;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.ModifyDataActivity;
import com.jediwus.learningapplication.activity.WordDetailActivity;
import com.jediwus.learningapplication.myUtil.MyApplication;

public class BottomSheetDialogFragmentCustom extends BottomSheetDialogFragment {

    public BottomSheetDialogFragmentCustom() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_bottom_sheet_custom, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 设置意图
        Intent intentModify = new Intent(MyApplication.getContext(), ModifyDataActivity.class);

        // 自定义释义
        TextView text_translation = view.findViewById(R.id.dialog_text_translation);
        text_translation.setOnClickListener(view_translation -> {
            intentModify.putExtra(ModifyDataActivity.MODE_NAME, ModifyDataActivity.MODE_MEANS);
            intentModify.putExtra(ModifyDataActivity.WORD_ID, WordDetailActivity.currentWord.getWordId());
            startActivity(intentModify);
            this.dismiss();
        });

        // 自定义例句
        TextView text_sentence = view.findViewById(R.id.dialog_text_sentence);
        text_sentence.setOnClickListener(view_sentence -> {
            intentModify.putExtra(ModifyDataActivity.MODE_NAME, ModifyDataActivity.MODE_SENTENCE);
            intentModify.putExtra(ModifyDataActivity.WORD_ID, WordDetailActivity.currentWord.getWordId());
            startActivity(intentModify);
            this.dismiss();
        });


        // 自定义词组
        TextView text_phrase = view.findViewById(R.id.dialog_text_phrase);
        text_phrase.setOnClickListener(view_phrase -> {
            intentModify.putExtra(ModifyDataActivity.MODE_NAME, ModifyDataActivity.MODE_PHRASE);
            intentModify.putExtra(ModifyDataActivity.WORD_ID, WordDetailActivity.currentWord.getWordId());
            startActivity(intentModify);
            this.dismiss();
        });

        // 自定义备注
        TextView text_memo = view.findViewById(R.id.dialog_text_memo);
        text_memo.setOnClickListener(view_memo -> {
            intentModify.putExtra(ModifyDataActivity.MODE_NAME, ModifyDataActivity.MODE_MEMO);
            intentModify.putExtra(ModifyDataActivity.WORD_ID, WordDetailActivity.currentWord.getWordId());
            startActivity(intentModify);
            this.dismiss();
        });

        // 自定义图片
        TextView text_picture = view.findViewById(R.id.dialog_text_picture);
        text_picture.setOnClickListener(view_picture -> {
            //在这里跳转到手机系统相册里面
            Intent intentMedia = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcher.launch(intentMedia);
            this.dismiss();
        });

    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        dialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialog1;
            FrameLayout bottomSheet = bottomSheetDialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        return dialog;
    }

    public void show(@NonNull FragmentManager fragmentManager, String tag) {
        if (!isAdded()) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        }
    }


}
