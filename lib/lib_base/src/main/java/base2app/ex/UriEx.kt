package base2app.ex

import android.net.Uri
import android.text.TextUtils
import java.util.*

/**
 * date : 2020/10/29
 * dest : UriEx
 */


/**
 * 获取 Uri 中的请求参数
 */
fun Uri.parameters(): Map<String?, String>? {
    val query = this.query ?: return emptyMap()
    val paramMap: MutableMap<String?, String> = LinkedHashMap()
    var start = 0
    do {
        val next = query.indexOf('&', start)
        val end = if (next == -1) query.length else next
        var separator = query.indexOf('=', start)
        if (separator > end || separator == -1) {
            separator = end
        }
        val name = query.substring(start, separator)
        if (!TextUtils.isEmpty(name)) {
            val value = if (separator == end) "" else query.substring(separator + 1, end)
            paramMap[Uri.decode(name)] = Uri.decode(value)
        }
        // Move start to end of name.
        start = end + 1
    } while (start < query.length)
    return Collections.unmodifiableMap(paramMap)
}

/**
 * scheme://host:post
 * 判断当前路径是否匹配路由 scheme 协议的规范
 */
fun String.isRouteUri():Boolean{
    return !(this.isEmpty() || !this.contains("zr://zrzq:8888"))
}

/**
 * 字符串转为 Uri 对象
 */
fun String.uri(): Uri? {
    return if (this.isEmpty() || !this.contains("://")) {
        null
    } else try {
        Uri.parse(this)
    } catch (ex: Exception) {
        null
    }
}

const val ZR_SCHEME = "zr://zrzq:8888"


fun generateUri(path: String, query: Map<String, Any?>?): Uri {
    val uri: Uri = Uri.parse("$ZR_SCHEME$path")
    var builder: Uri.Builder? = null
    query?.forEach() { item ->
        if (builder == null) builder = uri.buildUpon()
        item.value?.let {
            if (it is Comparable<*>){//如果只是基础数据 , 直接 put 在取时用 BundleEx 获取即可
                builder!!.appendQueryParameter(item.key, it.toString())
            }else{//非基础数据直接使用 gson 进行序列化
                builder!!.appendQueryParameter(item.key, it.gson())
            }
        }
    }
    return if (builder == null) uri
    else builder!!.build()
}