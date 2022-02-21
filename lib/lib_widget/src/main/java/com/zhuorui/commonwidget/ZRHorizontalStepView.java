package com.zhuorui.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import base2app.ex.PixelExKt;
import base2app.ex.ResourceKt;

/**
 * @date 2020/8/6 10:33
 * @desc 步骤进度条
 */
public class ZRHorizontalStepView extends View {

    public static final int TEXT_LOCATION_DOWN = 0;//文字在下
    public static final int TEXT_LOCATION_UP = 1;//文字在上

    public static final int STEP_WIDTH_MODE_AVERAGE = 0;
    public static final int STEP_WIDTH_MODE_FIXED = 1;

    /**
     * 步骤的数量
     */
    private int count;//步骤点的个数
    /**
     * 当前进度
     */
    private int currentVal = 0;
    /**
     * 普通的步骤点的图标
     */

    private Drawable normalDrawable;

    /**
     * 正在进行中的步骤点的图标
     */

    private Drawable ongoingDrawable;
    /**
     * 已经完成的步骤点的图标
     */
    private Drawable completedDrawable;
    /**
     * 步骤条的线条宽度
     */
    private int lineWidth;

    /**
     * 普通的步骤点的图标的宽高
     */
    private int normalPointWidth = 0;

    /**
     * 正在进行中的步骤点的图标的宽高
     */
    private int ongoingPointWidth = 0;

    /**
     * 已经完成的步骤点的图标的宽高
     */
    private int completedPointWidth = 0;

    /**
     * 普通的默认的线条的颜色
     */
    private int lineNormalColor;
    /**
     * 已经完成的步骤的线条的颜色
     */
    private int lineCompletedColor;
    /**
     * 说明文字的字体大小
     */
    private int descTextSize;//文字字体大小
    /**
     * 普通的默认的文字的颜色
     */
    private int descNormalTextColor;
    /**
     * 正在进行中的步骤的文字颜色
     */
    private int descOngoingTextColor;
    /**
     * 已经完成的步骤的文字颜色
     */
    private int descCompletedTextColor;
    /**
     * 说明文字与步骤条之间的距离
     */
    private int distanceFromText;
    /**
     * 说明文字相对于步骤条的位置，如果是水平方向时，0表示在下面，1表示在上面；
     * 如果是垂直方向时，0表示在右边，1表示在左边
     */
    private int textLocation;
    /**
     * 每个步骤的说明
     */
    private final List<String> descriptions = new ArrayList<>();

    private final List<StaticLayout> staticLayouts = new ArrayList<>();

    /**
     * 每个步骤项的宽度；
     */
    private int stepWidth = 100;

    /**
     * 步骤项与步骤项之间的间隔，默认为0
     */
    private final int stepInterval = 10;
    /**
     * 线条的Paint
     */
    private final Paint linePaint = new Paint();

    /**
     * 步骤条的高度
     */
    private int barHeight;

    /**
     * view的宽度
     */
    private int widthSize;
    /**
     * 说明文字的最大高度
     */
    public int textMaxHeight;
    /**
     * 步骤项宽高的计算方式
     * STEP_WIDTH_MODE_AVERAGE:View的宽度均分
     * STEP_WIDTH_MODE_FIXED:自己设置的宽度，这种模式下，必须要设置步骤项的宽度，否则报错
     */
    public int stepWidthMode;

    public ZRHorizontalStepView(Context context) {
        this(context, null);
    }

    public ZRHorizontalStepView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZRHorizontalStepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        linePaint.setAntiAlias(true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZRHorizontalStepView, defStyleAttr, 0);
        count = a.getInt(R.styleable.ZRHorizontalStepView_count, 1);
        if (count < 2) {
            //点数必须大于等于2，否则抛出异常
            throw new IllegalArgumentException("Step count cant be less than 2!");
        }
        if (currentVal + 1 > count) {
            throw new IllegalArgumentException("CurrentVal must be less than count!");
        }
        normalDrawable = a.getDrawable(R.styleable.ZRHorizontalStepView_normal_point);
        ongoingDrawable = a.getDrawable(R.styleable.ZRHorizontalStepView_ongoing_point);
        completedDrawable = a.getDrawable(R.styleable.ZRHorizontalStepView_completed_point);

