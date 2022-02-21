package com.zhuorui.commonwidget.tab

import android.content.Context
import android.content.res.Resources
import android.widget.LinearLayout
import androidx.annotation.Px
import base2app.ex.*
import com.zhuorui.commonwidget.R
import com.zhuorui.securities.base2app.ex.*
import com.zhuorui.securties.skin.view.ZRSkinAble
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

/**
 *    date   : 2021/1/14 18:21
 *    desc   : MagicIndicator tab button 样式 itemView
 */
class NavigatorButtonView : ColorTransitionPagerTitleView, ZRSkinAble {

    private var isHasChecked: Boolean? = null
    @Px
    var leftMargin = -1
    @Px
    var rightMargin = -1

    constructor(context: Context) : super(context) {
        setPadding(5f.dp2px().toInt(), 0, 5f.dp2px().toInt(), 0)
        textSize = 12f
        applyUIMode(resources)
    }

    override fun applyUIMode(resources: Resources?) {
        background = drawable(R.drawable.selector_radio_btn_bg)
        normalColor = color(R.color.main_label_unselected_text_color)
        selectedColor = color(R.color.label_selected_text_color)
        isHasChecked = null
        if (isSelected) {
            onSelected(0, 0)
        } else {
            onDeselected(0, 0)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        unregistSkin()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        skin {
            applyUIMode(it)
        }
        minWidth = 62f.dp2px().toInt()
        layoutParams?.let {
            if (it is LinearLayout.LayoutParams) {
                it.width = LinearLayout.LayoutParams.WRAP_CONTENT
                it.height = 28f.dp2px().toInt()
                it.leftMargin = if (leftMargin == -1) 5f.dp2px().toInt() else leftMargin
                it.rightMargin = if (rightMargin == -1) 5f.dp2px().toInt() else rightMargin
            }
        }
    }

    override fun onSelected(index: Int, totalCount: Int) {
        super.onSelected(index, totalCount)
        if (isHasChecked == null || isHasChecked == false) {
            isHasChecked = true
            isSelected = true
            setTextColor(mSelectedColor)
            sansSerifMedium()
        }
    }

    override fun onDeselected(index: Int, totalCount: Int) {
        super.onDeselected(index, totalCount)
        if (isHasChecked == null || isHasChecked == true) {
            isHasChecked = false
            isSelected = false
            setTextColor(mNormalColor)
            default()
        }
    }

}