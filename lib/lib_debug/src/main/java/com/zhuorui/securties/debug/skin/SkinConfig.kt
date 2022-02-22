package com.zhuorui.securties.debug.skin

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import base2app.ex.logd
import com.zhuorui.securties.debug.IDebugKit
import com.zhuorui.securties.debug.R
import com.zhuorui.securties.skin.ZRSkinManager

/**
 * SkinConfig
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  9:13
 */
class SkinConfig:IDebugKit {
    override val icon: Int
        get() = R.drawable.pifu
    override val name: Int
        get() = R.string.skin_config

    override fun onAppInit(context: Context?) {
    }

    override fun onClick(context: Context?) {
        val mode = when(ZRSkinManager.instance.isNight()){
            true->
                AppCompatDelegate.MODE_NIGHT_NO
            else->
                AppCompatDelegate.MODE_NIGHT_YES
        }
        logd(ZRSkinManager.instance.isNight())
        ZRSkinManager.instance.applySkin(mode,true)
    }
}