package com.zhuorui.commonwidget

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import base2app.ex.blod
import base2app.ex.color
import base2app.ex.dp2px
import base2app.ex.drawable
import com.zhuorui.securities.base2app.ex.*

/**
 * @date 2021/4/20 14:12
 * @desc 交易密码itemview @ZRInputPasswordView
 */
class PasswordItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        /**
         * 边框模式：框模式
         */
        const val BORDER_MODE_FRAME = 1

        /**
         * 边框模式：下划线模式
         */
        const val BORDER_MODE_UNDERLINE = 2
    }


    /**
     * 默认隐藏输入模式
     */
    private var isHideInput: Boolean = true

    /**
     * 默认框模式
     */
    private var borderMode: Int = BORDER_MODE_FRAME

    /**
     * 边框颜色
     */
    private var borderColor: Int = color(R.color.subtitle_text_color)

    /**
     * 边框宽度
     */
    private var borderWidth: Int = 1.dp2px().toInt()

    /**
     * 是否开启游标
     */
    private var isOpenCursor: Boolean = false

    /**
     * 游标颜色
     */
    private var cursorColor: Int = color(R.color.brand_main_color)

    /**
     * 是否隐藏输入
     */
    fun isHideInput(isHideInput: Boolean): PasswordItemView {
        this.isHideInput = isHideInput
        return this
    }

    /**
     * 边框模式
     */
    fun borderMode(borderMode: Int, borderColor: Int, borderWidth: Int): PasswordItemView {
        this.borderMode = borderMode
        this.borderColor = borderColor
        this.borderWidth = borderWidth
        return this
    }

    /**
     * 是否开启游标
     */
    fun isOpenCursor(isOpenCursor: Boolean, cursorColor: Int = color(R.color.brand_main_color)): PasswordItemView {
        this.isOpenCursor = isOpenCursor
        this.cursorColor = cursorColor
        return this
    }

    private var inputTextView: TextView? = null

    private var viewPoint: View? = null

    private var cursorView: View? = null

    private fun addInputTextView() {
        if (inputTextView == null) {
            inputTextView = TextView(context)
            inputTextView?.gravity = Gravity.CENTER
            inputTextView?.textSize = 16f
            inputTextView?.blod()
            inputTextView?.setTextColor(color(R.color.dialog_title_text))
            inputTextView?.visibility = GONE
        }
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        layoutParams.leftToLeft = LayoutParams.PARENT_ID
        layoutParams.rightToRight = LayoutParams.PARENT_ID
        layoutParams.topToTop = LayoutParams.PARENT_ID
        layoutParams.bottomToBottom = LayoutParams.PARENT_ID
        addView(inputTextView, layoutParams)
    }

    private fun addViewPoint() {
        if (viewPoint == null) {
            viewPoint = View(context)
            viewPoint?.background = drawable(R.drawable.shape_input_password_point_view)
            viewPoint?.visibility = GONE
        }
        val layoutParams = LayoutParams(8.dp2px().toInt(), 8.dp2px().toInt())
        layoutParams.leftToLeft = LayoutParams.PARENT_ID
        layoutParams.rightToRight = LayoutParams.PARENT_ID
        layoutParams.topToTop = LayoutParams.PARENT_ID
        layoutParams.bottomToBottom = LayoutParams.PARENT_ID
        addView(viewPoint, layoutParams)
    }


    private fun addUnderLineView() {
        val underLineView = View(context)
        underLineView.setBackgroundColor(borderColor)
        underLineView.visibility = VISIBLE
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 1.dp2px().toInt())
        layoutParams.leftToLeft = LayoutParams.PARENT_ID
        layoutParams.rightToRight = LayoutParams.PARENT_ID
        layoutParams.bottomToBottom = LayoutParams.PARENT_ID
        layoutParams.marginStart = 10.dp2px().toInt()
        layoutParams.marginEnd = 10.dp2px().toInt()
        addView(underLineView, layoutParams)
    }


    private fun addCursorView() {
        if (cursorView == null) {
            cursorView = View(context)
            cursorView?.setBackgroundColor(cursorColor)
            cursorView?.visibility = GONE
        }
        val layoutParams = LayoutParams(1.dp2px().toInt(), LayoutParams.MATCH_PARENT)
        layoutParams.leftToLeft = LayoutParams.PARENT_ID
        layoutParams.rightToRight = LayoutParams.PARENT_ID
        layoutParams.topToTop = LayoutParams.PARENT_ID
        layoutParams.bottomToBottom = LayoutParams.PARENT_ID
        layoutParams.topMargin = 5.dp2px().toInt()
        layoutParams.bottomMargin = 5.dp2px().toInt()
        addView(cursorView, layoutParams)
    }

    /**
     * 控制输入的显示与隐藏
     */
    fun controlInputText(curIndex: Int, inputText: String?) {
        if (inputText.isNullOrEmpty()) {
            if (isHideInput) {
                this.viewPoint?.visibility = GONE
            } else {
                this.inputTextView?.visibility = GONE
            }
        } else {
            if (curIndex < inputText.length) {
                if (isHideInput) {
                    this.viewPoint?.visibility = VISIBLE
                } else {
                    this.inputTextView?.visibility = VISIBLE
                    inputTextView?.text = inputText[curIndex].toString()
                }
            } else {
                if (isHideInput) {
                    this.viewPoint?.visibility = GONE
                } else {
                    this.inputTextView?.visibility = GONE
                }
            }
        }
    }


    private var valueAnimator: ValueAnimator? = null

    /**
     * 控制游标的显示与隐藏
     */
    fun controlCursorVisible(isVisible: Boolean) {
        if (isVisible) {
            valueAnimator?.cancel()
            valueAnimator = ValueAnimator.ofInt(0, 1).apply {
                duration = 480
                repeatMode = ValueAnimator.RESTART
                repeatCount = ValueAnimator.INFINITE
                addListener(object : SimpleAnimatorListener() {
                    override fun onAnimationStart(animation: Animator?) {
                        cursorView?.visibility = VISIBLE
                    }

                    override fun onAnimationRepeat(animation: Animator?) {
                        if (cursorView?.visibility == VISIBLE) {
                            cursorView?.visibility = GONE
                        } else {
                            cursorView?.visibility = VISIBLE
                        }
                    }
                })
                start()
            }
        } else {
            valueAnimator?.cancel()
            cursorView?.visibility = GONE
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        valueAnimator?.cancel()
    }

    fun build(): PasswordItemView {
        if (isHideInput) addViewPoint() else addInputTextView()
        if (borderMode == BORDER_MODE_FRAME)
            this.background = GradientDrawable().apply {
                setStroke(borderWidth / 2, borderColor)
            } else if (borderMode == BORDER_MODE_UNDERLINE)
            addUnderLineView()
        if (isOpenCursor) addCursorView()
        return this
    }
}