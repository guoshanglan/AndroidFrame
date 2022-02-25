package com.example.myframe.ui

import android.os.Bundle
import android.view.View
import androidx.navigation.INewArguments
import base2app.ex.setSafeClickListener
import base2app.viewbinding.viewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import base2app.ui.fragment.ZRMvpFragment
import com.example.myframe.R
import com.example.myframe.databinding.FragmentTestBinding
import com.zrlib.lib_service.test.TestRouterPath
import com.zrlib.matisse.Matisse
import com.zrlib.matisse.MimeType
import com.zrlib.matisse.intermal.entity.CaptureStrategy

/**

@author: guoshanglan
@description:
@date : 2022/2/18 14:57
 */

/**
 * 测试Fragment
 */
@Route(path = TestRouterPath.TestFragmentPath)
class TestFragment : ZRMvpFragment<MainFragmentView, MainFragmentPresenter>(R.layout.fragment_test),
    MainFragmentView, INewArguments {
    private val binding by viewBinding(FragmentTestBinding::bind)
    override val createPresenter: MainFragmentPresenter
        get() = MainFragmentPresenter()
    override val getView: MainFragmentView
        get() = this

    companion object {
        fun newInstance(name: String? = null): TestFragment {
            return TestFragment().apply {
                arguments = Bundle().apply {
                    putString("data", name)
                }
            }

        }
    }

    override fun onViewCreatedOnly(view: View, savedInstanceState: Bundle?) {
        super.onViewCreatedOnly(view, savedInstanceState)
        val name = arguments?.getString("data")
        binding.test.text = name ?: "无数据"
        binding.test.setSafeClickListener {
            Matisse.from(requireContext())
            .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
            .showSingleMediaType(true)
            .maxSelectable(9)
            .capture(false)
            .countable(true, false)
            .setOnResultListener { _, pathList ->
                pathList.forEachIndexed { _, path ->

                }
            }
            .forResult(100)
        }
    }

    override fun inOpenAccountTab(): Boolean {

        return false
    }

    override fun addTabBubble(tab: Int, bubbleView: View, offX: Int?, offY: Int?): Boolean {
        return false
    }

    override fun removeTabBubble(transaction: Int) {

    }

    override fun onNewArguments(args: Bundle) {
        val name = args.getString("data")
        binding.test.text = name ?: "无数据"
    }


}