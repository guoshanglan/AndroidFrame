package base2app.network

import base2app.ex.text
import base2app.util.ToastUtil
import com.example.lib_base.BuildConfig
import com.example.lib_base.R
import io.reactivex.functions.Consumer

/**
 *    author : Pengxianglin
 *    e-mail : peng_xianglin@163.com
 *    date   : 2019-06-19 17:03
 *    desc   : 抽象的一个网络请求失败的Consumer
 */
open class NetErrorConsumer<T : Throwable> : Consumer<T> {

    final override fun accept(t: T) {
        // 处理失败 debug模式才抛出异常
        if (BuildConfig.DEBUG && Network.isDebugNetWorkThrow(t)) {
            throw t
        }
        // 网络错误
        val msg = subAccept(t)
        onError(msg ?: text(R.string.network_anomaly))
    }

    /**
     * 子类可以重写这个方法，处理特殊报错
     */
    open fun subAccept(t: T): String? {
        return null
    }

    open fun onError(msg: String) {
        ToastUtil.instance.toast(msg)
    }
}