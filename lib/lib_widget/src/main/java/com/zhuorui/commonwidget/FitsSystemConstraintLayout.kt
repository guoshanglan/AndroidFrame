package com.zhuorui.commonwidget

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.WindowInsets
import androidx.constraintlayout.widget.ConstraintLayout

/**
 *    date   : 2020/8/21 08:27
 *    desc   : 解决fitsSystemWindows ，状态栏下移问题
 */
class FitsSystemConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    override fun fitSystemWindows(insets: Rect): Boolean {
        insets.top = 0
        insets.left = 0
        insets.right = 0
        return super.fitSystemWindows(insets)
    }

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets? {
        return super.onApplyWindowInsets(
            insets.replaceSystemWindowInsets(
                0,
                0,
                0,
                insets.systemWindowInsetBottom
            )
        )
    }

}