package com.zhuorui.securties.skin.view

import android.view.View
import java.lang.ref.WeakReference

/**
 * ZRBaseSkinView 类或者接口名称
 * @company 深圳市卓锐网络科技有限公司
 * @description 功能描述
 * @date 2020/12/10
 */
abstract class ZRBaseSkinView<T : View?>(
        mView: T,
        protected var mSkinAttrs: List<ZRSkinAttr?>?
) : ZRSkinAble {
    var viewRef: WeakReference<T> = WeakReference(mView)
}