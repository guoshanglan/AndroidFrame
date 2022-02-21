package com.zhuorui.commonwidget

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.OvershootInterpolator
import base2app.ex.color
import base2app.ex.dp2px
import base2app.ex.sp2px


/**
 * @author xuzuoliang
 * @email micrason@163.com
 * @date 2020/10/9 14:16
 * @desc 单选框
 */
class ZRRadioButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), View.OnClickListener {

    private val outCirclePaint = Paint()


    private val innerCirclePaint = Paint()
    private val titlePaint = Paint()

    /**
     * 默认选中状态
     */
    private var isChecked = false

    /**
     * 标题
     */
    private var radioTitle: String? = null

    /**
     * 外圆半径，受控件本身的高度约束
     */
    private var outCircleRadius = 0f

    /**
     * 内圆半径，受外圆半径约束
     */
    private var innerCircleRadius = 0f

    /**
     * 控件的高度
     */
    private var widgetHeight = 0f

    /**
     * 标题和圆之间的间隙
     */
    private var horizontalGap = 0f

    /**
     * 选中的颜色
     */
    private var checkColor: Int

    /**
     * 未选中的颜色
     */
    private var unCheckColor: Int

    private val viewState: ViewState = ViewState()

    init {
        setOnClickListener(this)
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.ZRRadioButton)

        isChecked = typeArray.getBoolean(R.styleable.ZRRadioButton_rb_isChecked, false)

        checkColor = typeArray.getColor(R.styleable.ZRRadioButton_rb_checkColor,color(R.color.brand_main_color))
        unCheckColor = typeArray.getColor(R.styleable.ZRRadioButton_rb_unCheckColor,color(R.color.subtitle_text_color))

        radioTitle = typeArray.getString(R.styleable.ZRRadioButton_rb_title)

        outCircleRadius = typeArray.getDimension(R.styleable.ZRRadioButton_rb_innerCircleRadius, 8.dp2px())
        innerCircleRadius = typeArray.getDimension(R.styleable.ZRRadioButton_rb_outCircleRadius, 4.dp2px())

        horizontalGap = typeArray.getDimension(R.styleable.ZRRadioButton_rb_gap, 8.dp2px())

        outCirclePaint.isAntiAlias = true
        innerCirclePaint.isAntiAlias = true
        titlePaint.isAntiAlias = true
        titlePaint.color =color(R.color.main_content_text_color)
        titlePaint.textSize = 14.sp2px()


        outCirclePaint.style = Paint.Style.STROKE
        innerCirclePaint.style = Paint.Style.FILL

        outCirclePaint.strokeWidth = 1.dp2px()


        viewState.innerCircleRadius = if (isChecked) innerCircleRadius else 0f
        viewState.innerCircleColor = if (isChecked) checkColor else Color.TRANSPARENT
        viewState.outCircleColor = if (isChecked) checkColor else unCheckColor

        typeArray.recycle()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        widgetHeight = h.toFloat()
        invalidate()
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val cx = outCircleRadius
        val cy = widgetHeight / 2

        //如果传入的外圆半径比控件本身的高度
        if (outCircleRadius > cy) {
            outCircleRadius = cy
        }

        //如果传入的内圆半径大于外圆半径，则内外圆重合绘制
        if (viewState.innerCircleRadius > outCircleRadius) {
            viewState.innerCircleRadius = outCircleRadius
        }

        val realOutCircleRadius = outCircleRadius - 2 * outCirclePaint.strokeWidth
        val realInnerCircleRadius = viewState.innerCircleRadius

        if (isChecked) {
            //绘制选中时的外圆和实心内圆
            outCirclePaint.color = viewState.outCircleColor
            innerCirclePaint.color = viewState.innerCircleColor
            canvas?.drawCircle(cx, cy, realOutCircleRadius, outCirclePaint)
            canvas?.drawCircle(cx, cy, realInnerCircleRadius, innerCirclePaint)
        } else {
            //绘制未选择时的外圆
            outCirclePaint.color = viewState.outCircleColor
            innerCirclePaint.color = viewState.innerCircleColor
            canvas?.drawCircle(cx, cy, realOutCircleRadius, outCirclePaint)
            canvas?.drawCircle(cx, cy, realInnerCircleRadius, innerCirclePaint)
        }
        //绘制标题
        radioTitle?.let {
            val fontMetrics = titlePaint.fontMetrics
            val distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom
            val baseline: Float = cy + distance
            canvas?.drawText(it, outCircleRadius * 2 + horizontalGap, baseline, titlePaint)
        }
    }

    private val argbEvaluator = ArgbEvaluator()


    override fun onClick(p0: View?) {
        if (!isChecked) {
            isChecked = true
            switch(isChecked)
        }
    }

    fun switch(isChecked: Boolean) {
        this.isChecked = isChecked
        if (isChecked) {
            ValueAnimator.ofFloat(0f, outCircleRadius, innerCircleRadius)
                .apply {
                    addUpdateListener {
                        val value = it.animatedValue as Float
                        viewState.innerCircleRadius = value
                        viewState.innerCircleColor = argbEvaluator.evaluate(it.animatedFraction, unCheckColor, checkColor) as Int
                        viewState.outCircleColor = argbEvaluator.evaluate(it.animatedFraction, unCheckColor, checkColor) as Int
                        invalidate()
                    }
                }
        } else {
            ValueAnimator.ofFloat(innerCircleRadius, 0f)
                .apply {
                    addUpdateListener {
                        interpolator = OvershootInterpolator()
                        val value = it.animatedValue as Float
                        viewState.innerCircleRadius = value
                        viewState.innerCircleColor = argbEvaluator.evaluate(it.animatedFraction, checkColor, unCheckColor) as Int
                        viewState.outCircleColor = argbEvaluator.evaluate(it.animatedFraction, checkColor, unCheckColor) as Int
                        invalidate()
                    }
                }
        }.setDuration(300).start()
        onZRRadioButtonCheckStateListener?.onZRRadioButtonCheckState(this.isChecked)
    }

    private var onZRRadioButtonCheckStateListener: OnZRRadioButtonCheckStateListener? = null

    fun setOnZRRadioButtonCheckStateListener(onZRRadioButtonCheckStateListener: OnZRRadioButtonCheckStateListener) {
        this.onZRRadioButtonCheckStateListener = onZRRadioButtonCheckStateListener
    }

    interface OnZRRadioButtonCheckStateListener {
        fun onZRRadioButtonCheckState(checkState: Boolean)
    }

    class ViewState {
        var innerCircleRadius: Float = 0f
        var innerCircleColor: Int = 0
        var outCircleColor: Int = 0
    }
}