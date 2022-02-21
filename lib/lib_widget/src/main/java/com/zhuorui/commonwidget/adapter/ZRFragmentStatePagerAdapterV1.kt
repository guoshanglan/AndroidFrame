package com.zhuorui.commonwidget.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager

/**
 *    date   : 2021/10/13 13:41
 *    desc   :
 */
@SuppressLint("WrongConstant")
class ZRFragmentStatePagerAdapterV1<T>(
    fm: FragmentManager,
    private val tags: Array<T>,
    private val itemFragment: ((T) -> Fragment)
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object{

        inline fun <reified T : Fragment> getItemFragment(viewPager: ViewPager?, position: Int): T? {
            viewPager?.adapter?.let {
                return getItemFragment(it,position)
            }
            return null
        }

        inline fun <reified T : Fragment> getItemFragment(adapterV1: PagerAdapter?, position: Int): T? {
            if (adapterV1 is ZRFragmentStatePagerAdapterV1<*> && position >= 0 && position < adapterV1.count) {
                return adapterV1.fragments[position]?.let {
                    if (it is T) it else null
                }
            }
            return null
        }
    }

    val fragments: Array<Fragment?> = arrayOfNulls<Fragment?>(tags.size)

    /**
     * Return the number of views available.
     */
    override fun getCount(): Int {
        return tags.size
    }

    /**
     * Return the Fragment associated with a specified position.
     */
    override fun getItem(position: Int): Fragment {
        return itemFragment.invoke(tags[position])
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val f = super.instantiateItem(container, position)
        fragments[position] = f as Fragment
        return f
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        fragments[position] = null
        super.destroyItem(container, position, `object`)
    }



}