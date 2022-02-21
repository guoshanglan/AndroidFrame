package com.zhuorui.commonwidget.flow;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.*;

import com.zhuorui.commonwidget.ImageListView;
import com.zhuorui.commonwidget.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import base2app.util.JsonUtil;


/**
 * 创建:  liuwei on 2017/8/16
 * 修改:  liuwei at 2017/8/16
 * 描述:  FlowParamItemView:
 */
public class FlowParamItemView extends LinearLayout {

    private int mTitleWidth;
    private int mTitleImgWidht, mTitleImgHight;
    private int mTitleTextStyle;
    private int mContentTextStyle;
    private int mItemSpace;
    private int mGroupItemSpace;
    private int mItemAlign;
    private KVInterdace mKVI;
    private final int mContentGroupNum = 2;

    @IntDef({KVInterdace.ITEM_ALIGN_LEFT, KVInterdace.ITEM_ALIGN_JUSTIFY, KVInterdace.ITEM_ALIGN_CENTER_HORIZONTAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ItemAlignMode {
    }

    public FlowParamItemView(Context context) {
        this(context, null);
    }

    public FlowParamItemView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowParamItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        String title;
        String content;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.FlowParamItemView);
        mTitleWidth = a.getDimensionPixelOffset(R.styleable.FlowParamItemView_fpiv_title_width, -1);
        mTitleTextStyle = a.getResourceId(R.styleable.FlowParamItemView_fpiv_title_text_style, mTitleTextStyle);
        mTitleImgWidht = a.getDimensionPixelOffset(R.styleable.FlowParamItemView_fpiv_title_img_width, -1);
        mTitleImgHight = a.getDimensionPixelOffset(R.styleable.FlowParamItemView_fpiv_title_img_hight, -1);
        mContentTextStyle = a.getResourceId(R.styleable.FlowParamItemView_fpiv_content_text_style, mContentTextStyle);
        mItemSpace = a.getDimensionPixelOffset(R.styleable.FlowParamItemView_fpiv_item_space, mItemSpace);
        mItemAlign = a.getInteger(R.styleable.FlowParamItemView_fpiv_item_align, KVInterdace.ITEM_ALIGN_JUSTIFY);
        mGroupItemSpace = a.getInteger(R.styleable.FlowParamItemView_fpiv_group_item_space, mGroupItemSpace);
        title = a.getString(R.styleable.FlowParamItemView_fpiv_title_text);
        content = a.getString(R.styleable.FlowParamItemView_fpiv_content_text);
        a.recycle();
        if (mItemAlign == KVInterdace.ITEM_ALIGN_CENTER_HORIZONTAL)
            setOrientation(HORIZONTAL);
        if (!TextUtils.isEmpty(title) || !TextUtils.isEmpty(content)) {
            bindData(new KV(title, content));
        }
//        setGravity(Gravity.CENTER_VERTICAL);
    }

    /**
     * 设置样式，需在bindData前设置生效
     *
     * @param titleTextStyle
     * @param contentTextStyle
     */
    public void setStyle(@StyleRes int titleTextStyle, @StyleRes int contentTextStyle) {
        mTitleTextStyle = titleTextStyle;
        mContentTextStyle = contentTextStyle;
    }

    /**
     * 设置样式，需在bindData前设置生效
     *
     * @param titleTextStyle
     * @param contentTextStyle
     * @param itemSpace
     * @param itemAlign
     */
    public void setStyle(@StyleRes int titleTextStyle, @StyleRes int contentTextStyle, @Px int itemSpace, @ItemAlignMode int itemAlign) {
        mTitleTextStyle = titleTextStyle;
        mContentTextStyle = contentTextStyle;
        mItemSpace = itemSpace;
        mItemAlign = itemAlign;
    }

    public void setTitleImgSize(@Px int width, @Px int hight) {
        mTitleImgWidht = width;
        mTitleImgHight = hight;
    }

    public void setTitleWidth(@Px int width) {
        mTitleWidth = width;
    }

    public void setGroupItemSpace(int itemSpace) {
        mGroupItemSpace = itemSpace;
    }

