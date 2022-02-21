package com.zhuorui.commonwidget.tab

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.view.Gravity
import androidx.annotation.ColorRes
import base2app.ex.*
import com.zhuorui.commonwidget.R
import com.zhuorui.securities.base2app.ex.*
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IMeasurablePagerTitleView

/**
 * TabLabelView
 * @descraption
 * @time 12 17:57
 */
@SuppressLint("ViewConstructor")
class TabLabelView(context: Context) : androidx.appcompat.widget.AppCompatCheckBox(context), IMeasurablePagerTitleView {

    var selectedSize: Float = 18f
        set(value) {
            if (isChecked) textSize = value
            field = value
        }

    var defaultTextSize: Float = 16f
        set(value) {
            if (!isChecked) textSize = value
            field = value
        }

    @ColorRes
    var mSelector: Int = R.color.selector_tab_text
        set(value) {
            setTextColor(colorState(value))
            field = value
        }

    var isCheckedScale: Boolean = true
        set(value) {
            if (!value)
                textSize = defaultTextSize
            field = value
        }

    private var isHasChecked: Boolean? = null

    init {
        gravity = Gravity.CENTER
        val padding = 10.dp2px().toInt()
        setPadding(padding, 0, padding, 0)
        setSingleLine()
        ellipsize = TextUtils.TruncateAt.END
        setTextColor(colorState(mSelector))
        buttonDrawable = null

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        skin {
            setTextColor(colorState(mSelector))
            if (isHasChecked == true) {
                blod()
            } else {
                default()
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unregistSkin()
    }


    override fun onSelected(index: Int, totalCount: Int) {
        if (isHasChecked == null || isHasChecked == false) {
            isHasChecked = true
            isChecked = true
            blod()
            if (isCheckedScale) {
                textSize = selectedSize
            }
        }
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        if (isHasChecked == null || isHasChecked == true) {
            isHasChecked = false
            isChecked = false
            default()
            if (isCheckedScale) {
                textSize = defaultTextSize
            }
        }
    }

    override fun onLeave(index: Int, totalCount: Int, leavePercent: Float, leftToRight: Boolean) {}

    override fun onEnter(index: Int, totalCount: Int, enterPercent: Float, leftToRight: Boolean) {}

    override fun getContentLeft(): Int {
        val bound = Rect()
        paint.getTextBounds(text.toString(), 0, text.length, bound)
        val contentWidth = bound.width()
        return left + width / 2 - contentWidth / 2
    }

    override fun getContentTop(): Int {
        val metrics = paint.fontMetrics
        val contentHeight = metrics.bottom - metrics.top
        return (height / 2 - contentHeight / 2).toInt()
    }

    override fun getContentRight(): Int {
        val bound = Rect()
        paint.getTextBounds(text.toString(), 0, text.length, bound)
        val contentWidth = bound.width()
        return left + width / 2 + contentWidth / 2
    }

    override fun getContentBottom(): Int {
        val metrics = paint.fontMetrics
        val contentHeight = metrics.bottom - metrics.top
        return (height / 2 + contentHeight / 2).toInt()
    }

}