package com.zhuorui.commonwidget.tab

import android.content.Context
import android.view.Gravity
import base2app.ex.text
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView

/**
 * SingleNavifatorAdapter
 * @descraption
 * @time 13 10:43
 */
open class SingleNavigatorAdapter(private val mMagicIndicator: MagicIndicator, val titles: Array<String>) : CommonNavigatorAdapter() {

    var action: ((index: Int) -> Unit)? = null

    companion object {
        fun getSingleNavigatorAdapter(mMagicIndicator: MagicIndicator,titleIds: Array<Int>): SingleNavigatorAdapter {
            return SingleNavigatorAdapter(mMagicIndicator,getStrings(titleIds))
        }

        private fun getStrings(titleIds: Array<Int>): Array<String> {
            return Array(titleIds.size){
                text(titleIds[it])
            }
        }
    }

    override fun getCount(): Int {
        return titles.size
    }

    override fun getTitleView(context: Context?, index: Int): IPagerTitleView {
        return TabLabelView(context!!).apply {
            text = titles[index]
            gravity = Gravity.CENTER
            setOnClickListener {
                action?.invoke(index)
                this.isChecked = true
                mMagicIndicator.onPageSelected(index)
                mMagicIndicator.onPageScrolled(index, 0.0F, 0)
            }
        }
    }

    override fun getIndicator(context: Context?): IPagerIndicator? {
        return null
    }

    fun onTabSelected(action: (index: Int) -> Unit) {
        this.action = action
    }
}