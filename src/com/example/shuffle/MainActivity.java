
package com.example.shuffle;

import android.os.Bundle;
import android.app.Activity;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * @author NashLegend
 */
public class MainActivity extends Activity {

    private ShuffleBoard board;
    private ScrollView scroller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.ctt);
        scroller = (ScrollView) findViewById(R.id.scroller);
        board = new ShuffleBoard(this);
        board.setLayoutParams(new LayoutParams(-1, -1));
        scroller.addView(board);
        board.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {
                        board.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                        board.startView(scroller);
                    }
                });
    }
}
