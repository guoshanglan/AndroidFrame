package base2app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import base2app.ex.logd
import com.zhuorui.securities.base2app.ui.fragment.ZRPresenter
import com.zhuorui.securities.base2app.ui.fragment.ZRView

/**
 * @date 2020/5/25 15:22
 * @desc
 */
abstract class ZRMvpFragment<V : ZRView, P : ZRPresenter<V>>(layoutId:Int, cache:Boolean? = null) : ZRFragment(layoutId,cache),
    ZRView {

    /**
     * 控制逻辑
     */
    protected var presenter: P? = null

    /***
     * 创建presenter
     */
    protected abstract val createPresenter: P

    /**
     * 返回View
     */
    protected abstract val getView: V

    @CallSuper
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        presenter = presenter?:createPresenter
        lifecycle.addObserver(presenter!!)
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreatedOnly(view: View, savedInstanceState: Bundle?) {
        super.onViewCreatedOnly(view, savedInstanceState)
        presenter?.bindView(getView)
        presenter?.init()
    }


    final override fun isDestroy(): Boolean {
        return presenter?.isDestroy() ?: true
    }

    @CallSuper
    override fun onDestroyViewOnly() {
        releaseInDestroy()
        super.onDestroyViewOnly()
    }

    /**
     * 子类需要在onDestroy调用presenter，请在super执行前调用
     */
    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        presenter?.let {
            if (it.isDestroy()) {
                presenter = null
            } else {
                logd("fragment onDestroy RuntimeException : $this")
                throw RuntimeException(this.javaClass.simpleName + " presenter没有onDestroy")
            }
        }

    }

    @Deprecated("在onDestroyViewOnly或onDestroy中回收资源")
    open fun releaseInDestroy() {

    }


}