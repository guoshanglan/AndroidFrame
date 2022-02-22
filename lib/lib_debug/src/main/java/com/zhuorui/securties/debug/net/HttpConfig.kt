package com.zhuorui.securties.debug.net

import android.content.Context
import com.zhuorui.securties.debug.Background
import com.zhuorui.securties.debug.IDebugKit
import com.zhuorui.securties.debug.R

/**
 * HttpConfig
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  9:16
 */
object HttpConfig : IDebugKit {

    var mView: HttpFloatViewProvider? = null

    override val icon: Int
        get() = R.drawable.http
    override val name: Int
        get() = R.string.http_config

    override fun onAppInit(context: Context?) {
    }

    override fun onClick(context: Context?) {
        if (mView == null){
            mView = HttpFloatViewProvider()
        }
        Background.registerFloatViewProvider(mView!!)
    }
}