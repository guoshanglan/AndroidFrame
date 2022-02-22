package com.zhuorui.securties.debug.profile

import android.os.Build
import android.os.Debug
import android.os.Handler
import android.os.HandlerThread
import com.zhuorui.securties.debug.Background
import com.zhuorui.securties.debug.BackgroundCallback
import com.zhuorui.securties.debug.fps.FpsConstants
import com.zhuorui.securties.debug.fps.FpsMonitor
import java.util.*

/**
 * ProfileMonitor
 * @author Martin
 * @email hy569835826@163.com
 * @descraption
 * @time  17:46
 */
object ProfileMonitor : BackgroundCallback, FpsMonitor.FrameListener {

    var mIsStarted: Boolean = false

    private var mSampleHandler: Handler


    // 是否是8.0及其以上
    private var mAboveAndroidO = false

    private val mCpuMonitor = CpuMonitor()

    private val memoryMonitor = MemoryMonitor()

    private var mLastFrameRate = 0

    private val mProfileListeners = ArrayList<ProfileListener>()

    init {
        FpsMonitor.addFrameListener(this)
        Background.registerBackgroundCallback(this)
        val sampleThread = HandlerThread("profile_sample")
        sampleThread.start()
        mSampleHandler = Handler(sampleThread.looper)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAboveAndroidO = true
        }
    }

    private val mProfileSampleTask: Runnable by lazy {
        object : Runnable {
            override fun run() {
                if (Debug.isDebuggerConnected()) return

                val cpuUsageInfo = mCpuMonitor.executeCpuData(mAboveAndroidO)
                val memory = memoryMonitor.memoryData

                mProfileListeners.forEach {
                    it.onProfile(mLastFrameRate, cpuUsageInfo, memory)
                }
//                Log.d(
//                    "ProfileMonitor",
//                    "mLastFrameRate  $mLastFrameRate  cpuUsageInfo  : $cpuUsageInfo  memory  : $memory"
//                )

                mSampleHandler.postDelayed(this, FpsConstants.MS_CPU_SECOND.toLong())
            }
        }
    }

    override fun onBack2foreground() {
        startProfile(true)
    }

    override fun onFore2background() {
        startProfile(false)
    }

    fun startProfile(start: Boolean) {
        if (start == mIsStarted) {
            return
        }
        if (start) {
            mSampleHandler.postDelayed(
                mProfileSampleTask,
                FpsConstants.MS_CPU_SECOND.toLong()
            )
        } else {
            mSampleHandler.removeCallbacks(mProfileSampleTask)
        }
        mIsStarted = start
    }

    override fun onFrame(rate: Int, frameCostMillis: Int) {
        mLastFrameRate = rate
    }

    override fun onRecord(recording: Boolean) {

    }

    fun addProfileListener(mProfileListener: ProfileListener) {
        mProfileListeners.add(mProfileListener)
    }

    interface ProfileListener {
        fun onProfile(rate: Int, cpu: Float, memory: Float)
    }


}