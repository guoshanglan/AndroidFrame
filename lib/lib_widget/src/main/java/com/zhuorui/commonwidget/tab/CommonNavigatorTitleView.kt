package com.zhuorui.commonwidget.tab

import android.content.Context
import android.content.res.Resources
import androidx.annotation.ColorRes
import base2app.ex.*
import com.zhuorui.commonwidget.R
import com.zhuorui.securities.base2app.ex.*
import com.zhuorui.securties.skin.view.ZRSkinAble
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

/**
 * date : 2020/9/14
 * dest : CommonNavigatorTitleView  横向指示器
 */
open class CommonNavigatorTitleView : ColorTransitionPagerTitleView, ZRSkinAble {

    private var normalColorId: Int? = null
    private var selectedColorId: Int? = null

    private var isHasChecked: Boolean? = null

    constructor(context: Context?):super(context){
        applyUIMode(context?.resources)
    }

    constructor(context: Context?, @ColorRes normalColorId: Int? = null, @ColorRes selectedColorId: Int? = null):super(context) {
        this.normalColorId = normalColorId
        this.selectedColorId = selectedColorId
        applyUIMode(context?.resources)
    }

    override fun applyUIMode(resources: Resources?) {
        normalColor = color(normalColorId ?: R.color.main_label_unselected_text_color)
        selectedColor = color(selectedColorId ?: R.color.main_content_text_color)
        isHasChecked = null
        if (isSelected) {
            onSelected(0,0)
        }else{
            onDeselected(0,0)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        skin { applyUIMode(it) }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unregistSkin()
    }

    override fun onSelected(index: Int, totalCount: Int) {
        super.onSelected(index, totalCount)
        if (isHasChecked == null || isHasChecked == false) {
            setTextColor(mSelectedColor)
            isHasChecked = true
            isSelected = true
            sansSerifMedium()
        }
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        super.onDeselected(index, totalCount)
        if (isHasChecked == null || isHasChecked == true) {
            isHasChecked = false
            setTextColor(mNormalColor)
            isSelected = false
            default()
        }
    }
}