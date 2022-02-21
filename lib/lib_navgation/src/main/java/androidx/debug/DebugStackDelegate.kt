package androidx.debug

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.R
import java.util.*

/**
 * Created by YoKey on 17/6/13.
 */
class DebugStackDelegate(private val mActivity: FragmentActivity) : SensorEventListener {
    private var mSensorManager: SensorManager? = null
    private var mStackDialog: AlertDialog? = null
    fun onCreate(mode: Int) {
        if (mode != SHAKE) return
        mSensorManager = mActivity.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensorManager!!.registerListener(
            this,
            mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun onPostCreate(mode: Int) {
        if (mode != BUBBLE) return
        val root = mActivity.findViewById<View>(android.R.id.content)
        if (root is FrameLayout) {
            val stackView = ImageView(mActivity)
            stackView.setImageResource(R.drawable.fragmentation_ic_stack)
            val params = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            params.gravity = Gravity.END
            val dp18 = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                18f,
                mActivity.resources.displayMetrics
            )
                .toInt()
            params.topMargin = dp18 * 7
            params.rightMargin = dp18
            stackView.layoutParams = params
            root.addView(stackView)
            stackView.setOnTouchListener(StackViewTouchListener(stackView, dp18 / 4))
            stackView.setOnClickListener { showFragmentStackHierarchyView() }
        }
    }

    fun onDestroy() {
        if (mSensorManager != null) {
            mSensorManager!!.unregisterListener(this)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val sensorType = event.sensor.type
        val values = event.values
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            val value = 12
            if (Math.abs(values[0]) >= value || Math.abs(values[1]) >= value || Math.abs(
                    values[2]
                ) >= value
            ) {
                showFragmentStackHierarchyView()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    /**
     * 调试相关:以dialog形式 show_psw 栈视图
     */
    fun showFragmentStackHierarchyView() {
        if (mStackDialog != null && mStackDialog!!.isShowing) return
        val container = DebugHierarchyViewContainer(mActivity)
        container.bindFragmentRecords(fragmentRecords)
        container.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mStackDialog = AlertDialog.Builder(mActivity)
            .setView(container)
            .setPositiveButton(android.R.string.cancel, null)
            .setCancelable(true)
            .create()
        mStackDialog?.show()
    }

    private val fragmentRecords: List<DebugFragmentRecord?>?
        private get() {
            val fragmentRecordList: MutableList<DebugFragmentRecord?> = ArrayList()
            val fragmentList = mActivity.supportFragmentManager.fragments
            if (fragmentList.size < 1) return null
            for (fragment in fragmentList) {
                addDebugFragmentRecord(fragmentRecordList, fragment)
            }
            return fragmentRecordList[0]?.childFragmentRecord?:fragmentRecordList
        }

    private fun addDebugFragmentRecord(
        fragmentRecords: MutableList<DebugFragmentRecord?>,
        fragment: Fragment?
    ) {
        if (fragment != null) {
            val backStackCount = fragment.parentFragmentManager.backStackEntryCount
            var name: CharSequence = fragment.javaClass.simpleName
            if (backStackCount == 0) {
                name = span(name)
            } else {
                for (j in 0 until backStackCount) {
                    val entry = fragment.parentFragmentManager
                        .getBackStackEntryAt(j)
                    if (entry.name != null && entry.name == fragment.tag
                        || entry.name == null && fragment.tag == null
                    ) {
                        break
                    }
                    if (j == backStackCount - 1) {
                        name = span(name)
                    }
                }
            }
            fragmentRecords.add(DebugFragmentRecord(name, getChildFragmentRecords(fragment)))
        }
    }

    private fun span(name: CharSequence): CharSequence {
        var name = name
        name = "$name *"
        return name
    }

    private fun getChildFragmentRecords(parentFragment: Fragment): List<DebugFragmentRecord?>? {
        val fragmentRecords: MutableList<DebugFragmentRecord?> = ArrayList()
        try {
            val fragmentList = parentFragment.childFragmentManager.fragments
            if (fragmentList.size < 1) return null
            for (i in fragmentList.indices.reversed()) {
                val fragment = fragmentList[i]
                addDebugFragmentRecord(fragmentRecords, fragment)
            }
        } catch (e: IllegalStateException) {
            // TODO 添加临时代码，分析此异常java.lang.IllegalStateException: Fragment has not been attached yet.
            val message = parentFragment.javaClass.name + "->" + e.message
            throw IllegalStateException(message)
        }
        return fragmentRecords
    }

    private inner class StackViewTouchListener internal constructor(
        private val stackView: View,
        private val clickLimitValue: Int
    ) : OnTouchListener {
        private var dX = 0f
        private var dY = 0f
        private var downX = 0f
        private var downY = 0f
        private var isClickState = false
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val X = event.rawX
            val Y = event.rawY
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isClickState = true
                    downX = X
                    downY = Y
                    dX = stackView.x - event.rawX
                    dY = stackView.y - event.rawY
                }
                MotionEvent.ACTION_MOVE -> if (Math.abs(X - downX) < clickLimitValue && Math.abs(Y - downY) < clickLimitValue && isClickState) {
                    isClickState = true
                } else {
                    isClickState = false
                    stackView.x = event.rawX + dX
                    stackView.y = event.rawY + dY
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> if (X - downX < clickLimitValue && isClickState) {
                    stackView.performClick()
                }
                else -> return false
            }
            return true
        }
    }

    companion object {
        /**
         * Dont display stack view.
         */
        const val NONE = 0

        /**
         * Shake it to display stack view.
         */
        const val SHAKE = 1

        /**
         * As a bubble display stack view.
         */
        const val BUBBLE = 2
    }
}