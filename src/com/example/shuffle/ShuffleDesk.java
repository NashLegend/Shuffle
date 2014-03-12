
package com.example.shuffle;

import java.util.ArrayList;
import java.util.Comparator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

public class ShuffleDesk extends RelativeLayout {
    private ArrayList<MovableButton> selectedButtons = new ArrayList<MovableButton>();
    private ArrayList<MovableButton> unselectedButtons = new ArrayList<MovableButton>();
    private int buttonHeightDip = 40;
    public static int buttonWidth = 0;
    public static int buttonHeight = 0;
    public static int Colums = 4;
    private int vGapDip = 2;// x2
    private int hGapDip = 3;// x2
    public static int vGap = 0;
    public static int hGap = 0;
    public static int buttonCellWidth = 0;
    public static int buttonCellHeight = 0;
    public static int animateVersion = 11;
    private int minSelectedZoneHeight = 0;
    private ShuffleCardSenator senator;
    private ShuffleCardCandidate candidate;
    public static int minButtons = 4;
    public static int maxButtons = 24;
    private static Toast mToast = null;

    public ShuffleDesk(Context context, ScrollView scrollView) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_shuffle, this);

        candidate = (ShuffleCardCandidate) findViewById(R.id.candidate);
        senator = (ShuffleCardSenator) findViewById(R.id.senator);

        candidate.setDesk(this, scrollView);
        senator.setDesk(this, scrollView);
    }

    public ShuffleDesk(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_shuffle, this);

        candidate = (ShuffleCardCandidate) findViewById(R.id.candidate);
        senator = (ShuffleCardSenator) findViewById(R.id.senator);
    }

    public void switch2Edit() {
        candidate.setVisibility(View.GONE);
    }

    public void switch2Normal() {
        candidate.setVisibility(View.VISIBLE);
    }

    public void InitDatas() {

        getButtons();

        vGap = dip2px(vGapDip, getContext());
        hGap = dip2px(hGapDip, getContext());

        buttonCellWidth = this.getWidth() / Colums;
        buttonHeight = dip2px(buttonHeightDip, getContext());

        buttonWidth = buttonCellWidth - hGap * 2;
        buttonCellHeight = buttonHeight + vGap * 2;

        minSelectedZoneHeight = buttonCellHeight * 4;
        senator.setStandardMinHeight(minSelectedZoneHeight);

        senator.setList(selectedButtons);
        candidate.setList(unselectedButtons);
    }

    public void initView() {
        shuffleButtons();
    }

    private void getButtons() {
        for (int i = 0; i < 12; i++) {
            MovableButton button = new MovableButton(getContext());
            button.setTitle("btn_" + i);
            button.setId(i);
            button.setSelected(true);
            selectedButtons.add(button);
        }

        for (int i = 20; i < 53; i++) {
            MovableButton button = new MovableButton(getContext());
            button.setTitle("btn_" + i);
            button.setId(i);
            button.setSelected(false);
            unselectedButtons.add(button);
        }
    }

    private void shuffleButtons() {
        senator.shuffleButtons();
        candidate.shuffleButtons();
    }

    public void slog(String string) {
        Log.i("shuffle", string);
    }

    public static void showToast(Context context, String text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    public static int dip2px(float dp, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int px2dip(float px, Context context) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public ShuffleCardSenator getSenator() {
        return senator;
    }

    public void setSenator(ShuffleCardSenator senator) {
        this.senator = senator;
    }

    public ShuffleCardCandidate getCandidate() {
        return candidate;
    }

    public void setCandidate(ShuffleCardCandidate candidate) {
        this.candidate = candidate;
    }
}
