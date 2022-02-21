package base2app.network

import android.util.Log
import com.google.gson.JsonSyntaxException
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import base2app.BaseApplication
import base2app.ex.loge
import base2app.ex.logw
import base2app.network.exception.NetComposeException
import base2app.network.exception.NetErrorException
import base2app.network.interceptor.HeaderInterceptor
import base2app.network.interceptor.TokenInterceptor
import base2app.network.interceptor.ZRSignFormatInterceptor
import base2app.network.ssl.SSLHelper
import com.zrlib.lib_service.base.BaseRouterPath
import com.zrlib.lib_service.service
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.EOFException
import java.io.IOException
import java.lang.reflect.UndeclaredThrowableException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.CancellationException
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLException


/**
 *    author : Pengxianglin
 *    e-mail : peng_xianglin@163.com
 *    date   : 2019-05-20 14:13
 *    desc   : 定义Retrofit网络配置
 */
object Network {

    const val TAG = "Retrofit"

    /**
     * 返回一个普通的Retrofit
     * @return Retrofit
     */
    var retrofit: Retrofit? = null
        private set

//    /**
//     * 返回一个请求不超时的Retrofit，适用于上传
//     * @return Retrofit
//     */
//    var noTimeoutRetrofit: Retrofit? = null
//        private set

    /**
     * 阿里云 OSS 请求地址 ， 目前仅有历史数据请求时才会用
     */
    var ossRetrofit: Retrofit? = null

