package com.example.myframe.ui

import android.app.Activity
import android.os.Bundle
import android.util.ArrayMap
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.switchFragment
import androidx.navigation.INewArguments
import androidx.navigation.SingleDestiantion
import base2app.util.AppUtil
import base2app.util.ToastUtil
import base2app.viewbinding.viewBinding
import com.example.myframe.ui.MainFragmentView.Companion.COMMUNITY
import com.example.myframe.ui.MainFragmentView.Companion.FIND
import com.example.myframe.ui.MainFragmentView.Companion.KEY_CHILD_DATA
import com.example.myframe.ui.MainFragmentView.Companion.KEY_TYPE
import com.example.myframe.ui.MainFragmentView.Companion.MARKET
import com.example.myframe.ui.MainFragmentView.Companion.MINE
import com.example.myframe.ui.MainFragmentView.Companion.TRANSACTION
import com.example.myframe.view.BottomBar
import com.example.myframe.view.BottomBarTab
import com.example.myframe.view.ZoomIconTab
import base2app.ui.fragment.ZRFragment
import base2app.ui.fragment.ZRMvpFragment
import com.example.myframe.R
import com.example.myframe.databinding.AppFragmentMainBinding

import com.zrlib.lib_service.base.CommService
import java.lang.ref.SoftReference

/**
 * Date: 2019/8/6
 * Desc: 主界面
 */
