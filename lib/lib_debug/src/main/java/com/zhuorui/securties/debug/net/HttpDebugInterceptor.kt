package com.zhuorui.securties.debug.net

import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.GzipSource
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.atomic.AtomicInteger

/**
 * HttpDebugInterceptor
 *
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time 16:23
 */
class HttpDebugInterceptor : Interceptor {

    private val mNextRequestId = AtomicInteger(0)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBody = request.body
        val hooker = RequestHooker()
        hooker.method = request.method

        hooker.requestId = mNextRequestId.incrementAndGet()
        hooker.url = request.url.toString()
        hooker.contetType = requestBody?.contentType().toString()
        hooker.requestHeader = request.headers
        hooker.requestBody = readRequestBody(requestBody)
        val startNs = System.nanoTime()
        hooker.requestTime = startNs
        requestHook(hooker)
        val response: Response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            hooker.responseTime = System.nanoTime()
            hooker.result = "<-- HTTP FAILED: $e"
            responseHook(hooker)
            throw e
        }

        hooker.responseTime = System.nanoTime()
        val responseBody = response.body
        val contentLength = responseBody!!.contentLength()
        hooker.responseSize = contentLength

        val headers = response.headers
        hooker.responseHeader = headers
        if (!response.promisesBody()) {
            hooker.result = "END HTTP"
            responseHook(hooker)
        } else if (bodyHasUnknownEncoding(response.headers)) {
            hooker.result = "HTTP (encoded body omitted)"
            responseHook(hooker)
        } else {
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            var buffer = source.buffer
            var gzippedLength: Long? = null
            if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                gzippedLength = buffer.size
                var gzippedResponseBody: GzipSource? = null
                try {
                    gzippedResponseBody = GzipSource(buffer.clone())
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                } finally {
                    gzippedResponseBody?.close()
                }
            }
            var charset = UTF8
            val contentType = responseBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
            }
            hooker.responseSize = buffer.size
            if (!isPlaintext(buffer)) {
                hooker.result =
                    "END HTTP (binary " + buffer.size + "-byte body omitted))"
                responseHook(hooker)
                return response
            }
            if (contentLength != 0L) {
                hooker.responseBody = buffer.clone().readString(charset)
            }
            if (gzippedLength != null) {
                hooker.result =
                    "<-- END HTTP (" + buffer.size + "-byte, " + gzippedLength + "-gzipped-byte body)"
                responseHook(hooker)
            } else {
                hooker.result = "END HTTP (" + buffer.size + "-byte body)"
                responseHook(hooker)
            }
        }
        return response
    }

    private fun requestHook(hooker: RequestHooker) {
        HttpMonitor.requestHook(hooker)
    }


    private fun responseHook(hooker: RequestHooker) {
        HttpMonitor.responseHook(hooker)
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

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"]
        return (contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
                && !contentEncoding.equals("gzip", ignoreCase = true))
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")

        /**
         * Returns true if the body in question probably contains human readable text. Uses a small sample
         * of code points to detect unicode control characters commonly used in binary file signatures.
         */
        fun isPlaintext(buffer: Buffer): Boolean {
            return try {
                val prefix = Buffer()
                val byteCount = if (buffer.size < 64) buffer.size else 64
                buffer.copyTo(prefix, 0, byteCount)
                for (i in 0..15) {
                    if (prefix.exhausted()) {
                        break
                    }
                    val codePoint = prefix.readUtf8CodePoint()
                    if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                        return false
                    }
                }
                true
            } catch (e: EOFException) {
                false // Truncated UTF-8 sequence.
            }
        }
    }
}