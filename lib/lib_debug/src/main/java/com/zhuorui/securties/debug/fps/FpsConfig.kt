package com.zhuorui.securties.debug.fps

import android.content.Context
import android.content.Intent
import com.zhuorui.securties.debug.IDebugKit
import com.zhuorui.securties.debug.R
import com.zhuorui.securties.debug.fps.view.JankStackInfosActivity

/**
 * FpsConfig
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  16:30
 */
class FpsConfig : IDebugKit {
    override val icon: Int
        get() = R.drawable.trace
    override val name: Int
        get() = R.string.fps_config

    override fun onAppInit(context: Context?) {
    }

    override fun onClick(context: Context?) {
        val intent = Intent(context, JankStackInfosActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context?.startActivity(intent)
    }
}