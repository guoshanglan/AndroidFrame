package com.zhuorui.commonwidget.multidirectional

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import base2app.ex.logd
import kotlin.math.abs

/**
 * date : 2020/8/24
 * dest : MultiDirectionalRecyclerView
 */
class MultiDirectionalRecyclerView : RecyclerView {

    private val mTouchSlop = 0
    private var mScrollDirection = 0 //1=vertical 2=horizontal

    private var mLastX = 0f
    private var mLastY = 0f

    private var mScrollView: LinkageHorizontalScrollView? = null

    private var isContent = true

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    fun bindScrollHead(mScrollView: LinkageHorizontalScrollView) {
        this.mScrollView = mScrollView
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (mScrollView == null /*|| adapter?.itemCount == 0*/) {
            logd(
                "mScrollView == null || adapter?.itemCount == 0"
            )
            return super.dispatchTouchEvent(event)
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastX = event.x
                mLastY = event.y
                isContent = true
                val child: View? = findChildViewUnder(event.x, event.y)
                if (child != null) {//用于判断作用域是否是主体内容
                    if (getChildViewHolder(child) !is MultiDirectionalRecyclerAdapter<*>.MultiDirectionalViewHolder<*>) {
                        isContent = false
                    }
                }
                mScrollView?.onTouchEvent(event)
            }
            MotionEvent.ACTION_MOVE -> {
                val offsetX: Float = abs(event.x - mLastX)
                val offsetY: Float = abs(event.y - mLastY)
                logd(
                    "$mScrollDirection  offsetY : $offsetY  offsetX : ${event.x - mLastX}   ${
                        mScrollView?.canScrollHorizontally(
                            (mLastX - event.x).toInt()
                        )
                    }"
                )
                if (mScrollView != null) {
                    if (mScrollView!!.canScrollHorizontally((mLastX - event.x).toInt())) {
                        if (mScrollDirection == 1) { //vertical
                            return super.dispatchTouchEvent(event)
                        } else if (mScrollDirection == 2 && isContent) { //horizontal
                            requestDisallowInterceptTouchEvent(true)
                            mScrollView?.onTouchEvent(event)
                            return true
                        } else {
                            if (offsetY > offsetX && offsetY > mTouchSlop) {
                                mScrollDirection = 1
                            } else if (offsetX > offsetY && isContent) {
                                mScrollDirection = 2
                                requestDisallowInterceptTouchEvent(true)
                                mScrollView?.onTouchEvent(event)
                                event.action = MotionEvent.ACTION_CANCEL
                                super.dispatchTouchEvent(event)
                                return true
                            }
                        }
                    } else {
                        requestDisallowInterceptTouchEvent(false)
                        return super.dispatchTouchEvent(event)
                    }
                } else {
                    return super.dispatchTouchEvent(event)
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mScrollView?.onTouchEvent(event)
                mScrollDirection = 0
            }
        }
        return super.dispatchTouchEvent(event)
    }

}