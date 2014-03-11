
package com.example.shuffle;

import android.content.Context;
import android.graphics.Point;
import android.view.View;

public class ShuffleCardCandidate extends ShuffleCard {

    public ShuffleCardCandidate(Context context, ShuffleDesk desk) {
        super(context, desk);
    }

    @Override
    public void shuffleButtons() {
        super.shuffleButtons();
        setClickListener();
    }

    @Override
    public void banishButton(MovableButton button) {
        super.banishButton(button);
        removeView(button);
    }

    @Override
    public void getResident(MovableButton button) {
        // TODO 自动生成的方法存根
        super.getResident(button);

        if (computeHeight() > getHeight()) {
            expand();
        }
        int i = list.size() - 1;
        Point point = new Point();
        point.x = i % ShuffleDesk.Colums;
        point.y = i / ShuffleDesk.Colums;
        button.setPosition(point);
        button.setTargetPosition(new Point(point.x, point.y));

        button.setXX(point.x * ShuffleDesk.buttonCellWidth + ShuffleDesk.hGap);
        button.setYY(point.y * ShuffleDesk.buttonCellHeight + ShuffleDesk.vGap);
        this.addView(button);
    }

    public void setClickListener() {
        for (MovableButton movableButton : list) {
            movableButton.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (desk.getSenator().getList().size() < ShuffleDesk.maxButtons) {
                        banishButton((MovableButton) v);
                        desk.getSenator().getResident((MovableButton) v);
                    } else {
                        // Too many
                    }
                }
            });
        }
    }

}
