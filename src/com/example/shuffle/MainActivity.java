
package com.example.shuffle;

import android.os.Bundle;
import android.app.Activity;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.RelativeLayout;

/**
 * @author NashLegend
 */
public class MainActivity extends Activity {

    private ShuffleBoard board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.ctt);
        board = new ShuffleBoard(this);
        board.setLayoutParams(new LayoutParams(-1, -1));
        rootLayout.addView(board);
        board.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {
                        board.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                        board.startView();
                    }
                });
    }
}
