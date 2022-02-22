package com.zhuorui.securties.debug.net

import okhttp3.Headers

/**
 * RequestHooker
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  17:32
 */
class RequestHooker {

    var method: String? = null

    var requestId: Int? = null

    var requestTime:Long?=null

    var url: String? = null

    var requestHeader: Headers? = null

    var requestBody: String? = null

    var contetType: String? = null

    var responseHeader: Headers? = null

    var responseBody: String? = null

    var responseTime:Long?=null

    var responseSize: Long?=null

    var result: String? = null
}
