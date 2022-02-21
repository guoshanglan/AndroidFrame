package com.zhuorui.commonwidget;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import base2app.ex.PixelExKt;


/**
 * @date 2020/7/2 16:29
 * @desc 开关View
 */
public class ZRSwitchCompat extends View {


    private final static int DEFAULT_ANIMATION_DURATION = 300;

    private int viewLeft;
    private int viewRight;

    private final RectF bgRectF = new RectF();

    private float viewRadius = 0f;

    private final Paint bgPaint = new Paint();


    private final Paint circlePaint = new Paint();

    private float circleCy;

    /**
     * 圆圈和外部间距
     */
    private final float circleBorderWidth;

    /**
     * 动画时间
     */
    private final int animationDuration;

    /**
     * 圆圈颜色
     */
    private final int unCheckCircleColor;
    private final int checkCircleColor;

    /**
     * 背景颜色
     */
    private final int unCheckBgColor;
    private final int checkBgColor;

    /**
     * 选中状态
     */
    private boolean checkState;


    /**
     * 标识是否已经计算完成
     */
    private boolean isSizeMeasureComplete = false;


    private final ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    private final SwitchCompatState switchCompatState = new SwitchCompatState();

    private ValueAnimator animator = null;

    public ZRSwitchCompat(Context context) {
        this(context, null);
    }

