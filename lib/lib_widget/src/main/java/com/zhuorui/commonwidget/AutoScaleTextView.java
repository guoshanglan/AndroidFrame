package com.zhuorui.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatTextView;

import base2app.ex.PixelExKt;

/**
 * 自动伸缩大小的textView
 */

public class AutoScaleTextView extends AppCompatTextView {
    private static final String TAG = "AutoScaleTextView";
    private float preferredTextSize;
    private float minTextSize;
    private final Paint textPaint;
    private final float textZoomFactor;
    private final float minSize;
    private boolean isFixedSize; //是否修改字体大小
    private OnRefitTextSizeChangeListener mTextSizeChange;
    private int mUnit;
    private float mSize;

    public AutoScaleTextView(Context context) {
        this(context, null);
    }

    public AutoScaleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AutoScaleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.textPaint = new Paint();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoScaleTextView, defStyleAttr, 0);
        //缩放比例
        textZoomFactor = a.getFloat(R.styleable.AutoScaleTextView_textZoomFactor, 0.85f);
        minSize = a.getDimensionPixelOffset(R.styleable.AutoScaleTextView_minTextSize, 0);
        isFixedSize = a.getBoolean(R.styleable.AutoScaleTextView_isFixedSize, true);
        a.recycle();
        initTextSize();
    }

    @Override
    public void setTextSize(int unit, float size) {
        super.setTextSize(unit, size);
        initTextSize();
    }

    private void initTextSize() {
        preferredTextSize = getTextSize();
        minTextSize = minSize > 0 ? minSize : Math.max(preferredTextSize * textZoomFactor, PixelExKt.sp2px(10f));
    }

    /**
     * 设置最小的size
     *
     * @param minTextSize
     */
    public void setMinTextSize(float minTextSize) {
        this.minTextSize = minTextSize;
    }

    /**
     * 根据填充内容调整textview
     *
     * @param text
     * @param textWidth
     */
    private void refitText(String text, int textWidth) {
        if (preferredTextSize == 0) return;
        if (textWidth <= 0 || TextUtils.isEmpty(text) || textPaint == null || !isFixedSize) {
            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, preferredTextSize);
            textSizeChange(TypedValue.COMPLEX_UNIT_PX, preferredTextSize);
            return;
        }
        int targetWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();

        final float threshold = 0.5f;
        textPaint.set(this.getPaint());
        textPaint.setTextSize(this.preferredTextSize);
        if (textPaint.measureText(text) <= targetWidth) {
            super.setTextSize(TypedValue.COMPLEX_UNIT_PX, preferredTextSize);
            textSizeChange(TypedValue.COMPLEX_UNIT_PX, preferredTextSize);
            return;
        }
        float tempMinTextSize = this.minTextSize;
        float tempPreferredTextSize = this.preferredTextSize;
        while ((tempPreferredTextSize - tempMinTextSize) > threshold) {
            float size = (tempPreferredTextSize + tempMinTextSize) / 2;
            textPaint.setTextSize(size);
            if (textPaint.measureText(text) >= targetWidth) {
                tempPreferredTextSize = size;
            } else {
                tempMinTextSize = size;
            }
        }
        super.setTextSize(TypedValue.COMPLEX_UNIT_PX, tempMinTextSize);
        textSizeChange(TypedValue.COMPLEX_UNIT_PX, tempMinTextSize);
    }

    private void textSizeChange(int unit, float size) {
        if (mTextSizeChange != null && (unit != mUnit || size != mSize)) {
            mUnit = unit;
            mSize = size;
            mTextSizeChange.onTextSizeChange(mUnit, mSize);
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        this.refitText(text.toString(), getRefitWidth(getWidth()));
    }

    @Override
    protected void onSizeChanged(int width, int h, int oldw, int oldh) {
        if (width != oldw) {
            this.refitText(this.getText().toString(), getRefitWidth(width));
        }
    }

    private int getRefitWidth(int width) {
        int max = getMaxWidth();
        if (max != Integer.MAX_VALUE && max != -1) {
            return Math.max(width, max);
        }
        return width;
    }

    public void setOnMarqueeListener(OnRefitTextSizeChangeListener listener) {
        this.mTextSizeChange = listener;
    }

    /**
     * 文字大小改变监听
     */
    public interface OnRefitTextSizeChangeListener {

        void onTextSizeChange(int unit, float size);
    }

    public void setFixedSize(boolean isFixedSize) {
        this.isFixedSize = isFixedSize;
    }
}