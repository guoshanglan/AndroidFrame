package base2app.network

import base2app.ex.text
import com.example.lib_base.R

/**
 * 默认响应
 */
open class BaseResponse {

    var request: BaseRequest? = null
    var msg: String = ""
        get() {
            return if (field.isEmpty() && code == NETWORK_BROKEN_STATUS) getNetworkBrokenText() else field
        }

    // Response只返回code并不返回status,故而code即表示http的请求状态又表示接口业务是否成功
    var code: String = ""
        get() {
            return if (field.isEmpty()) NETWORK_BROKEN_STATUS else field
        }

    open fun isSuccess(): Boolean {
        return isSuccess(code)
    }

    override fun toString(): String {
        return this.javaClass.simpleName + ": message = " + msg + ", code = " + code
    }

    companion object {
        const val RES_OK = "000000"
        const val NETWORK_BROKEN_STATUS = "1000"

        fun isSuccess(code:String?):Boolean{
            return code == RES_OK
        }

        fun getNetworkBrokenText(): String {
            return text(R.string.network_anomaly)
        }
    }
}
