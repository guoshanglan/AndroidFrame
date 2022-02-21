package com.zhuorui.commonwidget.adapter

import com.zhuorui.commonwidget.ZRMultiStateFrame


/**
 *    date   : 2020/6/12 11:51
 *    desc   :
 */
interface IZRStateAdapter {


    /**
     * 设置状态
     */
    fun setFrame(frame: ZRMultiStateFrame)

    /**
     *
     * 设置stateView最小高度
     */
    fun setStateMinimumHeight(minimunHeight: Int)

    /**
     *
     * 设置stateView最小高度
     */
    fun getStateMinimumHeight():Int


}