package com.zrlib.matisse.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.pop
import androidx.viewpager.widget.ViewPager
import base2app.ex.color
import base2app.ex.dp2px
import base2app.ex.setSafeClickListener
import base2app.viewbinding.viewBinding
import base2app.ui.fragment.ZRFragment
import com.zrlib.matisse.R
import com.zrlib.matisse.databinding.MatisseFragmentMediaPreviewBinding
import com.zrlib.matisse.intermal.entity.IncapableCause
import com.zrlib.matisse.intermal.entity.Item
import com.zrlib.matisse.intermal.entity.SelectionSpec
import com.zrlib.matisse.intermal.model.SelectedItemCollection
import com.zrlib.matisse.listener.OnFragmentInteractionListener
import com.zrlib.matisse.ui.adapter.PreviewPagerAdapter
import com.zrlib.matisse.ui.widget.CheckView

/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2020/8/17 10:57
 *    desc   :
 */
open class BasePreviewFragment : ZRFragment(R.layout.matisse_fragment_media_preview), ViewPager.OnPageChangeListener,
    OnFragmentInteractionListener {

    companion object {
        const val EXTRA_DEFAULT_BUNDLE = "extra_default_bundle"
        const val EXTRA_RESULT_BUNDLE = "extra_result_bundle"
        const val EXTRA_RESULT_APPLY = "extra_result_apply"
        const val EXTRA_RESULT_ORIGINAL_ENABLE = "extra_result_original_enable"
        const val CHECK_STATE = "checkState"
    }

    override fun isSupportSwipeBack(): Boolean {
        return false
    }

    protected var mSelectedCollection: SelectedItemCollection? = null
    protected var mSpec: SelectionSpec? = null
    protected var mOriginalEnable = false
    protected var mPager: ViewPager? = null
    protected var mAdapter: PreviewPagerAdapter? = null
    protected var mCheckView: CheckView? = null
    protected var mPreviousPos = -1
    private val binding by viewBinding(MatisseFragmentMediaPreviewBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSpec = SelectionSpec.getInstance()
//        if (mSpec!!.needOrientationRestriction()) {
////            setRequestedOrientation(mSpec!!.orientation)
//        }
        requireActivity().onBackPressedDispatcher.addCallback(this,object :OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                sendBackResult(false)
                pop()
            }

        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mSelectedCollection?.onSaveInstanceState(outState)
        outState.putBoolean("checkState", mOriginalEnable)
        super.onSaveInstanceState(outState)
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
        with(binding){
            CheckView(view.context).let {
                val padding = 10f.dp2px().toInt()
                it.layoutParams =
                    ConstraintLayout.LayoutParams(40f.dp2px().toInt(), 40f.dp2px().toInt())
                it.setPadding(padding, padding, padding, padding)
                mCheckView = it
                it.setDefaultColor(color(R.color.matisse_item_bdColor))
                topbar.addRightView(it)
            }
            topbar.setBackClickListener {
                sendBackResult(false)
                pop()
            }
            buttonApply.setSafeClickListener {
                sendBackResult(true)
                pop()
            }
        }
        mPager = view.findViewById(R.id.pager)
        mPager?.addOnPageChangeListener(this)
        mAdapter = PreviewPagerAdapter(childFragmentManager, null)
        mPager?.adapter = mAdapter
        mCheckView?.isCountable = mSpec?.preCountable

        mCheckView?.setSafeClickListener {
            val item: Item =
                mAdapter?.getMediaItem(mPager?.currentItem ?: 0) ?: return@setSafeClickListener
            if (mSelectedCollection?.isSelected(item) == true) {
                mSelectedCollection?.remove(item)
                if (mCheckView?.isCountable == true) {
                    mCheckView?.setCheckedNum(CheckView.UNCHECKED)
                } else {
                    mCheckView?.setChecked(false)
                }
            } else {
                if (assertAddSelection(item)) {
                    mSelectedCollection?.add(item)
                    if (mCheckView?.isCountable == true) {
                        mCheckView?.setCheckedNum(mSelectedCollection?.checkedNumOf(item) ?: 0)
                    } else {
                        mCheckView?.setChecked(true)
                    }
                }
            }
            updateApplyButton()
            mSpec?.onSelectedListener?.onSelected(
                mSelectedCollection!!.asListOfUri(), mSelectedCollection!!.asListOfString()
            )
        }
    }

//    override fun onCreateFragmentAnimator(): FragmentAnimator {
//        return DefaultVerticalAnimator()
//    }

    protected open fun sendBackResult(apply: Boolean) {
        Bundle().apply {
            putBundle(EXTRA_RESULT_BUNDLE, mSelectedCollection!!.dataWithBundle)
            putBoolean(EXTRA_RESULT_APPLY, apply)
            putBoolean(EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable)
        }.let {
            setResult(Activity.RESULT_OK,it)
//            setFragmentResult(ISupportFragment.RESULT_OK, it)
        }
    }

    private fun assertAddSelection(item: Item): Boolean {
        val cause: IncapableCause? = mSelectedCollection!!.isAcceptable(item)
        IncapableCause.handleCause(context, cause)
        return cause == null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        arguments?.let {
            mOriginalEnable = if (savedInstanceState == null) {
                mSelectedCollection?.onCreate(it.getBundle(EXTRA_DEFAULT_BUNDLE))
                it.getBoolean(EXTRA_RESULT_ORIGINAL_ENABLE, false)
            } else {
                mSelectedCollection?.onCreate(savedInstanceState)
                savedInstanceState.getBoolean(CHECK_STATE)
            }
        }
        super.onActivityCreated(savedInstanceState)
        updateApplyButton()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mSelectedCollection = SelectedItemCollection(context)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        val adapter = mPager!!.adapter as PreviewPagerAdapter?
        if (mPreviousPos != -1 && mPreviousPos != position) {
            (adapter!!.instantiateItem(mPager!!, mPreviousPos) as PreviewItemFragment).resetView()
            val item = adapter.getMediaItem(position)
            if (mCheckView?.isCountable == true) {
                val checkedNum = mSelectedCollection!!.checkedNumOf(item)
                mCheckView?.setCheckedNum(checkedNum)
                if (checkedNum > 0) {
                    mCheckView?.isEnabled = true
                } else {
                    mCheckView?.isEnabled = !mSelectedCollection!!.maxSelectableReached()
                }
            } else {
                val checked = mSelectedCollection!!.isSelected(item)
                mCheckView?.setChecked(checked)
                if (checked) {
                    mCheckView?.isEnabled = true
                } else {
                    mCheckView?.isEnabled = !mSelectedCollection!!.maxSelectableReached()
                }
            }
//            updateSize(item)
        }
        mPreviousPos = position
    }

    private fun updateApplyButton() {
        with(binding){
            val selectedCount = mSelectedCollection!!.count()
            if (selectedCount == 0) {
                buttonApply.isEnabled = false
            } else if (selectedCount == 1 && mSpec!!.singleSelectionModeEnabled()) {
                buttonApply.isEnabled = true
            } else {
                buttonApply.isEnabled = true
            }
        }
    }

    /**
     * ImageViewTouch 被点击了
     */
    override fun onClick() {
        if (!mSpec!!.autoHideToobar) {
            return
        }
    }


}