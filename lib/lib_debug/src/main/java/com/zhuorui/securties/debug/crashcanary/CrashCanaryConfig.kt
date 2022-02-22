package com.zhuorui.securties.debug.crashcanary

import android.content.Context
import android.content.Intent
import com.zhuorui.securties.debug.IDebugKit
import com.zhuorui.securties.debug.R
/**
 * @author xuzuoliang
 * @email micrason@163.com
 * @date 2022/1/20 15:34
 * @desc
 */
class CrashCanaryConfig: IDebugKit {

    override val icon: Int
        get() = R.drawable.crash_canary_icon

    override val name: Int
        get() = R.string.crash_canary_display_activity_label

    override fun onAppInit(context: Context?) {
    }

    override fun onClick(context: Context?) {
        val intent = Intent(context, CrashInfoActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context?.startActivity(intent)
    }
}