package com.zhuorui.securties.debug.ue

import android.content.Context
import com.zhuorui.securties.debug.IDebugKit
import me.ele.uetool.R
import me.ele.uetool.UETool

/**
 * author : Martin-huyuan
 * email : hy569835826@163.com
 * date : 2020/5/20
 * dest : UEToolKit
 */
class UEConfig : IDebugKit {
    override val icon: Int
        get() = R.drawable.uet_menu

    override val name: Int
        get() = R.string.uet_name

    override fun onAppInit(context: Context?) {}

    override fun onClick(context: Context?) {
        UETool.showUETMenu()
    }
}