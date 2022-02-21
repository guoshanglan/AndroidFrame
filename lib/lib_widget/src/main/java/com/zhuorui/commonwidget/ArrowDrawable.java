package com.zhuorui.commonwidget;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import base2app.ex.PixelExKt;
import base2app.ex.ResourceKt;


/**
 * date   : 2020/5/18 16:03
 * desc   : 需禁用硬件加速
 */
public class ArrowDrawable extends Drawable {

    private Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint bgShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint arrowShadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF bgRectF = new RectF();
    private RectF shadowRectF = new RectF();
    private Path arrowPath = new Path();
    private float arrowWidth;
    private float arrowHeight;
    //箭头Gravity对应模式下的偏移量,-1：偏移量由arrowAnchorWidth和Drawable比较决定
    private int arrowXOffset = -1;
    //箭头锚点控件宽
    private int arrowAnchorWidth = 0;
    private float bgRadius;
    private int shadowRadius;
    protected boolean isDown = false;
    private int gravity = Gravity.NO_GRAVITY;
    private boolean mScroll = false;
    private final float shadowPadding = PixelExKt.dp2px(1.5f);
    private final float innerShadowPadding = shadowPadding * 1.5f;

    public ArrowDrawable() {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        arrowWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, displayMetrics);
        arrowHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, displayMetrics);
        bgRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, displayMetrics);
        shadowRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, displayMetrics);
        bgShadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        arrowShadowPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
        bgShadowPaint.setAntiAlias(true);
        arrowShadowPaint.setAntiAlias(true);
        setColor(ResourceKt.color(R.color.dialog_daynight_background), ResourceKt.color(R.color.dialog_shadow_color));
    }


    public void setColor(int color, int shadowColor) {
        if (color != 0) {
            bgPaint.setColor(color);
            arrowPaint.setColor(color);
        }
        if (shadowColor != 0) {
            bgShadowPaint.setColor(shadowColor);
            arrowShadowPaint.setColor(shadowColor);
            arrowShadowPaint.setShadowLayer(shadowRadius, 0, 0, shadowColor);
            bgShadowPaint.setShadowLayer(shadowRadius, 0, 0, shadowColor);
        }
    }

    public void setDownArrow(boolean down) {
        isDown = down;
        changePath(getBounds());
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
        changePath(getBounds());
    }

    @Override
    public boolean getPadding(@NonNull Rect padding) {
        padding.set(0, getPaddingTop(), 0, getPaddingBottom());
        return true;
    }

    public int getPaddingTop() {
        int top = isDown ? 0 : (int) arrowHeight;
        return mScroll ? (int) (top + bgRadius) : top;
    }

    public int getPaddingBottom() {
        int bottom = isDown ? (int) arrowHeight : 0;
        return mScroll ? (int) (bottom + bgRadius) : bottom;
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawPath(arrowPath, arrowShadowPaint);
        canvas.drawRoundRect(shadowRectF, bgRadius, bgRadius, bgShadowPaint);
        canvas.drawPath(arrowPath, arrowPaint);
        canvas.drawRoundRect(bgRectF, bgRadius, bgRadius, bgPaint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public int getMinimumWidth() {
        return super.getMinimumWidth();
    }

    @Override
    public int getMinimumHeight() {
        return super.getMinimumHeight() + (isDown ? 1 : 0);
    }

    @Override
    public int getIntrinsicWidth() {
        Rect bounds = getBounds();
        return bounds.right - bounds.left;
    }

    @Override
    public int getIntrinsicHeight() {
        Rect bounds = getBounds();
        return bounds.bottom - bounds.top;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        changePath(bounds);
    }

    private void changePath(Rect bounds) {
        if (bounds.bottom - bounds.top == 0 || bounds.right - bounds.left == 0) return;
        int[] arrowXLoction = getArrowXLoction(bounds);
        int arrowXStart = arrowXLoction[0];
        int arrowXEnd = arrowXLoction[1];
        bgRectF.left = bounds.left + shadowPadding;
        bgRectF.right = bounds.right - shadowPadding;
        shadowRectF.left = bounds.left + innerShadowPadding;
        shadowRectF.right = bounds.right - innerShadowPadding;
        if (isDown) {
            shadowRectF.top = bounds.top + innerShadowPadding;
            shadowRectF.bottom = bounds.bottom - arrowHeight;
            bgRectF.top = bounds.top + shadowPadding;
            bgRectF.bottom = shadowRectF.bottom;
            arrowPath.moveTo(arrowXStart, bgRectF.bottom);
            arrowPath.lineTo(arrowXEnd, bgRectF.bottom);
            arrowPath.lineTo(arrowXStart + arrowWidth / 2, bounds.bottom);
        } else {
            shadowRectF.top = arrowHeight;
            shadowRectF.bottom = bounds.bottom - innerShadowPadding;
            bgRectF.top = arrowHeight;
            bgRectF.bottom = bounds.bottom - shadowPadding;
            arrowPath.moveTo(arrowXStart, arrowHeight);
            arrowPath.lineTo(arrowXEnd, arrowHeight);
            arrowPath.lineTo(arrowXStart + arrowWidth / 2, 0);
        }
        arrowPath.close();
    }

    private int[] getArrowXLoction(Rect bounds) {
        int minXOffset = (int) bgRadius;
        int arrowXStart = 0;
        int arrowXEnd = 0;
        int gravity = this.gravity;
        int arrowXOffset = this.arrowXOffset;
        int arrowAnchorWidth = this.arrowAnchorWidth;
        float arrowWidth = this.arrowWidth;
        if (arrowXOffset < 0) {
            int width = bounds.right - bounds.left;
            if (width > arrowAnchorWidth) {
                //当前背景宽比锚点控件要大
                if (isGravityLeft()) {
                    arrowXStart = Math.max(minXOffset, (int) (arrowAnchorWidth / 2 - arrowWidth / 2));
                    arrowXEnd = (int) (arrowXStart + arrowWidth);
                } else if (isGravityRight()) {
                    int offSet = Math.max(minXOffset, (int) (arrowAnchorWidth / 2 - arrowWidth / 2));
                    arrowXEnd = bounds.right - offSet;
                    arrowXStart = (int) (arrowXEnd - arrowWidth);
                } else {
                    arrowXStart = (int) (bounds.right - width / 2 - arrowWidth / 2);
                    arrowXEnd = (int) (arrowXStart + arrowWidth);
                }
            } else {
                //当前背景宽比锚点控件要小/相等
                arrowXStart = (int) (bounds.right - width / 2 - arrowWidth / 2);
                arrowXEnd = (int) (arrowXStart + arrowWidth);
            }
        } else {
            if (isGravityLeft()) {
                arrowXStart = (int) Math.max(bgRadius, arrowXOffset + arrowAnchorWidth / 2 - arrowWidth / 2);
                arrowXEnd = (int) (arrowXStart + arrowWidth);
            } else if (isGravityRight()) {
                int offSet = Math.max(minXOffset, (int) (arrowXOffset + arrowAnchorWidth / 2 - arrowWidth / 2));
                arrowXEnd = bounds.right - offSet;
                arrowXStart = (int) (arrowXEnd - arrowWidth);
            } else {
                int centenr = bounds.right - (bounds.right - bounds.left) / 2;
                arrowXStart = (int) (centenr - arrowWidth / 2);
                arrowXEnd = (int) (arrowXStart + arrowWidth);
            }
        }
        return new int[]{arrowXStart, arrowXEnd};
    }

    private boolean isGravityLeft() {
        return (gravity & Gravity.LEFT) == Gravity.LEFT || (gravity & Gravity.START) == Gravity.START;
    }

    private boolean isGravityRight() {
        return (gravity & Gravity.RIGHT) == Gravity.RIGHT || (gravity & Gravity.END) == Gravity.END;
    }

    @Override
    protected boolean onStateChange(int[] state) {
        return super.onStateChange(state);
    }

    @Override
    public boolean setState(@NonNull int[] stateSet) {
        return super.setState(stateSet);
    }

    public void setArrowXOffset(int xOffset, int arrowAnchorWidth) {
        arrowXOffset = xOffset;
        this.arrowAnchorWidth = arrowAnchorWidth;
        changePath(getBounds());
    }

    public void setScroll(boolean scroll) {
        mScroll = scroll;
    }
}
