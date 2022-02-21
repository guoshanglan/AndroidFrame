package com.zhuorui.securities.base2app.ui.fragment

import base2app.network.BaseResponse
import base2app.network.Network
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable

/**
 * @author liuxueyun
 * @email hueylauu@163.com
 * @date 2020/5/25 16:50
 * @desc
 */
open class ZRNetPresenter<V : ZRView> : ZREventPresenter<V>() {

    /**
     * 管理请求Disposable队列
     */
    protected var disposable: CompositeDisposable? = null
        get() {
            if (field == null) {
                field = CompositeDisposable()
            }
            return field
        }

    /**
     * 发起请求，并自动加入disposable队列
     */
    protected fun <T : BaseResponse> subscribe(
        observable: Observable<T>,
        callback: Network.SubscribeCallback<T>?,
    ) {
        Network.subscribe(observable, callback).let {
            disposable?.add(it)
        }
    }

    /**
     * 发起请求，并自动加入disposable队列
     */
    protected fun <T : BaseResponse> subscribe(
        observable: Observable<T>,
        requestScheduler: Scheduler,
        responseScheduler: Scheduler,
        callback: Network.SubscribeCallback<T>?,
    ) {
        Network.subscribe(observable, requestScheduler, responseScheduler, callback).let {
            disposable?.add(it)
        }
    }

    /**
     * 针对事务取消时需要特殊处理的回调方法
     */
    protected open fun onDispose() {}

    protected fun clearAllDisposable() {
        disposable?.clear()
    }

    override fun onDestory() {
        super.onDestory()
        if (disposable?.isDisposed == false) {
            onDispose()
        }
        disposable?.clear()
    }
}