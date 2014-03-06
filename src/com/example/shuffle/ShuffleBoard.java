package com.example.shuffle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class ShuffleBoard extends RelativeLayout {
	private ArrayList<MovableButton> selectedButtons = new ArrayList<MovableButton>();
	private ArrayList<MovableButton> unselectedButtons = new ArrayList<MovableButton>();
	private int buttonHeightDip = 40;
	private int buttonWidth = 0;
	private int buttonHeight = 0;
	private int selectedButtonsRows = 0;
	private int unselectedButtonsRows = 0;
	private int selectedButtonsTotalHeight = 0;
	private int unselectedButtonsTotalHeight = 0;
	public static int Colums = 4;
	private int vGapDip = 2;// x2
	private int hGapDip = 3;// x2
	private int vGap = 0;
	private int hGap = 0;
	public static int buttonCellWidth = 0;
	public static int buttonCellHeight = 0;
	public static int animateVersion = 11;
	private int groupVGapDip = 60;
	private int groupVGap = 0;
	private Point selectedButtonsVertex = new Point(0, 0);
	private Point unselectedButtonsVertex = new Point(0, 0);
	private AnimateView middleView;

	private int lastZone = 0;
	private int lastRow = 0;
	private int lastCol = 0;

	private static final int SELECTED_ZONE = 0;
	private static final int MIDDLE_ZONE = 1;
	private static final int UNSELECTED_ZONE = 2;

	private int finalCheckInt = 400;

	private int minSelectedZoneHeight = 0;

	private MovableButton currentButton;

	private int selectMarginTop = 100;

	public ShuffleBoard(Context context) {
		super(context);
	}

	public void startView() {
		InitDatas();
		initView();
	}

	private void InitDatas() {
		getButtons();

		groupVGap = dip2px(groupVGapDip, getContext());

		vGap = dip2px(vGapDip, getContext());
		hGap = dip2px(hGapDip, getContext());

		buttonCellWidth = this.getWidth() / Colums;
		buttonHeight = dip2px(buttonHeightDip, getContext());

		buttonWidth = buttonCellWidth - hGap * 2;
		buttonCellHeight = buttonHeight + vGap * 2;

		selectedButtonsRows = (int) Math.ceil((double) (selectedButtons.size())
				/ Colums);
		unselectedButtonsRows = (int) Math.ceil((double) (unselectedButtons
				.size()) / Colums);

		unselectedButtonsTotalHeight = unselectedButtonsRows * buttonCellHeight;
		selectedButtonsTotalHeight = selectedButtonsRows * buttonCellHeight;

		int totHeight = unselectedButtonsTotalHeight
				+ selectedButtonsTotalHeight + groupVGap
				+ selectedButtonsVertex.y;
		Log.i("shuffle", getHeight() + "");
		if (totHeight > this.getHeight()) {
			// TODO
			Toast.makeText(getContext(),
					"Sell your phone and go buy a bigger one please !",
					Toast.LENGTH_SHORT).show();
			// android.view.ViewGroup.LayoutParams params = this
			// .getLayoutParams();
			// params.height = totHeight;
			// this.setLayoutParams(params);
		}

		selectedButtonsVertex = new Point(0, selectMarginTop);
		unselectedButtonsVertex = new Point(0, selectedButtonsVertex.y
				+ selectedButtonsTotalHeight + groupVGap);
		minSelectedZoneHeight = (int) Math
				.ceil(((this.getHeight() * 0.4) / buttonCellHeight))
				* buttonCellHeight;

		updateStructureData();
	}

	private void initView() {
		middleView = new AnimateView(getContext());
		LayoutParams params = new LayoutParams(-1, groupVGap);
		params.topMargin = selectedButtonsTotalHeight + selectedButtonsVertex.y;
		middleView.setLayoutParams(params);
		middleView.setBackgroundColor(Color.parseColor("#00ffff"));
		this.addView(middleView);
		shuffleButtons();
	}

	private void getButtons() {
		for (int i = 0; i < 16; i++) {
			MovableButton button = new MovableButton(getContext());
			button.setTitle("btn_" + i);
			button.setId(i);
			button.setSelected(true);
			selectedButtons.add(button);
			button.setOnTouchListener(listener);
		}

		for (int i = 16; i < 31; i++) {
			MovableButton button = new MovableButton(getContext());
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
		private float ddx = 0f;
		private float ddy = 0f;
		private boolean moved = false;
		private float startX = 0f;
		private float startY = 0f;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if ((currentButton == null && event.getAction() == MotionEvent.ACTION_DOWN)
					|| currentButton == v) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (currentButton == null) {
						moved = false;
						currentButton = (MovableButton) v;
						if (currentButton.isSelected()) {
							lastZone = SELECTED_ZONE;
						} else {
							lastZone = UNSELECTED_ZONE;
						}
						startX = event.getRawX();
						startY = event.getRawY();
						lastRow = currentButton.getPosition().y;
						lastCol = currentButton.getPosition().x;
						lastX = startX;
						lastY = startY;
						ddx = event.getX();
						ddy = event.getY();
					}
					break;
				case MotionEvent.ACTION_MOVE:
					float dx = event.getRawX() - lastX;
					float dy = event.getRawY() - lastY;
					if (dx * dx + dy * dy > 9) {
						moved = true;
						lastX = event.getRawX();
						lastY = event.getRawY();
						moveButton(event.getRawX() - ddx, event.getRawY() - ddy);
					}
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
					// slog("OK Here it is up ~~~~~~~~~~~~");
					if (moved) {
						moveButton(event.getRawX() - ddx, event.getRawY() - ddy);
						putButtonDown();
					} else {
						flipAcross();
					}
					remeasure();
					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							finalCheck();
						}
					}, finalCheckInt);
					break;
				default:
					break;
				}
			}
			return false;
		}
	};

	private void moveButton(float dx, float dy) {
		// slog("\n*************************************\n");
		int[] location = { 0, 0 };
		this.getLocationOnScreen(location);
		currentButton.setXX(dx - location[0]);
		currentButton.setYY(dy - location[1]);
		getCurrentXone();
	}

	private void flipAcross() {
		if (currentButton == null) {
			return;
		}
		if (currentButton.isSelected()) {
			ArrayList<MovableButton> tmpButtons = getAnimatedButtonsAfter(
					SELECTED_ZONE, currentButton.getPosition().y,
					currentButton.getPosition().x, false);
			setupAnimator(SELECTED_ZONE, tmpButtons);
			Point point = new Point(unselectedButtons.size() % Colums,
					unselectedButtons.size() / Colums);
			currentButton.setTargetPosition(point);
			checkZone(UNSELECTED_ZONE, SELECTED_ZONE);
			currentButton.startAnimator(unselectedButtonsVertex);
			currentButton.setSelected(false);
			selectedButtons.remove(currentButton);
			unselectedButtons.add(currentButton);
		} else {
			ArrayList<MovableButton> tmpButtons = getAnimatedButtonsAfter(
					UNSELECTED_ZONE, currentButton.getPosition().y,
					currentButton.getPosition().x, false);
			setupAnimator(UNSELECTED_ZONE, tmpButtons);
			Point point = new Point(selectedButtons.size() % Colums,
					selectedButtons.size() / Colums);
			currentButton.setTargetPosition(point);
			checkZone(SELECTED_ZONE, UNSELECTED_ZONE);
			currentButton.startAnimator(selectedButtonsVertex);
			currentButton.setSelected(true);
			unselectedButtons.remove(currentButton);
			selectedButtons.add(currentButton);
		}

		setFinalPosition();
		currentButton = null;
	}

	private void getCurrentXone() {
		PointF pointF = getCurrentButtonCenter();
		int crtZone = 0;
		int crtRow = 0;
		int crtCol = 0;
		int aniZone = -1;
		ArrayList<MovableButton> buttons = new ArrayList<MovableButton>();
		if (pointF == null) {
			return;
		}
		if (pointF.y > unselectedButtonsVertex.y) {
			crtZone = UNSELECTED_ZONE;
			aniZone = crtZone;
			crtCol = (int) (pointF.x / buttonCellWidth);
			crtRow = (int) ((pointF.y - unselectedButtonsVertex.y) / buttonCellHeight);
			if (crtRow < 0) {
				crtRow = 0;
			}
			if (crtCol < 0) {
				crtCol = 0;
			}
			if (crtCol >= Colums) {
				crtCol = Colums - 1;
			}
			if (lastZone != crtZone) {
				if (lastZone == 1) {
					// slog("from DOWN_After" + currentButton.getText());
				} else {
					// slog("from DOWN_Fast" + currentButton.getText());
					ArrayList<MovableButton> tmpButtons = getAnimatedButtonsAfter(
							SELECTED_ZONE, lastRow, lastCol, false);
					setupAnimator(SELECTED_ZONE, tmpButtons);
				}
				buttons = getAnimatedButtonsAfter(crtZone, crtRow, crtCol, true);
			} else if (crtCol != lastCol || crtRow != lastRow) {
				// slog("from DOWN_Between" + currentButton.getText());
				buttons = getAnimatedButtonsBetween(crtZone, crtRow, crtCol,
						lastRow, lastCol);
			}
		} else if (pointF.y > unselectedButtonsVertex.y - groupVGap) {
			crtZone = MIDDLE_ZONE;
			aniZone = lastZone;
			crtRow = lastRow;
			crtCol = lastCol;
			// slog("from Middle_After" + currentButton.getTexst());
			buttons = getAnimatedButtonsAfter(lastZone, lastRow, lastCol, false);
		} else {
			crtZone = SELECTED_ZONE;
			aniZone = crtZone;
			crtCol = (int) (pointF.x / buttonCellWidth);
			crtRow = (int) (pointF.y - selectedButtonsVertex.y)
					/ buttonCellHeight;
			if (crtRow < 0) {
				crtRow = 0;
			}
			if (crtCol < 0) {
				crtCol = 0;
			}
			if (crtCol >= Colums) {
				crtCol = Colums - 1;
			}
			if (lastZone != crtZone) {
				if (lastZone == 1) {
					// slog("from UP_After" + currentButton.getText());
				} else {
					// slog("from UP_Fast" + currentButton.getText());
					ArrayList<MovableButton> tmpButtons = getAnimatedButtonsAfter(
							UNSELECTED_ZONE, lastRow, lastCol, false);
					setupAnimator(UNSELECTED_ZONE, tmpButtons);
				}
				buttons = getAnimatedButtonsAfter(crtZone, crtRow, crtCol, true);
			} else if (crtCol != lastCol || crtRow != lastRow) {
				// slog("from UP_Between" + currentButton.getText());
				buttons = getAnimatedButtonsBetween(crtZone, crtRow, crtCol,
						lastRow, lastCol);
			}
		}
		checkZone(crtZone, lastZone);
		setupAnimator(aniZone, buttons);
		currentButton.setTargetPosition(new Point(crtCol, crtRow));
		lastZone = crtZone;
		lastRow = crtRow;
		lastCol = crtCol;
	}

	private void setupAnimator(int zone, ArrayList<MovableButton> buttons) {
		// slog("Current Zone is " + zone
		// + " and here are the buttons to be animated");
		if (buttons != null && buttons.size() > 0) {
			Point point = selectedButtonsVertex;
			if (zone == UNSELECTED_ZONE) {
				point = unselectedButtonsVertex;
			}
			for (MovableButton movableButton : buttons) {
				// slog(movableButton.getText() + "");
				movableButton.startAnimator(point);
			}
		}
	}

	private ArrayList<MovableButton> getAnimatedButtonsBetween(int zone,
			int crtRow, int crtCol, int lastRow, int lastCol) {
		// slog("Between: " + zone + "_" + crtRow + "_" + crtCol + "_" + lastRow
		// + "_" + lastCol);
		boolean movingBack = crtRow * Colums + crtCol - lastRow * Colums
				- lastCol > 0;// movingBack指控制的按钮向后移动，后果是中间的按钮前移
		if (movingBack) {
			// slog("Move Forwards");
		} else {
			// slog("Move Back");
		}
		ArrayList<MovableButton> buttons = new ArrayList<MovableButton>();
		if (zone == SELECTED_ZONE) {
			for (int i = 0; i < selectedButtons.size(); i++) {
				MovableButton button = selectedButtons.get(i);
				if (button == currentButton) {
					// 这个以及下面的if判断是不会成立的……
					continue;
				}
				if (isBetweenPoint(button.getTargetPosition().y,
						button.getTargetPosition().x, crtRow, crtCol, lastRow,
						lastCol)) {
					buttons.add(button);
				}
			}
		} else if (zone == UNSELECTED_ZONE) {
			for (int i = 0; i < unselectedButtons.size(); i++) {
				MovableButton button = unselectedButtons.get(i);
				if (button == currentButton) {
					continue;
				}
				if (isBetweenPoint(button.getTargetPosition().y,
						button.getTargetPosition().x, crtRow, crtCol, lastRow,
						lastCol)) {
					buttons.add(button);
				}
			}
		}
		for (Iterator<MovableButton> iterator = buttons.iterator(); iterator
				.hasNext();) {
			MovableButton movableButton = (MovableButton) iterator.next();

			if (movingBack) {
				movableButton.setTargetPositionIsPrev();
			} else {
				movableButton.setTargetPositionIsNext();
			}
		}
		return buttons;
	}

	private boolean isBetweenPoint(int row, int col, int crtRow, int crtCol,
			int lastRow, int lastCol) {
		int tis = row * Colums + col;
		int crt = crtRow * Colums + crtCol;
		int lst = lastRow * Colums + lastCol;
		if (lst < tis && tis <= crt) {
			return true;
		} else if (lst > tis && tis >= crt) {
			return true;
		}
		return false;
	}

	private ArrayList<MovableButton> getAnimatedButtonsAfter(int zone,
			int lastRow, int lastCol, boolean isTarget) {
		// slog("After: " + zone + "_" + lastRow + "_" + lastCol + "_" +
		// isTarget);
		// if (isTarget) {
		// slog("Move Back");
		// } else {
		// slog("Move Forward");
		// }
		ArrayList<MovableButton> buttons = new ArrayList<MovableButton>();
		if (zone == SELECTED_ZONE) {
			for (int i = 0; i < selectedButtons.size(); i++) {
				MovableButton button = selectedButtons.get(i);
				if (button == currentButton) {
					continue;
				}
				if (isAfterPoint(button.getTargetPosition().y,
						button.getTargetPosition().x, lastRow, lastCol,
						isTarget)) {
					buttons.add(button);
				}
			}
		} else if (zone == UNSELECTED_ZONE) {
			for (int i = 0; i < unselectedButtons.size(); i++) {
				MovableButton button = unselectedButtons.get(i);
				if (button == currentButton) {
					continue;
				}
				if (isAfterPoint(button.getTargetPosition().y,
						button.getTargetPosition().x, lastRow, lastCol,
						isTarget)) {
					buttons.add(button);
				}
			}
		}

		for (Iterator<MovableButton> iterator = buttons.iterator(); iterator
				.hasNext();) {
			MovableButton movableButton = (MovableButton) iterator.next();
			if (isTarget) {
				movableButton.setTargetPositionIsNext();
			} else {
				movableButton.setTargetPositionIsPrev();
			}
		}
		return buttons;
	}

	private boolean isAfterPoint(int row, int col, int crtRow, int crtCol,
			boolean isTarget) {
		// 如果是目标区域，那么按钮们一定是半闭区间的，
		// 如果是起始区域，那么按钮们一定是开区间的
		int tis = row * Colums + col;
		int crt = crtRow * Colums + crtCol;
		if (isTarget && tis >= crt) {
			return true;
		}

		if (!isTarget && tis > crt) {
			return true;
		}

		return false;
	}

	private PointF getCurrentButtonCenter() {
		if (currentButton != null) {
			return new PointF(currentButton.getXX() + buttonWidth / 2,
					currentButton.getYY() + buttonHeight / 2);
		} else {
			return null;
		}
	}

	private void putButtonDown() {
		PointF pointF = getCurrentButtonCenter();
		int crtZone = 0;
		int crtRow = 0;
		int crtCol = 0;
		int aniZone = -1;
		ArrayList<MovableButton> buttons = new ArrayList<MovableButton>();
		if (pointF == null) {
			return;
		}
		if (pointF.y > unselectedButtonsVertex.y) {
			// 处在非选择区，如果不在最后一个之后则取
			crtZone = UNSELECTED_ZONE;
			aniZone = crtZone;
			crtCol = (int) (pointF.x / buttonCellWidth);
			crtRow = (int) ((pointF.y - unselectedButtonsVertex.y) / buttonCellHeight);
			if (crtRow < 0) {
				// 不可达
				crtRow = 0;
			}
			if (crtCol < 0) {
				crtCol = 0;
			}
			if (crtCol >= Colums) {
				crtCol = Colums - 1;
			}
			Point point = new Point(crtCol, crtRow);
			int ind = crtRow * Colums + crtCol;
			// slog(ind + ",,,,,,,,,," + unselectedButtons.size());
			if (currentButton.isSelected()) {
				if (ind >= unselectedButtons.size()) {
					// 放到最后一格。size+1
					point.x = unselectedButtons.size() % Colums;
					point.y = unselectedButtons.size() / Colums;
					// slog(point.x + ",,,,,,,,," + point.y);
				}
				// 上下顺序不能倒
				// 如果是来自选择区的，那么数组改变
				selectedButtons.remove(currentButton);
				unselectedButtons.add(currentButton);
			} else {
				if (ind >= unselectedButtons.size() - 1) {
					// 放到最后一格.size
					point.x = (unselectedButtons.size() - 1) % Colums;
					point.y = (unselectedButtons.size() - 1) / Colums;
				}
			}
			currentButton.setSelected(false);
			currentButton.setTargetPosition(point);
			buttons.add(currentButton);
		} else if (pointF.y > unselectedButtonsVertex.y - groupVGap) {
			crtZone = MIDDLE_ZONE;
			if (currentButton.isSelected()) {
				lastZone = SELECTED_ZONE;
				lastRow = (selectedButtons.size() - 1) / Colums;
				lastCol = (selectedButtons.size() - 1) % Colums;
				checkZone(SELECTED_ZONE, MIDDLE_ZONE);
			} else {
				lastZone = UNSELECTED_ZONE;
				lastRow = (unselectedButtons.size() - 1) / Colums;
				lastCol = (unselectedButtons.size() - 1) % Colums;
			}
			aniZone = lastZone;
			currentButton.setTargetPosition(new Point(lastCol, lastRow));
			buttons.add(currentButton);
		} else {
			crtZone = SELECTED_ZONE;
			aniZone = crtZone;
			crtCol = (int) (pointF.x / buttonCellWidth);
			crtRow = (int) (pointF.y - selectedButtonsVertex.y)
					/ buttonCellHeight;
			if (crtRow < 0) {
				crtRow = 0;
			}
			if (crtCol < 0) {
				crtCol = 0;
			}
			if (crtCol >= Colums) {
				crtCol = Colums - 1;
			}

			Point point = new Point(crtCol, crtRow);
			int ind = crtRow * Colums + crtCol;
			if (!currentButton.isSelected()) {
				if (ind >= selectedButtons.size()) {
					// 放到最后一格。size+1
					point.x = selectedButtons.size() % Colums;
					point.y = selectedButtons.size() / Colums;
				}
				// 如果是来自非选择区的，那么数组改变
				unselectedButtons.remove(currentButton);
				selectedButtons.add(currentButton);
			} else {
				if (ind >= selectedButtons.size() - 1) {
					// 放到最后一格.size
					point.x = (selectedButtons.size() - 1) % Colums;
					point.y = (selectedButtons.size() - 1) / Colums;
				}
			}
			currentButton.setSelected(true);
			currentButton.setTargetPosition(point);
			buttons.add(currentButton);
		}

		setupAnimator(aniZone, buttons);
		setFinalPosition();

		lastZone = 0;
		lastRow = 0;
		lastCol = 0;
		currentButton = null;
	}

	private void setFinalPosition() {
		for (MovableButton movableButton : selectedButtons) {
			movableButton
					.setPosition(new Point(movableButton.getTargetPosition().x,
							movableButton.getTargetPosition().y));
		}

		for (MovableButton movableButton : unselectedButtons) {
			movableButton
					.setPosition(new Point(movableButton.getTargetPosition().x,
							movableButton.getTargetPosition().y));
		}
	}

	private void shuffleButtons() {
		for (int i = 0; i < selectedButtons.size(); i++) {
			MovableButton button = selectedButtons.get(i);

			Point point = new Point();
			point.x = i % Colums;
			point.y = i / Colums;
			button.setPosition(point);
			button.setTargetPosition(new Point(point.x, point.y));

			LayoutParams params = new LayoutParams(buttonWidth, buttonHeight);
			params.leftMargin = point.x * buttonCellWidth;
			params.topMargin = selectedButtonsVertex.y + point.y
					* buttonCellHeight;
			button.setLayoutParams(params);
			this.addView(button);
		}

		for (int i = 0; i < unselectedButtons.size(); i++) {
			MovableButton button = unselectedButtons.get(i);

			Point point = new Point();
			point.x = i % Colums;
			point.y = i / Colums;
			button.setPosition(point);
			button.setTargetPosition(new Point(point.x, point.y));

			LayoutParams params = new LayoutParams(buttonWidth, buttonHeight);
			params.leftMargin = point.x * buttonCellWidth;
			params.topMargin = unselectedButtonsVertex.y + point.y
					* buttonCellHeight;
			button.setLayoutParams(params);
			this.addView(button);
		}
	}

	private void checkZone(int targetZone, int fromZone) {
		if (targetZone == fromZone) {
			return;
		}
		int len = 0;
		boolean ani = true;
		switch (targetZone) {
		case SELECTED_ZONE:
			if (currentButton.isSelected()) {
				len = selectedButtons.size();
			} else {
				len = selectedButtons.size() + 1;
			}
			break;
		case MIDDLE_ZONE:
			if (fromZone == SELECTED_ZONE) {
				if (currentButton.isSelected()) {
					len = selectedButtons.size() - 1;
				} else {
					len = selectedButtons.size();
				}
			} else {
				ani = false;
			}

			break;
		case UNSELECTED_ZONE:
			if (fromZone == SELECTED_ZONE) {
				if (currentButton.isSelected()) {
					len = selectedButtons.size() - 1;
				} else {
					len = selectedButtons.size();
				}
			} else {
				ani = false;
			}
			break;
		default:
			break;
		}

		boolean aniSec = true;
		if (ani) {
			if (len % Colums == 1 && fromZone != SELECTED_ZONE
					&& shouldExpand()) {
				expand();
				aniSec = false;
			} else if (len % Colums == 0 && fromZone == SELECTED_ZONE
					&& shouldShrink()) {
				shrink();
				aniSec = false;
			}
		}

		if (aniSec) {
			ani = true;
			switch (targetZone) {
			case UNSELECTED_ZONE:
				if (currentButton.isSelected()) {
					len = unselectedButtons.size() + 1;
				} else {
					len = unselectedButtons.size();
				}
				break;
			case MIDDLE_ZONE:
				if (fromZone == UNSELECTED_ZONE) {
					if (currentButton.isSelected()) {
						len = unselectedButtons.size();
					} else {
						len = unselectedButtons.size() - 1;
					}
				} else {
					ani = false;
				}
				break;
			case SELECTED_ZONE:
				if (fromZone == UNSELECTED_ZONE) {
					if (currentButton.isSelected()) {
						len = unselectedButtons.size();
					} else {
						len = unselectedButtons.size() - 1;
					}
				} else {
					ani = false;
				}
				break;
			default:
				break;
			}

			if (ani) {
				if (len % Colums == 1 && fromZone != UNSELECTED_ZONE
						&& shouldShrink2()) {
					// UNSELECTED_ZONE expand == SELECTED_ZONE shrink
					shrink();
				} else if (len % Colums == 0 && fromZone == UNSELECTED_ZONE
						&& shouldExpand2()) {
					// UNSELECTED_ZONE shrink == SELECTED_ZONE expand
					expand();
				}
			}
		}
	}

	private void expand() {
		selectedButtonsVertex = new Point(0, selectMarginTop);
		selectedButtonsTotalHeight += buttonCellHeight;
		unselectedButtonsVertex = new Point(0, selectedButtonsVertex.y
				+ selectedButtonsTotalHeight + groupVGap);
		ArrayList<MovableButton> tmpList = new ArrayList<MovableButton>();
		for (MovableButton movableButton : unselectedButtons) {
			if (movableButton == currentButton) {
				continue;
			}
			tmpList.add(movableButton);
		}
		middleView.startAnimator(selectedButtonsVertex.y
				+ selectedButtonsTotalHeight);
		setupAnimator(UNSELECTED_ZONE, tmpList);
	}

	private boolean shouldExpand() {
		// 外界进入SelectedZone
		// return true;
		if (currentButton.isSelected()) {
			if (buttonCellHeight * (selectedButtons.size() - 1) / Colums < selectedButtonsTotalHeight) {
				return false;
			} else {
				return true;
			}
		} else {
			if (buttonCellHeight * (selectedButtons.size() + 1) / Colums >= selectedButtonsTotalHeight) {
				return true;
			} else {
				return false;
			}
		}
	}

	private void shrink() {
		selectedButtonsVertex = new Point(0, selectMarginTop);
		selectedButtonsTotalHeight -= buttonCellHeight;
		unselectedButtonsVertex = new Point(0, selectedButtonsVertex.y
				+ selectedButtonsTotalHeight + groupVGap);
		ArrayList<MovableButton> tmpList = new ArrayList<MovableButton>();
		for (MovableButton movableButton : unselectedButtons) {
			if (movableButton == currentButton) {
				continue;
			}
			tmpList.add(movableButton);
		}
		middleView.startAnimator(selectedButtonsVertex.y
				+ selectedButtonsTotalHeight);
		setupAnimator(UNSELECTED_ZONE, tmpList);
	}

	private boolean shouldShrink() {
		// return true;
		if (currentButton.isSelected()) {
			if (buttonCellHeight * (selectedButtons.size() - 1) / Colums >= minSelectedZoneHeight) {
				return true;
			} else {
				if ((int) (Math.ceil((unselectedButtons.size() + 1) / Colums))
						* buttonCellHeight + groupVGap
						+ selectedButtonsVertex.y + selectedButtonsTotalHeight > getHeight()) {
					return true;
				} else {
					return false;
				}
			}
		} else {
			if (buttonCellHeight * selectedButtons.size() / Colums >= minSelectedZoneHeight) {
				return true;
			} else {
				if ((int) (Math.ceil(unselectedButtons.size() / Colums))
						* buttonCellHeight + groupVGap
						+ selectedButtonsVertex.y + minSelectedZoneHeight > getHeight()) {
					return true;
				} else {
					return false;
				}
			}
		}
	}

	private boolean shouldExpand2() {
		// as here means that selected zone's height won't changed,so i can
		// focus on the unselected zone's height
		// selectedButtonsTotalHeight<minSelectedZoneHeight==true
		if (selectedButtonsTotalHeight < minSelectedZoneHeight) {
			if (currentButton.isSelected()) {
				if (unselectedButtonsVertex.y + buttonCellHeight
						+ buttonCellHeight * unselectedButtons.size() / Colums < getHeight()) {
					return true;
				}
			} else if (unselectedButtonsVertex.y + buttonCellHeight
					+ buttonCellHeight * (unselectedButtons.size() - 1)
					/ Colums < getHeight()) {
				return true;
			}
		}

		return false;
	}

	private boolean shouldShrink2() {
		// as here means that selected zone's height won't changed,so i can
		// focus on the unselected zone's height
		if (currentButton.isSelected()) {
			if (unselectedButtonsVertex.y + buttonCellHeight
					* unselectedButtons.size() / Colums + buttonCellHeight > getHeight()) {
				return true;
			} else {
				return false;
			}
		} else {
			if (unselectedButtonsVertex.y + buttonCellHeight
					* (unselectedButtons.size() - 1) / Colums
					+ buttonCellHeight > getHeight()) {
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 
	 */
	private void updateStructureData() {
		if (selectedButtonsTotalHeight <= minSelectedZoneHeight) {
			if (unselectedButtonsTotalHeight + groupVGap
					+ minSelectedZoneHeight + selectedButtonsVertex.y > this
						.getHeight()) {
				selectedButtonsTotalHeight = buttonCellHeight
						* ((this.getHeight() - unselectedButtonsTotalHeight
								- groupVGap - selectedButtonsVertex.y) / buttonCellHeight);
				unselectedButtonsVertex.y = selectedButtonsVertex.y
						+ selectedButtonsTotalHeight + groupVGap;
			} else {
				selectedButtonsTotalHeight = minSelectedZoneHeight;
				unselectedButtonsVertex.y = selectedButtonsVertex.y
						+ selectedButtonsTotalHeight + groupVGap;
			}
		}
	}

	private void remeasure() {
		// 手指抬起后
		selectedButtonsRows = (int) Math.ceil((double) (selectedButtons.size())
				/ Colums);
		unselectedButtonsRows = (int) Math.ceil((double) (unselectedButtons
				.size()) / Colums);

		unselectedButtonsTotalHeight = unselectedButtonsRows * buttonCellHeight;
		selectedButtonsTotalHeight = selectedButtonsRows * buttonCellHeight;

		int totHeight = unselectedButtonsTotalHeight
				+ selectedButtonsTotalHeight + groupVGap
				+ selectedButtonsVertex.y;
		if (totHeight > this.getHeight()) {
			Toast.makeText(getContext(),
					"Sell your phone and go buy a bigger one please !",
					Toast.LENGTH_SHORT).show();
			// android.view.ViewGroup.LayoutParams params = this
			// .getLayoutParams();
			// params.height = totHeight;
			// this.setLayoutParams(params);
		}

		selectedButtonsVertex = new Point(0, selectMarginTop);
		unselectedButtonsVertex = new Point(0, selectedButtonsVertex.y
				+ selectedButtonsTotalHeight + groupVGap);
		updateStructureData();
	}

	private void finalCheck() {
		// 终极检查
		// 检查时要保证目前没有按钮在被拖动
		if (currentButton == null) {
			// updateStructureData();

			middleView.setYY(selectedButtonsTotalHeight
					+ selectedButtonsVertex.y);

			ButtonComparator comparator = new ButtonComparator();
			Collections.sort(selectedButtons, comparator);
			for (int i = 0; i < selectedButtons.size(); i++) {
				MovableButton button = selectedButtons.get(i);

				Point point = new Point();
				point.x = i % Colums;
				point.y = i / Colums;
				button.setPosition(point);
				button.setTargetPosition(new Point(point.x, point.y));
				button.setXX(point.x * buttonCellWidth);
				button.setYY(selectedButtonsVertex.y + point.y
						* buttonCellHeight);
			}

			Collections.sort(unselectedButtons, comparator);
			for (int i = 0; i < unselectedButtons.size(); i++) {
				MovableButton button = unselectedButtons.get(i);

				Point point = new Point();
				point.x = i % Colums;
				point.y = i / Colums;
				button.setPosition(point);
				button.setTargetPosition(new Point(point.x, point.y));

				button.setXX(point.x * buttonCellWidth);
				button.setYY(unselectedButtonsVertex.y + point.y
						* buttonCellHeight);
			}
		}
	}

	public void commitChange() {

	}

	public class ButtonComparator implements Comparator<MovableButton> {

		@Override
		public int compare(MovableButton lhs, MovableButton rhs) {
			int com = lhs.getIndex() - rhs.getIndex();
			if (com > 0) {
				return 1;
			} else if (com == 0) {
				return 0;
			} else {
				return -1;
			}

		}

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