    /*域名验证器*/
    private val unsafeOkHttpClient: OkHttpClient
        get() {
            try {
                val builder = OkHttpClient().newBuilder()
                val allHostnameVerifier = HostnameVerifier { _, _ -> true }
                builder.hostnameVerifier(allHostnameVerifier)
                return builder.build()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

    /**
     * 初始化Retrofit
     *
     * @param baseUrl             主域名
     * @param debug               是否处于debug模式
     * @param writeTimeout_secs   writeTimeout
     * @param readTimeout_secs    readTimeout
     * @param connectTimeout_secs connectTimeout
     */
    fun initRetrofit(
        baseUrl: String,
        oss: String,
        debug: Boolean,
        writeTimeout_secs: Long,
        readTimeout_secs: Long,
        connectTimeout_secs: Long,
    ) {
        /*配置OkHttp属性*/
        val builder = unsafeOkHttpClient.newBuilder()
            .writeTimeout(writeTimeout_secs, TimeUnit.SECONDS)
            .readTimeout(readTimeout_secs, TimeUnit.SECONDS)
            .connectTimeout(connectTimeout_secs, TimeUnit.SECONDS)
            .followRedirects(true)
        // 正式环境
        if (BaseApplication.baseApplication.config.runMode?.isQaMode == true) {
            val sslCertifcation = SSLHelper.getSSLCertifcation()
            if (sslCertifcation != null) {
                loge(TAG, "读取SSL配置成功")
                // 配置SSL
                builder.sslSocketFactory(sslCertifcation, SSLHelper.getX509TrustManager())
            } else {
                loge(TAG, "读取SSL配置失败")
            }
        }
        /*添加Header头信息*/
        builder.addInterceptor(HeaderInterceptor())
        /*添加检查Token状态*/
        builder.addInterceptor(TokenInterceptor())
        /*添加请求签名拦截器*/
        builder.addInterceptor(ZRSignFormatInterceptor())

        if (debug) {
            /*若是debug；则添加Http日志打印的拦截器进行打印请求信息*/
            val logger = HttpLoggingInterceptor.Logger { message -> logw(TAG, message) }
            val interceptorLog = HttpLoggingInterceptor(logger)
            interceptorLog.level = HttpLoggingInterceptor.Level.BODY/*请求日志打印信息；基本信息*/
            builder.addInterceptor(interceptorLog)

            service<DebugNetService>(BaseRouterPath.DEBUG_NET_SERVICE)?.provideNetIntercept()?.let {
                builder.addInterceptor(it)
            }
        }
        /*添加动态修改BaseUrl*/
//        builder.addInterceptor(BaseUrlInterceptor())
        val client = builder.build()
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            // 添加GSON解析：返回数据转换成GSON类型
            .addConverterFactory(GsonConverterFactory.create())
            // 添加Rxjava支持
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
            .client(client)
            .build()

        // 设置超时为120秒
        builder.writeTimeout(120, TimeUnit.SECONDS).readTimeout(120, TimeUnit.SECONDS)

//        noTimeoutRetrofit = Retrofit.Builder()
//            .baseUrl(baseUrl)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()

        ossRetrofit = Retrofit.Builder()
            .baseUrl(oss)
            // 添加Rxjava支持
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
            .client(
                unsafeOkHttpClient.newBuilder()
                    .writeTimeout(writeTimeout_secs, TimeUnit.SECONDS)
                    .readTimeout(readTimeout_secs, TimeUnit.SECONDS)
                    .connectTimeout(connectTimeout_secs, TimeUnit.SECONDS)
                    .apply {
                        if (debug) {
                            /*若是debug；则添加Http日志打印的拦截器进行打印请求信息*/
                            val logger = HttpLoggingInterceptor.Logger { message -> logw(TAG, message) }
                            val interceptorLog = HttpLoggingInterceptor(logger)
                            interceptorLog.level = HttpLoggingInterceptor.Level.BODY/*请求日志打印信息；基本信息*/
                            this.addInterceptor(interceptorLog)

                            service<DebugNetService>(BaseRouterPath.DEBUG_NET_SERVICE)?.provideNetIntercept()?.let {
                                this.addInterceptor(it)
                            }
                        }
                    }
                    .followRedirects(true).build()
            )
            .build()
    }

    /**
     * 发送网络请求，默认主线程响应
     */
    fun <T : BaseResponse, R : T> subscribe(
        observable: Observable<R>,
        callback: SubscribeCallback<T>? = null,
    ): Disposable {
        return subscribe(
            observable,
            AndroidSchedulers.mainThread(),
            AndroidSchedulers.mainThread(),
            callback
        )
    }

    /**
     * 发送网络请求
     * @param requestScheduler 请求发起的线程
     * @param responseScheduler 请求响应的线程
     */
    fun <T : BaseResponse, R : T> subscribe(
        observable: Observable<R>,
        requestScheduler: Scheduler,
        responseScheduler: Scheduler,
        callback: SubscribeCallback<T>?,
    ): Disposable {
        //此处不能直接用请求observable监听状态，会使请求不能发送，额外包装一层做状态监听
        return Observable.create(ObservableOnSubscribe<Boolean> {
            onNext(it, true)
        })
            .doOnSubscribe {
                callback?.onNetStart()
            }
            .doOnError {
                //Rxjava调用onError()方法进入该方法
                callback?.onDoError()
            }
            .doOnDispose {
                //在关闭连接，串联调用发生错误时会进入
                callback?.onDoOnDispose()
            }
            .flatMap {
                observable.subscribeOn(Schedulers.io())////请求执行时切换到IO线程进行
            }
            .subscribeOn(requestScheduler)//请求发起的线程（以上方法会在此线程）
            .observeOn(responseScheduler)//请求响应的线程
            .subscribe(object : NetConsumer<R>() {

                override fun onResponse(response: R) {
                    callback?.onNetEnd()
                    callback?.onNetResponse(response)
                }

                override fun onFail(response: R) {
                    callback?.let { call ->
                        call.onNetEnd()
                        when {
                            //外部消耗了业务错误
                            call.onBusinessFail(response) -> {
                            }
                            //外部消耗了错误
                            call.onNetFailure(ErrorResponse.valueOf(response)) -> {
                            }
                            //基类处理
                            else -> super.onFail(response)
                        }
                    }

                }

            }, object : NetErrorConsumer<Throwable>() {

                override fun subAccept(t: Throwable): String? {
                    return callback?.subErrorAccept(t)
                }

                override fun onError(msg: String) {
                    callback?.let { call ->
                        call.onNetEnd()
                        if (!call.onNetFailure(ErrorResponse("999999", msg))) {
                            super.onError(msg)
                        }
                    }

                }
            })

    }


    /**
     * 停止请求
     */
    fun stopDisposable(disposable: Disposable?) {
        if (disposable?.isDisposed == false) {
            disposable.dispose()
        }
    }

    /**
     * 发送数据
     */
    fun <T> onNext(emitter: ObservableEmitter<T>?, value: T?) {
        emitter?.let {
            if (!it.isDisposed) {
                value?.let { v ->
                    it.onNext(v)
                }
                it.onComplete()
            }
        }
    }

    /**
     * 是否需要在Debug抛出的网络异常
     */
    fun isDebugNetWorkThrow(t: Throwable): Boolean {
        when (t) {
            is NetComposeException -> {
                //如果是组合异常，那么判断是否需要抛出
                t.exceptions.forEach {
                    if (isDebugNetWorkThrow(it)) return true
                }
                return false
            }
            is CompositeException -> {
                //如果是组合异常，那么判断是否需要抛出
                t.exceptions.forEach {
                    if (isDebugNetWorkThrow(it)) {
                        return true
                    }
                }
                return false
            }

            /*网络相关错误*/
            is JsonSyntaxException -> {
                Log.e(TAG, "JsonSyntaxException: ", t)
                return false
            }
            is ConnectException,
            is UnknownHostException,
            is SocketTimeoutException,
            is SSLException,
            is HttpException,
            is SocketException,
            is NetErrorException,
            is EOFException,
                //使用Retrofit动态代理获取apiFun时，部分手机会将ConnectException包装成UndeclaredThrowableException抛出，
                // 因此这里出现UndeclaredThrowableException，也不主动抛出
            is UndeclaredThrowableException,
            is CancellationException
            -> {
                return false
            }
            is IOException -> {
                //okhttp3网络请求时网络异常报的错误
                return t.message?.contains("unexpected end of stream on") != true
            }
            else -> {
                return true
            }
        }
    }

    /**
     * rxJava 网络请求回调
     */
    interface SubscribeCallback<T> {

        /**
         * 处理特定报错信息
         * 默认不处理
         */
        fun subErrorAccept(t: Throwable): String? {
            return null
        }

        /**
         * Rxjava调用onError()方法进入该方法
         * （正常通过Rxjava网络请求一般不会进入该方法）
         */
        fun onDoError() {
            onNetEnd()
        }

        /**
         * 取消请求
         */
        fun onDoOnDispose() {
            onNetEnd()
        }

        /**
         * 网络请求开始
         * 可以在这个方法显示加载提示等操作
         */
        fun onNetStart() {
        }

        /**
         * 网络请求结束
         * 可以在这个方法关闭加载提示等操作
         * 注意，如果不想在这个方法关闭（如多个接口连续请求不想重复开打关闭提示）
         * 需要根据业务调整关闭逻辑（
         *  不在此处使用关闭逻辑需实现方法方案建议，多个接口连续请求按方案2实现，其他特殊需求按实际调整
         *  方案1： onDoComplete，onDoOnDispose，onDoError(可选，没有发送onError()事件可不用处理）
         *  方案2： onTokenOverdue，onNetResponse，onNetFailure，onDoOnDispose，onDoError(可选，没有发送onError()事件可不用处理)
         * ）
         */
        fun onNetEnd() {
        }

        /**
         * 请求成功
         */
        fun onNetResponse(response: T)

        /**
         * 请求失败，包括网络，业务
         * @return 返回true,基类不会对失败结果提示操作，false ,基类提示失败内容
         */
        fun onNetFailure(response: ErrorResponse): Boolean {
            return false
        }

        /**
         * 业务失败
         */
        fun onBusinessFail(response: T): Boolean {
            return false
        }
    }
}
