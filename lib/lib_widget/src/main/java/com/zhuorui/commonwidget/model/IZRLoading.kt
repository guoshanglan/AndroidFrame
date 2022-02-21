package com.zhuorui.commonwidget.model


/**
 * @date 2021/11/17 10:40
 * @desc 控制显示和隐藏ProgressDialog的接口
 */
interface IZRLoading {
    /**
     * 显示
     */
    fun showLoading(msg: String? = null)

    /**
     * 隐藏
     */
    fun closeLoading()
}