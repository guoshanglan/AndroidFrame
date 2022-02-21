package com.zhuorui.securities.base2app.ui.fragment

import android.content.Context
import android.view.View
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * @date 2020/5/25 16:47
 * @desc
 */
abstract class ZRPresenter<V : ZRView>: LifecycleEventObserver {

    protected var TAG: String = this.javaClass.simpleName

    protected var view: V? = null

    /**
     * 初始化
     */
    @CallSuper
    open fun init() {
    }

    fun isDestroy(): Boolean {
        return view == null
    }

    fun getContext(): Context? {
        val iView = view ?: return null
        return when (iView) {
            is Fragment -> iView.context
            is View -> iView.context
            is Context -> iView
            else -> null
        }
    }

    open fun bindView(view: V) {
        this.view = view
    }
    open fun onCreat() {
    }

    @CallSuper
    open fun onDestory() {
        this.view = null
    }

    open fun onPause() {
    }

    open fun onResume() {
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event){
            Lifecycle.Event.ON_CREATE->
                onCreat()
            Lifecycle.Event.ON_DESTROY->
                onDestory()
            Lifecycle.Event.ON_PAUSE->
                onPause()
            Lifecycle.Event.ON_RESUME->
                onResume()
            else -> {}
        }
    }


    fun getViewState(): Lifecycle.State? {
        return if (view == null){
            null
        }else{
            (view as LifecycleOwner).lifecycle.currentState
        }
    }

    fun isViewVisible(): Boolean {
        return getViewState() == Lifecycle.State.RESUMED
    }
}
