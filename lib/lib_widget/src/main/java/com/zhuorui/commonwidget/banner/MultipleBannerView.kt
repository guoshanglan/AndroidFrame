package com.zhuorui.commonwidget.banner

import android.annotation.SuppressLint
import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.util.SparseArray
import android.view.fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import base2app.ex.dp2px
import base2app.ex.skin
import base2app.ex.unregistSkin
import com.zhpan.bannerview.BannerViewPager
import com.zhpan.bannerview.constants.IndicatorGravity
import com.zhpan.indicator.IndicatorView
import com.zhpan.indicator.enums.IndicatorStyle
import com.zhuorui.commonwidget.R
import com.zhuorui.commonwidget.banner.adapter.BannerNormalAdapter
import com.zhuorui.commonwidget.banner.enum.BannerTypeEnum
import com.zhuorui.commonwidget.banner.model.BannerModel


/**
@author guoshanglan
@description:多图的bannerView
@date : 2021/8/12 15:00
 */
@SuppressLint("CustomViewStyleable")
class MultipleBannerView @kotlin.jvm.JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : BannerViewPager<BannerModel>(context, attrs, defStyleAttr),
    BannerDataListener, LifecycleEventObserver {

    private var bannerType: Int? = null    //banner类型
    private var bannerDataManager: BannerDataManager? = null
    private var loadCallback: LoadCallBack? = null

    init {
        val t = context?.obtainStyledAttributes(attrs, R.styleable.BaseBannerView)
        bannerType = t?.getInt(R.styleable.BaseBannerView_bannerType, BannerTypeEnum.UNKNOW.TYPE)
        t?.recycle()
        initBannerManager()
        initMultipleBannerView()
    }

    /**
     * 初始化多图片的bannerView 默认配置
     */
    private fun initMultipleBannerView() {
        val checkedWidth: Int = 10f.dp2px().toInt()  //选中的指示器长度
        val normalWidth: Int = 4f.dp2px().toInt()   //未选中的指示器长度
        setIndicatorStyle(IndicatorStyle.ROUND_RECT)
            .setIndicatorGravity(IndicatorGravity.CENTER)
            .setIndicatorSliderGap(4f.dp2px().toInt())
            .setIndicatorSliderWidth(normalWidth, checkedWidth)
            .setOnPageClickListener { _, position ->

            }
        skin {
            if (data.isNullOrEmpty()) {
                loadCallback?.onLoadEmpty()  //换肤需要重新设置background
            }
        }
        isSaveEnabled = false
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>?) {
        if (isSaveEnabled){
            super.dispatchRestoreInstanceState(container)
        }
    }

    /**
     * 设置数据监听
     */
    fun setListener(loadCallBack: LoadCallBack) {
        this.loadCallback = loadCallBack
    }


    /**
     * 初始化数据管理
     */
    private fun initBannerManager() {
        if (bannerDataManager == null) {
            bannerDataManager = BannerDataManager(this)
        }
    }

    /**
     * 查询banner数据,根据所穿的banner类型来查询
     */
    fun refreshBannerData() {
        if (bannerType == BannerTypeEnum.UNKNOW.TYPE) return
        bannerDataManager?.queryBannerData(bannerType)
    }

    /**
     * 设置自定义指示器
     */
    fun setIndicator(customIndicator: IndicatorView?) {
        customIndicator?.let {
            this.setIndicatorView(it)
        }
    }

    /**
     * 设置数据
     */
    fun setBannerData(bannerList: List<BannerModel>) {
        if (adapter == null) {
            adapter = BannerNormalAdapter(background)   //如果外部没有设置具体的adapter，就用默认的adapter
        }
        create(bannerList)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        fragment?.lifecycle?.addObserver(this)
    }

    /**
     * 页面销毁
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (parent == null) {
            fragment?.lifecycle?.removeObserver(this)
        }
    }

    /**
     * 取消网络请求的协程作用域，清除管理者
     */
    private fun onDestory() {
        bannerDataManager?.onDestory()
        bannerDataManager = null
        loadCallback = null
        unregistSkin()
    }

    /**
     * 获取数据成功
     */
    override fun getBannerDataSuccessFul(bannerList: ArrayList<BannerModel>?) {
        if (bannerList?.isNotEmpty() == true) {
            loadCallback?.onLoadSuccessFul()
            setBannerData(bannerList)
        } else {
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
        setLifecycleRegistry(source.lifecycle)
        if (event == Lifecycle.Event.ON_CREATE) {
            refreshBannerData()
        } else if (event == Lifecycle.Event.ON_DESTROY) {
            onDestory()
        }
    }


    interface LoadCallBack {

        /**
         * 加载数据成功
         */
        fun onLoadSuccessFul()

        /**
         * 加载数据为空或者失败
         */
        fun onLoadEmpty()


    }
}