package com.zhuorui.commonwidget

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.view.inputmethod.InputMethodManager


class AndroidBug5497Workaround constructor(view: ViewGroup) : ViewTreeObserver.OnGlobalLayoutListener {

    private var mChildOfContent: View?
    private var usableHeightPrevious: Int = 0
    private var frameLayoutParams: ViewGroup.LayoutParams? = null

    init {
        mChildOfContent = view
        mChildOfContent?.let {
            frameLayoutParams = mChildOfContent?.layoutParams as FrameLayout.LayoutParams
        }

    }

    fun addOnGlobalLayoutListener() {
        mChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener(this)

    }

    fun removeOnGlobalLayoutListener() {
        mChildOfContent?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
        mChildOfContent = null
    }

    fun onPause() {
        closeInputMethod()
    }

    private fun closeInputMethod() {
        val context = mChildOfContent?.context
        context?.let {
            if (context is Activity) {
                val mv = context.window.peekDecorView()
                if (mv != null) {
                    val manager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    manager.hideSoftInputFromWindow(mv.windowToken, 0)
                }
            }
        }
    }

    override fun onGlobalLayout() {
        val usableHeightNow = computeUsableHeight()
        if (usableHeightNow != usableHeightPrevious) {
            val usableHeightSansKeyboard = mChildOfContent?.rootView?.height ?: 0
            val heightDifference = usableHeightSansKeyboard - usableHeightNow
            if (heightDifference > usableHeightSansKeyboard / 4) {
                // keyboard probably just became visible
                frameLayoutParams?.height = usableHeightSansKeyboard - heightDifference
            } else {
                // keyboard probably just became hidden
                frameLayoutParams?.height = usableHeightSansKeyboard
            }
            mChildOfContent?.requestLayout()
            usableHeightPrevious = usableHeightNow
        }
    }

    private fun computeUsableHeight(): Int {
        val r = Rect()
        mChildOfContent?.getWindowVisibleDisplayFrame(r)
//        return r.bottom - r.top// 全屏模式下： return r.bottom
        return r.bottom// 全屏模式下： return r.bottom
    }

}