        lineWidth = a.getDimensionPixelSize(R.styleable.ZRHorizontalStepView_line_width, 16);
        lineNormalColor = a.getColor(R.styleable.ZRHorizontalStepView_line_normal_color, ResourceKt.color(R.color.step_normal_line_color));
        lineCompletedColor = a.getColor(R.styleable.ZRHorizontalStepView_line_completed_color, ResourceKt.color(R.color.brand_main_color));
        descTextSize = a.getDimensionPixelSize(R.styleable.ZRHorizontalStepView_desc_textSize, 12);
        descNormalTextColor = a.getColor(R.styleable.ZRHorizontalStepView_desc_normal_textColor, Color.BLACK);
        descOngoingTextColor = a.getColor(R.styleable.ZRHorizontalStepView_desc_ongoing_textColor, Color.BLACK);
        descCompletedTextColor = a.getColor(R.styleable.ZRHorizontalStepView_desc_completed_textColor, Color.BLACK);
        distanceFromText = a.getDimensionPixelSize(R.styleable.ZRHorizontalStepView_distance_from_text, 10);
        textLocation = a.getInt(R.styleable.ZRHorizontalStepView_text_location, TEXT_LOCATION_DOWN);
        ongoingPointWidth = a.getDimensionPixelSize(R.styleable.ZRHorizontalStepView_ongoing_point_width, ongoingPointWidth);
        completedPointWidth = a.getDimensionPixelSize(R.styleable.ZRHorizontalStepView_completed_point_width, completedPointWidth);
        normalPointWidth = a.getDimensionPixelSize(R.styleable.ZRHorizontalStepView_normal_point_width, normalPointWidth);
        stepWidthMode = a.getInt(R.styleable.ZRHorizontalStepView_stepWidthMode, STEP_WIDTH_MODE_AVERAGE);
        stepWidth = a.getDimensionPixelSize(R.styleable.ZRHorizontalStepView_step_width, stepWidth);
        a.recycle();

        linePaint.setStrokeWidth(lineWidth);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        widthSize = getWidthSize(widthMeasureSpec);
        int heightSize = getHeightSize(heightMeasureSpec);
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //画连线
        //画已完成的线
        int startX1 = getPaddingStart() + stepWidth / 2;
        int stopX1 = startX1 + stepWidth * (currentVal + 1 >= count ? currentVal : currentVal + 1) + stepInterval * currentVal;
        int startY = getPaddingTop() + barHeight / 2;
        int stopY = getPaddingTop() + barHeight / 2;
        if (textLocation == TEXT_LOCATION_UP) {
            startY += textMaxHeight + distanceFromText;
            stopY += textMaxHeight + distanceFromText;
        }
        linePaint.setColor(lineCompletedColor);
        canvas.drawLine(startX1, startY, stopX1, stopY, linePaint);
        //画未完成的线
        linePaint.setColor(lineNormalColor);
        canvas.drawLine(stopX1, startY, stepWidth * count + (count - 1) * stepInterval - stepWidth / 2, stopY, linePaint);

        //画步骤点
        int left = getPaddingStart();
        for (int i = 0; i < count; i++) {
            if (i == currentVal) {
                drawStepPoint(canvas, completedDrawable, left, ongoingPointWidth);
            } else if (i < currentVal) {
                drawStepPoint(canvas, completedDrawable, left, completedPointWidth);
            } else {
                drawStepPoint(canvas, normalDrawable, left, normalPointWidth);
            }
            left = left + stepWidth + stepInterval;
        }

        //画文字
        canvas.save();
        canvas.translate(0, textLocation == 0 ? barHeight + distanceFromText + getPaddingTop() : getPaddingTop());
        for (int i = 0; i < staticLayouts.size(); i++) {
            staticLayouts.get(i).draw(canvas);
            canvas.translate(stepWidth + stepInterval, 0);
        }
        canvas.restore();


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    private int getWidthSize(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int trueSize = 0;
        switch (mode) {
            case MeasureSpec.UNSPECIFIED://测量模式是不作限制，一般是在ScrollView中
            case MeasureSpec.AT_MOST://如果测量模式是当前尺寸能取的最大值，当wrap-content时
                //这时候size是父View的size，因为是wrap-content，我们应该测量自己的大小
                //获取View的宽度
                trueSize = count * stepWidth + (count - 1) * stepInterval + getPaddingStart() + getPaddingEnd();
                break;
            case MeasureSpec.EXACTLY://如果测量模式是精确值，也就是固定的大小
                if (stepWidthMode == STEP_WIDTH_MODE_AVERAGE) {
                    stepWidth = (size - stepInterval * (count - 1)) / count;//这时候每个步骤项的宽度，为总宽度除以步骤的数量
                }
                trueSize = size;//这时候size是我们xml设置的值
                break;
        }
        return trueSize;
    }


