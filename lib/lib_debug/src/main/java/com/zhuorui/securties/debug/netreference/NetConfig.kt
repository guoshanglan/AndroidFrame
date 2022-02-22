package com.zhuorui.securties.debug.netreference

import android.content.Context
import android.content.Intent
import com.zhuorui.securties.debug.IDebugKit
import com.zhuorui.securties.debug.R

/**
 * NetConfig
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  9:08
 */
class NetConfig : IDebugKit {

    override val icon: Int
        get() = R.drawable.networkprotection
    override val name: Int
        get() = R.string.net_config

    override fun onAppInit(context: Context?) {
    }

    override fun onClick(context: Context?) {
        val intent = Intent(context, ZRNetConfigActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context?.startActivity(intent)
    }

}