package com.zhuorui.securities.base2app.tread

import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.plugins.RxJavaPlugins
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * DispatcherBackedScheduler
 * @descraption 将协程的线程池替换掉rx的线程池 , 由协程统一控制管理
 * @time  16:18
 */
class DispatcherBackedScheduler(private val dispatcher: CoroutineDispatcher) : Scheduler() {
    override fun createWorker(): Worker = DispatcherBackedWorker(dispatcher)

    private class DispatcherBackedWorker(private val dispatcher: CoroutineDispatcher) : Worker(),
        CoroutineScope {
        private val job = SupervisorJob()//SupervisorJob 的取消只会向下传播
        override val coroutineContext: CoroutineContext = EmptyCoroutineContext + job
        override fun isDisposed(): Boolean = !job.isActive
        override fun dispose() = job.cancel()

        val handler = CoroutineExceptionHandler { _, exception ->
            RxJavaPlugins.onError(exception)
        }

        override fun schedule(run: Runnable, delay: Long, unit: TimeUnit): Disposable =
            //直接使用协程进行分发执行 , 这里使用的是一个 SupervisorJob 的监督 , 如果 work 取消会直接取消下游所有活动
            launch(dispatcher + handler) {
                if (delay > 0)
                    delay(unit.toMillis(delay))
                run.run()
            }.asDisposable()//这里将当前job 进行取消后 新构建了一个 Disposable
    }
}

private fun Job.asDisposable(): Disposable = object : Disposable {
    override fun isDisposed(): Boolean = !isActive
    override fun dispose() = cancel()
}