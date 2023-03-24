package com.jediwus.learningapplication.myUtil;

import android.content.Context;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.LayoutRes;

import com.jediwus.learningapplication.R;

import razerdp.basepopup.BasePopupWindow;

public class MyPopupWindow extends BasePopupWindow {

    public static int animationTime = 800;

    public MyPopupWindow(Context context) {
        super(context);
        setShowAnimation(createShowAnimation());
        setDismissAnimation(createDismissAnimation());
    }

    private Animation createShowAnimation() {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1,
            Animation.RELATIVE_TO_SELF, 0,
            Animation.RELATIVE_TO_SELF, -1,
            Animation.RELATIVE_TO_SELF, 0);

        animation.setDuration(animationTime);
        return animation;
    }

    private Animation createDismissAnimation() {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1);
        animation.setDuration(animationTime);
        return animation;
    }
}
