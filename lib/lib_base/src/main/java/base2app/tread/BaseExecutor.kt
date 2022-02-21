package com.zhuorui.securities.base2app.tread

/**
 * BaseExecutor
 * @description Thread Executor 抽象接口
 *
 *
 * @date 2018-03-29
 *
 * @lastModifyDate 2019-01-20 12:13
 */
interface BaseExecutor {
    /**
     * 单一线程处理 , 计算
     */
    fun singleIO(runnable: Runnable)

    /**
     * 网络请求 , io逻辑
     */
    fun networkIO(runnable: Runnable)

    /**
     * 密集型线程处理 ,计算
     */
    fun computationIO(runnable: Runnable)

    /**
     * 主线程处理
     */
    fun mainThread(runnable: Runnable)
}