
package com.example.shuffle;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class ShuffleBoard extends RelativeLayout {

    private boolean editMode = false;

    public ShuffleBoard(Context context) {
        super(context);
    }

    public ShuffleBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShuffleBoard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO 自动生成的方法存根
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Log.i("shuffle", "ote");
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                Log.i("shuffle", "down1");
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Log.i("shuffle", "move2");
//                break;
//            case MotionEvent.ACTION_UP:
//                Log.i("shuffle", "ote3");
//                break;
//
//            default:
//                break;
//        }
        return true;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

}
