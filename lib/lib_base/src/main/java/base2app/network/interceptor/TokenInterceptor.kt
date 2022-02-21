package base2app.network.interceptor

import android.text.TextUtils
import base2app.util.JsonUtil
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.nio.charset.Charset

/**
 * date   : 2019-05-20 14:13
 * desc   : 在网络请求拦截器中判断token是否失效
 */
class TokenInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // 获取网络返回Response
        val response = chain.proceed(request)
        val responseBody = response.body
        val source = responseBody!!.source()
        source.request(java.lang.Long.MAX_VALUE)
        val buffer = source.buffer
        var charset: Charset? = UTF8
        val contentType = responseBody.contentType()
        if (contentType != null) {
            charset = contentType.charset(UTF8)
        }
        // 拿到返回结果
        val bodyString = buffer.clone().readString(charset!!)
        try {
            if (!TextUtils.isEmpty(bodyString)) {
                val jsonObject = JsonUtil.toJSONObject(bodyString)
                // 判断token是否失效
                val code = jsonObject.optString("code")

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
    }

    companion object {

        /**
         * 全局平台token无效错误码，出现该错误，则需要退出平台账户，重新登录App
         */
        private const val GlobalPlatformTokenInvalidError = "000102"

        private val UTF8 = Charset.forName("UTF-8")
    }
}