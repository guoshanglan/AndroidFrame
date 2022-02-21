package com.zhuorui.commonwidget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.showSoftInput
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.graphics.drawable.toBitmap
import base2app.ex.dp2px
import base2app.ex.drawable

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2022/1/19 10:56
 *    desc   :
 */
class ZREditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isFilterBitmap = true
    }

    /**
     * 是否显示重置按钮
     */
    var showResetButton: Boolean = false

    /**
     * 重置按钮图片
     */
    private val resetBitmap: Bitmap

    /**
     * 重置按钮点击区域
     */
    private var resetClickRect = RectF()

    /**
     * 重置按钮开始位置
     */
    private var resetLoction = floatArrayOf(0f, 0f)

    /**
     * 重置按钮是否按下
     */
    private var downReset = false

    /**
     * 真实的右距离(布局/代码设置)
     */
    private var realPaddingRight = 0

    private var iconPadding = 10f.dp2px().toInt()

    init {
        realPaddingRight = super.getPaddingRight()
        val restWH = 14f.dp2px().toInt()
        resetBitmap = drawable(R.mipmap.ic_clean)!!.toBitmap(restWH, restWH)
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.ZREditText)
            showResetButton = a.getBoolean(R.styleable.ZREditText_resetButton, showResetButton)
            a.recycle()
        }
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        realPaddingRight = right
        val r = if (showResetButton) {
            right + resetClickRect.width().toInt() - iconPadding.coerceAtMost(realPaddingRight)
        } else {
            right
        }
        super.setPadding(left, top, r, bottom)
    }

    override fun getPaddingRight(): Int {
        return realPaddingRight
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (showResetButton && measuredWidth > 0 && measuredHeight > 0) {
            resetClickRect.right =
                measuredWidth.toFloat() - ((realPaddingRight - iconPadding).takeIf { it > 0 } ?: 0)
            resetClickRect.left =
                resetClickRect.right - resetBitmap.width - iconPadding - (realPaddingRight.coerceAtMost(
                    iconPadding
                ))
            (resetBitmap.width * 0.5f + iconPadding).let {
                val c = measuredHeight * 0.5f
                resetClickRect.top = c - it
                resetClickRect.bottom = c + it
            }
            resetLoction[0] = resetClickRect.left + iconPadding
            resetLoction[1] = resetClickRect.top + iconPadding
            setPadding(paddingLeft, paddingTop, realPaddingRight, paddingBottom)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (isDrawReset()) {
            canvas?.drawBitmap(
                resetBitmap,
                resetLoction[0] + scrollX,
                resetLoction[1] + scrollY,
                iconPaint
            )
        }
    }

    private fun isDrawReset(): Boolean {
        return showResetButton && isFocused && text?.toString()?.length ?: 0 > 0
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    downReset = false
                    if (isDrawReset() && isClick(event, resetClickRect)) {
                        downReset = true
                        return true
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (downReset && isDrawReset() && isClick(event, resetClickRect)) {
                        setText("")
                        showSoftInput()
                    }
                }
            }
        }
        return super.onTouchEvent(event)

    }

    private fun isClick(event: MotionEvent, click: RectF): Boolean {
        return event.x in (click.left + iconPadding * 0.5f)..click.right && event.y in click.top..click.bottom
    }


}