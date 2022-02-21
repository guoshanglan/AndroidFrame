package com.zhuorui.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.appcompat.widget.AppCompatImageView;

import com.zhuorui.commonwidget.util.OpenGLRenderUtil;

/**
 * date   : 2020/8/13 12:13
 * desc   : 支持圆角、花边框的ImageView
 */
public class ZRImageView extends AppCompatImageView {

    private boolean isFixedRatio; // 是否固定宽高比显示
    private float widthHeightRatio = 1f;//宽高比
    private boolean isCircle; // 是否显示为圆形，如果为圆形则设置的corner无效
    private boolean isCoverSrc; // border、inner_border是否覆盖图片
    private int borderWidth; // 边框宽度
    private int borderColor = Color.WHITE; // 边框颜色

    private int cornerTopLeftRadius; // 左上角圆角半径
    private int cornerTopRightRadius; // 右上角圆角半径
    private int cornerBottomLeftRadius; // 左下角圆角半径
    private int cornerBottomRightRadius; // 右下角圆角半径

    private int maskColor; // 遮罩颜色


    private float[] borderRadii;//矩形边框圆角信息
    private float[] srcRadii;//矩形图片圆角信息


    private Paint paint;//边框，遮罩画笔
    private Path maskPath;//遮罩区域
    private Path borderPath;//边框路径

    private Paint clearPaint;//清除画笔
    private Path clearPath;//清除区域
    private boolean isClear = false;//是否要清除功能
    private float cx = 0;
    private float cy = 0;
    private int openglRenderMax = 4096;//OpenGLRenderer绘制bitmap的最大限制,宽或高超出限制不能使用saveLayer新开图层，会无法正常现实


    public ZRImageView(Context context) {
        super(context);
        initView(null);
    }

