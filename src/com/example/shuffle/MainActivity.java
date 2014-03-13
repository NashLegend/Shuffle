
package com.example.shuffle;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * @author NashLegend
 */
public class MainActivity extends Activity {

    private ShuffleDesk desk;
    public ScrollView scroller;
    public Button doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        scroller = (ScrollView) findViewById(R.id.scroller);
        doneButton = (Button) findViewById(R.id.done);
        desk = new ShuffleDesk(this, scroller);
        scroller.addView(desk);
        getButtons();

        desk.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {
                        desk.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                        desk.InitDatas();
                        desk.initView();
                    }
                });

        doneButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                commitChange(desk.getButtons());
            }
        });
    }

    public void getButtons() {

        ArrayList<MovableButton> selectedButtons = new ArrayList<MovableButton>();
        for (int i = 0; i < 12; i++) {
            MovableButton button = new MovableButton(this);
            button.setTitle("btn_" + i);
            button.setId(i);
            button.setSelected(true);
            selectedButtons.add(button);
        }

        ArrayList<MovableButton> unselectedButtons = new ArrayList<MovableButton>();
        for (int i = 20; i < 30; i++) {
            MovableButton button = new MovableButton(this);
            button.setTitle("btn_" + i);
            button.setId(i);
            button.setSelected(false);
            unselectedButtons.add(button);
        }

        desk.setSelectedButtons(selectedButtons);
        desk.setUnselectedButtons(unselectedButtons);
    }

    public void commitChange(ArrayList<MovableButton> buttons) {

    }
}
