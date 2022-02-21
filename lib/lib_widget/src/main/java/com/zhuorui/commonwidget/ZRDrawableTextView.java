package com.zhuorui.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import base2app.ex.PixelExKt;
import base2app.ex.ResourceKt;


/**
 * @author liuxueyun
 * @email hueylauu@163.com
 * @date 2020/6/3 18:37
 * @desc
 */
public class ZRDrawableTextView extends AutoScaleTextView {

    private float height;
    private float width;
    private int drawableLeft;
    private int drawableTop;
    private int drawableRight;
    private int drawableBottom;

    public ZRDrawableTextView(Context context) {
        this(context, null);
    }

    public ZRDrawableTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZRDrawableTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ZRDrawableTextView);
        height = a.getFloat(R.styleable.ZRDrawableTextView_zr_drawable_height, 10);
        width = a.getFloat(R.styleable.ZRDrawableTextView_zr_drawable_width, 10);
        drawableLeft = a.getResourceId(R.styleable.ZRDrawableTextView_zr_drawable_left, 0);
        drawableTop = a.getResourceId(R.styleable.ZRDrawableTextView_zr_drawable_top, 0);
        drawableRight = a.getResourceId(R.styleable.ZRDrawableTextView_zr_drawable_right, 0);
        drawableBottom = a.getResourceId(R.styleable.ZRDrawableTextView_zr_drawable_bottom, 0);
        if (drawableLeft != 0) {
            setCompoundDrawablesWithIntrinsicBounds(ResourceKt.drawable(drawableLeft), null, null, null);
        }
        if (drawableTop != 0) {
            setCompoundDrawablesWithIntrinsicBounds(null, ResourceKt.drawable(drawableTop), null, null);
        }
        if (drawableRight != 0) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, ResourceKt.drawable(drawableRight), null);
        }
        if (drawableBottom != 0) {
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, ResourceKt.drawable(drawableBottom));
        }
        a.recycle();
    }

    @Override
    public void setCompoundDrawablesWithIntrinsicBounds(@Nullable Drawable left, @Nullable Drawable top, @Nullable Drawable right, @Nullable Drawable bottom) {
        if (left != null) {
            left.setBounds(0, 0, (int) PixelExKt.dp2px(width), (int) PixelExKt.dp2px(height));
        }
        if (right != null) {
            right.setBounds(0, 0, (int) PixelExKt.dp2px(width), (int) PixelExKt.dp2px(height));
        }
        if (top != null) {
            top.setBounds(0, 0, (int) PixelExKt.dp2px(width), (int) PixelExKt.dp2px(height));
        }
        if (bottom != null) {
            bottom.setBounds(0, 0, (int) PixelExKt.dp2px(width), (int) PixelExKt.dp2px(height));
        }
        setCompoundDrawables(left, top, right, bottom);
    }

    public void setDrawableHeight(float height) {
        this.height = height;
    }

    public void setDrawableWidth(float width) {
        this.width = width;
    }

    public void setDrawableRight(int drawableRight) {
        setCompoundDrawablesWithIntrinsicBounds(null, null, drawableRight == 0 ? null : ResourceKt.drawable(drawableRight), null);
    }

    public void setDrawableLeft(int drawableLeft) {
        setCompoundDrawablesWithIntrinsicBounds(drawableLeft == 0 ? null : ResourceKt.drawable(drawableLeft), null, null, null);
    }
}
