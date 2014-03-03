
package com.example.shuffle;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class animateView extends View {
    private Object animator;
    private int ver = 11;

    public animateView(Context context) {
        super(context);
    }

    public void startAnimator(float dy) {
        if (Build.VERSION.SDK_INT < ver) {
            LayoutParams params = (LayoutParams) getLayoutParams();
            params.topMargin = (int) dy;
            setLayoutParams(params);
        } else {
            if (animator != null && ((ValueAnimator) animator).isRunning()) {
                ((ValueAnimator) animator).cancel();
            }
            PropertyValuesHolder holdery = PropertyValuesHolder.ofFloat("y", getY(), dy);
            animator = ObjectAnimator.ofPropertyValuesHolder(this, holdery);
            ((ObjectAnimator) animator).setDuration(300);
            ((ObjectAnimator) animator).start();
        }
    }
}
