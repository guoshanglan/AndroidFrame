package com.zhuorui.securties.debug.fps

import android.annotation.SuppressLint
import android.os.*
import android.view.Choreographer
import base2app.BaseApplication
import com.zhuorui.securties.debug.Background
import com.zhuorui.securties.debug.BackgroundCallback
import com.zhuorui.securties.debug.fps.FpsConstants.*
import io.objectbox.Box
import java.io.*
import java.util.*


/**
 * FpsMonitor
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  13:34
 */
object FpsMonitor : BackgroundCallback {

    @SuppressLint("SdCardPath")
    const val DB_BASE_PATH = "/data/data/%s/database/"

    var mBox: Box<JankInfo>

    private var mSampleHandler: Handler

    /**
     * 记录App第一次启动的时间戳  , 主要用于对比数据库中缓存时间来判断本次获取以及历史获取的trace
     */
    var startTime: Long? = null

    init {
        val dbFilePath = String.format(
            DB_BASE_PATH,
            BaseApplication.baseApplication.applicationInfo?.packageName
        ) + "fps"

        mBox =
            MyObjectBox.builder().directory(File(dbFilePath)).build().boxFor(JankInfo::class.java)
//        Log.d("FpsMonitor","mBox.all.size : ${mBox.all.size}")
        Background.registerBackgroundCallback(this)
        val sampleThread = HandlerThread("trace_sample")
        sampleThread.start()
        mSampleHandler = Handler(sampleThread.looper)
    }

    private var mIsStarted: Boolean = false

    private val mFrameListeners = ArrayList<FrameListener>()

    private val mMainHandler = Handler(Looper.getMainLooper())

    private val mRateRunnable = FrameRateRunnable()

    private var mLastFrameTimeNanos: Long = 0

    private val mTracesInOneFrame: MutableList<Array<StackTraceElement>> = mutableListOf()


    /**
     * 读取fps的线程
     */
    private class FrameRateRunnable : Runnable, Choreographer.FrameCallback {

        private var totalFramesPerRate = 0

        private var diffFrameCoast = 0

        /**
         * 当前的帧率
         */
        private var mLastFrameRate: Int = FPS_MAX_DEFAULT

        override fun run() {
            mLastFrameRate = totalFramesPerRate
            if (mLastFrameRate > FPS_MAX_DEFAULT) {
                mLastFrameRate = FPS_MAX_DEFAULT
            }
            totalFramesPerRate = 0

            for (frameListener in mFrameListeners) {
                frameListener.onFrame(mLastFrameRate, diffFrameCoast)
            }
//            Log.d(
//                "FpsMonitor",
//                "mLastFrameRate  $mLastFrameRate   diffFrameCoast : $diffFrameCoast mLastCpuRate  $mLastCpuRate"
//            )
            dealPreFrameTraceInfo(diffFrameCoast)

            diffFrameCoast = FPS_INTERVAL_COST_DEFAULT

            //1s中统计一次
            mMainHandler.postDelayed(
                this,
                MS_PER_SECOND.toLong()
            )
        }

        //
        override fun doFrame(frameTimeNanos: Long) {
            if (startTime == null) startTime = System.currentTimeMillis()

            totalFramesPerRate++
            if (mLastFrameTimeNanos != 0L) {
                val temp =
                    ((frameTimeNanos - mLastFrameTimeNanos) / NANOS_PER_MS).toInt()
                if (temp > diffFrameCoast) {
                    diffFrameCoast = temp
                }
            }
            mLastFrameTimeNanos = frameTimeNanos

            mSampleHandler.removeCallbacks(mTracSampleTask)
            mSampleHandler.postDelayed(mTracSampleTask, METHOD_TRACE_SKIP_INTERVAL.toLong())

            Choreographer.getInstance().postFrameCallback(this)
        }
    }

