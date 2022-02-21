package com.zhuorui.securities.base2app.util

import android.content.Context
import androidx.lifecycle.LifecycleObserver

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/8/6 15:01
 *    desc   : APP生命周期
 */
interface IAppLifecycle: LifecycleObserver {

    /**
     * 绑定一上下文
     */
    fun appAttachBaseContext(context: Context?){

    }

    /**
     * qpp置于前台
     */
    fun app2Foreground(){

    }

    /**
     * app置于后台
     */
    fun app2Background(){

    }

    /**
     * app退出
     */
    fun appClose(){

    }
}