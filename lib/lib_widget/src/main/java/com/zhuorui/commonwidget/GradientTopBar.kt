package com.zhuorui.commonwidget

import android.animation.ArgbEvaluator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import base2app.BaseApplication
import base2app.ex.color
import base2app.util.StatusBarUtil
import com.zhuorui.securties.skin.ZRSkinManager

/**
 * @date 2021/1/15 09:02
 * @desc 渐变TopBar
 */
class GradientTopBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ZhuoRuiTopBar(context, attrs, defStyleAttr) {

    /**
     * 渐变插值器
     */
    private val argbEvaluator = ArgbEvaluator()


    private var curFraction: Float = 0f

    private var rightDayIcon: Int = R.mipmap.ic_share_light

    private var rightNightIcon: Int = R.mipmap.ic_share_dark

    private var isLightStyle = false


    init {
        controlTopBarStyle(curFraction)
    }

    /**
     * 根据当前RecyclerView滑动的位置来更新TopBar图标和文字样式以及状态来样式
     */
    fun controlTopBarStyle(fraction: Float) {
        this.curFraction = fraction
        val isNight = ZRSkinManager.instance.isNight()
        updateTopBarBgColor(fraction)
        updateGradientTopBarStyle(fraction, isNight)
        statusBarStyleMode(true)
    }


    fun setRight1DayNightIcon(@DrawableRes dayIcon: Int, @DrawableRes nightIcon: Int): GradientTopBar {
        this.rightDayIcon = dayIcon
        this.rightNightIcon = nightIcon
        return this
    }


    fun statusBarStyleMode(isVisible: Boolean) {
        BaseApplication.baseApplication.topActivity?.let {
            if (isVisible) {
                if (curFraction < 1) {
                    //默认设置为白色风格
                    StatusBarUtil.StatusBarBrightnessModeReal(it)
                } else {
                    //恢复默认
                    StatusBarUtil.StatusBarBrightnessMode(it)
                }
            } else {
                //恢复默认
                StatusBarUtil.StatusBarBrightnessMode(it)
            }
        }
    }


    /**
     * 控件返回键图标
     */
    private fun updateGradientTopBarStyle(fraction: Float, isNight: Boolean) {
        if (fraction < 1 || isNight) {
            if (!isLightStyle) {
                isLightStyle = true
                if (backView is ImageView) (backView as ImageView).setImageResource(R.mipmap.ic_back_light)
                if (titleView is TextView) (titleView as TextView).setTextColor(Color.WHITE)
                if (rightViews.isNotEmpty()) {
                    val rightIconView = getRightView(0)
                    if (rightIconView is ImageView) rightIconView.setImageResource(rightDayIcon)
                }
                statusBarStyleMode(true)
            }
        } else {
            if (isLightStyle) {
                isLightStyle = false
                if (backView is ImageView) (backView as ImageView).setImageResource(R.mipmap.ic_back_dark)
                if (titleView is TextView) (titleView as TextView).setTextColor(color(R.color.dialog_title_text))
                if (rightViews.isNotEmpty()) {
                    val rightIconView = getRightView(0)
                    if (rightIconView is ImageView) rightIconView.setImageResource(rightNightIcon)
                }
                statusBarStyleMode(true)
            }
        }
    }


    /**
     * 控制TopBar背景色
     */
    private fun updateTopBarBgColor(fraction: Float) {
        val fractionColor =
            argbEvaluator.evaluate(fraction, Color.TRANSPARENT, color(R.color.main_background)) as Int
        setBackgroundColor(fractionColor)
    }


}