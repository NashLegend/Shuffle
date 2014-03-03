
package com.example.shuffle;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.view.View;

public class animateView extends View {
    private ObjectAnimator animator = new ObjectAnimator();

    public animateView(Context context) {
        super(context);
    }

    public void startAnimator(float dy) {
        // TODO
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }

        PropertyValuesHolder holdery = PropertyValuesHolder.ofFloat("y", getY(),dy);

        animator = ObjectAnimator.ofPropertyValuesHolder(this, holdery);
        animator.setDuration(300);
        animator.start();
    }

    public ObjectAnimator getAnimator() {
        return animator;
    }

    public void setAnimator(ObjectAnimator animator) {
        this.animator = animator;
    }

}
