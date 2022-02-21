package base2app.network

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * @date 2021/3/29 10:53
 * @desc kotlin协程请求管理接口，第一实现类查看查看 ZRCoroutineScope
 */
interface IScope {

    /**
     * 通过zrCoroutineScope构建自定义协程
     */
    val zrCoroutineScope: ZRCoroutineScope

    /**
     * 发起单个请求，自动处理成功和失败回调
     * @param apiFun 发起请求的挂起函数
     * @param onResponse 返回结果
     * @param onError 错误返回
     * @param onStart 开始请求
     * @param onEnd 结束请求回调
     * @param onExceptionFormat 非后台的报错回调，如有必要可实现改方法，比如ConnectException，UnknownHostException等
     * @param onResponseDispatcher 定义onResponse回调时运行的线程，默认在发起线程运行。
     * @return 返回Job，如果需要对单个请求按需管理，则接收该参数
     */
    fun <T : BaseResponse> sendRequest(
        apiFun: suspend () -> T?,
        onResponse: ((response: T) -> Unit)? = null,
        onError: ((errorCode: String?, errorMsg: String, errorResponse: T?) -> Boolean)? = null,
        onStart: (() -> Unit)? = null,
        onEnd: (() -> Unit)? = null,
        onExceptionFormat: ((throwable: Throwable) -> String?)? = null,
        onResponseDispatcher: CoroutineDispatcher? = null
    ): Job


    /**
     * 发起多个互不干扰的并联请求组，如果同时失败，则回调失败，任意一个成功，则回调成功。
     * @param apiFunArray
     * @param onResponseList 回调BaseResponse组，回调的Response是顺序的且必然有值，外部自行根据BaseResponse类型判断
     * @param onError 错误返回
     * @param onStart 开始请求
     * @param onEnd 结束请求回调
     * @param onResponseDispatcher 定义onResponse回调时运行的线程或线程池，默认在发起线程运行。
     */
    fun sendSupervisorRequest(
        apiFunArray: Array<suspend () -> BaseResponse?>,
        onResponseList: ((responseList: List<BaseResponse>) -> Unit),
        onError: ((throwable: Throwable) -> Boolean)? = null,
        onStart: (() -> Unit)? = null,
        onEnd: (() -> Unit)? = null,
        onResponseDispatcher: CoroutineDispatcher? = null
    ): Job

    /**
     * 处理联合查询方式，例如：一次需要多次接口查询数据，适用该方法，自己实现协程体
     * @param onStart 请求开始回调
     * @param onWork 尝试执行的挂起代码块
     * @param onError 捕获异常的代码块
     * @param onEnd 请求结束回调
     * @return 返回Job，如果需要对单个请求按需管理，则接收该参数
     */
    fun sendWork(
        onWork: suspend CoroutineScope.() -> Unit,
        onError: ((throwable: Throwable) -> Boolean)? = null,
        onStart: (() -> Unit)? = null,
        onEnd: (() -> Unit)? = null,
    ): Job
}