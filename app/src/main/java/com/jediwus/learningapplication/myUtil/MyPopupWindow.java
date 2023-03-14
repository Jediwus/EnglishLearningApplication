package com.jediwus.learningapplication.myUtil;

import android.content.Context;
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
