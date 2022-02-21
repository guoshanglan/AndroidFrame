package com.zrlib.matisse.ui

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.pop
import base2app.ex.setSafeClickListener
import base2app.util.ToastUtil
import base2app.viewbinding.viewBinding
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import base2app.ui.fragment.ZRFragment
import com.zrlib.matisse.R
import com.zrlib.matisse.databinding.MatisseFragmentCutterBinding
import com.zrlib.matisse.intermal.entity.CutterStrategy
import com.zrlib.matisse.intermal.entity.SelectionSpec
import com.zrlib.matisse.intermal.utils.MediaStoreCompat
import com.zrlib.matisse.intermal.utils.SingleMediaScanner


/**
 *    author : liuwei
 *    e-mail : vsanliu@foxmail.com
 *    date   : 2020/9/10 15:27
 *    desc   : 图片剪裁
 */
class CutterFragment : ZRFragment(R.layout.matisse_fragment_cutter), SubsamplingScaleImageView.OnImageEventListener {

    companion object {

        fun newInstance(path: String): CutterFragment {
            return CutterFragment().apply {
                arguments = Bundle().apply {
                    putString("path", path)
                }
            }
        }
    }

    private val mSpec: SelectionSpec = SelectionSpec.getInstance()
    private var mMediaStoreCompat: MediaStoreCompat? = null
    private val binding by viewBinding(MatisseFragmentCutterBinding::bind)

    override fun onViewCreatedOnly(view: View, savedInstanceState: Bundle?) {
        super.onViewCreatedOnly(view, savedInstanceState)
        mMediaStoreCompat = MediaStoreCompat(this)
        if (mSpec.captureStrategy == null) {
            throw RuntimeException("Don't forget to set CaptureStrategy.")
        } else {
            mMediaStoreCompat?.setCaptureStrategy(mSpec.captureStrategy)
        }
        with(binding){
            mSpec.cutterStrategy?.let {
                //设置剪裁遮罩参数
                cutterMask.setAspectRatio(it.ratio)
                cutterMask.isCircle = it.maskStyle == CutterStrategy.MASK_CIRCLE
                cutterMask.setMaskColor(it.maskColor)
            }
            cutterMask.post {
                //设置剪裁区域参数
                cutterView.setCircle(cutterMask.isCircle)
                cutterView.setCutterFrame(cutterMask.frame)
            }
            buttonApply.setSafeClickListener {
                onCutter()
            }
            cutterView.setOnImageEventListener(this@CutterFragment)
            arguments?.getString("path")?.let {
                cutterView.setPath(it)
            }
        }

    }


    /**
     * 确认剪裁
     */
    private fun onCutter() {
        if (!mMediaStoreCompat!!.createCutterImageFile()) {
            ToastUtil.instance.toast(R.string.matisse_create_cutter_file_error)
            return
        }
        val path = mMediaStoreCompat!!.currentPhotoPath
        if (!binding.cutterView.cutter(path)) {
            ToastUtil.instance.toast(R.string.matisse_cutter_file_error)
            return
        }
        val uri = mMediaStoreCompat!!.currentPhotoUri
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            activity?.revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        SingleMediaScanner(context?.applicationContext, path) {}
        mSpec.onResultListener?.onSelected(arrayListOf(uri), arrayListOf(path))
        setResult(Activity.RESULT_OK)
//        setFragmentResult(RESULT_OK,Bundle())
        pop()
    }

    override fun onDestroyViewOnly() {
        binding.cutterView.setOnImageEventListener(null)
        super.onDestroyViewOnly()
    }

    override fun onReady() {
    }

    override fun onImageLoaded() {
        binding.buttonApply.isEnabled = true
    }

    override fun onPreviewLoadError(e: Exception?) {
    }

    override fun onImageLoadError(e: Exception?) {
    }

    override fun onTileLoadError(e: Exception?) {
    }

    override fun onPreviewReleased() {
    }

}