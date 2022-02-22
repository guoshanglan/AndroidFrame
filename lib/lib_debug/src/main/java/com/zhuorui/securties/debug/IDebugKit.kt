package com.zhuorui.securties.debug

import android.content.Context

/**
 * IDebugKit
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  9:05
 */
interface IDebugKit {

    /**
     * 图标id
     */
    val icon: Int

    /**
     * 配置名id
     */
    val name: Int

    /**
     * 初始化过程
     */
    fun onAppInit(context: Context?)

    /**
     * 点击事件
     */
    fun onClick(context: Context?)
}