    private val mTracSampleTask: Runnable by lazy {
        object : Runnable {
            override fun run() {
                if (Debug.isDebuggerConnected()) return
                val mainThread = Looper.getMainLooper().thread
                val stackArray = mainThread.stackTrace
                mTracesInOneFrame.add(stackArray)
//                Log.d(
//                    "FpsMonitor",
//                    "////////////////////  采样 ${mTracesInOneFrame.size} ////////////////////////"
//                )
                mSampleHandler.postDelayed(this, METHOD_TRACE_SKIP_INTERVAL.toLong())
            }
        }
    }

    /**
     * 如果上一帧到现在超出了限定时间 , 把上一帧的 trace 进行保存
     */
    private fun dealPreFrameTraceInfo(frameCostMillis: Int) {
        if (frameCostMillis > 200 && mTracesInOneFrame.isNotEmpty()) {
            mSampleHandler.post {
                if (Debug.isDebuggerConnected()) return@post
                val stackCountMap: MutableMap<String, Int> = HashMap(16)
                var traceStr: String
                for (trace in mTracesInOneFrame) {
                    traceStr = traceToString(trace)
                    val count = stackCountMap[traceStr]
                    if (null != count) {
                        stackCountMap[traceStr] = count + 1
                    } else {
                        stackCountMap[traceStr] = 1
                    }
                }
                val stackCountEntries: List<Map.Entry<String, Int>> =
                    ArrayList<Map.Entry<String, Int>>(stackCountMap.entries)

                @Suppress("JavaCollectionsStaticMethodOnImmutableList")
                Collections.sort(
                    stackCountEntries
                ) { arg0, arg1 -> arg1.value.compareTo(arg0.value) }
//                Log.d(
//                    "FpsMonitor",
//                    "********************  dealPreFrameTraceInfo ${stackCountEntries.size} *********************"
//                )
                storeJankTraceInfo(
                    System.currentTimeMillis(),
                    frameCostMillis,
                    stackCountEntries.map {
                        Pair(it.key, it.value)
                    })
                mTracesInOneFrame.clear()
            }
        } else {
            mTracesInOneFrame.clear()
        }
    }

    private fun traceToString(stackArray: Array<StackTraceElement>): String {
        if (stackArray.isEmpty()) {
            return "[]"
        }
        val b = StringBuilder()
        for (i in 0 until stackArray.size - METHOD_TRACE_SKIP) {
            if (i == stackArray.size - METHOD_TRACE_SKIP - 1) {
                return b.toString()
            }
            b.append(stackArray[i])
            b.append("\n")
        }
        return b.toString()
    }

    private fun storeJankTraceInfo(
        frameTimeMillis: Long,
        frameCostMillis: Int,
        stackCountEntries: List<Pair<String, Int>>,
    ) {
        mBox.put(JankInfo().apply {
            this.occurredTime = frameTimeMillis
            this.frameCost = frameCostMillis
            this.stackCountEntries = stackCountEntries
            this.resolved = false
        })
    }


    fun recordFps(start: Boolean) {
        if (start == mIsStarted) {
            return
        }
        for (frameListener in mFrameListeners) {
            frameListener.onRecord(start)
        }
        if (start) {
            mLastFrameTimeNanos = 0
            //开启定时任务
            mMainHandler.postDelayed(
                mRateRunnable,
                MS_PER_SECOND.toLong()
            )
            Choreographer.getInstance().postFrameCallback(mRateRunnable)
        } else {
            mMainHandler.removeCallbacks(mRateRunnable)
            mSampleHandler.removeCallbacks(mTracSampleTask)
            Choreographer.getInstance().removeFrameCallback(mRateRunnable)
        }
        mIsStarted = start
    }

    fun addFrameListener(frameListener: FrameListener) {
        mFrameListeners.add(frameListener)
    }

    override fun onBack2foreground() {
        recordFps(true)
    }

    override fun onFore2background() {
        recordFps(false)
    }


    interface FrameListener {
        fun onFrame(rate: Int, frameCostMillis: Int)

        fun onRecord(recording: Boolean)
    }
}