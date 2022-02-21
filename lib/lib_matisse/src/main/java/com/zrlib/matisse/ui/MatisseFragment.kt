package com.zrlib.matisse.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.*
import android.os.Looper.getMainLooper
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.findChildFragment
import androidx.fragment.app.pop
import androidx.navigation.Dest
import base2app.ex.color
import base2app.ex.dp2px
import base2app.ex.setSafeClickListener
import base2app.viewbinding.viewBinding
import base2app.ui.fragment.ZRFragment
import com.zrlib.matisse.R
import com.zrlib.matisse.databinding.MatisseFragmentBinding
import com.zrlib.matisse.intermal.entity.Album
import com.zrlib.matisse.intermal.entity.Item
import com.zrlib.matisse.intermal.entity.SelectionSpec
import com.zrlib.matisse.intermal.model.AlbumCollection
import com.zrlib.matisse.intermal.model.SelectedItemCollection
import com.zrlib.matisse.intermal.utils.MediaStoreCompat
import com.zrlib.matisse.intermal.utils.PathUtils
import com.zrlib.matisse.intermal.utils.SingleMediaScanner
import com.zrlib.matisse.ui.AlbumPreviewFragment.Companion.EXTRA_ALBUM
import com.zrlib.matisse.ui.AlbumPreviewFragment.Companion.EXTRA_ITEM
import com.zrlib.matisse.ui.BasePreviewFragment.Companion.EXTRA_DEFAULT_BUNDLE
import com.zrlib.matisse.ui.BasePreviewFragment.Companion.EXTRA_RESULT_APPLY
import com.zrlib.matisse.ui.adapter.AlbumMediaAdapter
import com.zrlib.matisse.ui.adapter.AlbumsAdapter
import com.zrlib.matisse.ui.widget.AlbumsSpinner
import com.zrlib.permission.ZRPermission
import com.zrlib.permission.bean.Permission
import com.zrlib.permission.callbcak.CheckRequestPermissionsListener
import java.util.*

/**
 *    date   : 2020/8/14 14:42
 *    desc   :
 */
