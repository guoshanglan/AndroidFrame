package com.zhuorui.commonwidget

import android.graphics.Rect
import android.graphics.drawable.StateListDrawable
import android.os.Build

/**
 *    date   : 2020/5/19 12:14
 *    desc   :
 */
class MenuPopBackgroundDrawable() : StateListDrawable() {

    private var enabledDrawable: ArrowDrawable? = null
    private var aboveDrawable: ArrowDrawable? = null
    private var mAbove = false
    private var isHasAutoAbove = true
    private val aboveAnchorStateSet = intArrayOf(android.R.attr.state_above_anchor)

    fun setColor(color: Int, shadowColor: Int) {
        aboveDrawable?.setColor(color, shadowColor)
        enabledDrawable?.setColor(color, shadowColor)
    }

    fun setAbove(above: Boolean) {
        isHasAutoAbove = false
        mAbove = above
        onStateChange(state)
    }

    init {
        aboveDrawable = ArrowDrawable().also {
            it.isDown = true
        }
        enabledDrawable = ArrowDrawable()
        addState(aboveAnchorStateSet, aboveDrawable)
        addState(intArrayOf(android.R.attr.state_enabled), enabledDrawable)
    }

    override fun getPadding(padding: Rect): Boolean {
        return if (mAbove) {
            aboveDrawable?.getPadding(padding) ?: false
        } else {
            enabledDrawable?.getPadding(padding) ?: false
        }
    }

    override fun onStateChange(stateSet: IntArray?): Boolean {
        val chang = super.onStateChange(stateSet)
        if (isHasAutoAbove) {
            val ba = state.indexOf(aboveAnchorStateSet[0]) != -1
            if (ba != mAbove) {
                mAbove = ba
                selectDrawable(if (ba) 0 else 1)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    onLayoutDirectionChanged(layoutDirection)
                }
                return true
            }
        } else {
            selectDrawable(0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                onLayoutDirectionChanged(layoutDirection)
            }
            return true
        }
        return chang
    }

    fun setGravity(gravity: Int) {
        enabledDrawable?.setGravity(gravity)
        aboveDrawable?.setGravity(gravity)
    }

    fun setArrowXOffset(xOffset: Int, arrowAnchorWidth: Int) {
        enabledDrawable?.setArrowXOffset(xOffset, arrowAnchorWidth)
        aboveDrawable?.setArrowXOffset(xOffset, arrowAnchorWidth)
    }

    fun setScroll(scroll: Boolean) {
        enabledDrawable?.setScroll(scroll)
        aboveDrawable?.setScroll(scroll)
    }

}