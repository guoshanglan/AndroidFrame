package com.zhuorui.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import base2app.ex.PixelExKt;
import base2app.ex.ResourceKt;


/**
 * @date 2020/5/11 17:43
 * @desc 带箭头的支持阴影的背景，主要用于popupwindow背景，目前箭头位置默认在顶部，后续可根据需要进行扩展
 */
public class ArrowShadowContainer extends FrameLayout {

    public final static int ARROW_POSITION_TOP = 1;

    public final static int ARROW_POSITION_BOTTOM = 2;

    private final Paint bgShadowPaint = new Paint();
    private final RectF bgRectF = new RectF();

    private final Paint arrowPaint = new Paint();
    private final Paint arrowShadowPaint = new Paint();
    private final Path arrowPath = new Path();


    /**
     * 箭头的宽度
     */
    private final int mArrowWidth;

    /**
     * 箭头的高度
     */
    private final int mArrowHeight;


    /**
     * 箭头的偏移位置
     */
    private int mArrowXOffset;


    /**
     * 背景圆角
     * ARROW_POSITION_TOP=1 顶部
     * ARROW_POSITION_BOTTOM = 2 底部
     */
    private final int mBackgroundRadius;

    /**
     * 阴影半径
     */
    private final int mShadowRadius;

    /**
     * 箭头方向
     */
    private int mArrowPosition;

    /**
     * 背景颜色，背景和箭头颜色一致
     */
    private final int mBackgroundColor;

    public ArrowShadowContainer(@NonNull Context context) {
        this(context, null);
    }

    public ArrowShadowContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArrowShadowContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ArrowShadowContainerView);
        mArrowWidth = array.getDimensionPixelSize(R.styleable.ArrowShadowContainerView_asc_arrow_width, (int) PixelExKt.dp2px(8));
        mArrowHeight = array.getDimensionPixelSize(R.styleable.ArrowShadowContainerView_asc_arrow_height, (int) PixelExKt.dp2px(6));

        mBackgroundRadius = array.getDimensionPixelSize(R.styleable.ArrowShadowContainerView_asc_bg_radius, (int) PixelExKt.dp2px(6));
        mArrowXOffset = array.getDimensionPixelOffset(R.styleable.ArrowShadowContainerView_asc_arrow_x_offset, (int) PixelExKt.dp2px(20));

        mShadowRadius = array.getDimensionPixelOffset(R.styleable.ArrowShadowContainerView_asc_shadow_radius, (int) PixelExKt.dp2px(5));
        int shadowColor = array.getColor(R.styleable.ArrowShadowContainerView_asc_shadow_color, Color.parseColor("#1a000000"));

        mBackgroundColor = array.getColor(R.styleable.ArrowShadowContainerView_asc_background_color, ResourceKt.color(R.color.main_tab_background));
        this.mArrowPosition = array.getInt(R.styleable.ArrowShadowContainerView_asc_arrow_position, ARROW_POSITION_TOP);

        setContainerPadding();

        array.recycle();

        bgShadowPaint.setAntiAlias(true);
        bgShadowPaint.setColor(shadowColor);
        bgShadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        bgShadowPaint.setShadowLayer(mShadowRadius, 0, 0, shadowColor);

        arrowPaint.setColor(mBackgroundColor);
        arrowPaint.setAntiAlias(true);
        CornerPathEffect mCornerEffect = new CornerPathEffect(PixelExKt.dp2px(1));
        arrowPaint.setPathEffect(mCornerEffect);

        arrowShadowPaint.setAntiAlias(true);
        arrowShadowPaint.setColor(mBackgroundColor);
        arrowShadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        arrowShadowPaint.setShadowLayer(mShadowRadius, 0, 0, shadowColor);
        arrowShadowPaint.setPathEffect(mCornerEffect);
    }


    /**
     * 设置内边距，以便能够完整的显示箭头和阴影位置
     */
    private void setContainerPadding() {
        if (this.mArrowPosition == ARROW_POSITION_TOP) {
            setPadding(mShadowRadius, mShadowRadius + mArrowHeight,
                    mShadowRadius, mShadowRadius);
        } else {
            setPadding(mShadowRadius, mShadowRadius, mShadowRadius, mShadowRadius + mArrowHeight);
        }
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawBackgroundAndShadow(canvas);
        drawArrowAndShadow(canvas);
    }


    /**
     * 动态更新箭头位置
     */
    public void setArrowXOffset(int xOffset) {
        if (xOffset != mArrowXOffset) {
            this.mArrowXOffset = xOffset;
            invalidate();
        }
    }


    /**
     * 设置箭头的位置
     *
     * @param arrowPosition ARROW_POSITION_TOP & ARROW_POSITION_BOTTOM
     */
    public void setArrowPosition(int arrowPosition) {
        if (arrowPosition != this.mArrowPosition) {
            this.mArrowPosition = arrowPosition;
            setContainerPadding();
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 绘制背景和阴影
     */
    private void drawBackgroundAndShadow(Canvas canvas) {
        float bgOffsetX = Math.abs(mShadowRadius);
        float bgOffsetY = Math.abs(mShadowRadius);
        if (mArrowPosition == ARROW_POSITION_TOP) {
            bgRectF.left = bgOffsetX;
            bgRectF.top = mArrowHeight + bgOffsetY;
            bgRectF.right = getWidth() - bgOffsetX;
            bgRectF.bottom = getHeight() - bgOffsetY;
        } else if (mArrowPosition == ARROW_POSITION_BOTTOM) {
            bgRectF.left = bgOffsetX;
            bgRectF.top = bgOffsetY;
            bgRectF.right = getWidth() - bgOffsetX;
            bgRectF.bottom = getHeight() - mArrowHeight - bgOffsetY;
        }
        //绘制背景阴影
        canvas.drawRoundRect(bgRectF, mBackgroundRadius, mBackgroundRadius, bgShadowPaint);
    }

    /**
     * 绘制三角形箭头和阴影
     */
    private void drawArrowAndShadow(Canvas canvas) {
        float arrowOffsetX = Math.max(mBackgroundRadius, mArrowXOffset) + Math.abs(mShadowRadius);
        float arrowOffsetY = Math.abs(mShadowRadius + PixelExKt.dp2px(1));
        if (mArrowPosition == ARROW_POSITION_TOP) {
            arrowPath.moveTo(arrowOffsetX, mArrowHeight + arrowOffsetY);
            arrowPath.lineTo(mArrowWidth + arrowOffsetX, mArrowHeight + arrowOffsetY);
            arrowPath.lineTo(mArrowWidth / 2f + arrowOffsetX, arrowOffsetY);
            arrowPath.close();
        } else if (mArrowPosition == ARROW_POSITION_BOTTOM) {
            arrowPath.moveTo(arrowOffsetX, getHeight() - arrowOffsetY - mArrowHeight);
            arrowPath.lineTo(mArrowWidth + arrowOffsetX, getHeight() - arrowOffsetY - mArrowHeight);
            arrowPath.lineTo(mArrowWidth / 2f + arrowOffsetX, getHeight() - arrowOffsetY);
            arrowPath.close();
        }
        arrowPaint.setColor(mBackgroundColor);
        arrowShadowPaint.setColor(mBackgroundColor);
        //绘制箭头
        canvas.drawPath(arrowPath, arrowPaint);
        //绘制箭头阴影
        canvas.drawPath(arrowPath, arrowShadowPaint);
    }
}
