package base2app.manager

import base2app.rxbus.RxBus
import base2app.rxbus.IRxBusEvent

/**
 * 类描述：抽象的集成RxBus的Manager
 */
abstract class AbsEventManager : IRxBusEvent {

    var isDestroy: Boolean = false
        private set

    init {
        isDestroy = false
        registerRxBus()
    }

    override fun registerRxBus() {
        val registered = RxBus.getDefault().isRegistered(this)
        if (!registered) {
            RxBus.getDefault().register(this)
        }
    }

    override fun unregisterRxBus() {
        val registered = RxBus.getDefault().isRegistered(this)
        if (registered) {
            RxBus.getDefault().unregister(this)
        }
    }


    open fun onDestroy() {
        isDestroy = true
        unregisterRxBus()
    }
}