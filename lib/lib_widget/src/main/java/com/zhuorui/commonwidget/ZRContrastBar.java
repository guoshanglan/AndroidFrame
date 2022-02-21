package com.zhuorui.commonwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * author : liuwei
 * e-mail : vsanliu@foxmail.com
 * date   : 2020/7/21 14:07
 * desc   : 对比条
 */
public class ZRContrastBar extends View {

    private float mMaxProgress;
    private float[] mItems;
    private int[] mItemColors;
    private float[] mItemWidthRatio;
    private Paint paint;


    public ZRContrastBar(Context context) {
        super(context);
        init();
    }

    public ZRContrastBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZRContrastBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mItemWidthRatio != null && mItemWidthRatio.length > 0) {
            int drawWidth = getWidth() - getPaddingLeft() - getPaddingRight();
            int perRight = 0 + getPaddingLeft();
            for (int i = 0, len = mItemWidthRatio.length; i < len; i++) {
                paint.setColor(mItemColors[i]);
                int left = perRight;
                int right = (int) (drawWidth * mItemWidthRatio[i] + left);
                int top = getTop() + getPaddingTop();
                int bottom = getBottom() - getPaddingBottom();
                Rect rect = new Rect(left, top, right, bottom);
                canvas.drawRect(rect, paint);
                perRight = right;
            }
        }
    }

    public void setMaxProgress(float maxProgress) {
        this.mMaxProgress = maxProgress;
        calculationWidthRatio();
        if (isAttachedToWindow()) invalidate();
    }

    public void setItems(float[] items, int[] colors) {
        this.mItems = items;
        this.mItemColors = colors;
        calculationWidthRatio();
        if (isAttachedToWindow()) invalidate();
    }

    public void setItems(float maxProgress, float[] items, int[] colors) {
        this.mMaxProgress = maxProgress;
        this.mItems = items;
        this.mItemColors = colors;
        calculationWidthRatio();
        if (isAttachedToWindow()) invalidate();
    }

    private void calculationWidthRatio() {
        if (mItems == null || mItems.length == 0) {
            mItemWidthRatio = null;
            return;
        }
        final int len = mItems.length;
        if (mItemWidthRatio == null || mItemWidthRatio.length != mItems.length) {
            mItemWidthRatio = new float[len];
        }
        int total = 0;
        for (int i = 0; i < len; i++) {
            float item = mItems[i];
            if (mMaxProgress > 0) {
                mItemWidthRatio[i] = item / mMaxProgress;
            }
            total += item;
        }
        if (total > mMaxProgress) {
            mMaxProgress = total;
            calculationWidthRatio();
        }

    }
}
