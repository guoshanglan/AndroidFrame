package base2app.network

import base2app.ex.isRunInUIThread
import base2app.ex.loge
import base2app.ex.mainThread
import base2app.network.exception.NetComposeException
import base2app.network.exception.NetErrorException
import base2app.util.ToastUtil
import com.example.lib_base.BuildConfig
import kotlinx.coroutines.*
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * @author xuzuoliang
 * @email micrason@163.com
 * @date 2021/3/25 16:56
 * @desc 相关kotlin协程异步处理作用域，其它presenter类通过委托方式实现该类
 */
open class ZRCoroutineScope : CoroutineScope by MainScope(), IScope {

    /**
     * 通过zrCoroutineScope构建自定义协程
     */
    override val zrCoroutineScope: ZRCoroutineScope
        get() = this


    /**
     * 注意，如果是异步线程发起，那么需要自己回到主线程，目前请求从哪个线程来，回到哪个线程
     * 发起单个请求，自动处理成功和失败回调
     * @param apiFun 发起请求的挂起函数
     * @param onResponse 返回结果
     * @param onError 错误返回，返回值为Boolean值，如果消费了返回true，不消费返回false，当返回false时会针对某些错误码自动弹出toast提示
     * @param onStart 开始请求
     * @param onEnd 结束请求回调
     * @param onExceptionFormat 非后台的报错回调，比如ConnectException，UnknownHostException等，如有必要可实现改方法，通过该方法自定义提示信息
     * @param onResponseDispatcher 定义onResponse回调时运行的线程，此参数不传，则根据发起的线程决定onResponse运行在哪个线程
     * @return 返回Job，如果需要对单个请求按需管理，则接收该参数
     */
    override fun <T : BaseResponse> sendRequest(
        apiFun: suspend () -> T?,
        onResponse: ((response: T) -> Unit)?,
        onError: ((errorCode: String?, errorMsg: String, errorResponse: T?) -> Boolean)?,
        onStart: (() -> Unit)?,
        onEnd: (() -> Unit)?,
        onExceptionFormat: ((throwable: Throwable) -> String?)?,
        onResponseDispatcher: CoroutineDispatcher?
    ): Job {
        return request(apiFun,onResponse,onError,onStart,onEnd,onExceptionFormat,onResponseDispatcher)
//        val isMainThread = isRunInUIThread()
//        val handlerException = handleRequestException(isMainThread, onExceptionFormat, onError, onEnd)
//        val coroutineContext = if (isMainThread) {
//            //主线程发起,直接使用当前作用域
//            handlerException
//        } else {
//            //非主线程调用，创建新的协程上下文，使其直接在调用线程开始,降低线程切换问题
//            Dispatchers.Unconfined + handlerException
//        }
//        return launch(coroutineContext) {
//            onStart?.invoke()
//            val response = withContext(Dispatchers.IO) { apiFun() }
//            if (response?.isSuccess() == true) {
//                if (isMainThread) {
//                    //如果当前是主线程发起，那么可以定义onResponse回调的运行的线程。如果未定义，那么直接回调主线程
//                    onResponseDispatcher
//                        ?.takeIf { it == Dispatchers.IO || it == Dispatchers.Default }
//                        ?.let { withContext(it) { onResponse?.invoke(response) } }
//                        ?: onResponse?.invoke(response)
//                } else {
//                    onResponseDispatcher?.let {
//                        withContext(it) { onResponse?.invoke(response) }
//                    } ?: onResponse?.invoke(response)
//                }
//            } else {
//                val errorMsg = response?.msg ?: BaseResponse.getNetworkBrokenText()
//                val isConsume = onError?.invoke(response?.code, errorMsg, response)
//                //是否消费了错误，如果没有，那么走默认消费该错误流程
//                if (isConsume == false && "000101" != response?.code && "000102" != response?.code) {
//                    ToastUtil.instance.toast(response?.msg ?: BaseResponse.getNetworkBrokenText())
//                }
//            }
//            onEnd?.invoke()
//        }
    }

