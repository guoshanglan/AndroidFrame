package com.zhuorui.securties.debug

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import base2app.ex.dp2px


/**
 * DebugDelegateHost
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  14:22
 */
class DebugDelegateHost : Application.ActivityLifecycleCallbacks {

    private inner class StackViewTouchListener constructor(
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

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityStarted(activity: Activity) {
        val root = activity.findViewById<View>(android.R.id.content)
        if (root is FrameLayout && root.findViewById<View>(R.id.debug_view_id) == null) {
            val debugView = ImageView(activity)
            debugView.id = R.id.debug_view_id
            debugView.setImageResource(R.drawable.debug_icon)
            val params = FrameLayout.LayoutParams(
                40.dp2px().toInt(),
                40.dp2px().toInt()
            )
            params.gravity = Gravity.END
            params.topMargin = (18.dp2px() * 10).toInt()
            params.rightMargin = 18.dp2px().toInt()
            debugView.layoutParams = params
            root.addView(debugView)
            debugView.setOnTouchListener(StackViewTouchListener(debugView, (18.dp2px() / 4).toInt()))
            debugView.setOnClickListener {
                DebugConfigListProvider().attach(root)
            }
        }
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}