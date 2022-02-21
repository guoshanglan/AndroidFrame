package com.zhuorui.commonwidget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.zhuorui.commonwidget.util.AnimationBuild;

/**
 * date   : 2019-08-27 18:39
 * desc   :
 */
public class ZRLoadingView extends LinearLayout {

    private ObjectAnimator rotate;//旋转动画
    private ImageView ivCircle;
    private TextView tvMsg;
    private int mCircleSize;
    private boolean mStart;
    private int msgTextColor = Color.parseColor("#818191");
    private Drawable stopDrawable;
    private Drawable rotateDrawable;
    private int mDelayTimeMillis = 0;//延时关闭时间
    private long mStartTimeMillis = 0;

    public ZRLoadingView(Context context) {
        this(context, null);
    }

    public ZRLoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZRLoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ZRLoadingView);
        if (!typedArray.hasValue(R.styleable.ZRLoadingView_android_orientation)) {
            setOrientation(VERTICAL);
        }
        if (!typedArray.hasValue(R.styleable.ZRLoadingView_android_gravity)) {
            setGravity(Gravity.CENTER);
        }
        if (typedArray.hasValue(R.styleable.ZRLoadingView_zr_CircleSize)) {
            mCircleSize = typedArray.getDimensionPixelOffset(R.styleable.ZRLoadingView_zr_CircleSize, 0);
        } else {
            mCircleSize = (int) (getResources().getDisplayMetrics().density * 32);
        }
        if (typedArray.hasValue(R.styleable.ZRLoadingView_zr_RotateDrawable)) {
            rotateDrawable = typedArray.getDrawable(R.styleable.ZRLoadingView_zr_RotateDrawable);
        } else {
            rotateDrawable = ContextCompat.getDrawable(getContext(), R.mipmap.ic_loading);
        }
        if (typedArray.hasValue(R.styleable.ZRLoadingView_zr_StopDrawable)) {
            stopDrawable = typedArray.getDrawable(R.styleable.ZRLoadingView_zr_StopDrawable);
        } else {
            stopDrawable = rotateDrawable;
        }
        mDelayTimeMillis = typedArray.getInt(R.styleable.ZRLoadingView_zr_DelayStopTime, mDelayTimeMillis);
        setCiecleView();
        if (typedArray.hasValue(R.styleable.ZRLoadingView_msg_text_color)) {
            msgTextColor = typedArray.getColor(R.styleable.ZRLoadingView_msg_text_color, msgTextColor);
        }
        if (typedArray.hasValue(R.styleable.ZRLoadingView_msg_text)) {
            setMessage(typedArray.getString(R.styleable.ZRLoadingView_msg_text));
        }
        typedArray.recycle();
    }

    @Override
    public void setOrientation(int orientation) {
        super.setOrientation(orientation);
        if (orientation == VERTICAL) {
            if (ivCircle != null) ((LayoutParams) ivCircle.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL;
            if (tvMsg != null) {
                LayoutParams lp = ((LayoutParams) tvMsg.getLayoutParams());
                lp.gravity = Gravity.CENTER_HORIZONTAL;
                lp.topMargin = (int) (getResources().getDisplayMetrics().density * 6);
                lp.leftMargin = 0;
                tvMsg.requestLayout();
            }
        } else {
            if (ivCircle != null) ((LayoutParams) ivCircle.getLayoutParams()).gravity = Gravity.CENTER_VERTICAL;
            if (tvMsg != null) {
                LayoutParams lp = ((LayoutParams) tvMsg.getLayoutParams());
                lp.gravity = Gravity.CENTER_VERTICAL;
                lp.topMargin = 0;
                lp.leftMargin = (int) (getResources().getDisplayMetrics().density * 10);
                tvMsg.requestLayout();
            }
        }

    }


    private void setCiecleView() {
        LayoutParams lp = new LayoutParams(mCircleSize, mCircleSize);
        lp.gravity = getOrientation() == VERTICAL ? Gravity.CENTER_HORIZONTAL : Gravity.CENTER_VERTICAL;
        ivCircle = new ImageView(getContext());
        ivCircle.setImageDrawable(stopDrawable);
        addView(ivCircle, lp);
    }

    public void start() {
        mStartTimeMillis = System.currentTimeMillis();
        mStart = true;
        ivCircle.setImageDrawable(rotateDrawable);
        if (getVisibility() == VISIBLE && isAttachedToWindow()) {
            ObjectAnimator rotate = getRotate();
            if (rotate.isPaused()) {
                rotate.resume();
            } else if (!rotate.isRunning()) {
                rotate.start();
            }
        }
    }

    private Runnable delayTimeRunnable = new Runnable() {
        @Override
        public void run() {
            stopImpl();
        }
    };

    public void stop() {
        removeCallbacks(delayTimeRunnable);
        if (mDelayTimeMillis > 0) {
            long delay = mDelayTimeMillis - (mStartTimeMillis - System.currentTimeMillis());
            if (delay > 0) {
                postDelayed(delayTimeRunnable, delay);
                return;
            }
        }
        stopImpl();
    }

    void stopImpl() {
        mStart = false;
        ObjectAnimator rotate = getRotate();
        if (rotate.isRunning()) {
            rotate.cancel();
        }
        ivCircle.setImageDrawable(stopDrawable);
        this.rotate = null;
    }

    public void pause() {
        ObjectAnimator rotate = getRotate();
        if (rotate.isRunning() && !rotate.isPaused()) {
            rotate.pause();
        }
    }

    public void resume() {
        if (getVisibility() == VISIBLE && isAttachedToWindow()) {
            ObjectAnimator rotate = getRotate();
            if (rotate.isPaused()) {
                rotate.resume();
            }
        }
    }

//    @Override
//    protected void onWindowVisibilityChanged(int visibility) {
//        super.onWindowVisibilityChanged(visibility);
//        if (mStart) {
//            if (visibility == View.VISIBLE) {
//                resume();
//            } else {
//                pause();
//            }
//        }
//
//    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (mStart) {
            if (visibility == VISIBLE) {
                resume();
            } else {
                pause();
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mStart) {
            start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopImpl();
    }

    public void setMessage(CharSequence msg) {
        if (!TextUtils.isEmpty(msg)) {
            if (tvMsg == null) {
                tvMsg = new TextView(getContext());
                tvMsg.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                tvMsg.setTextColor(msgTextColor);
            }
            if (tvMsg.getParent() == null) {
                LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                if (getOrientation() == VERTICAL) {
                    lp.gravity = Gravity.CENTER_HORIZONTAL;
                    lp.topMargin = (int) (getResources().getDisplayMetrics().density * 6);
                } else {
                    lp.gravity = Gravity.CENTER_VERTICAL;
                    lp.leftMargin = (int) (getResources().getDisplayMetrics().density * 10);
                }
                addView(tvMsg, lp);
            }
            tvMsg.setText(msg);
        } else if (tvMsg != null) {
            removeView(tvMsg);
        }
    }

    private void changeSize(int size) {
        if (mCircleSize == size) return;
        mCircleSize = size;
        LayoutParams lp = (LayoutParams) ivCircle.getLayoutParams();
        lp.width = size;
        lp.height = size;
        ivCircle.setLayoutParams(lp);
    }

    public void setCircleSize(int size) {
        changeSize(size);
    }

    public int getCircleSize(){
        return mCircleSize;
    }

    private ObjectAnimator getRotate() {
        if (rotate == null) {
            rotate = new AnimationBuild.PropertyAnimationBuilder()
                    .animation(ivCircle, AnimationBuild.ROTATION, 0f, 359f)
                    .duration(1200)
                    .build();
            rotate.setRepeatCount(Animation.INFINITE);
            rotate.setInterpolator(new LinearInterpolator());
        }
        return rotate;
    }
}
