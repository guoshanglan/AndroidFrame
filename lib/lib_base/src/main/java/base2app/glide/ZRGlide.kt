package com.zhuorui.securities.base2app.glide

import android.view.View
import android.view.fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager

/**
 * 用于修改Glide.with()方法
 */
object ZRGlide {

    /**
     * 用于Glide.with()方法使用view所在的fragment作为参数，以保证Glide加载生命周期
     */
    fun with(v: View): RequestManager {
        v.fragment?.let { f ->
            return Glide.with(f)
        } ?: kotlin.run {
            return Glide.with(v)
        }
    }
}