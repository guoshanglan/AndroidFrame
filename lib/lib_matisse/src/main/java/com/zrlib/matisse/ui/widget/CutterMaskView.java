package com.zrlib.matisse.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * author : liuwei
 * e-mail : vsanliu@foxmail.com
 * date   : 2020/9/10 18:05
 * desc   :
 */
public class CutterMaskView extends View {

    /**
     * 是否圆形
     */
    private boolean mCircle = true;

    /**
     * 裁剪区域宽高比
     */
    private float mRatio = 1;

    /**
     * 擦除画笔
     */
    private Paint eraser = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Rect frame = new Rect();
    private int mCentreX;
    private int mCentreY;

    private int maskColor = Color.parseColor("#99000000");

    public CutterMaskView(Context context) {
        super(context);
        init();
    }

    public CutterMaskView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CutterMaskView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 硬件加速不支持，图层混合。
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initPath();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画背景
        canvas.drawColor(maskColor);
        //擦除拍照区背景
        if (mCircle) {
            canvas.drawCircle(mCentreX, mCentreY, frame.width() / 2, eraser);
        } else {
            canvas.drawRect(frame, eraser);
        }


    }

    public void setMaskColor(int color) {
        maskColor = color;
        invalidate();
    }

    public void setCircle(boolean circle) {
        mCircle = circle;
        initPath();
    }

    public void setAspectRatio(String ratio) {
        String[] split = ratio.split(":");
        mRatio = Integer.valueOf(split[0]) * 1f / Integer.valueOf(split[1]);
        initPath();
    }

    public Rect getFrame() {
        return new Rect(frame);
    }

    private void initPath() {
        post(new Runnable() {
            @Override
            public void run() {
                int maxW = getMeasuredWidth() - getPaddingRight() - getPaddingLeft();
                mCentreX = getMeasuredWidth() / 2;
                mCentreY = getMeasuredHeight() / 2;
                int w;
                int h;
                if (mCircle || mRatio == 1) {
                    w = maxW;
                    h = w;
                } else if (mRatio > 1) {
                    w = maxW;
                    h = (int) (w / mRatio);
                } else {
                    w = maxW;
                    h = (int) (w / mRatio);
                    int maxH = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
                    if (h > maxH) {
                        h = maxH;
                        w = (int) (h * mRatio);
                    }
                }
                frame.left = mCentreX - w / 2;
                frame.top = mCentreY - h / 2;
                frame.right = frame.left + w;
                frame.bottom = frame.top + h;
                invalidate();
            }
        });


    }

    public boolean isCircle() {
        return mCircle;
    }
}