class MatisseFragment : ZRFragment(R.layout.matisse_fragment), AlbumCollection.AlbumCallbacks,
    MediaSelectionFragment.SelectionProvider, AlbumMediaAdapter.CheckStateListener,
    AlbumMediaAdapter.OnMediaClickListener, AlbumMediaAdapter.OnPhotoCapture,
    AdapterView.OnItemSelectedListener {

    companion object {
        const val EXTRA_RESULT_SELECTION = "extra_result_selection"
        const val EXTRA_RESULT_SELECTION_PATH = "extra_result_selection_path"
        const val EXTRA_RESULT_ORIGINAL_ENABLE = "extra_result_original_enable"
        private const val REQUEST_CODE_PREVIEW = 33
        private const val REQUEST_CODE_CAPTURE = 34
        private const val REQUEST_CODE_CUTTER = 35

        fun newInstance(): MatisseFragment {
            return MatisseFragment()
        }
    }

    private var mAlbumCollection: AlbumCollection = AlbumCollection()
    private var mSelectedCollection: SelectedItemCollection? = null
    private var mMediaStoreCompat: MediaStoreCompat? = null
    private var mAlbumsSpinner: AlbumsSpinner? = null
    private var mAlbumsAdapter: AlbumsAdapter? = null
    private val mSpec: SelectionSpec = SelectionSpec.getInstance()
    private var confirmBtn: TextView? = null
    private var mOriginalEnable = false
    private val binding by viewBinding(MatisseFragmentBinding::bind)
    private var handler: Handler = Handler(getMainLooper())
    private var runnable: Runnable = Runnable {
        onPhotoRefresh()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mSelectedCollection = SelectedItemCollection(context)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mSelectedCollection?.onSaveInstanceState(outState)
        mAlbumCollection.onSaveInstanceState(outState)
    }

    override fun onViewCreatedOnly(view: View, savedInstanceState: Bundle?) {
        super.onViewCreatedOnly(view, savedInstanceState)
        if (!mSpec.cutter && !mSpec.isSingleSelect) {
            with(binding) {
                topbar.getTextView(R.string.matisse_str_insert).let {
                    it.setSafeClickListener {
                        callbackResult(
                            mSelectedCollection?.asListOfUri()!!,
                            mSelectedCollection?.asListOfString()!!
                        )
                    }
                    confirmBtn = it
                    topbar.addRightView(it)
                }
            }
        }

        val titleView = TextView(requireContext()).apply {
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            setTextColor(color(R.color.main_content_text_color))
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            maxWidth = 250f.dp2px().toInt()
        }
        binding.topbar.titleView = titleView
        mSelectedCollection?.onCreate(savedInstanceState)
        mAlbumsAdapter = AlbumsAdapter(requireContext(), null, false)
        mAlbumsSpinner = AlbumsSpinner(requireContext()).also {
            it.setOnItemSelectedListener(this)
            it.setSelectedTextView(titleView)
            it.setPopupAnchorView(binding.topbar)
            it.setAdapter(mAlbumsAdapter)
        }
        mAlbumCollection.onCreate(this, this)
        mAlbumCollection.onRestoreInstanceState(savedInstanceState)
        if (mSpec.capture || mSpec.cutter) {
            mMediaStoreCompat = MediaStoreCompat(this)
            if (mSpec.captureStrategy == null) {
                throw RuntimeException("Don't forget to set CaptureStrategy.")
            } else {
                mMediaStoreCompat?.setCaptureStrategy(mSpec.captureStrategy)
            }
        }
        if (mSpec.cutter) {
            if (mSpec.cutterStrategy == null) {
                throw RuntimeException("Don't forget to set CutterStrategy.")
            }
        }
        updateBottomToolbar()
    }


    override fun onViewCreatedLazy() {
        super.onViewCreatedLazy()
        val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val callback = object : CheckRequestPermissionsListener {
            override fun onAllPermissionOk(allPermissions: Array<out Permission>?) {
                mAlbumCollection.loadAlbums()
            }

            override fun onPermissionDenied(refusedPermissions: Array<out Permission>?) {
                pop()
            }

        }
        ZRPermission.getInstance().checkRuntimePermission(permissions, callback)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        //拍照回调
        if (requestCode == REQUEST_CODE_CAPTURE) {
            mMediaStoreCompat?.let { mediaStoreCompat ->
                val contentUri: Uri = mediaStoreCompat.currentPhotoUri
                val path: String = mediaStoreCompat.currentPhotoPath
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                    this.activity?.revokeUriPermission(
                        contentUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                SingleMediaScanner(context?.applicationContext, path) {}
                if (mSpec.isSingleSelect) {   //如果相册是单选直接将结果返回原页面，如果为多选，那么需要返回到相册界面
                    callbackResult(mutableListOf(contentUri), mutableListOf(path))
                } else {
                    handler.postDelayed(runnable, 300)

                }
            }

        }

    }

    override fun onFragmentForResult(requestCode: Int, resultCode: Int, data: Bundle?) {
        super.onFragmentForResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        //大图预览回调
        if (requestCode == REQUEST_CODE_PREVIEW && data != null) {
            val resultBundle: Bundle? = data.getBundle(BasePreviewFragment.EXTRA_RESULT_BUNDLE)
            val selected =
                resultBundle?.getParcelableArrayList<Item>(SelectedItemCollection.STATE_SELECTION)
            val collectionType = resultBundle?.getInt(
                SelectedItemCollection.STATE_COLLECTION_TYPE,
                SelectedItemCollection.COLLECTION_UNDEFINED
            )
            mOriginalEnable = data.getBoolean(EXTRA_RESULT_ORIGINAL_ENABLE, false)
            if (data.getBoolean(EXTRA_RESULT_APPLY, false)) {
                val selectedUris = ArrayList<Uri>()
                val selectedPaths = ArrayList<String>()
                if (selected != null) {
                    for (item in selected) {
                        selectedUris.add(item.contentUri)
                        selectedPaths.add(PathUtils.getPath(context, item.contentUri))
                    }
                }
                callbackResult(selectedUris, selectedPaths)
            } else {
                selected?.let {
                    mSelectedCollection?.overwrite(
                        it,
                        collectionType ?: SelectedItemCollection.COLLECTION_UNDEFINED
                    )
                }
                val mediaSelectionFragment: Fragment? = childFragmentManager.findFragmentByTag(
                    MediaSelectionFragment::class.java.simpleName
                )
                if (mediaSelectionFragment is MediaSelectionFragment) {
                    mediaSelectionFragment.refreshMediaGrid()
                }
                updateBottomToolbar()
            }
        } else if (requestCode == REQUEST_CODE_CUTTER) {
            pop()
        }
    }


    private fun callbackResult(uriList: List<Uri?>, pathList: List<String?>) {
        if (mSpec.cutter) {
            val b = bundleOf("path" to pathList[0]!!)
            val dest = Dest.createDest(CutterFragment::class, b)
            startFragmentForResult(REQUEST_CODE_CUTTER, dest)
        } else {
            if (mSpec.onResultListener != null) {
                mSpec.onResultListener.onSelected(uriList, pathList)
            }
            pop()
        }
    }

    /**
     * 更新确认按钮状态
     */
    private fun updateBottomToolbar() {
        val selectedCount = mSelectedCollection!!.count()
        var color: Int = color(R.color.subtitle_text_color)
        if (selectedCount == 0) {
            confirmBtn?.isEnabled = false
        } else {
            confirmBtn?.isEnabled = true
            color = color(R.color.matisse_item_bgColor)
        }
        confirmBtn?.setTextColor(color)
    }

    override fun onDetach() {
        mSelectedCollection = null
        super.onDetach()
    }


    /**
     * 本地图片查找完成
     */
    override fun onAlbumLoad(cursor: Cursor?) {
        mAlbumsAdapter?.swapCursor(cursor)
        // select default album.
        Handler(getMainLooper()).post {
            cursor!!.moveToPosition(mAlbumCollection.currentSelection)
            mAlbumsSpinner?.setSelection(
                this.context,
                mAlbumCollection.currentSelection
            )
            val album: Album = Album.valueOf(cursor)
            if (album.isAll && SelectionSpec.getInstance().capture) {
                album.addCaptureCount()
            }
            onAlbumSelected(album)
        }
    }

    override fun onAlbumReset() {

    }


    /**
     * 切换选中图片目录
     */
    private fun onAlbumSelected(album: Album) {
        childFragmentManager
            .beginTransaction()
            .replace(
                R.id.container,
                MediaSelectionFragment.newInstance(album),
                MediaSelectionFragment::class.java.simpleName
            )
            .commitNowAllowingStateLoss()

    }

    override fun provideSelectedItemCollection(): SelectedItemCollection {
        return mSelectedCollection!!
    }

    /**
     * 图片选中，取消更新
     */
    override fun onUpdate() {
        // notify bottom toolbar that check state changed.
        updateBottomToolbar()
        if (mSpec.onSelectedListener != null) {
            mSpec.onSelectedListener.onSelected(
                mSelectedCollection?.asListOfUri()!!, mSelectedCollection?.asListOfString()!!
            )
        }
    }

    /**
     * 点击图片
     */
    override fun onMediaClick(album: Album?, item: Item?, adapterPosition: Int) {
        when {
            mSpec.cutter || mSpec.isSingleSelect -> {  //需要裁剪 || 是否为单选并且不需要裁剪，那么直接返回
                item?.let {
                    PathUtils.getPath(context, it.contentUri)?.let { path ->
                        callbackResult(mutableListOf(it.contentUri), mutableListOf(path))
                    }
                }
            }
            else -> {
                val b = Bundle().apply {
                    putParcelable(EXTRA_ALBUM, album)
                    putParcelable(EXTRA_ITEM, item)
                    putBundle(EXTRA_DEFAULT_BUNDLE, mSelectedCollection?.dataWithBundle)
                    putBoolean(EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable)
                }
                val dest = Dest.createDest(AlbumPreviewFragment::class, b)
                startFragmentForResult(REQUEST_CODE_PREVIEW, dest)
            }
        }
    }

    /**
     * 点击拍照
     */
    override fun capture() {
        // 请求权限
        val permissions: Array<String> = arrayOf(Manifest.permission.CAMERA)
        val callback = object : CheckRequestPermissionsListener {

            override fun onAllPermissionOk(allPermissions: Array<out Permission>?) {
                mMediaStoreCompat?.dispatchCaptureIntent(context, REQUEST_CODE_CAPTURE)
            }

            override fun onPermissionDenied(refusedPermissions: Array<out Permission>?) {

            }

        }
        ZRPermission.getInstance().checkRuntimePermission(permissions, callback)
    }

    /**
     * 图片目录选择回调
     */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        mAlbumCollection.setStateCurrentSelection(position)
        mAlbumsAdapter!!.cursor.moveToPosition(position)
        val album = Album.valueOf(mAlbumsAdapter!!.cursor)
        if (album.isAll && SelectionSpec.getInstance().capture) {
            album.addCaptureCount()
        }
        onAlbumSelected(album)
    }


    override fun onDestroyViewOnly() {
        SelectionSpec.getCleanInstance()
        mAlbumCollection.onDestroy()
        handler.removeCallbacksAndMessages(runnable)
        super.onDestroyViewOnly()
    }


    override fun onNothingSelected(parent: AdapterView<*>?) {

    }


    private fun onPhotoRefresh() {
        val fragment = findChildFragment(MediaSelectionFragment::class.java.simpleName)
        if (fragment is MediaSelectionFragment) {
            fragment.refreshAlbum()
        }
    }


}