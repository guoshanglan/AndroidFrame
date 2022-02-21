package com.example.myframe.view

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.widget.LinearLayout
import androidx.annotation.StringRes
import base2app.ex.dp2px

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/4/27 15:21
 *    desc   : 放大选中Icon Tab
 */
class ZoomIconTab(context: Context?, icon: Int, @StringRes titleResId: Int) :
    BottomBarTab(context, icon, titleResId) {

    private var lastAnim: ValueAnimator? = null

    override fun setSelected(selected: Boolean) {
        val oldSelected = isSelected
        super.setSelected(selected)
        if (oldSelected != selected) {
            layoutParams()
        }
    }

    override fun layoutParams() {
        lastAnim?.let {
            it.cancel()
        }
        if (isSelected) {
            mTvTitle.visibility = View.GONE
            val size = 36f.dp2px().toInt()
            if (mIcon.layoutParams == null) {
                mIcon.layoutParams = LinearLayout.LayoutParams(size, size)
            }
            val oldSize = mIcon.layoutParams.width
            if (size != oldSize) {
                val anim = ValueAnimator.ofFloat(oldSize.toFloat(), size.toFloat())
                anim.duration = 100
                anim.interpolator = AccelerateInterpolator()
                anim.addUpdateListener {
                    val size = (it.animatedValue as Float).toInt()
                    mIcon.layoutParams.width = size
                    mIcon.layoutParams.height = size
                    mIcon.requestLayout()
                }
                anim.start()
                lastAnim = anim
            }
        } else {
            mTvTitle.visibility = View.VISIBLE
            super.layoutParams()
        }
    }
}