    /**
     * 发起多个互不干扰的并联请求组，如果同时失败，则回调失败，任意一个成功，则回调成功。
     * @param apiFunArray retrofit请求组
     * @param onResponseList 回调BaseResponse组，回调的Response是顺序的且必然有值，外部自行根据BaseResponse类型判断
     * @param onError 全部失败时，错误返回
     * @param onStart 开始请求
     * @param onEnd 结束请求回调
     * @param onResponseDispatcher 定义onResponse回调时运行的线程或线程池，此参数不传，则根据发起的线程决定onResponse运行在哪个线程
     */
    override fun sendSupervisorRequest(
        apiFunArray: Array<suspend () -> BaseResponse?>,
        onResponseList: (responseList: List<BaseResponse>) -> Unit,
        onError: ((throwable: Throwable) -> Boolean)?,
        onStart: (() -> Unit)?,
        onEnd: (() -> Unit)?,
        onResponseDispatcher: CoroutineDispatcher?
    ): Job {
        val isMainThread = isRunInUIThread()
        val handlerException = handleWorkException(isMainThread, onError, onEnd)
        val coroutineContext = if (isMainThread) {
            //主线程发起,直接使用当前作用域
            handlerException
        } else {
            //非主线程调用，创建新的协程上下文，使其直接在调用线程开始,降低线程切换问题
            Dispatchers.Unconfined + handlerException
        }
        return launch(coroutineContext) {
            //使用supervisorScope作用域，构建互不干扰协程组
            supervisorScope {
                onStart?.invoke()
                //发起并联请求
                val apiDeferredList =
                    apiFunArray.map { return@map async(Dispatchers.IO) { it() } }.toList()
                val errorList = ArrayList<Exception>()
                val responseList = ArrayList<BaseResponse>()
                apiDeferredList.forEach { deferred ->
                    try {
                        val response = deferred.await()
                        if (response?.isSuccess() == true) {
                            //获取结果成功，则返回结果
                            responseList.add(response)
                        } else {
                            //失败，添加到错误列表
                            val errorMsg = response?.msg ?: BaseResponse.getNetworkBrokenText()
                            errorList.add(NetErrorException(response?.code, errorMsg, response))
                            responseList.add(ErrorResponse.valueOf(response))
                        }
                    } catch (e: Exception) {
                        //异常，添加到错误列表
                        errorList.add(e)
                        responseList.add(ErrorResponse.exceptionResponse(e))
                    }
                }
                if (errorList.size == apiFunArray.size) {
                    //如果错误组得个数和请求组得个数相等，则说明全部失败了，此时回调NetComposeException组合错误
                    val composeException = NetComposeException(errorList)
                    if (isThrowException(composeException)) {
                        composeException.exceptions.forEachIndexed { index, throwable ->
                            loge(
                                "ZRCoroutineScope",
                                ">>>>>>>>>> current active ${index + 1} exception [ $throwable ] by SupervisorRequest <<<<<<<<<<"
                            )
                        }
                        throw composeException
                    }
                    val isConsume = onError?.invoke(composeException)
                    if (isConsume == false) ToastUtil.instance.toast(BaseResponse.getNetworkBrokenText())
                } else {
                    //不等于请求组个数，说明有成功的，则回调成功。外部自行根据BaseResponse判断
                    if (isMainThread) {
                        //如果当前是主线程发起，那么可以定义onResponse回调的运行的线程。如果未定义，那么直接回调主线程
                        onResponseDispatcher
                            ?.takeIf { it == Dispatchers.IO || it == Dispatchers.Default }
                            ?.let { withContext(it) { onResponseList.invoke(responseList) } }
                            ?: onResponseList.invoke(responseList)
                    } else {
                        //其它线程，直接回调，外部处理
                        onResponseDispatcher?.let {
                            withContext(it) { onResponseList.invoke(responseList) }
                        } ?: onResponseList.invoke(responseList)
                    }
                }
                onEnd?.invoke()
            }
        }
    }


    /**
     * 处理串并连复合查询方式。例如：（第2个接口依赖第一个接口，第3个第4个接口又依赖第2个接口这种模式）适用该方法，自己实现协程体
     * @param onStart 请求开始回调
     * @param onWork 尝试执行的挂起代码块
     * @param onError 捕获异常的代码块
     * @param onEnd 请求介素回调
     * @return 返回Job，如果需要对单个请求按需管理，则接收该参数
     */
    override fun sendWork(
        onWork: suspend CoroutineScope.() -> Unit,
        onError: ((throwable: Throwable) -> Boolean)?,
        onStart: (() -> Unit)?,
        onEnd: (() -> Unit)?,
    ): Job {
        //主线程发起
        val isMainThread = isRunInUIThread()
        val handlerException = handleWorkException(isMainThread, onError, onEnd)
        val coroutineContext = if (isMainThread) {
            //主线程发起,直接使用当前作用域
            handlerException
        } else {
            //非主线程调用，直接在调用线程开始,降低线程切换问题
            Dispatchers.Unconfined + handlerException
        }
        return launch(coroutineContext) {
            onStart?.invoke()
            onWork()
            onEnd?.invoke()
        }
    }

    /**
     * 处理联合查询异常
     */
    private fun handleWorkException(
        forceToMainThread: Boolean,
        onError: ((throwable: Throwable) -> Boolean)?,
        onEnd: (() -> Unit)?
    ) = object : AbstractCoroutineContextElement(CoroutineExceptionHandler),
        CoroutineExceptionHandler {
        override fun handleException(context: CoroutineContext, exception: Throwable) {
            if (isThrowException(exception)) {
                loge("ZRCoroutineScope", ">>>>>>>>>> current active exception [ $exception ] by handleWorkException <<<<<<<<<<")
                throw exception
            }
            handlerExceptionCallback(forceToMainThread) {
                val isConsume = onError?.invoke(exception)
                if (isConsume == false) ToastUtil.instance.toast(BaseResponse.getNetworkBrokenText())
                onEnd?.invoke()
            }
        }
    }

}


