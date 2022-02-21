package com.zhuorui.securities.base2app.ui.fragment

import base2app.rxbus.IRxBusEvent
import base2app.rxbus.RxBus

/**
 * @date 2020/5/25 16:49
 * @desc
 */
open class ZREventPresenter<V : ZRView> : ZRPresenter<V>(), IRxBusEvent {

    override fun onCreat() {
        super.onCreat()
        registerRxBus()
    }


    override fun registerRxBus() {
        val rxBus = RxBus.getDefault()
        val registered = rxBus.isRegistered(this)
        if (!registered) {
            rxBus.register(this)
        }
    }

    override fun unregisterRxBus() {
        val rxBus = RxBus.getDefault()
        val registered = rxBus.isRegistered(this)
        if (registered) {
            rxBus.unregister(this)
        }
    }

    override fun onDestory() {
        super.onDestory()
        unregisterRxBus()
    }
}