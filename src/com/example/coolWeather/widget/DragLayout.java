package com.example.coolWeather.widget;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

public class DragLayout extends LinearLayout {

	private ViewDragHelper.Callback dragCallBack;
	private ViewDragHelper mDragged;
	private slideListenerCallBack onSlideCallBack;
	private View contentView;
	private View actionView;

	private int actionDistance;
	private float downY;
	private float downX;
	protected int dragX;

	private final int AUTO_OPEN_SPEED_LIMIT = 400;

	public DragLayout(Context context, AttributeSet attrs) {
		super(context, attrs, -1);
		// 创建一个带有回调接口的ViewDragHelper
		dragCallBack = getDragCallBack();		
		mDragged = ViewDragHelper.create(this, 1.0f, dragCallBack);
		onSlideCallBack = new slideListenerCallBack() {					
			@Override
			public void onSlided(Boolean flag) {
				// TODO Auto-generated method stub						
			}
			@Override
			public void onClick() {
				// TODO Auto-generated method stub						
			}
		};
	}

	private Callback getDragCallBack() {
		return new ViewDragHelper.Callback() {

			@Override
			public boolean tryCaptureView(View arg0, int arg1) {
				// TODO Auto-generated method stub
				int a = 0;
				if(arg0 == contentView) a = 1;
				if(arg0 == actionView) a = 2;

				Log.v("drag", "This is tryCaptureView! view = " + a);
				return arg0 == contentView || arg0 == actionView;
			}

			public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
				dragX = left;
				getParent().requestDisallowInterceptTouchEvent(true);
				if(changedView == contentView){
					actionView.offsetLeftAndRight(dx);
				}
				if(changedView == actionView){
					contentView.offsetLeftAndRight(dx);
				}	
				if (actionView.getVisibility() == View.GONE) {
					actionView.setVisibility(View.VISIBLE);
				}
				invalidate();
				Log.v("drag", "This is onViewPositionChanged!");
			}

			public int clampViewPositionHorizontal(View child, int left, int dx) {
				if (child == contentView) {
					final int minLeftBound = -getPaddingLeft() - actionDistance;
					final int newLeft = Math.min(Math.max(minLeftBound, left), 0);
					Log.v("drag", "This is clampViewPositionHorizontal! contentView newLeft = " + newLeft);
					
					Log.v("drag", "minLeftbound = " + minLeftBound + "  actionDistance = " + actionDistance + "  left = " + left);
					return newLeft;
				} else {
					final int minLeftBound = getPaddingLeft()
							+ contentView.getMeasuredWidth() - actionDistance;
					final int maxLeftBound = getPaddingLeft()
							+ contentView.getMeasuredWidth() + getPaddingRight();
					final int newLeft = Math.min(Math.max(left, minLeftBound),
							maxLeftBound);
					Log.v("drag", "This is clampViewPositionHorizontal! actionView newLeft = " + newLeft);
					return newLeft;
				}
			}
			public int getViewHorizontalDragRange(View child) {
				return actionDistance;
			}
			public void onViewReleased(View releasedChild, float xvel, float yvel) {
				Log.v("drag", "This is onViewReleased!");
				super.onViewReleased(releasedChild, xvel, yvel);

				if(xvel <= -AUTO_OPEN_SPEED_LIMIT || dragX <= -actionDistance/2){
					mDragged.smoothSlideViewTo(contentView, -actionDistance, 0);
					onSlideCallBack.onSlided(true);
				}else{
					mDragged.smoothSlideViewTo(contentView, 0, 0);
					onSlideCallBack.onSlided(false);
				}			
				ViewCompat.postInvalidateOnAnimation(DragLayout.this);
			}
		};

	}

	@Override
	protected void onFinishInflate() {
		// TODO Auto-generated method stub
		super.onFinishInflate();
		contentView = getChildAt(0);
		actionView = getChildAt(1);
		actionView.setVisibility(View.GONE);
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		actionDistance = actionView.getMeasuredWidth();
		Log.v("drag", "onMeasure!  actionDistance = " + actionDistance);
	}
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		getParent().requestDisallowInterceptTouchEvent(false);
		if(mDragged.shouldInterceptTouchEvent(ev) == true){
			return true;
		}
		return super.onInterceptTouchEvent(ev);
	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			downX = event.getX();
			downY = event.getY();
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			if(Math.abs(downX-event.getX()) > 10 && Math.abs(downY-event.getY()) >10){
				onSlideCallBack.onClick();
			}
		}
		mDragged.processTouchEvent(event);
		return true;
	}

	public void revert(){
		if(mDragged != null){
			mDragged.smoothSlideViewTo(contentView, 0, 0);
			invalidate();
		}
	}	
	public void setOnslide(slideListenerCallBack callBack){
		this.onSlideCallBack = callBack;
	}
	@Override
	public void computeScroll() {
		super.computeScroll();
		if (mDragged.continueSettling(true)) {
			/**
			 * 导致失效发生在接下来的动画时间步,通常下显示帧。
			 *  这个方法可以从外部的调用UI线程只有当这种观点是附加到一个窗口。
			 */
			ViewCompat.postInvalidateOnAnimation(this);
		}
	}



	public interface slideListenerCallBack{
		void onClick();
		void onSlided(Boolean flag);
	}
}
