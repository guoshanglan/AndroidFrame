package com.zhuorui.securties.skin.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.AttributeSet
import android.util.Log
import android.view.InflateException
import android.view.LayoutInflater.Factory2
import android.view.View
import androidx.annotation.StyleableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.ActionMode
import androidx.collection.ArrayMap
import com.zhuorui.securties.skin.util.Utils
import com.zhuorui.securties.skin.view.*
import java.lang.ref.WeakReference
import java.lang.reflect.Constructor
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

/**
 * ZRLayoutInflaterFactory 类或者接口名称
 * @company 深圳市卓锐网络科技有限公司
 * @description
 * @date 2020/12/10 20:25
 */
class ZRLayoutInflaterFactory(context: Context, private val iViewCreator: IViewCreator?) : Factory2,
    AppCompatCallback {

    var mCompatDelegate: AppCompatDelegate? = null

    private val mZRSkinViews: CopyOnWriteArrayList<ZRBaseSkinView<*>> = CopyOnWriteArrayList()

    private val mZRSkinAbles: CopyOnWriteArrayList<WeakReference<ZRSkinAble>> =
        CopyOnWriteArrayList()

    @StyleableRes
    private val mSupportStyleables: IntArray = ZRSkinAttrType.supportStyleables

    private val mContext: Context

    private val mConstructorArgs = arrayOfNulls<Any>(2)

    init {
        if (context is AppCompatActivity) {
            mCompatDelegate = context.delegate
        } else if (context is Activity) { //普通的Activity , 必须要构建出 AppCompatDelegate 否则不会过带parent的构造方法
            mCompatDelegate = AppCompatDelegate.create(context, this)
        }
        mContext = context
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        Utils.correctConfigUiMode(context)
        var view: View?

        //APT 创建view代码
        view = iViewCreator?.createView(name, context, attrs)

        if (view == null) {
//            Log.e(TAG, "正在使用AppCompatDelegate创建的 view : $name")
            view = mCompatDelegate?.createView(parent, name, context, attrs)
        }
        if (view == null) {
//            Log.e(TAG, "正在反射创建创建的 view : $name")
            view = createViewFromTag(context, name, attrs)
        }
        if (view != null) {
//            Log.e(TAG,"创建的 view : $name");
            collectViewAttr(view, context, attrs)
        } else {
//            Log.e(TAG,"创建不了的 view : $name")
        }
        return view
    }

    private fun createViewFromTag(context: Context, viewName: String, attrs: AttributeSet): View? {
        var name = viewName
        if ("view" == name) {
            name = attrs.getAttributeValue(null, "class")
        }
        return try {
            mConstructorArgs[0] = context
            mConstructorArgs[1] = attrs
            if (-1 == name.indexOf('.')) {
                for (i in sClassPrefixList.indices) {
                    val view = createView(context, name, sClassPrefixList[i])
                    if (view != null) {
                        return view
                    }
                }
                null
            } else {
                createView(context, name, null)
            }
        } catch (e: Exception) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            null
        } finally {
            // Don't retain references on context.
            mConstructorArgs[0] = null
            mConstructorArgs[1] = null
        }
    }

    @Throws(ClassNotFoundException::class, InflateException::class)
    private fun createView(context: Context, name: String, prefix: String?): View? {
        var constructor = sConstructorMap[name]
        return try {
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                val clazz = context.classLoader.loadClass(
                    if (prefix != null) prefix + name else name
                ).asSubclass(View::class.java)
                constructor = clazz.getConstructor(*sConstructorSignature)
                sConstructorMap[name] = constructor
            }
            constructor!!.isAccessible = true
            constructor.newInstance(*mConstructorArgs)
        } catch (e: Exception) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            null
        }
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
//        XLog.d("onCreateView 的 view : " + name);
        return null
    }

    private fun collectViewAttr(view: View, context: Context, attrs: AttributeSet) {
        //如果是实现 ZRSkinAble 的自定义 view
        if (view is ZRSkinAble) {
            mZRSkinAbles.add(0, WeakReference(view))
        }
//        for (index in 0 until attrs.attributeCount) {
//            Log.d(
//                TAG,
//                "AttributeName->" + attrs.getAttributeName(index) + "     AttributeNameResource->" + attrs.getAttributeNameResource(
//                    index
//                )
//            )
//        }
        val skinAttrs: MutableList<ZRSkinAttr?> = ArrayList()
        //mSupportStyleables 必须要经过排序才能解析
        val a = view.context.obtainStyledAttributes(attrs, mSupportStyleables)
        attrs.attributeCount
        for (i in 0 until a.length()) {
            @SuppressLint("ResourceType") val resId = a.getResourceId(i, -1)
            if (resId != -1) {
                val resName = context.resources.getResourceEntryName(resId)
                val attrType = context.resources.getResourceTypeName(resId)
                //在这里直接赋值一个支持的属性 ， 通用的处理方法会自动将其进行处理
                skinAttrs.add(
                    0,
                    ZRSkinAttr(
                        ZRSkinAttrType.supportAttrTypes[i],
                        attrType,
                        mSupportStyleables[i],
                        resId,
                        resName
                    )
                )
//                Log.e(
//                    TAG,
//                    "$resName collectViewAttr  the $attrType  parse Resource id : $resId resName : $resName    ZRSkinAttrType : ${ZRSkinAttrType.supportAttrTypes[i].name}"
//                )
                //                XLog.d(name + " collectViewAttr  the " + attrType + "  parse Resource id : " + resId +" resName : " + resName);
            }
        }
        a.recycle()
        if (skinAttrs.isNotEmpty()) {
            val skin = ZRSkinView(view, skinAttrs)
            registSkin(skin)
        }

    }

    fun applyUiMode(resources: Resources) {
        Log.d(TAG, "clearUselessView applyUiMode : ${mZRSkinViews.size}")
        mZRSkinViews.asIterable().forEach { skinView ->
            skinView.let { view ->
                view.viewRef.get()?.let {
                    Utils.correctConfigUiMode(it.context)
                    view.applyUIMode(resources)
                }
            }
        }

        mZRSkinAbles.asIterable().forEach { skinView ->
            skinView.get()?.applyUIMode(resources)
        }
    }

    override fun onSupportActionModeStarted(mode: ActionMode) {}

    override fun onSupportActionModeFinished(mode: ActionMode) {}

    override fun onWindowStartingSupportActionMode(callback: ActionMode.Callback): ActionMode? {
        return null
    }

    fun registSkin(skinable: ZRBaseSkinView<*>) {
        mZRSkinViews.add(0, skinable)
    }

    fun clearUselessView() {

        mZRSkinViews.iterator().forEach { skinView ->
            skinView.let {
                if (it.viewRef.get() == null) {//由于fragment框架的原因 , 需要移除空引用
                    mZRSkinViews.remove(skinView)
                }
            }
        }

        mZRSkinAbles.iterator().forEach { skinView ->
            skinView.let {
                if (it.get() == null) {//由于fragment框架的原因 , 需要移除空引用
                    mZRSkinAbles.remove(skinView)
                }
            }
        }
    }

    companion object {
        private val sConstructorSignature = arrayOf(
            Context::class.java, AttributeSet::class.java
        )

        private val sClassPrefixList = arrayOf(
            "android.widget.",
            "android.view.",
            "android.webkit."
        )
        private val sConstructorMap: MutableMap<String, Constructor<out View>?> = ArrayMap()

        const val TAG = "ZRLayoutInflaterFactory"
    }

}