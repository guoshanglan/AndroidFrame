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
    private val path:String="https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fimg.jj20.com%2Fup%2Fallimg%2F1114%2F0G320105A7%2F200G3105A7-4-1200.jpg&refer=http%3A%2F%2Fimg.jj20.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1657851921&t=c0af3efd72dae8031969ac3b1355cb2e"

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
            route<HomeRouter>()?.toSecondFragment(path)?.startTo()
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

    override fun onNewArguments(args: Bundle) {
        val name = args.getString("data")
        binding.test.text = name ?: "无数据"
    }


}