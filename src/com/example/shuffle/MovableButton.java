
package com.example.shuffle;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.widget.Button;

public class MovableButton extends Button {
    private String title = "";
    private int id = 1;
    private Point position = new Point(0, 0);
    private Point targetPosition = new Point(0, 0);
    private ObjectAnimator animator = new ObjectAnimator();

    public Point getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(Point targetPosition) {
        this.targetPosition = targetPosition;
    }

    private boolean selected = false;
    private AnimatorSet animatorSet = new AnimatorSet();

    public MovableButton(Context context) {
        super(context);
    }

    public void setTargetPositionIsNext() {
        if (targetPosition.x < (MainActivity.Colums - 1)) {
            targetPosition.x++;
        } else {
            targetPosition.x = 0;
            targetPosition.y++;
        }
    }

    public void setTargetPositionIsPrev() {
        if (targetPosition.x == 0 && targetPosition.y > 0) {
            targetPosition.x = MainActivity.Colums - 1;
            targetPosition.y--;
        } else if (targetPosition.x > 0) {
            targetPosition.x--;
        }
    }

    public void startAnimator(Point anchorPoint) {
        // TODO
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }

        PropertyValuesHolder holderx = PropertyValuesHolder.ofFloat("x", getX(),
                MainActivity.buttonCellWidth * targetPosition.x);
        PropertyValuesHolder holdery = PropertyValuesHolder.ofFloat("y", getY(),
                MainActivity.buttonCellHeight * targetPosition.y + anchorPoint.y);

        animator = ObjectAnimator.ofPropertyValuesHolder(this, holderx, holdery);
        animator.setDuration(300);
        animator.start();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        setText(title);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private void slog(String string) {
        Log.i("shuffle", string);
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
