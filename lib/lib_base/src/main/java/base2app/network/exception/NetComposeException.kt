package base2app.network.exception

import java.lang.RuntimeException

/**
 * @date 2021/5/26 15:47
 * @desc App网络请求组合错误
 */
class NetComposeException(val exceptions: List<Throwable>) : RuntimeException("Coroutine NetComposeException")