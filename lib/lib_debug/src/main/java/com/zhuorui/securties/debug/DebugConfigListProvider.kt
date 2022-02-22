package com.zhuorui.securties.debug

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zhuorui.securties.debug.crashcanary.CrashCanaryConfig
import com.zhuorui.securties.debug.develop.DevelopConfig
import com.zhuorui.securties.debug.floatview.FloatViewProvider
import com.zhuorui.securties.debug.fps.FpsConfig
import com.zhuorui.securties.debug.info.InfoConfig
import com.zhuorui.securties.debug.net.HttpConfig
import com.zhuorui.securties.debug.netreference.NetConfig
import com.zhuorui.securties.debug.profile.ProfileConfig
import com.zhuorui.securties.debug.skin.SkinConfig
import com.zhuorui.securties.debug.ue.UEConfig

/**
 * DebugConfigListActivity
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  15:32
 */
class DebugConfigListProvider : FloatViewProvider() {

    override val layout: Int
        get() = R.layout.debug_config_list_activity
    override val isKetBack: Boolean
        get() = true
    override val isTouchDelegate: Boolean
        get() = false

    override fun onAttach(root: ViewGroup, container: View?) {
        container?.apply {
            layoutParams.height = ViewGroup.MarginLayoutParams.MATCH_PARENT
            layoutParams.width = ViewGroup.MarginLayoutParams.MATCH_PARENT
        }
        container?.findViewById<View>(R.id.back)?.setOnClickListener {
            detached(root)
        }
        container?.findViewById<RecyclerView>(R.id.recyclerview)?.adapter =
            ConfigAdapter(this,root).apply {
                datas.add(UEConfig())
                datas.add(NetConfig())
                datas.add(InfoConfig())
                datas.add(FpsConfig())
                datas.add(ProfileConfig())
                datas.add(DevelopConfig())
                datas.add(HttpConfig)
                datas.add(CrashCanaryConfig())
            }
    }


}