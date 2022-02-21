package com.zhuorui.commonwidget.multidirectional

import android.util.ArrayMap
import android.view.View

/**
 * @date 2020/4/18 15:44
 * @desc 联动数据缓存及处理类
 */
class MultiDirectionalDelegate : LinkageHorizontalScrollView.OnScrollListener {

    private val mCacheItemViews: ArrayMap<Int, LinkageHorizontalScrollView> = ArrayMap()

    /**
     * 头部联动View
     */
    private var headerLinkageScrollView: LinkageHorizontalScrollView? = null

    companion object {
        fun create(): MultiDirectionalDelegate {
            return MultiDirectionalDelegate()
        }
    }

    /**
     * 添加头部联动View
     */
    fun cacheHeaderLinkage(headerLinkageScrollView: LinkageHorizontalScrollView) {
        this.headerLinkageScrollView = headerLinkageScrollView
        headerLinkageScrollView.addOnScrollListener(this)
    }

    /**
     * 添加需要联动的ScrollView
     */
    fun cacheLinkageView(target: LinkageHorizontalScrollView) {
        val targetKey = target.hashCode()
        target.setScrollable(false)
        headerLinkageScrollView?.let {
            target.scrollTo(it.scrollX, 0)
        }

        //当布局变化 , 布局变动导致的误差问题
        target.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View?,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                headerLinkageScrollView?.let {
                    target.scrollTo(it.scrollX, 0)
                }
                target.removeOnLayoutChangeListener(this)
            }
        })

        mCacheItemViews[targetKey] = target
    }

    fun removeCache(target: LinkageHorizontalScrollView){
        val targetKey = target.hashCode()
        mCacheItemViews.remove(targetKey)
    }


    fun clearCacheItemView() {
        if (mCacheItemViews.size > 0) {
            mCacheItemViews.clear()
            this.headerLinkageScrollView?.let {
                cacheHeaderLinkage(it)
            }
        }
    }

    override fun onScroll(scrollX: Int) {
//        logd(" onScroll  :$scrollX ")
        mCacheItemViews.forEach {
            val itemScrollView = it.value
            itemScrollView.smoothScrollTo(scrollX, 0)
        }
    }
}