    @SuppressWarnings("WrongConstant")
    public void bindData(KVInterdace data) {
        if (data == null) {
            removeAllViews();
            return;
        }
        int dataItemAlign = data.getItemAlign();
        if (dataItemAlign >= 0)
            mItemAlign = dataItemAlign;
        //不可重用已添加的View,清除子View
        if (!isReusing(data))
            removeAllViews();
        mKVI = data;
        //获取布局模式并设置
        int orientation = getItemOrientation(data.getOrientation());
        setOrientation(orientation);
        View title = initTitleView(data, orientation);
        View content = initContentView(data);
        if (data.getKType() == KVInterdace.K_TYPE_IMG) {
            //文字上下会有间隙，临时上下加4单位，使文字与图片最大程度同一线上，后期优化
            int ca = (int) (getResources().getDisplayMetrics().density * 2);
            title.setPadding(title.getPaddingLeft(), title.getPaddingTop() + ca, title.getPaddingRight(), +title.getPaddingBottom() + ca);
        }
        //没有添加则添加新的View
        if (!isAdd()) {
            addView(title);
            addView(content);
        }
    }

    /**
     * 获取布局模式
     *
     * @param orientation
     * @return
     */
    private int getItemOrientation(int orientation) {
        if (mItemAlign == KVInterdace.ITEM_ALIGN_CENTER_HORIZONTAL)
            return VERTICAL;
        switch (orientation) {
            case KVInterdace.ORIENTATION_TB:
                return VERTICAL;
            case KVInterdace.ORIENTATION_RL:
            case KVInterdace.ORIENTATION_NO_K:
            default:
                return HORIZONTAL;
        }
    }

    /**
     * 初始化titleView
     *
     * @param data
     * @return view
     */
    private View initTitleView(KVInterdace data, int orientation) {
        View title;
        switch (data.getKType()) {
            case KVInterdace.K_TYPE_IMG:
                title = getTitleViewInImg(data);
                break;
            case KVInterdace.K_TYPE_TEXT:
            default:
                title = getTitleViewInTxt(data);
                break;
        }
        //根据布局模式设置Space
        LayoutParams lp = (LayoutParams) title.getLayoutParams();
        if (orientation == VERTICAL) {
            lp.setMargins(0, 0, 0, mItemSpace);
        } else {
            lp.setMargins(0, 0, mItemSpace, 0);
        }
        if (mTitleWidth > 0) {
            lp.width = mTitleWidth;
        } else {
            lp.width = LayoutParams.WRAP_CONTENT;
        }
        title.setLayoutParams(lp);
        return title;
    }

    private View getTitleViewInImg(KVInterdace data) {
        LinearLayout ll;
        ImageView img;
        LayoutParams imgLp;
        LayoutParams lp;
        if (isAdd()) {
            ll = (LinearLayout) getChildAt(0);
            lp = (LayoutParams) ll.getLayoutParams();
            img = (ImageView) ll.getChildAt(0);
            imgLp = (LayoutParams) img.getLayoutParams();
        } else {
            lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.weight = 0;
            ll = new LinearLayout(getContext());
            ll.setLayoutParams(lp);
            imgLp = new LayoutParams(0, 0);
            img = new ImageView(getContext());
            img.setLayoutParams(imgLp);
            ll.addView(img);
        }
        int dataTitleImgHight = data.getTitleImgHight();
        int imgH = dataTitleImgHight > 0 ? dataTitleImgHight : mTitleImgHight;
        if (imgH < 0) {
            //没有大小，给默认高度
            if (imgH < 0)
                imgH = (int) (getResources().getDisplayMetrics().density * 14);
        }
        int dataTitleWidth = data.getTitleImgWidth();
        int imgW = dataTitleWidth > 0 ? dataTitleWidth : mTitleImgWidht;
        imgW = imgW >= 0 ? imgW : imgH;
        imgLp.width = imgW;
        imgLp.height = imgH;
        img.setLayoutParams(imgLp);
        if (mItemAlign == KVInterdace.ITEM_ALIGN_CENTER_HORIZONTAL) {
            lp.gravity = Gravity.CENTER;
            ll.setGravity(Gravity.CENTER);
        } else {
            lp.gravity = Gravity.LEFT;//| Gravity.CENTER_VERTICAL)
            ll.setGravity(Gravity.LEFT);//| Gravity.CENTER_VERTICAL)
        }
        ll.setLayoutParams(lp);
        Object k = data.getK();
        int resId = 0;
        if (k != null && k instanceof Integer) {
            resId = (int) k;
        }
        boolean isShow = data.getOrientation() != KVInterdace.ORIENTATION_NO_K && resId != 0;
        ll.setVisibility(isShow ? VISIBLE : GONE);
        if (isShow)
            img.setImageResource(resId);
        return ll;
    }