/**
 * 扩展函数:取消当前作用域下的协程
 * ps:该方法只能调用一次，如果需要对单个请求进行单独取消处理，
 * 请使用sendRequest/sendWork返回的Job处理
 */
fun IScope.cancelScope() {
    this.zrCoroutineScope.cancel()
}

/**
 * 扩展函数:判断当前作用域顶级协程是否处理活动状态
 */
fun IScope.isActiveScope(): Boolean {
    return this.zrCoroutineScope.isActive
}

/**
 * 扩展函数:取消单个请求,针对指定job进行取消
 */
fun Job?.cancelJob() {
    this?.takeIf { it.isActive }?.cancel()
}

/**
 * 扩展函数:判断当前job是否处于活动状态
 */
fun Job?.isActiveJob(): Boolean {
    return this?.isActive == true
}

fun <T : BaseResponse> CoroutineScope.request(
    apiFun: suspend () -> T?,
    onResponse: ((response: T) -> Unit)? = null,
    onError: ((errorCode: String?, errorMsg: String, errorResponse: T?) -> Boolean)? = null,
    onStart: (() -> Unit)? = null,
    onEnd: (() -> Unit)? = null,
    onExceptionFormat: ((throwable: Throwable) -> String?)? = null,
    onResponseDispatcher: CoroutineDispatcher?= null
): Job {
    val isMainThread = isRunInUIThread()
    val handlerException = handleRequestException(isMainThread, onExceptionFormat, onError, onEnd)
    val coroutineContext = if (isMainThread) {
        //主线程发起,直接使用当前作用域
        handlerException
    } else {
        //非主线程调用，创建新的协程上下文，使其直接在调用线程开始,降低线程切换问题
        Dispatchers.Unconfined + handlerException
    }
    return launch(coroutineContext) {
        onStart?.invoke()
        val response = withContext(Dispatchers.IO) { apiFun() }
        if (response?.isSuccess() == true) {
            if (isMainThread) {
                //如果当前是主线程发起，那么可以定义onResponse回调的运行的线程。如果未定义，那么直接回调主线程
                onResponseDispatcher
                    ?.takeIf { it == Dispatchers.IO || it == Dispatchers.Default }
                    ?.let { withContext(it) { onResponse?.invoke(response) } }
                    ?: onResponse?.invoke(response)
            } else {
                onResponseDispatcher?.let {
                    withContext(it) { onResponse?.invoke(response) }
                } ?: onResponse?.invoke(response)
            }
        } else {
            val errorMsg = response?.msg ?: BaseResponse.getNetworkBrokenText()
            val isConsume = onError?.invoke(response?.code, errorMsg, response)
            //是否消费了错误，如果没有，那么走默认消费该错误流程
            if (isConsume == false && "000101" != response?.code && "000102" != response?.code) {
                ToastUtil.instance.toast(response?.msg ?: BaseResponse.getNetworkBrokenText())
            }
        }
        onEnd?.invoke()
    }
}

/**
 * 处理单个网络请求异常
 */
private fun <T> handleRequestException(
    forceToMainThread: Boolean,
    onExceptionFormat: ((Throwable) -> String?)? = null,
    onError: ((errorCode: String?, errorMsg: String, errorResponse: T?) -> Boolean)?,
    onEnd: (() -> Unit)?
) = object : AbstractCoroutineContextElement(CoroutineExceptionHandler),
    CoroutineExceptionHandler {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        if (isThrowException(exception)) {
            loge("ZRCoroutineScope", ">>>>>>>>>> current active exception [ $exception ] by handleRequestException <<<<<<<<<<")
            throw exception
        }
        handlerExceptionCallback(forceToMainThread) {
            val msg = onExceptionFormat?.invoke(exception) ?: BaseResponse.getNetworkBrokenText()
            val isConsume = onError?.invoke(null, msg, null)
            if (isConsume == false) ToastUtil.instance.toast(msg)
            onEnd?.invoke()
        }
    }
}

/**
 * 处理捕获异常的回调
 */
private inline fun handlerExceptionCallback(forceToMainThread: Boolean, crossinline callback: () -> Unit) {
    if (forceToMainThread) {
        //强制回调到主线程。
        mainThread { callback.invoke() }
    } else {
        //不强制，则直接回调
        callback.invoke()
    }
}

/**
 * 是否需要抛出异常
 */
private fun isThrowException(t: Throwable): Boolean {
    return BuildConfig.DEBUG && Network.isDebugNetWorkThrow(t)
}