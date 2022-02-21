package com.zhuorui.commonwidget.banner

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import base2app.ex.setSafeViewClickListener
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.zhuorui.commonwidget.R
import com.zhuorui.commonwidget.ZRImageView
import com.zhuorui.commonwidget.banner.enum.BannerTypeEnum
import com.zhuorui.commonwidget.banner.model.BannerModel
import com.zhuorui.securities.base2app.glide.ZRGlide

/**
onStateChanged
@author guoshanglan
@description:单张图 banner
@date : 2021/8/12 16:33
 */
@SuppressLint("CustomViewStyleable")
class SingleBannerView @JvmOverloads constructor(
        context: Context?,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
) : ZRImageView(context, attrs, defStyleAttr), BannerDataListener, LifecycleEventObserver,
        RequestListener<Drawable> {

    private var bannerModel: BannerModel? = null   //banner数据model
    private var bannerType: Int? = null    //banner类型
    private var bannerDataManager: BannerDataManager? = null   //banner的数据管理者
    private var loadCallback: LoadCallBack? = null

    init {
        val t = context?.obtainStyledAttributes(attrs, R.styleable.BaseBannerView)
        bannerType = t?.getInt(R.styleable.BaseBannerView_bannerType, BannerTypeEnum.UNKNOW.TYPE)
        t?.recycle()
        setSafeViewClickListener {

        }
        initBannerManager()
    }

    /**
     * 初始化数据banner数据请求管理者
     */
    private fun initBannerManager() {
        if (bannerDataManager == null) {
            bannerDataManager = BannerDataManager(this)
        }
    }

    /**
     * 设置数据监听
     */
    fun setListener(loadCallBack: LoadCallBack) {
        this.loadCallback = loadCallBack
    }

    /**
     * 查询banner数据
     */
    fun refreshBannerData() {
        if (bannerType == BannerTypeEnum.UNKNOW.TYPE) return
        bannerDataManager?.queryBannerData(bannerType)

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fragment?.lifecycle?.addObserver(this)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (parent == null) {     //防止如果控件为动态加载父类为空要移除监听
            fragment?.lifecycle?.removeObserver(this)
        }
    }

    /**
     * 取消网络请求的协程作用域
     */
    private fun onDestory() {
        bannerDataManager?.onDestory()
        bannerDataManager = null
        loadCallback = null
    }

    /**
     * 获取数据成功
     */
    override fun getBannerDataSuccessFul(bannerList: ArrayList<BannerModel>?) {
        if (bannerList.isNullOrEmpty()) {
            loadCallback?.onLoadEmpty()
            return
        }
        bannerModel = bannerList[0]
        bannerModel?.imageUrl?.let {
            loadCallback?.onLoadSuccessFul()
            ZRGlide.with(this)
                    .load(it)
                    .addListener(this)
                    .placeholder(background)
                    .dontAnimate()
                    .into(this)
        } ?: kotlin.run {
            loadCallback?.onLoadEmpty()
        }
    }

    /**
     * 获取数据失败
     */
    override fun getBannerDataFair() {
        loadCallback?.onLoadEmpty()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        if (event == Lifecycle.Event.ON_CREATE) {
            refreshBannerData()
        } else if (event == Lifecycle.Event.ON_DESTROY) {
            onDestory()
        }
    }

    interface LoadCallBack {
        /**
         * 加载图片为空
         */
        fun onLoadEmpty()

        /**
         * 加载图片成功
         */
        fun onLoadSuccessFul()
    }

    override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
    ): Boolean {

        loadCallback?.onLoadEmpty()
        return true
    }

    override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
    ): Boolean {
        setImageDrawable(resource)
        return true
    }


}