    public ZRSwitchCompat(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZRSwitchCompat(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bgPaint.setAntiAlias(true);
        circlePaint.setAntiAlias(true);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ZRSwitchCompat);

        unCheckCircleColor = array.getColor(R.styleable.ZRSwitchCompat_zr_switchCompatUnCheckCircleColor, ContextCompat.getColor(getContext(), R.color.switch_compat_uncheck_circle_color));
        checkCircleColor = array.getColor(R.styleable.ZRSwitchCompat_zr_switchCompatCheckCircleColor, ContextCompat.getColor(getContext(), R.color.switch_compat_check_circle_color));

        unCheckBgColor = array.getColor(R.styleable.ZRSwitchCompat_zr_switchCompatUnCheckBgColor, ContextCompat.getColor(getContext(), R.color.switch_compat_uncheck_main_bg_color));
        checkBgColor = array.getColor(R.styleable.ZRSwitchCompat_zr_switchCompatCheckBgColor, ContextCompat.getColor(getContext(), R.color.switch_compat_check_bg_color));

        circleBorderWidth = array.getDimensionPixelSize(R.styleable.ZRSwitchCompat_zr_switchCompatCircleBroadWidth, (int) PixelExKt.dp2px(2));

        checkState = array.getBoolean(R.styleable.ZRSwitchCompat_zr_switchCompatChecked, false);

        animationDuration = array.getInteger(R.styleable.ZRSwitchCompat_zr_switchCompatAnimationDuration, DEFAULT_ANIMATION_DURATION);

        array.recycle();

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startSwitch(true);
            }
        });
    }


    private OnBeforeClickActionListener onBeforeClickActionListener;

    public void setOnBeforeClickActionListener(OnBeforeClickActionListener onBeforeClickActionListener) {
        this.onBeforeClickActionListener = onBeforeClickActionListener;
    }

    public interface OnBeforeClickActionListener {
        boolean onBeforeClickAction(boolean checkState);
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        Bundle bundle = new Bundle();
        bundle.putBoolean("key_check_state", checkState);
        bundle.putParcelable("key_super_state", superState);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable parcelable) {
        Bundle bundle = (Bundle) parcelable;
        this.checkState = bundle.getBoolean("key_check_state");
        //恢复原始状态
        Parcelable superState = bundle.getParcelable("key_super_state");
        super.onRestoreInstanceState(superState);
    }

    /**
     * 切换
     */
    public void toggle(boolean animation) {
        startSwitch(animation);
    }


    private void startSwitch(boolean animation) {
        if (isSizeMeasureComplete) {
            if (onBeforeClickActionListener == null) {
                checkState = !checkState;
                changeCompat(animation);
                onCallBack(animation);
            } else {
                if (onBeforeClickActionListener.onBeforeClickAction(checkState)) {
                    checkState = !checkState;
                    changeCompat(animation);
                    onCallBack(animation);
                }
            }
        }
    }

    public boolean getCheckState() {
        return checkState;
    }


    /**
     * 设置选中状态
     */
    public void setCheckState(boolean checkState) {
        setCheckState(checkState, true);
    }


    /**
     * 设置选中状态
     */
    public void setCheckState(boolean checkState, boolean animation) {
        if (this.checkState != checkState) {
            this.checkState = checkState;
            if (isSizeMeasureComplete) {
                changeCompat(animation);
            }
            onCallBack(animation);

        }
    }

    private void onCallBack(boolean animation) {
//        if (animation){
//            postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (onSwitchCompatCheckStateListener != null) {
//                        onSwitchCompatCheckStateListener.onCheckState(ZRSwitchCompat.this.checkState);
//                    }
//                }
//            },animationDuration);
//        }else {
            if (onSwitchCompatCheckStateListener != null) {
                onSwitchCompatCheckStateListener.onCheckState(checkState);
            }
//        }
    }


    /**
     * 设置选择
     * @param checkState
     */
    public void setChecked(boolean checkState) {
        this.checkState = checkState;
        if (isSizeMeasureComplete) {
            changeCompat(false);
        }
    }

    private void changeCompat(boolean animation) {
        if (animator != null)animator.cancel();
        if (animation) {
            final float startCircleCx, endCircleCx;
            final int startBgColor, endBgColor, startCircleColor, endCircleColor;
            if (checkState) {
                startCircleCx = viewRadius;
                endCircleCx = viewRight - viewLeft - viewRadius;
            } else {
                startCircleCx = viewRight - viewLeft - viewRadius;
                endCircleCx = viewRadius;
            }
            if (checkState) {
                startBgColor = unCheckBgColor;
                endBgColor = checkBgColor;
                startCircleColor = unCheckCircleColor;
                endCircleColor = checkCircleColor;
            } else {
                startBgColor = checkBgColor;
                endBgColor = unCheckBgColor;
                startCircleColor = checkCircleColor;
                endCircleColor = unCheckCircleColor;
            }
            animator = ValueAnimator.ofFloat(startCircleCx, endCircleCx);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = animation.getAnimatedFraction();
                    switchCompatState.circleCxState = (float) animation.getAnimatedValue();
                    switchCompatState.circleColorState = (int) argbEvaluator.evaluate(value, startCircleColor, endCircleColor);
                    switchCompatState.bgColorState = (int) argbEvaluator.evaluate(value, startBgColor, endBgColor);
                    postInvalidate();
                }
            });
            animator.setDuration(animationDuration);
            animator.start();
        } else {
            if (checkState) {
                switchCompatState.circleCxState = viewRight - viewLeft - viewRadius;
                switchCompatState.circleColorState = checkCircleColor;
                switchCompatState.bgColorState = checkBgColor;
            } else {
                switchCompatState.circleCxState = viewRadius;
                switchCompatState.circleColorState = unCheckCircleColor;
                switchCompatState.bgColorState = unCheckBgColor;
            }
            postInvalidate();
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initSize();
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initSize();
    }

    private void initSize() {
        viewLeft = 0;
        viewRight = getWidth();
        int viewTop = 0;
        viewRadius = getHeight() * .5f;

        if (checkState) {
            switchCompatState.circleCxState = viewRight - viewLeft - viewRadius;
            switchCompatState.circleColorState = checkCircleColor;
            switchCompatState.bgColorState = checkBgColor;
        } else {
            switchCompatState.circleCxState = viewRadius;
            switchCompatState.circleColorState = unCheckCircleColor;
            switchCompatState.bgColorState = unCheckBgColor;
        }

        circleCy = viewRadius;

        bgRectF.left = viewLeft;
        bgRectF.right = viewRight;
        bgRectF.top = viewTop;
        bgRectF.bottom = getHeight();

        isSizeMeasureComplete = true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景
        bgPaint.setColor(switchCompatState.bgColorState);
        canvas.drawRoundRect(bgRectF, viewRadius, viewRadius, bgPaint);

        //绘制圆
        circlePaint.setColor(switchCompatState.circleColorState);
        canvas.drawCircle(switchCompatState.circleCxState, circleCy, viewRadius - circleBorderWidth, circlePaint);
    }


    /**
     * 变化状态缓存类
     */
    private static class SwitchCompatState {
        private int bgColorState;
        private float circleCxState;
        private int circleColorState;
    }

    private OnSwitchCompatCheckStateListener onSwitchCompatCheckStateListener;


    /**
     * 设置状态监听回调
     */
    public void setOnSwitchCompatCheckStateListener(OnSwitchCompatCheckStateListener onSwitchCompatCheckStateListener) {
        this.onSwitchCompatCheckStateListener = onSwitchCompatCheckStateListener;
    }

    public interface OnSwitchCompatCheckStateListener {
        void onCheckState(boolean checkState);
    }
}
