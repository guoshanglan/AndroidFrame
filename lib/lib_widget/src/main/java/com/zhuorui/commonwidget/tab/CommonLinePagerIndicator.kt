package com.zhuorui.commonwidget.tab

import android.content.Context
import android.content.res.Resources
import androidx.annotation.ColorRes
import base2app.ex.color
import base2app.ex.dp2px
import base2app.ex.skin
import base2app.ex.unregistSkin
import com.zhuorui.commonwidget.R
import com.zhuorui.securties.skin.view.ZRSkinAble
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

/**

 *    date   : 2021/1/18 15:58
 *    desc   :
 */
class CommonLinePagerIndicator : LinePagerIndicator, ZRSkinAble {

    private var colorId: Int? = null

    init {
        lineHeight = 2f.dp2px()
        lineWidth = 20f.dp2px()
        roundRadius = 1f.dp2px()
    }

    constructor(context: Context?):super(context){
        applyUIMode(context?.resources)
    }

    constructor(context: Context?, @ColorRes colorId: Int):super(context) {
        this.colorId = colorId
        applyUIMode(context?.resources)
    }

    override fun applyUIMode(resources: Resources?) {
        setColors(color(colorId ?: R.color.brand_main_color))
        paint.color = color(colorId ?: R.color.brand_main_color)
        invalidate()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        skin { applyUIMode(it) }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unregistSkin()
    }

}