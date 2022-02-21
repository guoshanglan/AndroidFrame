package com.zhuorui.securties.skin.view

import android.R
import android.annotation.SuppressLint
import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StyleableRes
import java.util.*

/**
 * ZRSkinAttrType
 * @description
 * @date 2020/12/11 14:14
 */
@SuppressLint("ResourceType")
enum class ZRSkinAttrType(
    var mAttrType: String,
    @field:StyleableRes @param:StyleableRes var mAttrDefinition: Int
) {
    BACKGROUND("background", R.attr.background) {
        override fun apply(view: View, attr: ZRSkinAttr, resources: Resources) {
//            Log.d("background", " apply " + attr.mAttrName + "  parse Resource id : " + attr.mResId +" resName : "
//                    + attr.mResName)
            when (attr.mAttrName) {
                "color" -> {
                    view.setBackgroundColor(resources.getColor(attr.mResId))
                }
                "drawable" -> {
                    view.background = resources.getDrawable(attr.mResId)
                }
                "mipmap" -> {
                    view.background = resources.getDrawable(attr.mResId)
                }
            }
        }
    },
    SRC("src", R.attr.src) {
        override fun apply(view: View, attr: ZRSkinAttr, resources: Resources) {
//            Log.d("src","view : " + view::class.simpleName + "attr.mAttrType : " + attr.mAttrType +" attr.mResName  " + attr.mResName + " attr.mAttrName : " + attr.mAttrName)
            if (view is ImageView) {
//                ImageButton 继承 ImageView
                view.setImageDrawable(resources.getDrawable(attr.mResId))
            } else if (view is ImageSwitcher) {
                view.setImageDrawable(resources.getDrawable(attr.mResId))
            }
        }
    },
    TEXT_COLOR("textColor", R.attr.textColor) {
        override fun apply(view: View, attr: ZRSkinAttr, resources: Resources) {
//            Log.d("personal_textcolor","view : " + view::class.simpleName + "attr.mAttrType : " + attr.mAttrType +" attr.mResName  " + attr.mResName + " attr.mAttrName : " + attr.mAttrName)
            if (view is TextView) {
/*                if (attr.mResName?.contains("personal_selector_tab_textcolor") == true){
                    Log.d("personal_textcolor","view : " + view::class.simpleName + "attr.mAttrType : " + attr.mAttrType +" attr.mResName  " + attr.mResName + " attr.mAttrName : " + attr.mAttrName)
                }*/
//                ImageButton 继承 ImageView
                if ("color" == attr.mAttrName) {
                    view.setTextColor(resources.getColorStateList(attr.mResId))
                }
            }
        }
    },
    TEXT("text", R.attr.text) {
        override fun apply(view: View, attr: ZRSkinAttr, resources: Resources) {
            if (view is TextView) {
                //文字一般使用代码进行设置会比较好 , 例如列表页的默认显示会干扰换肤之后的显示
//                ImageButton 继承 ImageView
//                ((TextView) view).setText(resources.getText(attr.mResId));
            }
        }
    },
    DRAWABLE_LEFT("drawableLeft", R.attr.drawableLeft) {
        override fun apply(view: View, attr: ZRSkinAttr, resources: Resources) {}
    },
    DRAWABLE_TOP("drawableTop", R.attr.drawableTop) {
        override fun apply(view: View, attr: ZRSkinAttr, resources: Resources) {}
    },
    DRAWABLE_RIGHT("drawableRight", R.attr.drawableRight) {
        override fun apply(view: View, attr: ZRSkinAttr, resources: Resources) {}
    },
    DRAWABLE_BOTTOM("drawableBottom", R.attr.drawableBottom) {
        override fun apply(view: View, attr: ZRSkinAttr, resources: Resources) {}
    };

    abstract fun apply(view: View, attr: ZRSkinAttr, resources: Resources)

    companion object {
        var supportAttrTypes: MutableList<ZRSkinAttrType>
            private set

        @StyleableRes
        var supportStyleables: IntArray
            private set

        var mSupportAttrTypeStrs: MutableList<String>
            private set

        init {
            //定义我们一般要获取的属性
            val mSupportAttrTypeArrays = arrayOf(
                BACKGROUND,
                SRC,
                TEXT_COLOR,  /*      YtSkinAttrType.TEXT,*/
                DRAWABLE_TOP,
                DRAWABLE_BOTTOM,
                DRAWABLE_LEFT,
                DRAWABLE_RIGHT
            )
            supportAttrTypes = mutableListOf(*mSupportAttrTypeArrays)

            supportAttrTypes.let {
                // notice 这里必须进行排序 , 否则在attributes 中无法正常解析 , 此为系统的一个bug
                // https://stackoverflow.com/questions/19034597/get-multiple-style-attributes-with-obtainstyledattributes
                it.sortWith(Comparator { o1, o2 -> o1.mAttrDefinition - o2.mAttrDefinition })
                val size = it.size
                supportStyleables = IntArray(size)
                mSupportAttrTypeStrs = ArrayList()

                for (i in 0 until size) {
                    val type = it[i]
                    supportStyleables[i] = type.mAttrDefinition
                    mSupportAttrTypeStrs.add(type.mAttrType)
                }
            }
            //        XLog.d("mSupportStyleables : " + Arrays.toString(mSupportAttrTypes.toArray()));
        }
    }
}