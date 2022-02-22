package com.zhuorui.securties.debug.develop

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.provider.Settings
import com.zhuorui.securties.debug.IDebugKit
import com.zhuorui.securties.debug.R
import java.lang.Exception

/**
 * DevelopConfig
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  9:34
 */
class DevelopConfig : IDebugKit {
    override val icon: Int
        get() = R.drawable.developer_options

    override val name: Int
        get() = R.string.develop_config

    override fun onAppInit(context: Context?) {}

    override fun onClick(context: Context?) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context?.startActivity(intent)
        } catch (e: Exception) {
            try {
                val componentName = ComponentName(
                    "com.android.settings",
                    "com.android.settings.DevelopmentSettings"
                )
                val intent = Intent()
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.component = componentName
                intent.action = "android.intent.action.View"
                context?.startActivity(intent)
            } catch (e1: Exception) {
                try {
                    //部分小米手机采用这种方式跳转
                    val intent = Intent("com.android.settings.APPLICATION_DEVELOPMENT_SETTINGS")
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context?.startActivity(intent)
                } catch (e2: Exception) {
                    e2.printStackTrace()
                }
            }
        }
    }

}