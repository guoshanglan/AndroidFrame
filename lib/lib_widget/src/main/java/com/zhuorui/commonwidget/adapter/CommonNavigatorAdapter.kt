package com.zhuorui.commonwidget.adapter

import android.content.Context
import com.zhuorui.commonwidget.R
import com.zhuorui.commonwidget.tab.CommonLinePagerIndicator
import com.zhuorui.commonwidget.tab.CommonNavigatorTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator

/**
 * @date 2020/4/21 15:35
 * @desc
 */
class SuperCommonNavigatorAdapter(private val onSwitchTabItem: (Int) -> Unit, private val tabArray: Array<String>) : CommonNavigatorAdapter() {

    override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
        val colorTransitionPagerTitleView = CommonNavigatorTitleView(context,R.color.description_text_color,R.color.main_content_text_color)
//        colorTransitionPagerTitleView.normalColor = color(R.color.description_text_color)
//        colorTransitionPagerTitleView.selectedColor = color(R.color.main_content_text_color)
        colorTransitionPagerTitleView.text = tabArray[index]
        colorTransitionPagerTitleView.textSize = 16f
        colorTransitionPagerTitleView.setOnClickListener { onSwitchTabItem.invoke(index) }
        return colorTransitionPagerTitleView
    }

    override fun getCount(): Int {
        return tabArray.size
    }

    override fun getIndicator(context: Context?): IPagerIndicator {
        val indicator = CommonLinePagerIndicator(context)
        indicator.mode = LinePagerIndicator.MODE_EXACTLY
//        indicator.setColors(color(R.color.brand_main_color))
//        indicator.lineHeight = 2.dp2px()
//        indicator.lineWidth = 20.dp2px()
//        indicator.roundRadius = 1.dp2px()
        return indicator
    }
}