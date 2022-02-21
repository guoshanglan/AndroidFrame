package com.zrlib.matisse.ui

import android.database.Cursor
import android.os.Bundle
import com.zrlib.matisse.intermal.entity.Album
import com.zrlib.matisse.intermal.entity.Item
import com.zrlib.matisse.intermal.model.AlbumMediaCollection
import com.zrlib.matisse.ui.adapter.PreviewPagerAdapter
import java.util.*

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2020/8/17 11:11
 *    desc   :
 */
class AlbumPreviewFragment : BasePreviewFragment(), AlbumMediaCollection.AlbumMediaCallbacks {

    companion object {
        const val EXTRA_ALBUM = "extra_album"
        const val EXTRA_ITEM = "extra_item"
    }

    private val mCollection = AlbumMediaCollection()
    private var mIsAlreadySetPosition = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        mCollection.onCreate(this, this)
        super.onActivityCreated(savedInstanceState)
    }

    override fun onViewCreatedLazy() {
        super.onViewCreatedLazy()
        arguments?.let { bundle ->
            val album: Album? = bundle.getParcelable(EXTRA_ALBUM)
            mCollection.load(album)
            mSelectedCollection?.let {
                val item: Item? = bundle.getParcelable(EXTRA_ITEM)
                if (mCheckView?.isCountable == true) {
                    mCheckView?.setCheckedNum(it.checkedNumOf(item))
                } else {
                    mCheckView?.setChecked(it.isSelected(item))
                }
            }
        }
    }

    override fun onDestroy() {
        mCollection.onDestroy()
        super.onDestroy()
    }

    override fun onAlbumMediaLoad(cursor: Cursor?) {
        val items: MutableList<Item> = ArrayList<Item>()
        while (cursor!!.moveToNext()) {
            items.add(Item.valueOf(cursor))
        }
        if (items.isEmpty()) {
            return
        }
        mPager?.adapter?.let { adapter ->
            if (adapter is PreviewPagerAdapter) {
                adapter.addAll(items)
                adapter.notifyDataSetChanged()
            }
        }
        if (!mIsAlreadySetPosition) {
            //onAlbumMediaLoad is called many times..
            mIsAlreadySetPosition = true
            arguments?.let {
                val selected: Item? = it.getParcelable(EXTRA_ITEM)
                val selectedIndex = items.indexOf(selected)
                mPager?.setCurrentItem(selectedIndex, false)
                mPreviousPos = selectedIndex
            }

        }
    }

    override fun onAlbumMediaReset() {
    }


}