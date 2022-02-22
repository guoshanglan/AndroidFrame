package com.zhuorui.securties.debug.net

import java.util.concurrent.ConcurrentLinkedQueue

/**
 * HttpMonitor
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  14:57
 */
object HttpMonitor {

    val hookers: LimitQueue<RequestHooker> = LimitQueue(25)

    var selecedHooker: RequestHooker? = null

    var http: HttpListener? = null


    fun requestHook(hooker: RequestHooker) {
        hookers.offer(hooker)
        notifyHooker(hooker)
    }

    fun responseHook(hooker: RequestHooker) {
        notifyHooker(hooker)
    }

    private fun notifyHooker(hooker: RequestHooker) {
        http?.notifyHookerChange()
    }

    fun addHttpListener(http: HttpListener) {
        this.http = http
    }


    interface HttpListener {
        fun notifyHookerChange()
    }


    class LimitQueue<E>(  // 队列长度
        private val limit: Int
    ) {
        val queue: ConcurrentLinkedQueue<E> =
            ConcurrentLinkedQueue<E>()

        /**
         * 入列：当队列大小已满时，把队头的元素poll掉
         */
        fun offer(e: E) {
            if (queue.size >= limit) {
                queue.poll()
            }
            queue.offer(e)
        }

        operator fun get(position: Int): E {
            return queue.elementAt(position)
        }

        fun size(): Int {
            return queue.size
        }
    }

}