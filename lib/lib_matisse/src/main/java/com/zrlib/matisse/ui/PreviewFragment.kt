package com.zrlib.matisse.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.pop
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2
import base2app.util.JsonUtil
import base2app.viewbinding.viewBinding
import com.google.gson.reflect.TypeToken
import base2app.ui.fragment.ZRFragment
import com.zrlib.matisse.Matisse
import com.zrlib.matisse.R
import com.zrlib.matisse.databinding.MatisseFragmentPreviewBinding
import com.zrlib.matisse.intermal.entity.SelectionSpec
import com.zrlib.matisse.listener.OnFragmentInteractionListener
import com.zrlib.matisse.ui.adapter.PreviewPathPagerAdapter
import java.util.*

/**
 *    date   : 2020/8/18 11:09
 *    desc   : 查看大图页面
 */
class PreviewFragment : ZRFragment(R.layout.matisse_fragment_preview),
    OnFragmentInteractionListener, Matisse.SelectedPositionListener {

    companion object {
        const val PREVIEW_LIST = "preview_list"
        const val PREVIEW_ITEM = "preview_item"

        fun newInstance(
            item: IPreviewItem?,
            list: List<IPreviewItem>?,
            errRes: Int? = 0
        ): PreviewFragment {
            val items = mutableListOf<PreviewItem>()
            var pos = 0
            if (errRes != 0) {
                items.add(PreviewItem("", 0, 0, errRes))
            } else {
                pos = list?.indexOf(item) ?: 0
                list?.forEach { items.add(PreviewItem(it.path(), it.width(), it.width())) }
            }
            return PreviewFragment().apply {
                arguments = Bundle().apply {
                    putInt(PREVIEW_ITEM, pos)
                    putString(PREVIEW_LIST, JsonUtil.toJson(items))

                }
            }
        }
    }

    private var mIsAlreadySetPosition = false
    private var mPager: ViewPager2? = null
    private var mAdapter: PreviewPathPagerAdapter? = null
    private var selected: Int = 0
    private val binding by viewBinding(MatisseFragmentPreviewBinding::bind)


    private val mPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {

        @SuppressLint("SetTextI18n")
        override fun onPageSelected(position: Int) {

            binding.num.text = "${position + 1}/${mAdapter?.itemCount ?: 0}"

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.matisse_transition)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //Todo ContextThemeWrapper
        return super.onCreateView(
            inflater.cloneInContext(
                ContextThemeWrapper(
                    activity,
                    SelectionSpec.getInstance().themeId
                )
            ),
            container,
            savedInstanceState
        )
    }

    override fun onViewCreatedOnly(view: View, savedInstanceState: Bundle?) {
        super.onViewCreatedOnly(view, savedInstanceState)
        mPager = view.findViewById(R.id.pager)

        mPager?.registerOnPageChangeCallback(mPageChangeCallback)
        mAdapter = PreviewPathPagerAdapter(this)
        mPager?.adapter = mAdapter
        arguments?.let { bundle ->
            val type = object : TypeToken<ArrayList<PreviewItem>>() {}.type
            val list: ArrayList<PreviewItem>? =
                JsonUtil.fromJson(bundle.getString(PREVIEW_LIST) ?: "[]", type)
            if (list.isNullOrEmpty()) {
                return
            }
            if (list.size == 1 && null != list.first().errRes && list.first().errRes != 0) {
                binding.num.visibility = View.GONE
            }
            mPager?.adapter?.let { adapter ->
                if (adapter is PreviewPathPagerAdapter) {
                    adapter.addAll(list)
                    adapter.notifyDataSetChanged()
                }
            }
            if (!mIsAlreadySetPosition) {
                //onAlbumMediaLoad is called many times..
                mIsAlreadySetPosition = true
                arguments?.let {
                    selected = bundle.getInt(PREVIEW_ITEM, 0)
                    //  mPageChangeCallback.onPageSelected(selected)
                    mPager?.setCurrentItem(selected, false)

                }

            }

        }


    }


    /**
     * ImageViewTouch 被点击了
     */
    override fun onClick() {
        pop()
    }

    fun getViewPage(): ViewPager2? {
        return mPager
    }


    override fun getPosition(): Int {
        return mPager?.currentItem ?: 0
    }

    override fun onDestroyViewOnly() {
        mPager?.unregisterOnPageChangeCallback(mPageChangeCallback)
        super.onDestroyViewOnly()
    }
}


