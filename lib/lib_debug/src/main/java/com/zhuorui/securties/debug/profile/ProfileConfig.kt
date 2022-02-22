package com.zhuorui.securties.debug.profile

import android.content.Context
import com.zhuorui.securties.debug.Background
import com.zhuorui.securties.debug.IDebugKit
import com.zhuorui.securties.debug.R

/**
 * ProfileConfig
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  16:30
 */
class ProfileConfig : IDebugKit {

    var mView: ProfileFloatViewProvider? = null

    override val icon: Int
        get() = R.drawable.profile
    override val name: Int
        get() = R.string.profile_config

    override fun onAppInit(context: Context?) {
    }

    override fun onClick(context: Context?) {
//        ProfileMonitor.startProfile(!ProfileMonitor.mIsStarted)
        if (mView == null){
            mView = ProfileFloatViewProvider()
        }
        Background.registerFloatViewProvider(mView!!)
    }
}