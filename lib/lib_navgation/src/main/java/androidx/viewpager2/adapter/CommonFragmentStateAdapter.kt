package androidx.viewpager2.adapter

import androidx.fragment.app.Fragment

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2021/7/16 18:06
 *    desc   :
 */
class CommonFragmentStateAdapter<T>(
    fragment: Fragment,
    private val tags: List<T>,
    private val itemFragment: ((T) -> Fragment)
) : FragmentStateAdapter(fragment) {

    private val mItemIds = arrayOfNulls<Long>(tags.size)

    init {
        tags.forEachIndexed { index, t ->
            mItemIds[index] = getItemIdByTag(t)
        }
    }

    fun findFragment(position:Int): Fragment? {
        if (position < 0 || position >= mItemIds.size)return null
        return mFragments.get(getItemId(position))
    }

    private fun getItemIdByTag(tag: T): Long {
        return when (tag) {
            is Long -> tag
            is Int -> tag.toLong()
            is Enum<*> -> tag.ordinal.toLong()
            is String -> tag.hashCode().toLong()
            is FragmentItem -> tag.getItemId()
            else -> throw IllegalArgumentException("tag ->> ${(tag as Any)::class.java} ,Illegal Argument,tag support Long,Int,Enum,String,FragmentItem")
        }
    }

    override fun containsItem(itemId: Long): Boolean {
        return mItemIds.contains(itemId)
    }

    override fun getItemId(position: Int): Long {
        return mItemIds[position]!!
    }

    override fun getItemCount(): Int {
        return tags.size
    }

    override fun createFragment(position: Int): Fragment {
        return itemFragment.invoke(tags[position])
    }

    interface FragmentItem {

        fun getItemId(): Long

    }
}