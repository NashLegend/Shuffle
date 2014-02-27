
package com.example.shuffle;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.widget.Button;

public class MovableButton extends Button {
    private String title = "";
    private int id = 1;
    private Point position=new Point(0, 0);
    private boolean selected=false;

    public MovableButton(Context context) {
        super(context);
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

    private void slog(String string) {
        Log.i("shuffle", string);
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
