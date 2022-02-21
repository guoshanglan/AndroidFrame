package base2app.network.interceptor

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.serializer.SerializerFeature
import base2app.BaseApplication
import base2app.ex.logd
import base2app.util.SignUtil
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.nio.charset.Charset


/**
 * ZRBodyFormatInterceptor
 * @descraption
 * @time 2021/9/15 10:53
 */
class ZRSignFormatInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val oldRequest = chain.request()
        if (oldRequest.method.equals("post", ignoreCase = true)){
            val body = readRequestBody(oldRequest.body)
            if (!body.contains("\"sign\":")){
                logd("添加签名前  : $body")
                val jsonObject = JSON.parseObject(body)
                jsonObject["sign"] = generateSign(jsonObject)?:""
                logd("添加签名后  : $jsonObject")
                return chain.proceed(newRequest(jsonObject.toString(),oldRequest))
            }
        }
        return chain.proceed(oldRequest)
    }


    private fun readRequestBody(body: RequestBody?): String {
        val buffer = Buffer()
        try {
            body?.writeTo(buffer)
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }
        //编码设为UTF-8
        var charset = Charset.forName("UTF-8")
        val contentType = body?.contentType()
        if (contentType != null) {
            charset = contentType.charset(Charset.forName("UTF-8"))
        }
        //拿到request
        return buffer.readString(charset)
    }

    /**
     * 根据参数生成签名
     */
    private fun generateSign(obj: JSONObject): String? {
        try {
//            // 拿到所有的参数进行升序排列
            val json = JSON.toJSONString(obj, SerializerFeature.MapSortField)
            // 根据新产生的json进行加密
            return SignUtil.createSHA1Sign(
                json,
                BaseApplication.baseApplication.config.privateKey()!!
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }


    private fun newRequest(content: String, oldRequest: Request): Request {
        return oldRequest.newBuilder()
            .method(
                oldRequest.method,
                content.toRequestBody(oldRequest.body?.contentType())
            )
            .build()
    }

    // todo 确认了下,  表单上传是字符串的方式 , 所以想要将其 json 序列化不实际 ,
    //  唯一方法是修改 retrofit 的 requestFactory 在其内部进行 json 的拼接 ;
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val oldRequest = chain.request()
//        if (oldRequest.method.equals("post", ignoreCase = true)
//            && oldRequest.body is FormBody
//        ) {
//            //属于公司站点的post表单请求 , 需要额外处理成 json 二进制再提交
//            val formBody = oldRequest.body as FormBody?
//            val map: MutableMap<String, String> = HashMap()
//            for (i in 0 until formBody!!.size) {
//                map[formBody.encodedName(i)] = formBody.encodedValue(i)
//            }
//            if (map.isNotEmpty()) {
//                map["sign"] =
//                    generateSign(JSON.toJSONString(map, SerializerFeature.MapSortField)) ?: ""
//                return chain.proceed(oldRequest)
//            }
//        }
//        return chain.proceed(oldRequest)
//    }
//
//
}