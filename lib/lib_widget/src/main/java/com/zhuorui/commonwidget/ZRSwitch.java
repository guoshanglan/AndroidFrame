package com.zhuorui.commonwidget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Checkable;

import base2app.ex.ResourceKt;

/**
 * author : liuwei
 * e-mail : vsanliu@foxmail.com
 * date   : 2019-08-30 09:48
 * desc   :
 */
public class ZRSwitch extends View implements Checkable {
    private static final int DEFAULT_WIDTH = dp2pxInt(58);
    private static final int DEFAULT_HEIGHT = dp2pxInt(36);

    /**
     * 动画状态：
     * 1.静止
     * 2.进入拖动
     * 3.处于拖动
     * 4.拖动-复位
     * 5.拖动-切换
     * 6.点击切换
     **/
    private final int ANIMATE_STATE_NONE = 0;
    private final int ANIMATE_STATE_PENDING_DRAG = 1;
    private final int ANIMATE_STATE_DRAGING = 2;
    private final int ANIMATE_STATE_PENDING_RESET = 3;
    private final int ANIMATE_STATE_PENDING_SETTLE = 4;
    private final int ANIMATE_STATE_SWITCH = 5;

    public ZRSwitch(Context context) {
        this(context, null);
    }

    public ZRSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZRSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    @Override
    public final void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(0, 0, 0, 0);
    }

    /**
     * 初始化参数
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = null;
        if (attrs != null) {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZRSwitch);
        }
        uncheckCircleColor = optColor(typedArray, R.styleable.ZRSwitch_zr_switchUncheckCircleColor, 0XffA1A1A1);//0XffAAAAAA;
        checkCircleColor = optColor(typedArray, R.styleable.ZRSwitch_zr_switch_checkCircleColor, 0XffA1A1A1);
        shadowEffect = optBoolean(typedArray, R.styleable.ZRSwitch_zr_switchShadowEffect, false);
        shadowRadius = optPixelSize(typedArray, R.styleable.ZRSwitch_zr_switchShadowRadius, 0);//dp2pxInt(2.5f);
        shadowOffset = optPixelSize(typedArray, R.styleable.ZRSwitch_zr_switchShadowOffset, 0);//dp2pxInt(1.5f);
        shadowColor = optColor(typedArray, R.styleable.ZRSwitch_zr_switchShadowColor, 0X33000000);//0X33000000;
        uncheckColor = optColor(typedArray, R.styleable.ZRSwitch_zr_switchUncheckColor, 0XffE5E5E5);//0XffDDDDDD;
        checkedColor = optColor(typedArray, R.styleable.ZRSwitch_zr_switchCheckedColor, 0Xff1A6ED2);//0Xff51d367;
        borderWidth = optPixelSize(typedArray, R.styleable.ZRSwitch_zr_switchBorderWidth, dp2pxInt(2));//dp2pxInt(1);
        checkLineColor = optColor(typedArray, R.styleable.ZRSwitch_zr_switchCheckLineColor, Color.WHITE);//Color.WHITE;
        int buttonColor = optColor(typedArray, R.styleable.ZRSwitch_zr_switchButtonColor, Color.WHITE);//Color.WHITE;
        int effectDuration = optInt(typedArray, R.styleable.ZRSwitch_zr_switchEffectDuration, 300);//300;
        isChecked = optBoolean(typedArray, R.styleable.ZRSwitch_zr_switchChecked, false);
        showIndicator = optBoolean(typedArray, R.styleable.ZRSwitch_zr_switchShowIndicator, false);
        enableEffect = optBoolean(typedArray, R.styleable.ZRSwitch_zr_switchEnableEffect, true);
        if (typedArray != null) {
            typedArray.recycle();
        }
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonPaint.setColor(buttonColor);
        if (shadowEffect) {
            buttonPaint.setShadowLayer(shadowRadius, 0, shadowOffset, shadowColor);
        }
        viewState = new ViewState();
        beforeState = new ViewState();
        afterState = new ViewState();
        valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(effectDuration);
        valueAnimator.setRepeatCount(0);
        valueAnimator.addUpdateListener(animatorUpdateListener);
        valueAnimator.addListener(animatorListener);
        super.setClickable(true);
        this.setPadding(0, 0, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(dp2pxInt(12));
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.UNSPECIFIED || widthMode == MeasureSpec.AT_MOST) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_WIDTH, MeasureSpec.EXACTLY);
        }
        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_HEIGHT, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float viewPadding = Math.max(shadowRadius + shadowOffset, borderWidth);
        height = h - viewPadding - viewPadding;
        viewRadius = height * .5f;
        buttonRadius = viewRadius - borderWidth;
        left = viewPadding;
        top = viewPadding;
        right = w - viewPadding;
        bottom = h - viewPadding;
        centerY = (top + bottom) * .5f;
        buttonMinX = left + viewRadius;
        buttonMaxX = right - viewRadius;
        if (isChecked()) {
            setCheckedViewState(viewState);
        } else {
            setUncheckViewState(viewState);
        }
        isUiInited = true;
        //计算baseline
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
        textBaseline = centerY + distance;
        postInvalidate();
    }

    private void setUncheckViewState(ViewState viewState) {
        viewState.radius = 0;
        viewState.checkStateColor = uncheckColor;
        viewState.checkCircleColor = uncheckCircleColor;
        viewState.checkedLineColor = Color.TRANSPARENT;
        viewState.buttonX = buttonMinX;
    }

    private void setCheckedViewState(ViewState viewState) {
        viewState.radius = viewRadius;
        viewState.checkStateColor = checkedColor;
        viewState.checkCircleColor = checkCircleColor;
        viewState.checkedLineColor = checkLineColor;
        viewState.buttonX = buttonMaxX;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStrokeWidth(borderWidth);
        //绘制小圆圈
        if (showIndicator) {
            drawCheckedIndicator(canvas);
            drawUncheckIndicator(canvas);
        }
        //绘制开启背景色
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(viewState.checkStateColor);
        drawRoundRect(canvas, left, top, right, bottom, viewRadius, paint);
        //绘制按钮
        drawButton(canvas, viewState.buttonX, centerY);
    }


    /**
     * 绘制选中状态指示器
     */
    protected void drawCheckedIndicator(Canvas canvas) {
        textPaint.setColor(viewState.checkedLineColor);
        canvas.drawText(ResourceKt.text(R.string.yes), buttonMinX, textBaseline, textPaint);
    }


    /**
     * 绘制关闭状态指示器
     */
    private void drawUncheckIndicator(Canvas canvas) {
        textPaint.setColor(viewState.checkedLineColor);
        canvas.drawText(ResourceKt.text(R.string.no), buttonMaxX, textBaseline, textPaint);
    }

    private void drawRoundRect(Canvas canvas, float left, float top, float right, float bottom, float backgroundRadius, Paint paint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(left, top, right, bottom, backgroundRadius, backgroundRadius, paint);
        } else {
            rect.set(left, top, right, bottom);
            canvas.drawRoundRect(rect, backgroundRadius, backgroundRadius, paint);
        }
    }

    private void drawButton(Canvas canvas, float x, float y) {
        buttonPaint.setColor(viewState.checkCircleColor);
        canvas.drawCircle(x, y, buttonRadius, buttonPaint);
    }

    @Override
    public void setChecked(boolean checked) {
        if (checked == isChecked()) {
            postInvalidate();
            return;
        }
        toggle(enableEffect, false);
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        toggle(true);
    }

    /**
     * 切换状态
     */
    public void toggle(boolean animate) {
        toggle(animate, true);
    }

    private void toggle(boolean animate, boolean broadcast) {
        if (!isEnabled()) {
            return;
        }

        if (isEventBroadcast) {
            throw new RuntimeException("should NOT switch the state in method: [onCheckedChanged]!");
        }
        if (!isUiInited) {
            isChecked = !isChecked;
            if (broadcast) {
                broadcastEvent();
            }
            return;
        }

        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }

        if (!enableEffect || !animate) {
            isChecked = !isChecked;
            if (isChecked()) {
                setCheckedViewState(viewState);
            } else {
                setUncheckViewState(viewState);
            }
            postInvalidate();
            if (broadcast) {
                broadcastEvent();
            }
            return;
        }

        animateState = ANIMATE_STATE_SWITCH;
        beforeState.copy(viewState);

        if (isChecked()) {
            //切换到unchecked
            setUncheckViewState(afterState);
        } else {
            setCheckedViewState(afterState);
        }
        valueAnimator.start();
    }

    /**
     *
     */
    private void broadcastEvent() {
        if (onCheckedChangeListener != null) {
            isEventBroadcast = true;
            onCheckedChangeListener.onCheckedChanged(this, isChecked());
        }
        isEventBroadcast = false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;
        }
        int actionMasked = event.getActionMasked();

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                isTouchingDown = true;
                touchDownTime = System.currentTimeMillis();
                //取消准备进入拖动状态
                removeCallbacks(postPendingDrag);
                //预设100ms进入拖动状态
                postDelayed(postPendingDrag, 100);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                float eventX = event.getX();
                if (isPendingDragState()) {
                    //在准备进入拖动状态过程中，可以拖动按钮位置
                    float fraction = eventX / getWidth();
                    fraction = Math.max(0f, Math.min(1f, fraction));

                    viewState.buttonX = buttonMinX
                            + (buttonMaxX - buttonMinX)
                            * fraction;

                } else if (isDragState()) {
                    //拖动按钮位置，同时改变对应的背景颜色
                    float fraction = eventX / getWidth();
                    fraction = Math.max(0f, Math.min(1f, fraction));

                    viewState.buttonX = buttonMinX
                            + (buttonMaxX - buttonMinX)
                            * fraction;

                    viewState.checkStateColor = (int) argbEvaluator.evaluate(
                            fraction,
                            uncheckColor,
                            checkedColor
                    );

                    viewState.checkCircleColor = (int) argbEvaluator.evaluate(
                            fraction,
                            uncheckCircleColor,
                            checkCircleColor
                    );
                    postInvalidate();

                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                isTouchingDown = false;
                //取消准备进入拖动状态
                removeCallbacks(postPendingDrag);

                if (System.currentTimeMillis() - touchDownTime <= 300) {
                    //点击时间小于300ms，认为是点击操作
                    toggle();
                } else if (isDragState()) {
                    //在拖动状态，计算按钮位置，设置是否切换状态
                    float eventX = event.getX();
                    float fraction = eventX / getWidth();
                    fraction = Math.max(0f, Math.min(1f, fraction));
                    boolean newCheck = fraction > .5f;
                    if (newCheck == isChecked()) {
                        pendingCancelDragState();
                    } else {
                        isChecked = newCheck;
                        pendingSettleState();
                    }
                } else if (isPendingDragState()) {
                    //在准备进入拖动状态过程中，取消之，复位
                    pendingCancelDragState();
                }
                break;
            }
            case MotionEvent.ACTION_CANCEL: {
                isTouchingDown = false;

                removeCallbacks(postPendingDrag);

                if (isPendingDragState()
                        || isDragState()) {
                    //复位
                    pendingCancelDragState();
                }
                break;
            }
        }
        return true;
    }


    /**
     * 是否在动画状态
     */
    private boolean isInAnimating() {
        return animateState != ANIMATE_STATE_NONE;
    }

    /**
     * 是否在进入拖动或离开拖动状态
     */
    private boolean isPendingDragState() {
        return animateState == ANIMATE_STATE_PENDING_DRAG
                || animateState == ANIMATE_STATE_PENDING_RESET;
    }

    /**
     * 是否在手指拖动状态
     */
    private boolean isDragState() {
        return animateState == ANIMATE_STATE_DRAGING;
    }

    /**
     * 设置是否启用阴影效果
     *
     * @param shadowEffect true.启用
     */
    public void setShadowEffect(boolean shadowEffect) {
        if (this.shadowEffect == shadowEffect) {
            return;
        }
        this.shadowEffect = shadowEffect;

        if (this.shadowEffect) {
            buttonPaint.setShadowLayer(
                    shadowRadius,
                    0, shadowOffset,
                    shadowColor);
        } else {
            buttonPaint.setShadowLayer(
                    0,
                    0, 0,
                    0);
        }
    }

    public void setEnableEffect(boolean enable) {
        this.enableEffect = enable;
    }

    /**
     * 开始进入拖动状态
     */
    private void pendingDragState() {
        if (isInAnimating()) {
            return;
        }
        if (!isTouchingDown) {
            return;
        }

        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }

        animateState = ANIMATE_STATE_PENDING_DRAG;

        beforeState.copy(viewState);
        afterState.copy(viewState);

        if (isChecked()) {
            afterState.checkStateColor = checkedColor;
            afterState.checkCircleColor = checkCircleColor;
            afterState.buttonX = buttonMaxX;
            afterState.checkedLineColor = checkedColor;
        } else {
            afterState.checkCircleColor = uncheckCircleColor;
            afterState.checkStateColor = uncheckColor;
            afterState.buttonX = buttonMinX;
            afterState.radius = viewRadius;
        }

        valueAnimator.start();
    }


    /**
     * 取消拖动状态
     */
    private void pendingCancelDragState() {
        if (isDragState() || isPendingDragState()) {
            if (valueAnimator.isRunning()) {
                valueAnimator.cancel();
            }

            animateState = ANIMATE_STATE_PENDING_RESET;
            beforeState.copy(viewState);

            if (isChecked()) {
                setCheckedViewState(afterState);
            } else {
                setUncheckViewState(afterState);
            }
            valueAnimator.start();
        }
    }


    /**
     * 动画-设置新的状态
     */
    private void pendingSettleState() {
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }

        animateState = ANIMATE_STATE_PENDING_SETTLE;
        beforeState.copy(viewState);

        if (isChecked()) {
            setCheckedViewState(afterState);
        } else {
            setUncheckViewState(afterState);
        }
        valueAnimator.start();
    }


    @Override
    public final void setOnClickListener(OnClickListener l) {
    }

    @Override
    public final void setOnLongClickListener(OnLongClickListener l) {
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener l) {
        onCheckedChangeListener = l;
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(ZRSwitch view, boolean isChecked);
    }

    /*******************************************************/
    private static float dp2px(float dp) {
        Resources r = Resources.getSystem();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    private static int dp2pxInt(float dp) {
        return (int) dp2px(dp);
    }

    private static int optInt(TypedArray typedArray,
                              int index,
                              int def) {
        if (typedArray == null) {
            return def;
        }
        return typedArray.getInt(index, def);
    }



    private static int optPixelSize(TypedArray typedArray,
                                    int index,
                                    int def) {
        if (typedArray == null) {
            return def;
        }
        return typedArray.getDimensionPixelOffset(index, def);
    }

    private static int optColor(TypedArray typedArray,
                                int index,
                                int def) {
        if (typedArray == null) {
            return def;
        }
        return typedArray.getColor(index, def);
    }

    private static boolean optBoolean(TypedArray typedArray,
                                      int index,
                                      boolean def) {
        if (typedArray == null) {
            return def;
        }
        return typedArray.getBoolean(index, def);
    }


    /**
     * 阴影半径
     */
    private int shadowRadius;
    /**
     * 阴影Y偏移px
     */
    private int shadowOffset;
    /**
     * 阴影颜色
     */
    private int shadowColor;

    /**
     * 背景半径
     */
    private float viewRadius;
    /**
     * 按钮半径
     */
    private float buttonRadius;

    /**
     * 背景高
     */
    private float height;
    /**
     * 背景位置
     */
    private float left;
    private float top;
    private float right;
    private float bottom;
    private float centerY;

    /**
     * 背景关闭颜色
     */
    private int uncheckColor;
    /**
     * 背景打开颜色
     */
    private int checkedColor;
    /**
     * 边框宽度px
     */
    private int borderWidth;

    /**
     * 打开指示线颜色
     */
    private int checkLineColor;
    /**
     * 关闭圆圈颜色
     */
    private int uncheckCircleColor;

    /**
     * 开启圆圈颜色
     */
    private int checkCircleColor;


    /**
     * 按钮最左边
     */
    private float buttonMinX;
    /**
     * 按钮最右边
     */
    private float buttonMaxX;

    /**
     * 按钮画笔
     */
    private Paint buttonPaint;
    /**
     * 背景画笔
     */
    private Paint paint;

    /**
     * 文字画笔
     */
    private Paint textPaint;

    /**
     * 文字基线
     */
    private float textBaseline;

    /**
     * 当前状态
     */
    private ViewState viewState;
    private ViewState beforeState;
    private ViewState afterState;

    private final RectF rect = new RectF();
    /**
     * 动画状态
     */
    private int animateState = ANIMATE_STATE_NONE;

    /**
     *
     */
    private ValueAnimator valueAnimator;

    private final android.animation.ArgbEvaluator argbEvaluator
            = new android.animation.ArgbEvaluator();

    /**
     * 是否选中
     */
    private boolean isChecked;
    /**
     * 是否启用动画
     */
    private boolean enableEffect;
    /**
     * 是否启用阴影效果
     */
    private boolean shadowEffect;
    /**
     * 是否显示指示器
     */
    private boolean showIndicator;
    /**
     * 手势是否按下
     */
    private boolean isTouchingDown = false;
    /**
     *
     */
    private boolean isUiInited = false;
    /**
     *
     */
    private boolean isEventBroadcast = false;

    private OnCheckedChangeListener onCheckedChangeListener;

    /**
     * 手势按下的时刻
     */
    private long touchDownTime;

    private final Runnable postPendingDrag = new Runnable() {
        @Override
        public void run() {
            if (!isInAnimating()) {
                pendingDragState();
            }
        }
    };

    private final ValueAnimator.AnimatorUpdateListener animatorUpdateListener
            = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (Float) animation.getAnimatedValue();
            switch (animateState) {
                case ANIMATE_STATE_PENDING_SETTLE: {
                }
                case ANIMATE_STATE_PENDING_RESET: {
                }
                case ANIMATE_STATE_PENDING_DRAG: {
                    viewState.checkedLineColor = (int) argbEvaluator.evaluate(
                            value,
                            beforeState.checkedLineColor,
                            afterState.checkedLineColor
                    );

                    viewState.radius = beforeState.radius
                            + (afterState.radius - beforeState.radius) * value;

                    if (animateState != ANIMATE_STATE_PENDING_DRAG) {
                        viewState.buttonX = beforeState.buttonX
                                + (afterState.buttonX - beforeState.buttonX) * value;
                    }

                    viewState.checkCircleColor = (int) argbEvaluator.evaluate(
                            value,
                            beforeState.checkCircleColor,
                            afterState.checkCircleColor
                    );

                    viewState.checkStateColor = (int) argbEvaluator.evaluate(
                            value,
                            beforeState.checkStateColor,
                            afterState.checkStateColor
                    );

                    break;
                }
                case ANIMATE_STATE_SWITCH: {
                    viewState.buttonX = beforeState.buttonX
                            + (afterState.buttonX - beforeState.buttonX) * value;

                    float fraction = (viewState.buttonX - buttonMinX) / (buttonMaxX - buttonMinX);

                    viewState.checkStateColor = (int) argbEvaluator.evaluate(
                            fraction,
                            uncheckColor,
                            checkedColor
                    );

                    viewState.checkCircleColor = (int) argbEvaluator.evaluate(fraction, uncheckCircleColor, checkCircleColor);

                    viewState.radius = fraction * viewRadius;
                    viewState.checkedLineColor = (int) argbEvaluator.evaluate(
                            fraction,
                            Color.TRANSPARENT,
                            checkLineColor
                    );
                    break;
                }
                default:
                case ANIMATE_STATE_DRAGING: {
                }
                case ANIMATE_STATE_NONE: {
                    break;
                }
            }
            postInvalidate();
        }
    };

    private final Animator.AnimatorListener animatorListener
            = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            switch (animateState) {
                case ANIMATE_STATE_DRAGING: {
                    break;
                }
                case ANIMATE_STATE_PENDING_DRAG: {
                    animateState = ANIMATE_STATE_DRAGING;
                    viewState.checkedLineColor = Color.TRANSPARENT;
                    viewState.radius = viewRadius;

                    postInvalidate();
                    break;
                }
                case ANIMATE_STATE_PENDING_RESET: {
                    animateState = ANIMATE_STATE_NONE;
                    postInvalidate();
                    break;
                }
                case ANIMATE_STATE_PENDING_SETTLE: {
                    animateState = ANIMATE_STATE_NONE;
                    postInvalidate();
                    broadcastEvent();
                    break;
                }
                case ANIMATE_STATE_SWITCH: {
                    isChecked = !isChecked;
                    animateState = ANIMATE_STATE_NONE;
                    postInvalidate();
                    broadcastEvent();
                    break;
                }
                default:
                case ANIMATE_STATE_NONE: {
                    break;
                }
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };


    /**
     * 保存动画状态
     */
    private static class ViewState {
        /**
         * 按钮x位置[buttonMinX-buttonMaxX]
         */
        float buttonX;
        /**
         * 状态背景颜色
         */
        int checkStateColor;

        int checkCircleColor;
        /**
         * 选中线的颜色
         */
        int checkedLineColor;
        /**
         * 状态背景的半径
         */
        float radius;

        ViewState() {
        }

        private void copy(ViewState source) {
            this.buttonX = source.buttonX;
            this.checkStateColor = source.checkStateColor;
            this.checkedLineColor = source.checkedLineColor;
            this.radius = source.radius;
            this.checkCircleColor = source.checkCircleColor;
        }
    }

}