    private int getHeightSize(int measureSpec) {
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int trueSize = 0;
        barHeight = getBarHeight();
        switch (mode) {
            case MeasureSpec.UNSPECIFIED://测量模式是不作限制
            case MeasureSpec.AT_MOST://如果测量模式是当前尺寸能取的最大值，当wrap-content时
                //这时候size是父View的size，因为是wrap-content，我们应该测量自己的大小
                //获取步骤条的高度

                if (descriptions.size() != 0) {
                    //如果有说明文字，那么高度的大小就要加上说明文字中的最大高度
                    textMaxHeight = getMultiTextMaxHeight();
                    trueSize = barHeight + textMaxHeight + getPaddingTop() + getPaddingBottom() + distanceFromText;
                } else {
                    trueSize = barHeight + getPaddingTop() + getPaddingBottom() + distanceFromText;
                }
                break;
            case MeasureSpec.EXACTLY://如果测量模式是精确值，也就是固定的大小
                trueSize = size;//这时候size是我们xml设置的值
                textMaxHeight = size - barHeight - getPaddingTop() - getPaddingBottom();
                break;
        }
        return trueSize;
    }

    //画Bitmap步骤点
    private void drawStepPoint(Canvas canvas, Drawable drawable, int left, int setWidth) {
        int destLeft;
        int destTop = textLocation == TEXT_LOCATION_UP ? getPaddingTop() + textMaxHeight + distanceFromText : getPaddingTop();
        int destRight;
        int destBottom;

        if (setWidth == 0) {
            //如果没有设置图片的宽高
            if (drawable.getIntrinsicWidth() >= stepWidth) {
                destLeft = left;
                destRight = destLeft + stepWidth;
            } else {
                destLeft = left + stepWidth / 2 - drawable.getIntrinsicWidth() / 2;
                destRight = destLeft + drawable.getIntrinsicWidth();
            }
            if (drawable.getIntrinsicHeight() > stepWidth) {
                destBottom = destTop + stepWidth;
            } else {
                destTop += barHeight / 2 - drawable.getIntrinsicHeight() / 2;
                destBottom = destTop + drawable.getIntrinsicHeight();
            }
        } else {
            if (setWidth >= stepWidth) {
                destLeft = left;
                destRight = destLeft + stepWidth;
                destBottom = destTop + stepWidth;
            } else {
                destLeft = left + stepWidth / 2 - setWidth / 2;
                destRight = destLeft + setWidth;
                destTop += barHeight / 2 - setWidth / 2;
                destBottom = destTop + setWidth;
            }
        }


        drawable.setBounds(destLeft, destTop, destRight, destBottom);
        drawable.draw(canvas);
    }


    //获得步骤条的高度
    private int getBarHeight() {
        int normalHeight = normalPointWidth == 0 ? normalDrawable.getIntrinsicHeight() : normalPointWidth;
        int ongoingHeight = ongoingPointWidth == 0 ? ongoingDrawable.getIntrinsicHeight() : ongoingPointWidth;
        int completedHeight = completedPointWidth == 0 ? completedDrawable.getIntrinsicHeight() : completedPointWidth;
        int max = getMax(normalHeight, ongoingHeight, completedHeight, lineWidth);
        return Math.min(max, stepWidth);
    }


    //得到多个int值中的最大值
    private int getMax(int... sizes) {
        int temp = 0;
        for (int size : sizes) {
            if (size > temp) {
                temp = size;
            }
        }
        return temp;
    }

    //获得多个Text最大的高度
    private int getMultiTextMaxHeight() {
        int temp = 0;
        staticLayouts.clear();
        for (int i = 0; i < descriptions.size(); i++) {
            TextPaint textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(descTextSize);
            textPaint.setTypeface(Typeface.DEFAULT);
            if (i > currentVal) {
                textPaint.setColor(descNormalTextColor);
            } else {
                textPaint.setColor(descCompletedTextColor);
            }
            StaticLayout staticLayout = new StaticLayout(descriptions.get(i), textPaint, stepWidth, Layout.Alignment.ALIGN_CENTER, 1, 0, true);
            staticLayouts.add(staticLayout);
            if (staticLayout.getHeight() > temp) {
                temp = staticLayout.getHeight();
            }
        }
        return temp;
    }

    //设置说明文字
    public void setDescriptions(List<String> descs) {
        descriptions.clear();
        descriptions.addAll(descs);
        invalidate();
        requestLayout();
    }

    //设置当前步骤
    public void setCurrentVal(int val) {
        currentVal = val;
        invalidate();
    }

    //设置说明文字
    public void setDescriptions(String[] descs) {
        descriptions.clear();
        setDescriptions(Arrays.asList(descs));
    }

    //设置普通步骤点的图标的宽高
    public void setNormalPointWidth(int dp) {
        normalPointWidth = (int) PixelExKt.dp2px(dp);
        invalidate();
    }

    //设置正在进行步骤点的图标的宽高
    public void setOngoingPointWidth(int dp) {
        ongoingPointWidth = (int) PixelExKt.dp2px(dp);
        invalidate();
    }

    //设置已经完成的步骤点的图标的宽高
    public void setCompletedPointWidth(int dp) {
        completedPointWidth = (int) PixelExKt.dp2px(dp);
        invalidate();
    }
}
