package com.example.home.test.ui

import base2app.ex.safeString
import base2app.ui.fragment.ZRFragment
import base2app.viewbinding.viewBinding
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.example.home.R
import com.example.home.databinding.HomeFragmentSecondBinding
import com.zrlib.lib_service.home.HomeRouterPath

/**

@author: guoshanglan
@description:
@date : 2022/3/2 9:38
 */
@Route(path = HomeRouterPath.SecondFragmentPath)
class SecondFragment : ZRFragment(R.layout.home_fragment_second) {
    private val binding by viewBinding(HomeFragmentSecondBinding::bind)

    override fun onViewCreatedLazy() {
        super.onViewCreatedLazy()
        arguments?.safeString("path")?.let {
            Glide.with(binding.ivImage).load(it).into(binding.ivImage)
        }
    }


}