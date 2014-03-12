
package com.example.shuffle;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;

/**
 * @author NashLegend
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MovableButton extends Button {
    private String title = "";
    private int id = 1;
    private Point position = new Point(0, 0);
    private Point targetPosition = new Point(0, 0);
    private Object animator;

    public Point getTargetPosition() {
        return targetPosition;
    }

    public void setTargetPosition(Point targetPosition) {
        this.targetPosition = targetPosition;
    }

    private boolean selected = false;
    
    public MovableButton clone() {
        MovableButton button=new MovableButton(getContext());
        LayoutParams params = new LayoutParams(ShuffleDesk.buttonWidth,
                ShuffleDesk.buttonHeight);
        button.setLayoutParams(params);
        button.setText(getText());
        return button;
    }

    public MovableButton(Context context) {
        super(context);
        // setBackgroundColor(Color.parseColor("#00ffff"));
    }

    public void setTargetPositionIsNext() {
        if (targetPosition.x < (ShuffleDesk.Colums - 1)) {
            targetPosition.x++;
        } else {
            targetPosition.x = 0;
            targetPosition.y++;
        }
    }

    public void setTargetPositionIsPrev() {
        if (targetPosition.x == 0 && targetPosition.y > 0) {
            targetPosition.x = ShuffleDesk.Colums - 1;
            targetPosition.y--;
        } else if (targetPosition.x > 0) {
            targetPosition.x--;
        }
    }

    public int getIndex() {
        return position.y * ShuffleDesk.Colums + position.x;
    }

    public void startAnimator(Point anchorPoint) {
        if (Build.VERSION.SDK_INT < ShuffleDesk.animateVersion) {
            LayoutParams params = (LayoutParams) getLayoutParams();
            params.leftMargin = ShuffleDesk.buttonCellWidth * targetPosition.x + ShuffleDesk.hGap;
            params.topMargin = ShuffleDesk.buttonCellHeight * targetPosition.y + anchorPoint.y
                    + ShuffleDesk.vGap;
            setLayoutParams(params);
        } else {
            if (animator != null && ((ValueAnimator) animator).isRunning()) {
                ((ValueAnimator) animator).cancel();
            }
            PropertyValuesHolder holderx = PropertyValuesHolder.ofFloat("x", getX(),
                    ShuffleDesk.buttonCellWidth * targetPosition.x + ShuffleDesk.hGap);
            PropertyValuesHolder holdery = PropertyValuesHolder.ofFloat("y", getY(),
                    ShuffleDesk.buttonCellHeight * targetPosition.y + anchorPoint.y
                            + ShuffleDesk.vGap);
            animator = ObjectAnimator.ofPropertyValuesHolder(this, holderx, holdery);
            ((ObjectAnimator) animator).setDuration(300);
            ((ObjectAnimator) animator).start();
        }
    }

    public void setXX(float x) {
        if (Build.VERSION.SDK_INT < ShuffleDesk.animateVersion) {
            LayoutParams params = (LayoutParams) getLayoutParams();
            params.leftMargin = (int) x;
            setLayoutParams(params);
        } else {
            super.setX(x);
        }
    }

    public void setYY(float y) {
        if (Build.VERSION.SDK_INT < ShuffleDesk.animateVersion) {
            LayoutParams params = (LayoutParams) getLayoutParams();
            params.topMargin = (int) y;
            setLayoutParams(params);
        } else {
            super.setY(y);
        }
    }

    public float getXX() {
        if (Build.VERSION.SDK_INT < ShuffleDesk.animateVersion) {
            return this.getLeft();
        } else {
            return super.getX();
        }
    }

    public float getYY() {
        if (Build.VERSION.SDK_INT < ShuffleDesk.animateVersion) {
            return this.getTop();
        } else {
            return super.getY();
        }
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
