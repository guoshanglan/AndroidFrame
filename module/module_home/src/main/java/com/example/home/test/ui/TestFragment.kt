package com.example.home.test.ui

import android.os.Bundle
import android.view.View
import androidx.navigation.INewArguments
import base2app.ex.setSafeClickListener
import base2app.ex.startTo
import base2app.viewbinding.viewBinding
import base2app.ui.fragment.ZRMvpFragment
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.home.R
import com.example.home.databinding.HomeFragmentTestBinding
import com.example.home.test.view.MainFragmentView
import com.example.myframe.ui.MainFragmentPresenter
import com.zrlib.lib_service.home.HomeRouter
import com.zrlib.lib_service.home.HomeRouterPath
import com.zrlib.lib_service.route.route
import com.zrlib.matisse.Matisse
import com.zrlib.matisse.MimeType

/**
 * 测试Fragment
 */
@Route(path = HomeRouterPath.TestFragmentPath)
class TestFragment : ZRMvpFragment<MainFragmentView, MainFragmentPresenter>(R.layout.home_fragment_test),
    MainFragmentView, INewArguments {
    private val binding by viewBinding(HomeFragmentTestBinding::bind)

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
           //
            route<HomeRouter>()?.toSecondFragment("111")?.startTo()
//            Matisse.from(requireContext())
//            .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
//            .showSingleMediaType(true)
//            .maxSelectable(9)
//            .capture(false)
//            .countable(true, false)
//            .setOnResultListener { _, pathList ->
//
//
//                }
//            .forResult(100)
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