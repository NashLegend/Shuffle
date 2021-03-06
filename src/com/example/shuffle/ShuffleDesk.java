
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
import android.widget.LinearLayout;
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
    private LinearLayout senatorLayout;
    private ShuffleCardCandidate candidate;
    private LinearLayout candidateLayout;
    public static int minButtons = 4;
    public static int maxButtons = 24;
    private static Toast mToast = null;

    public ShuffleDesk(Context context, ScrollView scrollView) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_shuffle, this);

        candidateLayout = (LinearLayout) findViewById(R.id.CandidateLayout);
        senatorLayout = (LinearLayout) findViewById(R.id.SenatorLayout);

        candidate = (ShuffleCardCandidate) findViewById(R.id.candidate);
        senator = (ShuffleCardSenator) findViewById(R.id.senator);

        candidate.setDesk(this, scrollView, candidateLayout);
        senator.setDesk(this, scrollView, senatorLayout);
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
        candidateLayout.setVisibility(View.GONE);
    }

    public void switch2Normal() {
        candidateLayout.setVisibility(View.VISIBLE);
    }

    public void InitDatas() {

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

    private void shuffleButtons() {
        senator.shuffleButtons();
        candidate.shuffleButtons();
    }
    
    public ArrayList<MovableButton> getButtons() {
        ArrayList<MovableButton> buttons=new ArrayList<MovableButton>();
        buttons.addAll(senator.getList());
        buttons.addAll(candidate.getList());
        return buttons;
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

    public ArrayList<MovableButton> getSelectedButtons() {
        return selectedButtons;
    }

    public void setSelectedButtons(ArrayList<MovableButton> selectedButtons) {
        this.selectedButtons = selectedButtons;
    }

    public ArrayList<MovableButton> getUnselectedButtons() {
        return unselectedButtons;
    }

    public void setUnselectedButtons(ArrayList<MovableButton> unselectedButtons) {
        this.unselectedButtons = unselectedButtons;
    }
}
