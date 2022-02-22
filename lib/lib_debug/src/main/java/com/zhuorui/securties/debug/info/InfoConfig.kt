package com.zhuorui.securties.debug.info

import android.content.Context
import android.content.Intent
import com.zhuorui.securties.debug.IDebugKit
import com.zhuorui.securties.debug.R

/**
 * InfoConfig
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  17:09
 */
class InfoConfig:IDebugKit {
    override val icon: Int
        get() = R.drawable.info
    override val name: Int
        get() = R.string.info_config

    override fun onAppInit(context: Context?) {
    }

    override fun onClick(context: Context?) {
        val intent = Intent(context, ZRInfoConfigActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context?.startActivity(intent)
    }
}