package com.zhuorui.commonwidget

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import base2app.ex.dp2px
import base2app.ex.sansSerifMedium
import kotlin.math.abs

/**
 *    date   : 2020/12/23 11:18
 *    desc   : 消息气泡View
 */
class MessageBadgeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var mShowMessage: Boolean? = null

    private val guideline = View(context).apply {
        id = View.generateViewId()
        isSaveEnabled = false
        layoutParams = ConstraintLayout.LayoutParams(0, 0)
    }

    init {
        if (id == View.NO_ID) {
            id = View.generateViewId()
            isSaveEnabled = false
        }
        setTextColor(Color.WHITE)
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 8f)
        gravity = Gravity.CENTER
        setBackgroundResource(R.drawable.shape_badge_view_round_rect_bg)
        sansSerifMedium()
        //默认不显示
        visibility = View.GONE
    }

    fun setMessage(message: String?, showMessage: Boolean) {
        if (TextUtils.isEmpty(message)) {
            visibility = View.GONE
        } else {
            if (mShowMessage != showMessage) {
                mShowMessage = showMessage
                changeBadgeStyle()
            }
            visibility = View.VISIBLE
            text = if (showMessage) message else ""
        }
    }

    /**
     * 正数显示数字
     * 负数显示红点
     * 0 不显示
     */
    fun setMessage(num: Int?) {
        val message = num?.let {
            when {
                it == 0 -> null
                it >= 100 -> "99+"
                else -> abs(it).toString()
            }
        }
        setMessage(message, num ?: 0 > 0)
    }

    fun attachView(attach: View) {
        val attachParent = attach.parent
        if (attachParent !is ConstraintLayout) return
        attachParent.addView(guideline)
        attachParent.addView(this)
        val set = ConstraintSet()
        set.clone(attachParent)
        set.connect(
            guideline.id,
            ConstraintSet.TOP,
            attach.id,
            ConstraintSet.TOP,
            attach.paddingTop
        )
        set.connect(
            guideline.id,
            ConstraintSet.RIGHT,
            attach.id,
            ConstraintSet.RIGHT,
            attach.paddingRight
        )
        set.connect(id, ConstraintSet.LEFT, guideline.id, ConstraintSet.LEFT)
        set.connect(id, ConstraintSet.TOP, guideline.id, ConstraintSet.TOP)
        set.connect(id, ConstraintSet.RIGHT, guideline.id, ConstraintSet.RIGHT)
        set.connect(id, ConstraintSet.BOTTOM, guideline.id, ConstraintSet.BOTTOM)
        set.applyTo(attachParent)
        changeBadgeStyle()
    }

    private fun changeBadgeStyle() {
        val parentView = parent
        if (parentView !is ConstraintLayout) return
        if (mShowMessage == true) {
            this.layoutParams = layoutParams?.apply {
                width = ConstraintLayout.LayoutParams.WRAP_CONTENT
                height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            } ?: ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            minWidth = 19.dp2px().toInt()
            minHeight = 12.dp2px().toInt()
            val lrPadding = 3.dp2px().toInt()
            setPadding(lrPadding, 0, lrPadding, 0)
        } else {
            val wh = 6.dp2px().toInt()
            this.layoutParams = layoutParams?.apply {
                width = wh
                height = wh
            } ?: ConstraintLayout.LayoutParams(wh, wh)
            minWidth = 0
            minHeight = 0
            setPadding(0, 0, 0, 0)
        }
        minimumWidth = minWidth
        minimumHeight = minHeight
    }


}