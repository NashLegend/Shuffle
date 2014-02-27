package com.example.shuffle;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.widget.Button;

public class MovableButton extends Button {
	private String title = "";
	private int id = 1;
	private Point position = new Point(0, 0);
	private Point targetPosition = new Point(0, 0);

	public Point getTargetPosition() {
		return targetPosition;
	}

	public void setTargetPosition(Point targetPosition) {
		this.targetPosition = targetPosition;
	}

	private boolean selected = false;
	private AnimatorSet animatorSet = new AnimatorSet();

	public MovableButton(Context context) {
		super(context);
	}

	public void setTargetPositionIsNext() {
		if (targetPosition.x < (MainActivity.Colums - 1)) {
			targetPosition.x++;
		} else {
			targetPosition.x = 0;
			targetPosition.y++;
		}
	}

	public void setTargetPositionIsPrev() {
		if (targetPosition.x == 0 && targetPosition.y > 0) {
			targetPosition.x = MainActivity.Colums - 1;
			targetPosition.y--;
		} else if (targetPosition.x > 0) {
			targetPosition.x--;
		}
	}

	public void startAnimator(Point anchorPoint) {
		// TODO
		// 如果起止位置相差太小则无动画
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