    public ZRImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public ZRImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        if (!isInEditMode())
            openglRenderMax = OpenGLRenderUtil.getOpenglRenderLimitValue();
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ZRImageView, 0, 0);
        int cornerRadius = ta.getDimensionPixelOffset(R.styleable.ZRImageView_corner_radius, 0);
        cornerTopLeftRadius = cornerTopRightRadius = cornerBottomLeftRadius = cornerBottomRightRadius = cornerRadius;
        for (int i = 0; i < ta.getIndexCount(); i++) {
            int attr = ta.getIndex(i);
            if (attr == R.styleable.ZRImageView_is_cover_src) {
                isCoverSrc = ta.getBoolean(attr, isCoverSrc);
            } else if (attr == R.styleable.ZRImageView_is_circle) {
                isCircle = ta.getBoolean(attr, isCircle);
            } else if (attr == R.styleable.ZRImageView_border_width) {
                borderWidth = ta.getDimensionPixelSize(attr, borderWidth);
            } else if (attr == R.styleable.ZRImageView_border_color) {
                borderColor = ta.getColor(attr, borderColor);
            } else if (attr == R.styleable.ZRImageView_corner_top_left_radius) {
                cornerTopLeftRadius = ta.getDimensionPixelSize(attr, cornerTopLeftRadius);
            } else if (attr == R.styleable.ZRImageView_corner_top_right_radius) {
                cornerTopRightRadius = ta.getDimensionPixelSize(attr, cornerTopRightRadius);
            } else if (attr == R.styleable.ZRImageView_corner_bottom_left_radius) {
                cornerBottomLeftRadius = ta.getDimensionPixelSize(attr, cornerBottomLeftRadius);
            } else if (attr == R.styleable.ZRImageView_corner_bottom_right_radius) {
                cornerBottomRightRadius = ta.getDimensionPixelSize(attr, cornerBottomRightRadius);
            } else if (attr == R.styleable.ZRImageView_mask_color) {
                maskColor = ta.getColor(attr, maskColor);
            } else if (attr == R.styleable.ZRImageView_is_fixed_ratio) {
                isFixedRatio = ta.getBoolean(attr, isFixedRatio);
            } else if (attr == R.styleable.ZRImageView_width_height_ratio) {
                widthHeightRatio = ta.getFloat(attr, widthHeightRatio);
            }
        }
        ta.recycle();
        borderRadii = new float[8];
        srcRadii = new float[8];
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        maskPath = new Path();
        borderPath = new Path();
        borderPath.setFillType(Path.FillType.EVEN_ODD);
        clearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        clearPaint.setFilterBitmap(true);
        clearPaint.setDither(true);
        clearPaint.setStyle(Paint.Style.FILL);
        clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        clearPath = new Path();
        clearPath.setFillType(Path.FillType.INVERSE_WINDING);
        calculateRadii();
        initPath();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        while (bm.getWidth() > (openglRenderMax - 20) || bm.getHeight() > (openglRenderMax - 20)) {
            Matrix matrix = new Matrix();
            matrix.setScale(0.5f, 0.5f);
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        }
        super.setImageBitmap(bm);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initPath();
    }

    private void initPath() {
        isClear = false;
        maskPath.reset();
        clearPath.reset();
        borderPath.reset();
        final float w = getWidth() - getPaddingLeft() - getPaddingRight();
        final float h = getHeight() - getPaddingTop() - getPaddingBottom();
        if (w <= 0 || h <= 0) return;
        final float xRadius = w / 2.0f;
        final float yRadius = h / 2.0f;
        cx = xRadius + getPaddingLeft();
        cy = yRadius + getPaddingTop();
        if (isCircle) {
            final float circleRadius = Math.min(xRadius, yRadius);
            isClear = true;
            if (borderWidth > 0) {
                //边框外边
                borderPath.addCircle(cx, cy, circleRadius, Path.Direction.CCW);
                //边框内边
                borderPath.addCircle(cx, cy, circleRadius - borderWidth, Path.Direction.CCW);
            }
            maskPath.addCircle(cx, cy, circleRadius - borderWidth, Path.Direction.CCW);
            clearPath.addCircle(cx, cy, circleRadius, Path.Direction.CCW);
        } else {
            isClear = cornerTopLeftRadius > 0 || cornerTopRightRadius > 0 || cornerBottomLeftRadius > 0 || cornerBottomRightRadius > 0;
            RectF borderRectF = new RectF(cx - xRadius, cy - yRadius, cx + xRadius, cy + yRadius);
            if (borderWidth > 0) {
                //边框外边
                borderPath.addRoundRect(borderRectF, borderRadii, Path.Direction.CCW);
                //边框内边
                RectF inBorderRectF = new RectF(borderRectF.left + borderWidth, borderRectF.top + borderWidth, borderRectF.right - borderWidth, borderRectF.bottom - borderWidth);
                borderPath.addRoundRect(inBorderRectF, srcRadii, Path.Direction.CCW);
                maskPath.addRoundRect(inBorderRectF, srcRadii, Path.Direction.CCW);
                clearPath.addRoundRect(borderRectF, borderRadii, Path.Direction.CCW);
            } else {
                maskPath.addRoundRect(borderRectF, srcRadii, Path.Direction.CCW);
                clearPath.addRoundRect(borderRectF, srcRadii, Path.Direction.CCW);
            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (isCircle) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.EXACTLY);
        } else if (isFixedRatio) {
            int h = (int) (MeasureSpec.getSize(widthMeasureSpec) * widthHeightRatio);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void draw(Canvas canvas) {
        if (isClear && getHeight() <= openglRenderMax && getWidth() <= openglRenderMax) {
            int c = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
            super.draw(canvas);
            canvas.drawPath(clearPath, clearPaint);
            canvas.restoreToCount(c);
        } else {
            super.draw(canvas);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isCoverSrc && borderWidth > 0) {
            //边框不遮盖图片
            int c = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
            float sx = 1.0f * (getWidth() - 2 * borderWidth) / getWidth();
            float sy = 1.0f * (getHeight() - 2 * borderWidth) / getHeight();
            // 缩小画布，使图片内容不被borders覆盖
            canvas.scale(sx, sy, cx, cy);
            super.onDraw(canvas);
            canvas.restoreToCount(c);
            //清除在边框内的内容
            canvas.drawPath(borderPath, clearPaint);
        } else {
            super.onDraw(canvas);
        }
        drawMask(canvas);
        drawBorders(canvas);
    }

    /**
     * 绘制遮罩
     */
    private void drawMask(Canvas canvas) {
        if (maskColor != 0) {
            paint.reset();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(maskColor);
            canvas.drawPath(maskPath, paint);
        }
    }

    /**
     * 绘制边框
     *
     * @param canvas
     */
    private void drawBorders(Canvas canvas) {
        if (borderWidth > 0) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(borderColor);
            canvas.drawPath(borderPath, paint);
        }
    }

    /**
     * 计算RectF的圆角半径
     */
    private void calculateRadii() {

        borderRadii[0] = borderRadii[1] = cornerTopLeftRadius;
        borderRadii[2] = borderRadii[3] = cornerTopRightRadius;
        borderRadii[4] = borderRadii[5] = cornerBottomRightRadius;
        borderRadii[6] = borderRadii[7] = cornerBottomLeftRadius;

        if (borderWidth > 0) {
            srcRadii[0] = srcRadii[1] = cornerTopLeftRadius - borderWidth / 2.0f;
            srcRadii[2] = srcRadii[3] = cornerTopRightRadius - borderWidth / 2.0f;
            srcRadii[4] = srcRadii[5] = cornerBottomRightRadius - borderWidth / 2.0f;
            srcRadii[6] = srcRadii[7] = cornerBottomLeftRadius - borderWidth / 2.0f;
        } else {
            srcRadii[0] = srcRadii[1] = cornerTopLeftRadius;
            srcRadii[2] = srcRadii[3] = cornerTopRightRadius;
            srcRadii[4] = srcRadii[5] = cornerBottomRightRadius;
            srcRadii[6] = srcRadii[7] = cornerBottomLeftRadius;
        }

    }


    public void openFixedRatio(float ratio) {
        isFixedRatio = true;
        widthHeightRatio = ratio;
        if (isAttachedToWindow() && getMeasuredWidth() > 0) {
            initPath();
            invalidate();
        }
    }

    public void isCoverSrc(boolean isCoverSrc) {
        this.isCoverSrc = isCoverSrc;
        initPath();
        invalidate();
    }

    public void isCircle(boolean isCircle) {
        this.isCircle = isCircle;
        initPath();
        invalidate();
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        calculateRadii();
        initPath();
        invalidate();
    }

    public void setBorderColor(@ColorInt int borderColor) {
        this.borderColor = borderColor;
        invalidate();
    }

    public void setCornerRadius(int cornerRadius) {
        cornerTopLeftRadius = cornerRadius;
        cornerTopRightRadius = cornerRadius;
        cornerBottomLeftRadius = cornerRadius;
        cornerBottomRightRadius = cornerRadius;
        calculateRadii();
        initPath();
        invalidate();
    }

    public void setCornerTopLeftRadius(int cornerTopLeftRadius) {
        this.cornerTopLeftRadius = cornerTopLeftRadius;
        calculateRadii();
        initPath();
        invalidate();
    }

    public void setCornerTopRightRadius(int cornerTopRightRadius) {
        this.cornerTopRightRadius = cornerTopRightRadius;
        calculateRadii();
        initPath();
        invalidate();
    }

    public void setCornerBottomLeftRadius(int cornerBottomLeftRadius) {
        this.cornerBottomLeftRadius = cornerBottomLeftRadius;
        calculateRadii();
        initPath();
        invalidate();
    }

    public void setCornerBottomRightRadius(int cornerBottomRightRadius) {
        this.cornerBottomRightRadius = cornerBottomRightRadius;
        calculateRadii();
        initPath();
        invalidate();
    }

    public void setMaskColor(@ColorInt int maskColor) {
        this.maskColor = maskColor;
        invalidate();
    }


}
