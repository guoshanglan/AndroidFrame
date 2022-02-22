package com.zhuorui.securties.debug.floatview

import android.view.*
import base2app.ex.dp2px
import base2app.util.StatusBarUtil

/**
 * SuspensionViewProvider
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  9:34
 */
abstract class FloatViewProvider {

    private var container: View? = null

    protected abstract val layout: Int

    protected abstract val isKetBack: Boolean

    protected abstract val isTouchDelegate: Boolean

    fun attach(root: ViewGroup) {
        if (container == null) {
            container = LayoutInflater.from(root.context).inflate(layout, root, false).apply {
                layoutParams.height = ViewGroup.MarginLayoutParams.WRAP_CONTENT
                layoutParams.width = ViewGroup.MarginLayoutParams.WRAP_CONTENT
                val barHeight = StatusBarUtil.getStatusBarHeight(context)
                (layoutParams as ViewGroup.MarginLayoutParams ).topMargin = barHeight
                if (isKetBack)
                    setOnKeyListener { _, keyCode, _ ->
                        //监听返回
                        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
                            if (container?.parent != null) {
                                detached(root)
                                return@setOnKeyListener true
                            }
                        }
                        return@setOnKeyListener false
                    }

                if (isTouchDelegate)
                    setOnTouchListener(StackViewTouchListener(this, (18.dp2px() / 4).toInt()))
                isFocusable = true
                isFocusableInTouchMode = true
                requestFocus()
            }
        }
        root.addView(container)
        onAttach(root,container)
    }

    protected abstract fun onAttach(root: ViewGroup,container: View?)

    open fun detached(root: ViewGroup) {
        root.removeView(container)
        root.requestLayout()
    }


    class StackViewTouchListener constructor(
        private val debugView: View,
        private val clickLimitValue: Int
    ) : View.OnTouchListener {
        private var dX = 0f
        private var dY = 0f
        private var downX = 0f
        private var downY = 0f
        private var isClickState = false
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val X = event.rawX
            val Y = event.rawY
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isClickState = true
                    downX = X
                    downY = Y
                    dX = debugView.x - event.rawX
                    dY = debugView.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> if (Math.abs(X - downX) < clickLimitValue && Math.abs(Y - downY) < clickLimitValue && isClickState) {
                    isClickState = true
                } else {
                    isClickState = false
                    debugView.x = event.rawX + dX
                    debugView.y = event.rawY + dY
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> if (X - downX < clickLimitValue && isClickState) {
                    debugView.performClick()
                }
                else -> return false
            }
            return true
        }
    }
}