    private View getTitleViewInTxt(KVInterdace data) {
        TextView title;
        LayoutParams lp;
        if (isAdd()) {
            title = (TextView) getChildAt(0);
            lp = (LayoutParams) title.getLayoutParams();
        } else {
            lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            lp.weight = 0;
            title = new TextView(getContext());
            title.setLayoutParams(lp);
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
        Object k = data.getK();
        CharSequence titleStr = k != null && k instanceof CharSequence ? (CharSequence) k : null;
        title.setText(titleStr);
        title.setTextColor(Color.parseColor("#666666"));
        //优先取数据配置样式
        int textStyle = data.getTitleTextStyle();
        //数据中没有配置样式，使用View中配置样式
        textStyle = textStyle == 0 ? mTitleTextStyle : textStyle;
        if (textStyle != 0)
            setTextViewAppearance(title, textStyle);
        if (mItemAlign == KVInterdace.ITEM_ALIGN_CENTER_HORIZONTAL) {
            lp.gravity = Gravity.CENTER;
            title.setGravity(Gravity.CENTER);
        } else {
            lp.gravity = Gravity.LEFT;//| Gravity.CENTER_VERTICAL)
            title.setGravity(Gravity.LEFT);//| Gravity.CENTER_VERTICAL)
        }
        title.setLayoutParams(lp);
        boolean isShow = data.getOrientation() != KVInterdace.ORIENTATION_NO_K && !TextUtils.isEmpty(titleStr);
        title.setVisibility(isShow ? VISIBLE : GONE);
        return title;
    }

    /**
     * 初始化cotentView
     *
     * @param data
     * @return view
     */
    private View initContentView(KVInterdace data) {
        LinearLayout content = getContentView(data);
        switch (data.getVType()) {
            case KVInterdace.V_TYPE_IMG:
                return initContentViewInImg(content, data);
            case KVInterdace.V_TYPE_TEXT:
            default:
                return initContentViewInTxt(content, data);
        }
    }

    private View initContentViewInImg(LinearLayout content, KVInterdace data) {
        List<CharSequence> txts = getVData(data);
        if (txts != null && !txts.isEmpty()) {
            //数据bind
            ImageListView imgList = null;
            View v = content.getChildAt(0);
            if (v == null || !(v instanceof ImageListView)) {
                content.removeAllViews();
                imgList = new ImageListView(getContext());
                content.addView(imgList);
            }
            imgList.setDatas(txts);
        } else {
            //没有数据，将上一次数据设置的内容清除
            content.removeAllViews();
        }
        return content;
    }

    /**
     * 获取数据类型为txt的Content View
     *
     * @param content
     * @param data
     * @return
     */
    private View initContentViewInTxt(LinearLayout content, KVInterdace data) {
        //数据解析
        if (data.getV() != null) {
            if (data.getVFormat() == KVInterdace.V_FORMAT_ARRAY_GROUP) {
                List<CharSequence> txts = getVData(data);
                if (!txts.isEmpty()) {
                    addContentTextGroupView(content, txts, data.getContentTextStyle(), data.getVColor());
                }
            } else if (data.getVFormat() == KVInterdace.V_FORMAT_ARRAY) {
                List<CharSequence> txts = getVData(data);

                if (!txts.isEmpty()) {
                    addContentTextView(content, txts, data.getContentTextStyle(), data.getVColor());
                }
            } else {
                List<CharSequence> charSequences = new ArrayList<>();
                charSequences.add((CharSequence) data.getV());
                addContentTextView(content, charSequences, data.getContentTextStyle(), data.getVColor());
            }
        } else {
            //没有数据，将上一次数据设置的内容清除
            content.removeAllViews();
        }
        return content;
    }

    /**
     * 获取content View的容器
     *
     * @return
     */
    private LinearLayout getContentView(KVInterdace data) {
        LinearLayout content = null;
        if (isReusing(data) && isAdd())
            content = (LinearLayout) getChildAt(1);
        if (content == null) {
            content = new LinearLayout(getContext());
            content.setOrientation(VERTICAL);
            LayoutParams clp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            clp.weight = 1;
            clp.gravity = Gravity.CENTER_VERTICAL;
            content.setLayoutParams(clp);
        }
        content.setGravity(getItemAlign());
        return content;
    }

    /**
     * 给Content View 添加文本内容
     *
     * @param ll
     * @param txts
     * @param contentTextStyle
     * @param vColor
     */
    private void addContentTextView(@NonNull LinearLayout ll, @NonNull List<CharSequence> txts, @StyleRes int contentTextStyle, String vColor) {
        int childCount = ll.getChildCount();
        //先将子View全部隐藏
        for (int i = 0; i < childCount; i++) {
            ll.getChildAt(i).setVisibility(GONE);
        }
        int dataLen = txts.size();
        LayoutParams lp = null;
        for (int i = 0; i < dataLen; i++) {
            TextView tv = getContentTextView(ll, i, childCount, dataLen);
            bindContentTextData(tv, txts.get(i), contentTextStyle, vColor);
        }
    }

    /**
     * 获取Content View 中的 TextView
     *
     * @param ll
     * @param i
     * @param childCount
     * @param dataLen
     * @return
     */
    private TextView getContentTextView(LinearLayout ll, int i, int childCount, int dataLen) {
        LayoutParams lp = null;
        TextView tv = null;
        //已经存在，直接获取使用，不存在创建
        if (i < childCount) {
            View v = ll.getChildAt(i);
            tv = (TextView) v;
            tv.setVisibility(VISIBLE);
            lp = (LayoutParams) tv.getLayoutParams();
        } else {
            lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tv = new TextView(getContext());
            ll.addView(tv);
        }
        if (mItemAlign == KVInterdace.ITEM_ALIGN_CENTER_HORIZONTAL) {
            lp.gravity = Gravity.CENTER;
            tv.setGravity(Gravity.CENTER);
        } else {
            lp.gravity = getItemAlign();
            tv.setGravity(Gravity.LEFT);
        }
        if (i == dataLen - 1) {
            lp.setMargins(0, 0, 0, 0);
        } else {
            lp.setMargins(0, 0, 0, mGroupItemSpace);
        }
        tv.setLayoutParams(lp);
        return tv;
    }

    /**
     * 给Content View  添加文本内容,两两分组形式
     *
     * @param ll
     * @param txts
     * @param contentTextStyle
     * @param vColor
     */
    private void addContentTextGroupView(@NonNull LinearLayout ll, @NonNull List<CharSequence> txts, @StyleRes int contentTextStyle, String vColor) {
        int childCount = ll.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ll.getChildAt(i).setVisibility(GONE);
        }
        int dataLen = txts.size();
        int x = dataLen / mContentGroupNum;
        int y = dataLen % mContentGroupNum > 0 ? 1 : 0;
        int itemLem = x + y;
        TextView tv1;
        TextView tv2;
        LinearLayout group;
        for (int i = 0; i < itemLem; i++) {
            group = getContentGroup(ll, i, childCount, dataLen);
            if (group.getChildCount() == 2) {
                tv1 = (TextView) group.getChildAt(0);
                tv2 = (TextView) group.getChildAt(1);
            } else {
                tv1 = getGroupTextView();
                tv2 = getGroupTextView();
                group.removeAllViews();
                group.addView(tv1);
                group.addView(tv2);
            }
            int index = i * 2;
            bindContentTextData(tv1, txts.get(index), contentTextStyle, vColor);
            index = index + 1;
            if (index < dataLen) {
                bindContentTextData(tv2, txts.get(index), contentTextStyle, vColor);
            } else {
                tv2.setText("");
            }

        }
    }

