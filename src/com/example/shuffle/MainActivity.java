
package com.example.shuffle;

import java.util.ArrayList;

import android.R.integer;
import android.os.Bundle;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity {

    private ArrayList<MovableButton> selectedButtons = new ArrayList<MovableButton>();
    private ArrayList<MovableButton> unselectedButtons = new ArrayList<MovableButton>();
    private RelativeLayout board;
    private int buttonHeightDip = 90;
    private int buttonWidth = 0;
    private int buttonHeight = 0;
    private int selectedButtonsRows = 0;
    private int unselectedButtonsRows = 0;
    private int selectedButtonsTotalHeight = 0;
    private int unselectedButtonsTotalHeight = 0;
    private final int Colums = 4;
    private int vGapDip = 2;// x2
    private int hGapDip = 3;// x2
    private int vGap = 0;
    private int hGap = 0;
    private int buttonCellWidth = 0;
    private int buttonCellHeight = 0;
    private int groupVGapDip = 60;
    private int groupVGap = 0;
    private Point selectedButtonsVertex = new Point(0, 0);
    private Point unselectedButtonsVertex = new Point(0, 0);
    private View middleView;

    private int lastZone = 0;
    private int lastRow = 0;
    private int lastCol = 0;

    private AnimatorSet selectedAnimatorSet = new AnimatorSet();
    private AnimatorSet unselectedAnimatorSet = new AnimatorSet();
    private AnimatorSet betweenAnimatorSet = new AnimatorSet();

    private MovableButton currentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.ctt);
        board = new ShuffleBoard(this);
        board.setLayoutParams(new LayoutParams(-1, -1));
        rootLayout.addView(board);
        middleView = new View(this);
        board.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {
                        // TODO 自动生成的方法存根
                        board.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        InitDatas();
                        initView();
                    }
                });
    }

    private void InitDatas() {
        getButtons();

        groupVGap = dip2px(groupVGapDip, this);

        vGap = dip2px(vGapDip, this);
        hGap = dip2px(hGapDip, this);

        buttonCellWidth = board.getWidth() / Colums;
        buttonHeight = dip2px(buttonHeightDip, this);

        buttonWidth = buttonCellWidth - hGap * 2;
        buttonCellHeight = buttonHeight + vGap * 2;

        selectedButtonsRows = (int) Math.ceil((double) (selectedButtons.size()) / Colums);
        unselectedButtonsRows = (int) Math.ceil((double) (unselectedButtons.size())
                / Colums);

        unselectedButtonsTotalHeight = unselectedButtonsRows * buttonCellHeight;
        selectedButtonsTotalHeight = selectedButtonsRows * buttonCellHeight;

        int totHeight = unselectedButtonsTotalHeight + selectedButtonsTotalHeight + groupVGap;
        if (totHeight > board
                .getHeight()) {
            Toast.makeText(this, "Sell your phone and buy a bigger one please !",
                    Toast.LENGTH_SHORT).show();
            android.view.ViewGroup.LayoutParams params = board.getLayoutParams();
            params.height = totHeight;
            board.setLayoutParams(params);
        }

        selectedButtonsVertex = new Point(0, 0);
        unselectedButtonsVertex = new Point(0, selectedButtonsTotalHeight + groupVGap);

    }

    private void initView() {
        LayoutParams params = new LayoutParams(-1, groupVGap);
        params.topMargin = selectedButtonsTotalHeight;
        middleView.setLayoutParams(params);
        middleView.setBackgroundColor(Color.parseColor("#00ffff"));
        board.addView(middleView);
        shuffleButtons();
    }

    private void getButtons() {
        for (int i = 0; i < 13; i++) {
            MovableButton button = new MovableButton(this);
            button.setTitle("btn_" + i);
            button.setId(i);
            button.setSelected(true);
            selectedButtons.add(button);
            button.setOnTouchListener(listener);
        }

        for (int i = 13; i < 20; i++) {
            MovableButton button = new MovableButton(this);
            button.setTitle("btn_" + i);
            button.setId(i);
            button.setSelected(false);
            unselectedButtons.add(button);
            button.setOnTouchListener(listener);
        }
    }

    private OnTouchListener listener = new OnTouchListener() {

        private float lastX = 0f;
        private float lastY = 0f;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    currentButton = (MovableButton) v;
                    lastZone = 0;
                    lastRow = 0;
                    lastCol = 0;
                    lastX = event.getRawX();
                    lastY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = event.getRawX() - lastX;
                    float dy = event.getRawY() - lastY;
                    if (dx * dx + dy * dy > 5) {
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        moveButton(v, dx, dy);
                    } else {

                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    putButtonDown(v);
                    break;
                default:
                    break;
            }
            return false;
        }
    };

    private void moveButton(View button, float dx, float dy) {
        button.setX(button.getX() + dx);
        button.setY(button.getY() + dy);
    }

    private void getCurrentXone() {
        PointF pointF = getCurrentButtonCenter();
        int crtZone = 0;
        int crtRow = 0;
        int crtCol = 0;
        if (pointF.y > selectedButtonsTotalHeight + groupVGap) {
            crtZone = 2;
            crtRow = (int) (pointF.x / buttonCellWidth);
            crtCol = (int) (pointF.y - unselectedButtonsVertex.y / buttonCellHeight - 1);
            if (lastZone != crtZone) {
                // pre zone is 0
                // even zone changed,shuffle
            } else {
                if (crtCol != lastCol || crtRow != lastRow) {
                    ArrayList<MovableButton> buttons = getAnimatedButtonsBetween(crtZone,
                            crtRow, crtCol, lastRow, lastCol);
                    if (crtRow * Colums + crtCol > lastRow * Colums + lastCol) {

                    } else {
                        // moveforwards
                    }
                    lastCol = crtCol;
                    lastRow = crtRow;
                    lastZone = crtZone;
                }
            }
        } else if (pointF.y > selectedButtonsTotalHeight) {
            // must shuffle
            crtZone = 1;
            crtRow = lastRow;
            crtCol = lastCol;
        } else {
            crtZone = 0;
            crtRow = (int) (pointF.x / buttonCellWidth);
            crtCol = (int) (pointF.y / buttonCellHeight - 1);
        }
        lastZone = 0;
        lastRow = 0;
        lastCol = 0;
    }

    private void setupAnimator(int zone, ArrayList<MovableButton> buttons, boolean forwards) {
        if (zone == 0) {
            // selected
        } else if (zone == 2) {
            // unselectedzone
        }
    }

    private ArrayList<MovableButton> getAnimatedButtonsBetween(int zone, int crtRow, int crtCol,
            int lastRow, int lastCol) {
        return null;
    }

    private ArrayList<MovableButton> getAnimatedButtonsBefore(int zone,
            int lastRow, int lastCol) {
        return null;
    }

    private ArrayList<MovableButton> getAnimatedButtonsAfter(int zone,
            int lastRow, int lastCol) {
        return null;
    }

    private PointF getCurrentButtonCenter() {
        if (currentButton != null) {
            return new PointF(currentButton.getX(), currentButton.getY());
        } else {
            return null;
        }
    }

    private void putButtonDown(View v) {
        currentButton = null;
        lastZone = 0;
        lastRow = 0;
        lastCol = 0;
        // TODO
        // ((MovableButton)v).setSelected(true);
        // ((MovableButton)v).setPosition(null);
    }

    private void shuffleButtons() {
        for (int i = 0; i < selectedButtons.size(); i++) {
            MovableButton button = selectedButtons.get(i);

            LayoutParams params = new LayoutParams(buttonWidth, buttonHeight);
            params.leftMargin = (i % Colums) * buttonCellWidth;
            params.topMargin = (i / Colums) * buttonCellHeight;
            button.setLayoutParams(params);
            board.addView(button);
        }

        for (int i = 0; i < unselectedButtons.size(); i++) {
            MovableButton button = unselectedButtons.get(i);

            LayoutParams params = new LayoutParams(buttonWidth, buttonHeight);
            params.leftMargin = (i % Colums) * buttonCellWidth;
            params.topMargin = unselectedButtonsVertex.y + (i / Colums) * buttonCellHeight;
            button.setLayoutParams(params);
            board.addView(button);
        }
    }

    private void updateStructure() {

    }

    private void slog(String tagString, String string) {
        Log.i(tagString, string);
    }

    private void slog(String string) {
        Log.i("shuffle", string);
    }

    public static int dip2px(float dp, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dip(float px, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

}
