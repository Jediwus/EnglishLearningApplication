package com.jediwus.learningapplication.activity.fragment;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.BaseActivity;
import com.jediwus.learningapplication.activity.DaySentenceActivity;
import com.jediwus.learningapplication.activity.MainActivity;
import com.jediwus.learningapplication.activity.SearchActivity;
import com.jediwus.learningapplication.activity.WordDetailActivity;
import com.jediwus.learningapplication.activity.WordFolderActivity;
import com.jediwus.learningapplication.config.ConfigData;
import com.jediwus.learningapplication.database.MyDate;
import com.jediwus.learningapplication.database.Word;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class FragmentWord extends Fragment implements View.OnClickListener {

    private static final String TAG = "FragmentWord";

    public static int prepareData = 0;

    private int currentRandomId;

    private FloatingActionButton fab_search;

    private Button btn_start;

    private CardView cardStart, cardSearch;

    private ImageView img_refresh, img_flag;

    private View trans_flagView, trans_folderView;

    private TextView text_start;

    private RelativeLayout layout_files;

    private TextView text_book, text_wordNum, text_word, text_meaning;

    private TextView text_day, text_month;
    // 声明一个视图对象
    protected View mView;
    // 声明一个上下文对象
    protected Context mContext;

    // 创建碎片视图，创建Fragment的布局视图时调用，该方法返回一个View对象，用于构建Fragment的UI界面。
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_word, container, false);
        fab_search = mView.findViewById(R.id.fab);
        // 创建动画效果
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(500);
        // 设置动画效果的监听器
        animator.addUpdateListener(animation -> {
            // 获取动画进度值
            float progress = (float) animation.getAnimatedValue();
            // 缩放 FloatingActionButton 控件
            fab_search.setScaleX(progress);
            fab_search.setScaleY(progress);
        });
        // 开始动画效果
        animator.start();

        fab_search.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

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
                        assert container != null;
                        int maxX = container.getWidth() - view.getWidth();
                        int maxY = container.getHeight() - view.getHeight();
                        newX = Math.min(Math.max(0, newX), maxX);
                        newY = Math.min(Math.max(0, newY), maxY);
                        view.setX(newX);
                        view.setY(newY);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if ((motionEvent.getRawX() - initialTouchX) == 0 || (motionEvent.getRawY() - initialTouchY) == 0) {
                            Intent intentSearch = new Intent(getActivity(), SearchActivity.class);
                            intentSearch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intentSearch);
                            // 处理点击事件
                            //handleClickEvent();
                        }

                        return true;
                }
                return false;
            }
        });

        if (savedInstanceState != null) {
            int fabX = savedInstanceState.getInt("fabX");
            int fabY = savedInstanceState.getInt("fabY");
            fab_search.setX(fabX);
            fab_search.setY(fabY);
        }

        return mView;
    }

    // 当Fragment的布局视图被创建时调用，该方法可以访问Fragment布局中的视图元素，如Button、TextView等。
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        text_month = view.findViewById(R.id.text_main_month);
        text_day = view.findViewById(R.id.text_main_day);
        text_book = view.findViewById(R.id.text_main_book_name);
        text_wordNum = view.findViewById(R.id.text_main_word_num);
        text_word = view.findViewById(R.id.text_main_word);

        text_meaning = view.findViewById(R.id.text_main_word_meaning);
        text_meaning.setOnClickListener(this);

        img_flag = view.findViewById(R.id.img_top_flag);
        img_flag.setOnClickListener(this);

        img_refresh = view.findViewById(R.id.img_refresh);
        img_refresh.setOnClickListener(this);

        layout_files = view.findViewById(R.id.layout_main_word_file);
        layout_files.setOnClickListener(this);

        btn_start = view.findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);

        trans_folderView = view.findViewById(R.id.view_folder_transition);

        trans_flagView = view.findViewById(R.id.view_flag_transition);

        Log.d(TAG, "onViewCreated: 单词界面的碎片已创建");

        if (MainActivity.needRefresh) {
            prepareData = 0;
            new Thread(BaseActivity::prepareDailyData).start();
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("fabX", (int) fab_search.getX());
        outState.putInt("fabY", (int) fab_search.getY());
    }

//    private void handleClickEvent() {
//        /*// 创建 PopupMenu
//        PopupMenu popupMenu = new PopupMenu(FragmentWord.this.getContext(), fab_search);
//        popupMenu.getMenuInflater().inflate(R.menu.bottom_nav_menu, popupMenu.getMenu());
//        // 设置菜单项的点击事件
//        popupMenu.setOnMenuItemClickListener(item -> {
//            // 处理菜单项的点击事件
//            // ...
//            return true;
//        });
//
//        // 显示 PopupMenu
//        popupMenu.show();*/
//        Intent intentSearch = new Intent(getActivity(), SearchActivity.class);
//        intentSearch.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        ActivityOptionsCompat activityOptionsCompat2 = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),
//                trans_searchView, "fab_transition");
//        startActivity(intentSearch, activityOptionsCompat2.toBundle());
//    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_top_flag:
                Intent mIntent = new Intent(getActivity(), DaySentenceActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(),
                        trans_flagView, "view_flag_transition");
                startActivity(mIntent, activityOptionsCompat.toBundle());
                break;

            case R.id.img_refresh:
                // 旋转动画
                RotateAnimation animation = new RotateAnimation(0.0f, -360.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(700);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        setRandomWord();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                img_refresh.startAnimation(animation);
                break;

            case R.id.text_main_word_meaning:
                WordDetailActivity.wordId = currentRandomId;
                Intent intent = new Intent(getActivity(), WordDetailActivity.class);
                intent.putExtra(WordDetailActivity.TYPE_NAME, WordDetailActivity.TYPE_GENERAL);
                startActivity(intent);
                Log.d(TAG, "onClick: 点击跳转至单词释义的页面");
                break;

            case R.id.layout_main_word_file:
                Intent intentWordFolder = new Intent(getContext(), WordFolderActivity.class);
                startActivity(intentWordFolder);
//                startActivity(intentWordFolder, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                Log.d(TAG, "onClick: 点击收藏的relativelayout");
                break;

            case R.id.index_start:


                break;

            default:
                break;

        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        Calendar calendar = Calendar.getInstance();
        text_day.setText(calendar.get(Calendar.DATE) + "");
        text_month.setText(DaySentenceActivity.getMonthName(calendar));





    }


    /**
     * 设置一个随机单词
     */
    private void setRandomWord() {

    }
}
