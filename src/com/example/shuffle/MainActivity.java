
package com.example.shuffle;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * @author NashLegend
 */
public class MainActivity extends Activity {

    private ShuffleDesk desk;
    public ScrollView scroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scroller = (ScrollView) findViewById(R.id.scroller);
        desk = new ShuffleDesk(this, scroller);
        scroller.addView(desk);
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
    }
}
