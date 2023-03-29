package com.jediwus.learningapplication.myUtil;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import razerdp.basepopup.BasePopupWindow;

public class MyPopupWindow extends BasePopupWindow {

    public static int animationTime = 800;

    public MyPopupWindow(Context context) {
        super(context);
        setShowAnimation(createShowAnimation());
        setDismissAnimation(createDismissAnimation());
    }

    private Animation createShowAnimation() {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 3,
                Animation.RELATIVE_TO_SELF, 0);

        animation.setDuration(animationTime);
        return animation;
    }

    private Animation createDismissAnimation() {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 3);
        animation.setDuration(animationTime);
        return animation;
    }
    /*
     * fromXType：动画开始前的X坐标类型。取值范围为ABSOLUTE（绝对位置）、RELATIVE_TO_SELF（以自身宽或高为参考）、RELATIVE_TO_PARENT（以父控件宽或高为参考）。
     * fromXValue：动画开始前的X坐标值。当对应的Type为ABSOLUTE时，表示绝对位置；否则表示相对位置，1.0表示100%。
     * toXType：动画结束后的X坐标类型。
     * toXValue：动画结束后的X坐标值。
     * fromYType：动画开始前的Y坐标类型。
     * fromYValue：动画开始前的Y坐标值。
     * toYType：动画结束后的Y坐标类型。
     * toYValue：动画结束后的Y坐标值
     * */
}
