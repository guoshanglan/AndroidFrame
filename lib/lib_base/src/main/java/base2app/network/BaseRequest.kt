package base2app.network

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import base2app.BaseApplication
import base2app.ex.logd
import base2app.util.SignUtil

/**
 * 请求的基类
 */
open class BaseRequest {

    /**
     * 所有接口的公共参数，客户端发起请求时客户端的当前时间戳
     */
    var timeStamp = System.currentTimeMillis()

    /**
     * android，ios客户端发起的所有post请求均需要签名校验
     * 生成规则：将所有的字段进行升序排列，再使用私钥进行加密
     */
    var sign: String? = null

    constructor()

    constructor(sign: Boolean) {
        if (sign) {
            generateSign()
        }
    }

    /**
     * 根据参数生成签名
     */
    protected fun generateSign() {
        try {
            // 拿到所有的参数进行升序排列
            val json = JSON.toJSONString(this, SerializerFeature.MapSortField)
            logd("Retrofit", "待签名：$json")
            // 根据新产生的json进行加密
            sign = SignUtil.createSHA1Sign(
                json,
                BaseApplication.baseApplication.config.privateKey()
            )
            logd("Retrofit", "签名后：$sign")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun toString(): String {
        return "BaseRequest(timeStamp=$timeStamp, sign=$sign')"
    }

    companion object {

        fun instance(): BaseRequest {
            return BaseRequest()
        }

    }


}
