package base2app.intercept


/**
 * date : 2020/10/29
 * dest : ZRIntercept
 */
interface IZRInterceptor {

    /**
     * 获取当前拦截器的优先级 , 具体参数位置 [com.zrlib.lib_service.ZRRouteInterceptorPriority]
     */
    fun getPriority(): Int

    /**
     * action 如果当前逻辑进行拦截  , 保留 action 操作 , 等下一步回调再进行处理
     */
    fun process(action: ((Boolean) -> Unit)?, befor: ((priority: Int,action: () -> Unit) -> Boolean)?)
}