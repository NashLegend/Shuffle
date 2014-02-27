package com.example.shuffle;

import java.util.ArrayList;
import java.util.Iterator;

import android.R.integer;
import android.os.Bundle;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * @author NashLegend
 * 
 */
public class MainActivity extends Activity {

	private ArrayList<MovableButton> selectedButtons = new ArrayList<MovableButton>();
	private ArrayList<MovableButton> unselectedButtons = new ArrayList<MovableButton>();
	private RelativeLayout board;
	private int buttonHeightDip = 90;
	private int buttonWidth = 0;
	private int buttonHeight = 0;
	private int selectedButtonsRows = 0;
	private int unselectedButtonsRows = 0;
	private int selectedButtonsTotalHeight = 0;
	private int unselectedButtonsTotalHeight = 0;
	public static final int Colums = 4;
	private int vGapDip = 2;// x2
	private int hGapDip = 3;// x2
	private int vGap = 0;
	private int hGap = 0;
	private int buttonCellWidth = 0;
	private int buttonCellHeight = 0;
	private int groupVGapDip = 60;
	private int groupVGap = 0;
	private Point selectedButtonsVertex = new Point(0, 0);
	private Point unselectedButtonsVertex = new Point(0, 0);
	private View middleView;

	private int lastZone = 0;
	private int lastRow = 0;
	private int lastCol = 0;

	private AnimatorSet selectedAnimatorSet = new AnimatorSet();
	private AnimatorSet unselectedAnimatorSet = new AnimatorSet();
	private AnimatorSet betweenAnimatorSet = new AnimatorSet();

	private static final int SELECTED_ZONE = 0;
	private static final int MIDDLE_ZONE = 1;
	private static final int UNSELECTED_ZONE = 2;

