package base2app.network

import base2app.ex.logd
import base2app.util.ToastUtil
import io.reactivex.functions.Consumer

/**
 *    author : Pengxianglin
 *    e-mail : peng_xianglin@163.com
 *    date   : 2019-06-19 18:07
 *    desc   : 抽象的一个网络请求成功的Consumer
 */
abstract class NetConsumer<T : BaseResponse> : Consumer<T> {

    final override fun accept(t: T) {
        logd("netWork", t.code + " " + t.msg)
        if (t.isSuccess()) {
            onResponse(t)
        } else {
            onFail(t)
        }
    }

    open fun onFail(response: T) {
        if ("000101" != response.code && "000102" != response.code) {
            ToastUtil.instance.toast(response.msg)
        }
    }

    abstract fun onResponse(response: T)
}