package com.zhuorui.securties.skin.view

import android.content.res.Resources
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.StyleableRes

/**
 * ZRSkinAttr
 * @company 深圳市卓锐网络科技有限公司
 * @description
 * @date 2020/12/11 10:08
 */
class ZRSkinAttr(
    var mZRSkinAttrType: ZRSkinAttrType? = null,
    /**
     * 属性名 例如：background、textColor）
     */
    var mAttrName: String? = null,
    /**
     * 属性类型 （例如：drawable、color）
     */
    @StyleableRes var mAttrType: Int? = null,
    /**
     * 资源Id （例如：123456）
     */
    @IdRes var mResId: Int,
    /**
     * 资源名称 （例如：ic_bg）
     */
    var mResName: String? = null
) {

    fun apply(view: View, resources: Resources?) {
        if (mZRSkinAttrType != null) mZRSkinAttrType!!.apply(view, this, resources!!)
    }
}