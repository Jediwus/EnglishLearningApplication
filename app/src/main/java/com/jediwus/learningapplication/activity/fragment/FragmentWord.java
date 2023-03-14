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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jediwus.learningapplication.R;
import com.jediwus.learningapplication.activity.BaseActivity;
import com.jediwus.learningapplication.activity.DailyMottoActivity;
import com.jediwus.learningapplication.activity.MainActivity;
import com.jediwus.learningapplication.activity.SearchActivity;
import com.jediwus.learningapplication.activity.WordDetailActivity;
import com.jediwus.learningapplication.activity.WordFavoritesActivity;
import com.jediwus.learningapplication.config.DataConfig;
import com.jediwus.learningapplication.config.ExternalData;
import com.jediwus.learningapplication.database.MyDate;
import com.jediwus.learningapplication.database.Translation;
import com.jediwus.learningapplication.database.UserPreference;
import com.jediwus.learningapplication.database.Word;
import com.jediwus.learningapplication.myUtil.NumberController;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.List;

public class FragmentWord extends Fragment implements View.OnClickListener {

    private static final String TAG = "FragmentWord";

    private boolean isOnClick = true;

    public static int prepareData = 0;

    private int currentBookId;

    private int currentRandomId;

    private FloatingActionButton fab_search;

    private Button btn_start;

    private CardView cardSearch;

    private ImageView img_refresh, img_flag;

    private View trans_flagView;

    private RelativeLayout layout_files;

    private TextView text_book, text_wordNum, text_word, text_meaning;

    private TextView text_day, text_month;
    // 声明一个视图对象
    protected View mView;

    // 创建碎片视图，创建Fragment的布局视图时调用，该方法返回一个View对象，用于构建Fragment的UI界面。
    @SuppressLint("ClickableViewAccessibility")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_word, container, false);
        fab_search = mView.findViewById(R.id.fab);
        CardView cardView_today_word = mView.findViewById(R.id.card_today_word);
        CardView cardView_word_folder = mView.findViewById(R.id.card_word_folder);
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

            cardView_today_word.setScaleX(progress);
            cardView_today_word.setScaleY(progress);

            cardView_word_folder.setScaleX(progress);
            cardView_word_folder.setScaleY(progress);

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
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                    requireActivity(), fab_search, "fab_transition");
                            startActivity(intentSearch, options.toBundle());
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_top_flag:
                Intent mIntent = new Intent(getActivity(), DailyMottoActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        requireActivity(), trans_flagView, "view_flag_transition");
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
                intent.putExtra(WordDetailActivity.TYPE_NAME, WordDetailActivity.TYPE_CHECK);

                ActivityOptionsCompat options = ActivityOptionsCompat.makeScaleUpAnimation(
                        view, 0, 0, view.getWidth(), view.getHeight());

                startActivity(intent,options.toBundle());
                Log.d(TAG, "onClick: 跳转至单词释义的页面");
                break;

            case R.id.layout_main_word_file:
                Intent intentWordFolder = new Intent(getContext(), WordFavoritesActivity.class);
                startActivity(intentWordFolder, ActivityOptions.makeSceneTransitionAnimation(requireActivity()).toBundle());
                Log.d(TAG, "onClick: 跳转至单词夹页面");
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
        text_month.setText(DailyMottoActivity.getMonthName(calendar));

        List<Word> wordList = LitePal.where("deepMasterTimes <> ?", 3 + "").select("wordId").find(Word.class);
        List<MyDate> myDateList = LitePal.where("year = ? and month = ? and date = ? and userId = ?",
                calendar.get(Calendar.YEAR) + "",
                (calendar.get(Calendar.MONTH) + 1) + "",
                calendar.get(Calendar.DATE) + "",
                DataConfig.getWeChatNumLogged() + "").find(MyDate.class);
        if (!wordList.isEmpty()) {
            if (myDateList.isEmpty()) { // 计划为待完成状态
                btn_start.setText("点击此处开启今日任务");
                isOnClick = true;
            } else {
                if ((myDateList.get(0).getWordLearnNumber() + myDateList.get(0).getWordReviewNumber()) > 0) { // 完成计划
                    btn_start.setBackgroundColor(requireActivity().getColor(R.color.colorSurfaceVariant));
                    btn_start.setTextColor(requireActivity().getColor(R.color.colorOnSurfaceVariant));
                    btn_start.setText("今日任务已完成!");
                    btn_start.setClickable(false);
                    isOnClick = false;
                } else { // 未完成计划
                    btn_start.setText("点击此处开启今日任务");
                    isOnClick = true;
                }
            }
        } else {
            btn_start.setBackgroundColor(requireActivity().getColor(R.color.colorSurfaceVariant));
            btn_start.setTextColor(requireActivity().getColor(R.color.colorOnSurfaceVariant));
            btn_start.setText("您对此书的试炼已完成！");
            btn_start.setClickable(false);
            isOnClick = false;
        }
        // 设置界面数据
        List<UserPreference> userPreferenceList = LitePal.where("userId = ?",
                DataConfig.getWeChatNumLogged() + "").find(UserPreference.class);
        currentBookId = userPreferenceList.get(0).getCurrentBookId();
        text_wordNum.setText("每日任务：" + userPreferenceList.get(0).getWordNeedReciteNum() + "词");
        text_book.setText(ExternalData.getBookNameById(currentBookId));
        if (prepareData == 0) {
            // 设置随机数据
            setRandomWord();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

    /**
     * 设置一个随机单词
     */
    private void setRandomWord() {
        ++prepareData;
        Log.d(TAG, "setRandomWord: " + ExternalData.getWordsTotalNumbersById(currentBookId));

        int randomId = NumberController.getRandomNumber(1, ExternalData.getWordsTotalNumbersById(currentBookId));
        currentRandomId = randomId;
        Log.d(TAG, "setRandomWord: 当前randomId-" + randomId + " ,要传入的currentRandomId-" + currentRandomId);

        Word word = LitePal.where("wordId = ?", randomId + "").select("wordId", "word").find(Word.class).get(0);
        text_word.setText(word.getWord());

        List<Translation> translations = LitePal.where("wordId = ?", word.getWordId() + "").find(Translation.class);

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < translations.size(); ++i) {
            stringBuilder.append(translations.get(i).getWordType())
                    .append(". ")
                    .append(translations.get(i).getCnMeaning());
            if (i != translations.size() - 1) {
                stringBuilder.append("\n");
            }
        }
        text_meaning.setText(stringBuilder.toString());
    }
}