    /**
     * 获取Content View 两两分组时，TextView 的容器
     *
     * @param ll
     * @param i
     * @param childCount
     * @param dataLen
     * @return
     */
    private LinearLayout getContentGroup(LinearLayout ll, int i, int childCount, int dataLen) {
        LinearLayout vll = null;
        LayoutParams vlllp = null;
        //已经存在，直接获取使用，不存在创建
        if (i < childCount) {
            vll = (LinearLayout) ll.getChildAt(i);
            vll.setVisibility(VISIBLE);
            vlllp = (LayoutParams) vll.getLayoutParams();
        } else {
            vlllp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            vll = new LinearLayout(getContext());
            ll.addView(vll);
        }
        if (i > 0) {
            vlllp.setMargins(mGroupItemSpace, 0, 0, 0);
        } else {
            vlllp.setMargins(0, 0, 0, 0);
        }
        vll.setLayoutParams(vlllp);
        return vll;
    }

    /**
     * 生成分组中的TextView
     *
     * @return
     */
    private TextView getGroupTextView() {
        TextView tv = new TextView(getContext());
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        lp.weight = 1;
        tv.setLayoutParams(lp);
        return tv;
    }

    /**
     * 设置content TextView 数据
     *
     * @param tv
     * @param data
     * @param contentTextStyle
     * @param vColor
     */
    private void bindContentTextData(TextView tv, CharSequence data, int contentTextStyle, String vColor) {
        tv.setText(data);
        //默认色，权限最低
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tv.setTextColor(Color.parseColor("#333333"));
        //数据中有样式，优先使用数据中的,再使用全局配置的
        int textStyle = contentTextStyle != 0 ? contentTextStyle : mContentTextStyle;
        setTextViewAppearance(tv, textStyle);
        //服务端控制的颜色,权限最高
        int textColor = isColorString(vColor) ? Color.parseColor(vColor) : 0;
        if (textColor != 0)
            tv.setTextColor(textColor);
    }


