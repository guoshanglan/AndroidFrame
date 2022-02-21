package com.zhuorui.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @date 2020/4/20 11:00
 * @desc
 */
public class ZRSharpView extends View {

    private final Paint mSharpPaint = new Paint();

    private final boolean isReverseSharpView;

    private final Path mSharpPath = new Path();

    public ZRSharpView(Context context) {
        this(context, null);
    }

    public ZRSharpView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZRSharpView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ZRSharpView);
        int bgColor = array.getColor(R.styleable.ZRSharpView_sv_backgroundColor, Color.WHITE);
        boolean isRoundCorner = array.getBoolean(R.styleable.ZRSharpView_sv_isRoundCorner, false);
        float roundRadius = array.getDimension(R.styleable.ZRSharpView_sv_roundRadius, 5f);
        //是否反转三角形
        isReverseSharpView = array.getBoolean(R.styleable.ZRSharpView_sv_isReverse, false);
        array.recycle();

        mSharpPaint.setAntiAlias(true);
        mSharpPaint.setColor(bgColor);

        if (isRoundCorner) {
            CornerPathEffect mCornerEffect = new CornerPathEffect(roundRadius);
            mSharpPaint.setPathEffect(mCornerEffect);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();
        if (isReverseSharpView) {
            mSharpPath.moveTo(0, 0);
            mSharpPath.lineTo(w / 2, h);
            mSharpPath.lineTo(w, 0);
        } else {
            mSharpPath.moveTo(0, h);
            mSharpPath.lineTo(w, h);
            mSharpPath.lineTo(w / 2, 0);
        }
        canvas.drawPath(mSharpPath, mSharpPaint);
    }
}
