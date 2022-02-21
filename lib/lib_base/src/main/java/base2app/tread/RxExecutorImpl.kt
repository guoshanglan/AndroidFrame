package com.zhuorui.securities.base2app.tread

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

/**
 * date : 2020/11/20
 * @description 基于 Rx 的线程实现类
 * @date 2018-03-29
 * @lastModifyDate 2019-01-20 12:13
 */
class RxExecutorImpl : BaseExecutor {

    override fun singleIO(runnable: Runnable) {
        Dispatchers.Default.asExecutor().execute(runnable)
    }

    override fun networkIO(runnable: Runnable) {
        Dispatchers.IO.asExecutor().execute(runnable)
    }

    override fun computationIO(runnable: Runnable) {
        Dispatchers.Default.asExecutor().execute(runnable)
    }

    override fun mainThread(runnable: Runnable) {
        Dispatchers.Main.asExecutor().execute(runnable)
    }
}