    private boolean isColorString(String colorStr) {
        if (TextUtils.isEmpty(colorStr)) return false;
        int len = colorStr.length();
        if (colorStr.startsWith("#") && (len == 7 || len == 9))
            return true;
        return false;
    }

    private List<CharSequence> getVData(KVInterdace data) {
        List<CharSequence> txts = null;
        switch (data.getVFormat()) {
            case KVInterdace.V_FORMAT_STRING:
                String v = (String) data.getV();
                if (!TextUtils.isEmpty(v)) {
                    txts = new ArrayList<>();
                    txts.add(v);
                }
                break;
            case KVInterdace.V_FORMAT_ARRAY:
            case KVInterdace.V_FORMAT_ARRAY_GROUP:
                if (data.getV() instanceof String) {
                    txts = getArrayData((String) data.getV());
                } else {
                    txts = getArrayData(JsonUtil.toJson(data.getV()));
                }
                break;
        }
        return txts;
    }


    private List<CharSequence> getArrayData(@Nullable String data) {
        if (!TextUtils.isEmpty(data)) {
            try {
                JSONArray array = new JSONArray(data);
                if (array != null) {
                    List<CharSequence> datas = new ArrayList<>();
                    int length = array.length();
                    for (int i = 0; i < length; i++) {
                        datas.add(array.getString(i));
                    }
                    return datas;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private int getItemAlign() {
        switch (mItemAlign) {
            case KVInterdace.ITEM_ALIGN_LEFT:
                return Gravity.LEFT;//| Gravity.CENTER_VERTICAL)
            case KVInterdace.ITEM_ALIGN_CENTER_HORIZONTAL:
                return Gravity.CENTER;
            case KVInterdace.ITEM_ALIGN_JUSTIFY:
            default:
                return Gravity.RIGHT;//| Gravity.CENTER_VERTICAL)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setTextViewAppearance(@NonNull TextView tv, @StyleRes int style) {
        if (style != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                tv.setTextAppearance(style);
            } else {
                tv.setTextAppearance(getContext(), style);
            }
        }
    }


    /**
     * 是否可以重用子View
     *
     * @param data
     * @return
     */
    private boolean isReusing(@Nullable KVInterdace data) {
        //没有设置过
        if (mKVI == null || !isAdd())
            return false;
        //数据格式
        int newVFormat = data.getVFormat();
        int curVFormat = mKVI.getVFormat();
        if (newVFormat != curVFormat)
            return false;
        //数据类型
        int newVType = data.getVType();
        int curVType = mKVI.getVType();
        if (newVType != curVType)
            return false;
        int newKFormat = data.getKFormat();
        int curKFormat = mKVI.getKFormat();
        if (newKFormat != curKFormat)
            return false;
        int newKType = data.getKType();
        int curKType = mKVI.getKType();
        if (newKType != curKType)
            return false;
        return true;
    }

    private boolean isAdd() {
        //此处强匹配，只有ChildCount为2合法
        return getChildCount() == 2;
    }

    private float mDensity;

    private int dip2Px(float dip) {
        if (mDensity == 0)
            mDensity = getResources().getDisplayMetrics().density;
        return (int) (mDensity * dip);
    }

}
