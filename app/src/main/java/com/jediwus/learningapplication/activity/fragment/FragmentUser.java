package com.jediwus.learningapplication.activity.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.database.MyDate;
import com.jediwus.learningapplication.database.User;
import com.jediwus.learningapplication.myUtil.MyApplication;

import org.litepal.LitePal;

import java.util.List;

public class FragmentUser extends Fragment implements View.OnClickListener {

    private ImageView img_portrait;
    private TextView tv_day;
    private TextView tv_number;
    private TextView tv_coins;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

    /**
     * 当Fragment的布局视图被创建时调用，该方法可以访问Fragment布局中的视图元素，如Button、TextView等。
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tv_day = view.findViewById(R.id.text_user_days);
        tv_number = view.findViewById(R.id.text_user_words);
        tv_coins = view.findViewById(R.id.text_user_coins);

        img_portrait = view.findViewById(R.id.img_user_portrait);
        //加载图片
        Glide.with(MyApplication.getContext())
                .load(R.drawable.portrait_hikari)
                //设置图片圆角属性
                .transform(new CircleCrop())
                .into(img_portrait);

    }

    @Override
    public void onStart() {
        super.onStart();
        // 更新视图信息
        updateView();
    }

    /**
     * 设置用户信息
     */
    @SuppressLint("SetTextI18n")
    private void updateView() {
        List<MyDate> myDateList = LitePal
                .where("userId = ?", DataConfig.getWeChatNumLogged() + "")
                .find(MyDate.class);
        tv_day.setText(myDateList.size() + "");
        List<User> userList = LitePal
                .where("userId = ?", DataConfig.getWeChatNumLogged() + "")
                .find(User.class);
        tv_number.setText(userList.get(0).getUserVocabulary() + "");
        tv_coins.setText(userList.get(0).getUserCoins() + "");
    }

    @Override
    public void onClick(View view) {

    }
}
