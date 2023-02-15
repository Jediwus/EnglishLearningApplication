package com.jediwus.learningapplication.util;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.jediwus.learningapplication.R;

import razerdp.basepopup.BasePopupWindow;

public class MyPopupWindow extends BasePopupWindow {

    public static int animationTime = 300;

    public MyPopupWindow(Context context) {
        super(context);
        setContentView(R.layout.item_welcome_introduction);

        setShowAnimation(createShowAnimation());
        setDismissAnimation(createDismissAnimation());
    }

//    @Override
//    protected Animator onCreateShowAnimator() {
//        ObjectAnimator showAnimator = ObjectAnimator.ofFloat(getDisplayAnimateView(), View.TRANSLATION_X, -getWidth(), 0);
//        showAnimator.setDuration(animationTime);
//        //showAnimator.setInterpolator(new OvershootInterpolator(3));
//        return showAnimator;
//    }
//
//    @Override
//    protected Animator onCreateDismissAnimator(int width, int height) {
//        ObjectAnimator showAnimator = ObjectAnimator.ofFloat(getDisplayAnimateView(), View.TRANSLATION_X, 0, getWidth());
//        showAnimator.setDuration(animationTime);
//        //showAnimator.setInterpolator(new OvershootInterpolator(-3));
//        return showAnimator;
//    }

    private Animation createShowAnimation() {
        AlphaAnimation animation = new AlphaAnimation(0f, 1f);
        animation.setDuration(animationTime);
        return animation;
    }

    private Animation createDismissAnimation() {
        AlphaAnimation animation = new AlphaAnimation(1f, 0f);
        animation.setDuration(animationTime);
        return animation;
    }
}
