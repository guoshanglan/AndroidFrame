package com.zhuorui.commonwidget.adapter

import android.annotation.SuppressLint
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * ZRCommonFragmentAdapterV1
 * @descraption
 * @time 2021/9/13 14:15
 */
@SuppressLint("WrongConstant")
open class ZRCommonFragmentAdapterV1<T>(
    mFragmentManager: FragmentManager,
    var tags: Array<T>,
    private val itemFragment: ((T) -> Fragment)
) : FragmentPagerAdapter(mFragmentManager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return itemFragment(tags[position])
    }

    override fun getCount(): Int {
        return tags.size
    }

    open fun getFragmentTag(viewId: Int,position: Int): String? {
        return "android:switcher:$viewId:${getItemId(position)}"
    }
}