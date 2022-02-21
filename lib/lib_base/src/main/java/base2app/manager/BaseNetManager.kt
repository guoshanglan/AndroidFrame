package base2app.manager

import base2app.ex.text
import base2app.rxbus.EventThread
import base2app.rxbus.RxSubscribe
import base2app.network.BaseResponse
import base2app.network.ErrorResponse
import base2app.util.ToastUtil
import com.example.lib_base.R

/**
 * 类描述：BaseManager针对有网络请求的基类管理类
 */
open class BaseNetManager : AbsEventManager() {

    /**
     * 请求失败处理
     *
     * @param vo ErrorResponse
     */
    protected open fun baseError(vo: ErrorResponse) {
        ToastUtil.instance.toast(
            if (vo.isNetworkBroken) text(R.string.network_anomaly) else vo.msg
        )
    }

    /**
     * 请求成功处理；默认Response为BaseResponse
     *
     * @param vo BaseResponse
     */
    protected open fun baseResponse(vo: BaseResponse) {

    }

    @RxSubscribe(observeOnThread = EventThread.MAIN)
    fun onErrorVo(vo: ErrorResponse) {
        this.baseError(vo)
    }

    @RxSubscribe(observeOnThread = EventThread.MAIN)
    fun onBaseVo(vo: BaseResponse) {
        if (!vo.isSuccess()) return
        this.baseResponse(vo)
    }
}
