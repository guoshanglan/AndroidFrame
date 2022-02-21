package com.zhuorui.commonwidget.popwindow

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import base2app.BaseApplication
import base2app.ui.activity.AbsActivity
import java.lang.ref.WeakReference

/**
 * @author liuxueyun
 * @email hueylauu@163.com
 * @date 2021/8/5 14:37
 * @desc PopWindow与Fragment绑定生命周期帮助类
 */
object PopWindowHelper {

    fun bindFragmentByPop(popWindow: DimPopupWindow, context: Context) {
        bindFragment(WeakReference(popWindow), context)
    }

    fun bindFragmentByMenuPop(popWindow: MenuPopWindow<*>, context: Context) {
        bindFragment(WeakReference(popWindow), context)
    }

    /**
     * 与Fragment生命周期绑定，在Fragment处于onPause时关闭
     */
    private fun bindFragment(popWindow: WeakReference<Any>, context: Context) {
        val fragment = (context as AbsActivity).topFragment as Fragment
        fragment.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_PAUSE) {
                    val pop = popWindow.get()
                    if (pop != null && !BaseApplication.baseApplication.isInBackground && fragment.activity?.lifecycle?.currentState != Lifecycle.State.STARTED) {
                        when (pop) {
                            is DimPopupWindow -> {
                                pop.dismiss()
                            }
                            is MenuPopWindow<*> -> {
                                pop.dismiss()
                            }
                        }
                    }
                }
            }
        })
    }
}