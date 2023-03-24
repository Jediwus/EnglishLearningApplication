package com.jediwus.learningapplication.activity.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.database.Favorites;
import com.jediwus.learningapplication.myUtil.MyApplication;
import com.jediwus.learningapplication.myUtil.TimeController;

public class BottomSheetDialogFragmentAddF extends BottomSheetDialogFragment {

    private OnDismissListener mListener;

    // 定义回调接口
    public interface OnDismissListener {
        void onDismiss();
    }

    public BottomSheetDialogFragmentAddF() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (OnDismissListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context + " must implement OnDismissListener");
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mListener != null) {
            mListener.onDismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_bottom_sheet_add_f, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText editTitle = view.findViewById(R.id.edit_text_favorites_title);
        EditText editMemo = view.findViewById(R.id.edit_text_favorites_memo);
        Button btn_save = view.findViewById(R.id.btn_save_favorites);
        btn_save.setOnClickListener(view1 -> {
            if (!TextUtils.isEmpty(editTitle.getText().toString().trim())) {
                Favorites favorites = new Favorites();
                favorites.setCreateTime(TimeController.getCurrentTimeStamp() + "");
                favorites.setName(editTitle.getText().toString().trim());
                if (!TextUtils.isEmpty(editMemo.getText().toString().trim()))
                    favorites.setRemark(editMemo.getText().toString().trim());
                favorites.save();
                this.dismiss();
                Toast.makeText(MyApplication.getContext(), "已为你创建" + favorites.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MyApplication.getContext(), "请务必按要求填写", Toast.LENGTH_SHORT).show();
            }
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
