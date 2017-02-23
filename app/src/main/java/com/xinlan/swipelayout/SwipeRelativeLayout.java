package com.xinlan.swipelayout;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

/**
 * Created by panyi on 2017/2/23.
 */

public class SwipeRelativeLayout extends RelativeLayout {
    private static final int MASK = 0xffff;

    public static final int SWIPE_MODE_LEFT_DISMISS = 0x1;
    public static final int SWIPE_MODE_RIGHT_DISMISS = 0x2;
    public static final int SWIPE_MODE_UP_DISMISS = 0x4;
    public static final int SWIPE_MODE_DOWN_DISMISS = 0x8;

    public static final int DISMISS_DIRECTION_RIGHT = 1;
    public static final int DISMISS_DIRECTION_LEFT = 2;
    public static final int DISMISS_DIRECTION_TOP = 3;
    public static final int DISMISS_DIRECTION_BOTTOM = 4;

    private int directionMode = SWIPE_MODE_LEFT_DISMISS | SWIPE_MODE_RIGHT_DISMISS;

    private float view_x;
    private float view_y;

    private float start_x;
    private float start_y;

    private float delta_x;
    private float delta_y;

    private int mTouchSlop;

    private VelocityTracker mTracker;

    public SwipeRelativeLayout(Context context) {
        super(context);
        init(context);
    }

    public SwipeRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SwipeRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SwipeRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mTracker = VelocityTracker.obtain();
        //System.out.println("touchSlop =  " + mTouchSlop);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        view_x = getX();
        view_y = getY();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = super.onTouchEvent(event);
        mTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ret = true;
                start_x = event.getRawX();
                start_y = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                //ViewHelper.setTranslationY(this,);
                delta_x = event.getRawX() - start_x;
                delta_y = event.getRawY() - start_y;
                //this.setX(view_x + delta_x);
                moveLayout(delta_x);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTracker.computeCurrentVelocity(50);
                //System.out.println("vel = " + mTracker.getXVelocity());
                if (Math.abs(delta_x) > getMeasuredWidth() * 0.6f
                        || (Math.abs(delta_x) > 10 && mTracker.getXVelocity() > 40)) {
                    dismiss(delta_x > 0 ? DISMISS_DIRECTION_RIGHT : DISMISS_DIRECTION_LEFT);
                } else {
                    backTrack();
                }
                delta_x = 0;
                delta_y = 0;
                break;
        }//end switch

        return ret;
    }

    /**
     * 复原
     */
    public void backTrack() {
        ViewPropertyAnimator.animate(this).x(view_x).alpha(1);
    }

    private Animator.AnimatorListener mDissmissAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            viewQuit();
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            viewQuit();
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    };

    public void viewQuit() {
        this.setVisibility(View.GONE);
        ViewHelper.setX(this, view_x);
        ViewHelper.setAlpha(this, 1);
        ViewPropertyAnimator.animate(this).setListener(null);
    }

    /**
     *
     */
    public void dismiss(final int direction) {
        float last_x = 0;
        switch (direction) {
            case DISMISS_DIRECTION_LEFT:
                last_x = view_x - getMeasuredWidth();
                break;
            case DISMISS_DIRECTION_RIGHT:
                last_x = view_x + getMeasuredWidth();
                break;
            case DISMISS_DIRECTION_TOP:
                break;
            case DISMISS_DIRECTION_BOTTOM:
                break;
            default:
                last_x = view_x + getMeasuredWidth();
                break;
        }

        ViewPropertyAnimator.animate(this).x(last_x).alpha(0).setListener(mDissmissAnimationListener).start();
    }


    public void moveLayout(final float delta_x) {
        ViewHelper.setX(this, view_x + delta_x);
        ViewHelper.setAlpha(this, 1 - (Math.abs(delta_x) / getMeasuredWidth()));
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTracker != null) {
            mTracker.clear();
            mTracker.recycle();
        }
    }

    int y = 100;

    public void showView(){
        y-=10;
        ViewHelper.setY(this,y);
    }
}//end class