	private MovableButton currentButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		RelativeLayout rootLayout = (RelativeLayout) findViewById(R.id.ctt);
		board = new ShuffleBoard(this);
		board.setLayoutParams(new LayoutParams(-1, -1));
		rootLayout.addView(board);
		middleView = new View(this);
		board.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {

					@SuppressWarnings("deprecation")
					@Override
					public void onGlobalLayout() {
						// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍�
						board.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
						InitDatas();
						initView();
					}
				});
	}

	private void InitDatas() {
		getButtons();

		groupVGap = dip2px(groupVGapDip, this);

		vGap = dip2px(vGapDip, this);
		hGap = dip2px(hGapDip, this);

		buttonCellWidth = board.getWidth() / Colums;
		buttonHeight = dip2px(buttonHeightDip, this);

		buttonWidth = buttonCellWidth - hGap * 2;
		buttonCellHeight = buttonHeight + vGap * 2;

		selectedButtonsRows = (int) Math.ceil((double) (selectedButtons.size())
				/ Colums);
		unselectedButtonsRows = (int) Math.ceil((double) (unselectedButtons
				.size()) / Colums);

		unselectedButtonsTotalHeight = unselectedButtonsRows * buttonCellHeight;
		selectedButtonsTotalHeight = selectedButtonsRows * buttonCellHeight;

		int totHeight = unselectedButtonsTotalHeight
				+ selectedButtonsTotalHeight + groupVGap;
		if (totHeight > board.getHeight()) {
			Toast.makeText(this,
					"Sell your phone and buy a bigger one please !",
					Toast.LENGTH_SHORT).show();
			android.view.ViewGroup.LayoutParams params = board
					.getLayoutParams();
			params.height = totHeight;
			board.setLayoutParams(params);
		}

		selectedButtonsVertex = new Point(0, 0);
		unselectedButtonsVertex = new Point(0, selectedButtonsTotalHeight
				+ groupVGap);

	}

	private void initView() {
		LayoutParams params = new LayoutParams(-1, groupVGap);
		params.topMargin = selectedButtonsTotalHeight;
		middleView.setLayoutParams(params);
		middleView.setBackgroundColor(Color.parseColor("#00ffff"));
		board.addView(middleView);
		shuffleButtons();
	}

	private void getButtons() {
		for (int i = 0; i < 13; i++) {
			MovableButton button = new MovableButton(this);
			button.setTitle("btn_" + i);
			button.setId(i);
			button.setSelected(true);
			selectedButtons.add(button);
			button.setOnTouchListener(listener);
		}

		for (int i = 13; i < 20; i++) {
			MovableButton button = new MovableButton(this);
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

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				currentButton = (MovableButton) v;
				lastZone = 0;
				lastRow = 0;
				lastCol = 0;
				lastX = event.getRawX();
				lastY = event.getRawY();
				break;
			case MotionEvent.ACTION_MOVE:
				float dx = event.getRawX() - lastX;
				float dy = event.getRawY() - lastY;
				if (dx * dx + dy * dy > 5) {
					lastX = event.getRawX();
					lastY = event.getRawY();
					moveButton(v, dx, dy);
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				putButtonDown(v);
				break;
			default:
				break;
			}
			return false;
		}
	};

	private void moveButton(View button, float dx, float dy) {
		button.setX(button.getX() + dx);
		button.setY(button.getY() + dy);
		getCurrentXone();
	}

	private void getCurrentXone() {
		// 如果要修改高度，那么记得相关数也要修改，如vertex等等
		// 如果以前的zone和现在的zone不一样，那么在手指松开的时候，要修改两个数组
		// 以前的zone不是指lastZone，而是按下时的zone
		// 为避免运动带来的影响，每次跨区移动都进行一次zone扩展，不论是否需要
		// 扩展zone,如果扩展了这个zone。那么有可能会影响其他的zone，这都要考虑,扩展zone暂时先不给动画。
		// 【如果这个crtZone已经之前填满了，也是就size==columns*n，那么要扩展这个zone】->无效
		// 如果crtZone处在选择区，一定要shuffle
		// 如果导致了lastZone高度的改变，暂无动画。
		// 如果从选择区到达此地并且导致了选择区高度变小，会不会有可能因为选择区变小导致中间区身上移动而导致currentButton直接移动到非选择区,其实只要中间zone足够高就可以避免
		// 一会再考虑 TODO
		PointF pointF = getCurrentButtonCenter();
		int crtZone = 0;
		int crtRow = 0;
		int crtCol = 0;
		ArrayList<MovableButton> buttons = null;
		if (pointF.y > selectedButtonsTotalHeight + groupVGap) {
			// 处在非选择区

			// TODO
			crtZone = UNSELECTED_ZONE;
			crtRow = (int) (pointF.x / buttonCellWidth);
			crtCol = (int) ((pointF.y - unselectedButtonsVertex.y)
					/ buttonCellHeight - 1);
			if (lastZone != crtZone) {
				if (lastZone == 1) {
					buttons = getAnimatedButtonsAfter(crtZone, crtRow, crtCol,
							true);
					// TODO 扩展
				} else {
					// 不可达代码
				}
			} else if (crtCol != lastCol || crtRow != lastRow) {
				buttons = getAnimatedButtonsBetween(crtZone, crtRow, crtCol,
						lastRow, lastCol);
			}
		} else if (pointF.y > selectedButtonsTotalHeight) {
			// TODO
			crtZone = MIDDLE_ZONE;
			crtRow = lastRow;
			crtCol = lastCol;
			buttons = getAnimatedButtonsAfter(lastZone, lastRow, lastCol, false);
		} else {
			// 处在选择区
			// TODO 扩展
			crtZone = SELECTED_ZONE;
			crtRow = (int) (pointF.x / buttonCellWidth);
			crtCol = (int) (pointF.y / buttonCellHeight - 1);

			if (lastZone != crtZone) {
				if (lastZone == 1) {
					buttons = getAnimatedButtonsAfter(crtZone, crtRow, crtCol,
							true);
					// TODO 扩展
				} else {
					// 不可达代码
				}
			} else if (crtCol != lastCol || crtRow != lastRow) {
				buttons = getAnimatedButtonsBetween(crtZone, crtRow, crtCol,
						lastRow, lastCol);
			}
		}
		if (buttons != null) {
			setupAnimator(crtZone, buttons);
		}
		currentButton.setTargetPosition(new Point(crtRow, crtCol));
		lastZone = crtZone;
		lastRow = crtRow;
		lastCol = crtCol;
	}

	/**
	 * 这个函数的动画只发生在一个zone内,所以好像不需要zone，根据相对位置可以算出来下一个位置。
	 * 
	 * 动画过程是这样：每个按钮都有一个起始位置——起始点，一个将要去的位置——目标点。在静止不动的情况下起始点就是目标点
	 * 动画发起时这个按钮有可能是静止不动的，也有可能正在进行某个动画 这个按钮不必关心其他按钮，只要关心自己即可。
	 * 如果被控制的按钮currentButton的起始点在自己目标点的前方 ，落点在自己目标点的后方（后指比自己在，前指比自己小），则自己前移。 如果
	 * 被控制的按钮的起始点在自己目标点的后方而落点在自己目标点的前方则自己后移。 那么我们为什么要起始点？先不管，加上再说……
	 * 
	 * @param zone
	 * @param buttons
	 * @param forwards
	 * 
	 */
	private void setupAnimator(int zone, ArrayList<MovableButton> buttons) {
		if (buttons != null && buttons.size() > 0) {
			Point point = selectedButtonsVertex;
			if (zone == UNSELECTED_ZONE) {
				point = unselectedButtonsVertex;
			}
			for (MovableButton movableButton : buttons) {
				movableButton.startAnimator(point);
			}
		}

	}

	private ArrayList<MovableButton> getAnimatedButtonsBetween(int zone,
			int crtRow, int crtCol, int lastRow, int lastCol) {
		boolean movingBack = crtRow * Colums + crtCol - lastRow * Colums
				- lastCol > 0;// movingBack指控制的按钮向后移动，后果是中间的按钮前移
		ArrayList<MovableButton> buttons = new ArrayList<MovableButton>();
		if (zone == SELECTED_ZONE) {
			for (int i = 0; i < selectedButtons.size(); i++) {
				MovableButton button = selectedButtons.get(i);
				if (button == currentButton) {
					// 这个以及下面的if判断是不会成立的……
					continue;
				}
				if (isBetweenPoint(button.getTargetPosition().x,
						button.getTargetPosition().y, crtRow, crtCol, lastRow,
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
				if (isBetweenPoint(button.getTargetPosition().x,
						button.getTargetPosition().y, crtRow, crtCol, lastRow,
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
		// 好像不是开区间，而是从(last,crt];
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
		// 如果是目标区域，那么按钮们一定是要向后移动的
		// 如果是起始区域，那么按钮们一定是要向前走的
		ArrayList<MovableButton> buttons = new ArrayList<MovableButton>();
		if (zone == SELECTED_ZONE) {
			for (int i = 0; i < selectedButtons.size(); i++) {
				MovableButton button = selectedButtons.get(i);
				if (button == currentButton) {
					continue;
				}
				if (isAfterPoint(button.getTargetPosition().x,
						button.getTargetPosition().y, lastRow, lastCol,
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
				if (isAfterPoint(button.getTargetPosition().x,
						button.getTargetPosition().y, lastRow, lastCol,
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
			return new PointF(currentButton.getX(), currentButton.getY());
		} else {
			return null;
		}
	}

	private void putButtonDown(View v) {
		// 必须这发生在一个move之后，所以不必管其他按钮，其他按钮自然后回到相应的位置，因为其他按钮的动画一定已经开始甚至结束了
		// 当然例外是在MIDDLE_ZONE松手……
		// 回到原位置，则之后的都要后退，有可能会改变高度……那么相应的vertex有可能改变
		// Up之后统一设置position与targetPosition相等
		// TODO
		PointF pointF = getCurrentButtonCenter();
		int crtZone = 0;
		int crtRow = 0;
		int crtCol = 0;
		ArrayList<MovableButton> buttons = new ArrayList<MovableButton>();
		if (pointF.y > selectedButtonsTotalHeight + groupVGap) {
			// 处在非选择区，如果不在最后一个之后则取
			crtZone = UNSELECTED_ZONE;
			crtRow = (int) (pointF.x / buttonCellWidth);
			crtCol = (int) ((pointF.y - unselectedButtonsVertex.y)
					/ buttonCellHeight - 1);
			Point point = new Point(crtRow, crtCol);
			int ind = crtRow * Colums + crtCol;
			if (currentButton.isSelected()) {
				if (ind >= unselectedButtons.size()) {
					// 放到最后一格。size+1
					point.x = unselectedButtons.size() % Colums;
					point.y = unselectedButtons.size() / Colums;
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
			buttons.add(currentButton);// 是不是应该不能动画……但是太远就得有动画……
		} else if (pointF.y > selectedButtonsTotalHeight) {
			// TODO
			// 在中间松手则回到最初的位置，有可能改变高度
			crtZone = MIDDLE_ZONE;
			crtRow = lastRow;
			crtCol = lastCol;
			buttons = getAnimatedButtonsAfter(lastZone, lastRow, lastCol, false);
			currentButton.setTargetPosition(currentButton.getPosition());
			buttons.add(currentButton);
		} else {
			// 处在选择区
			// TODO 扩展
			crtZone = SELECTED_ZONE;
			crtRow = (int) (pointF.x / buttonCellWidth);
			crtCol = (int) (pointF.y / buttonCellHeight - 1);

			Point point = new Point(crtRow, crtCol);
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
		
		setupAnimator(crtZone, buttons);

		for (MovableButton movableButton : selectedButtons) {
			// TODO ???
			movableButton.setPosition(new Point(movableButton.getPosition().x,
					movableButton.getPosition().y));
		}

		for (MovableButton movableButton : unselectedButtons) {
			// TODO ???可以这样咩？
			movableButton.setPosition(new Point(movableButton.getPosition().x,
					movableButton.getPosition().y));
		}

		lastZone = 0;
		lastRow = 0;
		lastCol = 0;
		currentButton = null;
		// TODO
		// ((MovableButton)v).setSelected(true);
		// ((MovableButton)v).setPosition(null);
	}

	private void shuffleButtons() {
		for (int i = 0; i < selectedButtons.size(); i++) {
			MovableButton button = selectedButtons.get(i);

			// TODO -1 x
			Point point = new Point();
			point.x = i % Colums;
			point.y = i / Colums;
			button.setPosition(point);
			// TODO Point是不是一个引用，都指向point会不会有问题？
			button.setTargetPosition(new Point(point.x, point.y));

			LayoutParams params = new LayoutParams(buttonWidth, buttonHeight);
			params.leftMargin = point.x * buttonCellWidth;
			params.topMargin = point.y * buttonCellHeight;
			button.setLayoutParams(params);
			board.addView(button);
		}

		for (int i = 0; i < unselectedButtons.size(); i++) {
			MovableButton button = unselectedButtons.get(i);

			Point point = new Point();
			point.x = i % Colums;
			point.y = i / Colums;
			button.setPosition(point);
			// TODO Point是不是一个引用，都指向point会不会有问题？
			button.setTargetPosition(new Point(point.x, point.y));

			LayoutParams params = new LayoutParams(buttonWidth, buttonHeight);
			params.leftMargin = point.x * buttonCellWidth;
			params.topMargin = unselectedButtonsVertex.y + point.y
					* buttonCellHeight;
			button.setLayoutParams(params);
			board.addView(button);
		}
	}

	private void updateStructure() {

	}

	private void slog(String tagString, String string) {
		Log.i(tagString, string);
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
