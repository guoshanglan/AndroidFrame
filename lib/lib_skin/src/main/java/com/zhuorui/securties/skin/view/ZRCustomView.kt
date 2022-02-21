package com.zhuorui.securties.skin.view

import android.content.res.Resources
import android.view.View
import java.lang.ref.WeakReference

/**
 * ZRCustomView 类或者接口名称
 * @company 深圳市卓锐网络科技有限公司
 * @description 功能描述
 * @date 2020/12/10
 */
class ZRCustomView(view: View, var skin: (Resources) -> Unit) :
    ZRBaseSkinView<View?>(view, null) {

    override fun applyUIMode(resources: Resources?) {
        resources?.let { res ->
            viewRef.get()?.let {
                skin(res)
            }
        }
    }
}