class MainFragment :
    ZRMvpFragment<MainFragmentView, MainFragmentPresenter>(R.layout.app_fragment_main),
    MainFragmentView, SingleDestiantion {
    private val binding by viewBinding(AppFragmentMainBinding::bind)

    override val createPresenter: MainFragmentPresenter
        get() = MainFragmentPresenter()

    override val getView: MainFragmentView
        get() = this

    /**
     * 底部导航
     */
    private val tabs = arrayOf(FIND, MARKET, TRANSACTION, COMMUNITY, MINE)
    private val tabName = arrayOf("发现", "市场", "交易", "社区", "我的")
    /**
     * tab 汽泡提示View缓存
     */
    private var mTabBubbleView: ArrayMap<Int, SoftReference<View>>? = null
    private var mSelectType: Int = FIND

    override fun isSupportSwipeBack(): Boolean {
        return false
    }

    /**
     * 获取交易/开户title
     */

    private fun typeToTag(type: Int): String {
        return "main.$type"
    }

    private fun tagToType(tag: String): Int {
        return tag.substring("main.".length).toInt()
    }

    override fun onNewArguments(args: Bundle) {
        view?.post {
            val type = args.getInt(KEY_TYPE, -1)
            if (type != -1) {
                binding.bottomBar.setCurrentItem(tabs.indexOf(type))
                switchTargetFragment(type, args.getBundle(KEY_CHILD_DATA))
            }

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("mSelectType", mSelectType)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            mSelectType = it.getInt("mSelectType", mSelectType)
        }
        CommService.instance.getNetworkConnectLiveData().observe(this, { connected ->
            if (connected) {
                //网络恢复拉取版本升级，弹窗数据
                presenter?.loadVersionAndPopData()
            }
        })
    }

    override fun onViewCreatedOnly(view: View, savedInstanceState: Bundle?) {
        super.onViewCreatedOnly(view, savedInstanceState)
        val bBar = binding.bottomBar
        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            FragmentBackPressedCallback(bBar)
        )
        initBottomBar(bBar)
        val bundle=Bundle().apply {
            putString("data",tabName[mSelectType])
        }
        switchTargetFragment(mSelectType,bundle)
    }

    /**
     * 初始化底部菜单导航
     */
    private fun initBottomBar(bBar: BottomBar) {
        val act = requireActivity()
        tabs.forEach {
            val tab = when (it) {
                MARKET -> BottomBarTab(
                    act,
                    R.drawable.app_tab_market,
                    R.string.app_market_info
                )
                TRANSACTION -> BottomBarTab(
                    act,
                    R.drawable.app_tab_transaction,
                    R.string.app_open_account
                )
                FIND -> ZoomIconTab(
                    act,
                    R.drawable.app_tab_zhuorui,
                    R.string.app_main_tab_zhuorui
                )
                COMMUNITY -> BottomBarTab(
                    act,
                    R.drawable.app_tab_community,
                    R.string.app_community
                )
                MINE -> BottomBarTab(
                    act,
                    R.drawable.app_tab_mine,
                    R.string.app_my_self
                )
                else -> BottomBarTab(requireContext(), 0, 0)
            }
            bBar.addItem(tab)
        }
        bBar.setOnTabSelectedListener(object : BottomBar.OnTabSelectedListener {
            override fun onTabSelected(position: Int, prePosition: Int) {
                val bundle=Bundle().apply {
                    putString("data",tabName[position])
                }
                switchTargetFragment(tabs[position],bundle)
            }

            override fun onTabUnselected(position: Int) {

            }

            override fun onTabReselected(position: Int) {
            }

        })
        bBar.setCurrentItem(tabs.indexOf(mSelectType))
    }

    override fun onViewCreatedLazy() {
        super.onViewCreatedLazy()
        presenter?.lazyInit()
    }

    /**
     * 选中对应的fragment
     */
    private fun switchTargetFragment(type: Int, auments: Bundle? = null) {
        mSelectType = type
        switchFragment(R.id.fl_tab_container, typeToTag(type)) {
            when (tagToType(it)) {
                MARKET -> TestFragment.newInstance("市场")
                TRANSACTION -> TestFragment.newInstance("交易")
                FIND -> TestFragment.newInstance("发现")
                COMMUNITY -> TestFragment.newInstance("社区")
                MINE -> TestFragment.newInstance("我的")
                else -> ZRFragment()
            }.apply { arguments = auments }
        }?.let {
            if (auments != null && it is INewArguments) {
                it.onNewArguments(auments)
            }
        }

    }


    override fun inOpenAccountTab(): Boolean {
        return tabs[binding.bottomBar.currentItemPosition] == TRANSACTION
    }

    /**
     * 显示tab汽泡提示
     */
    override fun addTabBubble(tab: Int, bubbleView: View, offX: Int?, offY: Int?): Boolean {
        val rootView = if (view is FrameLayout) view as FrameLayout else return false
        removeTabBubble(tab)
        val tabView = binding.bottomBar.getItem(tabs.indexOf(tab)) ?: return false
        val lp = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        val location = IntArray(2)
        tabView.mIcon.getLocationInWindow(location)
        if (location[0] > 0) {
            lp.gravity = Gravity.BOTTOM
            lp.bottomMargin = binding.bottomBar.height + (offY ?: 0)
            lp.leftMargin = location[0] - (offX ?: 0)
            rootView.addView(bubbleView, lp)
            if (mTabBubbleView == null) {
                mTabBubbleView = ArrayMap()
            }
            mTabBubbleView?.put(tab, SoftReference(bubbleView))
            return true
        }
        return false
    }

    /**
     * 移除Tab汽泡提示
     */
    override fun removeTabBubble(transaction: Int) {
        val bubbleView = mTabBubbleView?.remove(transaction)?.get() ?: return
        bubbleView.parent?.let {
            if (it is ViewGroup) it else null
        }?.removeView(bubbleView)
    }


    class FragmentBackPressedCallback(view: BottomBar) : OnBackPressedCallback(true) {

        private val mBottomBar = SoftReference(view)
        private var lastIndex = 0
        private var lastBackPressedTime = 0L

        override fun handleOnBackPressed() {
            val bottomBar = mBottomBar.get() ?: kotlin.run {
                isEnabled = false
                return
            }
            val oldTime = lastBackPressedTime
            val oldIndex = lastIndex
            lastBackPressedTime = System.currentTimeMillis()
            lastIndex = mBottomBar.get()?.currentItemPosition ?: oldIndex
            if (lastIndex == oldIndex && lastBackPressedTime - oldTime < 1500) {
                isEnabled = false
                (bottomBar.context as Activity).onBackPressed()
            } else {
                val ctx = bottomBar.context
                ToastUtil.instance.toast(
                    ctx.getString(
                        R.string.press_again_exit,
                        AppUtil.getAppName(ctx) ?: ""
                    )
                )
            }

        }

    }

}