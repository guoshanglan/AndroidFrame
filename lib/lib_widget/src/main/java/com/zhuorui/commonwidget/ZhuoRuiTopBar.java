package com.zhuorui.commonwidget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewEx;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentEx;
import androidx.navigation.activity.NavigationActivity;

import com.zhuorui.securties.skin.view.ZRSkinAble;

import java.util.ArrayList;
import java.util.List;

import base2app.ex.ResourceKt;
import base2app.ex.ViewEXKt;
import base2app.util.StatusBarUtil;
import kotlin.jvm.functions.Function0;

/**
 * author : liuwei
 * e-mail : vsanliu@foxmail.com
 * date   : 2019-08-20 15:38
 * desc   : 自定义TopBar
 */
public class ZhuoRuiTopBar extends ConstraintLayout implements ZRSkinAble {

    /**
     * 返回view
     */
    private View vBackView;
    /**
     * title
     */
    private View vTitleView;

    /**
     * pop
     */
    private final int popResId;

    /**
     * 副标题
     */
    private View vSubTitleView;
    /**
     * 左侧view
     */
    private View vLeftView;
    /**
     * 右侧view
     */
    private List<View> mRightViews;
    /**
     * 绑定webview
     */
   // private ZRWebView vWebView;

    private final List<Function0> skinViewFunctionList = new ArrayList<>();

    /**
     * 是否减去状态栏的高度
     */
    private boolean mFitsSystemWindowsPadding = false;

    //    private final QuickClickUtil quickClickUtil = new QuickClickUtil(5000L);
    private OnButtonClickListener mRightClickListenr;
    private final int maxHeight;
    private final int btnPadding;
    private int leftPadding;
    private int rightPadding;
    private final int defIconHight;
    private int rightTextColor;

    int titleColor;
    int background;

    public static int getTopBarHeight(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (density * 48);
    }

    public ZhuoRuiTopBar(Context context) {
        this(context, null);
    }

    public ZhuoRuiTopBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("ResourceType")
    public ZhuoRuiTopBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        float density = getResources().getDisplayMetrics().density;
        maxHeight = getMinHeight() > 0 ? getMinHeight() : (int) (density * 48);
        btnPadding = (int) (density * 10);
        defIconHight = (int) (density * 20);
        leftPadding = rightPadding = (int) (density * 13);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ZhuoRuiTopBar);
        leftPadding = a.getDimensionPixelOffset(R.styleable.ZhuoRuiTopBar_zr_paddingLeft, leftPadding);
        rightPadding = a.getDimensionPixelOffset(R.styleable.ZhuoRuiTopBar_zr_paddingRight, rightPadding);
        rightTextColor = a.getColor(R.styleable.ZhuoRuiTopBar_zr_topbarRightTextColor, rightTextColor);
        mFitsSystemWindowsPadding = a.getBoolean(R.styleable.ZhuoRuiTopBar_zr_fitsSystemWindowsPadding, mFitsSystemWindowsPadding);
        popResId = a.getResourceId(R.styleable.ZhuoRuiTopBar_zr_popResourceId, 0);
        String title = a.getString(R.styleable.ZhuoRuiTopBar_zr_topbarTitle);
        String subTitle = a.getString(R.styleable.ZhuoRuiTopBar_zr_topbarSubTitle);
        titleColor = a.getResourceId(R.styleable.ZhuoRuiTopBar_zr_topbarTitleColor, 0);
        boolean isback = a.getBoolean(R.styleable.ZhuoRuiTopBar_zr_isBack, true);
        boolean isRefresh = a.getBoolean(R.styleable.ZhuoRuiTopBar_zr_isRefresh, false);
        int backResId = a.getResourceId(R.styleable.ZhuoRuiTopBar_zr_backResourceId, 0);
        int leftResId = a.getResourceId(R.styleable.ZhuoRuiTopBar_zr_popResourceId, 0);
        int rightResId = a.getResourceId(R.styleable.ZhuoRuiTopBar_zr_rightBtnResourceId, 0);
        int right2ResId = a.getResourceId(R.styleable.ZhuoRuiTopBar_zr_right2BtnResourceId, 0);
        background = a.getResourceId(R.styleable.ZhuoRuiTopBar_zr_background, 0);
        a.recycle();
        if (!mFitsSystemWindowsPadding) {
            int barHeight = StatusBarUtil.getStatusBarHeight(context);
            setMinHeight(maxHeight + barHeight + getPaddingTop() + getPaddingBottom());
            setPadding(getPaddingLeft(), barHeight + getPaddingTop(), getPaddingRight(), getPaddingBottom());
        } else {
            setMinHeight(maxHeight + getPaddingTop() + getPaddingBottom());
        }

        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }
        if (!TextUtils.isEmpty(subTitle)) {
            setSubTitle(subTitle);
        }
        if (isback) {
            addBackView(backResId);
        }
        if (leftResId != 0) {
            setLeftView(getViewByResourceId(leftResId));
        }
        if (rightResId != 0) {
            addRightView(getViewByResourceId(rightResId));
        }
        if (isRefresh) {
            addRightView(new ZRTopBarRefreshView(context));
        }
        if (right2ResId != 0) {
            addRightView(getViewByResourceId(right2ResId));
        }
        setResources();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        titleContentSizeChange();
    }

    private void setResources() {
        if (background == 0) {
            setBackgroundColor(ResourceKt.color(R.color.main_background));
        } else {
            setBackgroundColor(ResourceKt.color(background));
        }
        if (titleColor != 0) {
            setTitleColor(titleColor);
        } else {
            setTitleColor(R.color.main_content_text_color);
        }
    }

    @Override
    public void applyUIMode(@org.jetbrains.annotations.Nullable Resources resources) {
        setResources();
        for (Function0 viewFunction : skinViewFunctionList) {
            viewFunction.invoke();
        }
    }

    private void setTitleColor(int titleColor) {
        if (vTitleView == null) {
            setTitleView(createTitleView());
        }
        if (vTitleView instanceof TextView) {
            ((TextView) vTitleView).setTextColor(ResourceKt.color(titleColor));
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() == 0) {
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(getMinHeight(), MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (vTitleView != null) {
            titleContentSizeChange();
        }
    }

    /**
     * @param resourcesId
     */
    public View getViewByResourceId(int resourcesId) {
        String name = getResources().getResourceTypeName(resourcesId);
        if ("mipmap".equals(name) || "drawable".equals(name)) {
            return getImageView(resourcesId);
        } else if ("string".equals(name)) {
            return getTextView(resourcesId);
        }
        //color
        return null;
    }

    /**
     * 获取默认返回事件
     *
     * @return
     */
    public OnClickListener getDefBackClickListener() {
        return view -> {
//            if (quickClickUtil.clickRecord(view.getId())) {
            pop(view.getContext());
//            }
        };
    }

    /**
     * 退出界面
     *
     * @param context
     */
    public synchronized void pop(Context context) {
        if (context instanceof NavigationActivity) {
            ((Activity) context).onBackPressed();
        } else if (context instanceof ContextWrapper) {
            context = ((ContextWrapper) context).getBaseContext();
            if (context instanceof NavigationActivity) {
                ((Activity) context).onBackPressed();
            } else if (context instanceof Activity) {
                Activity activity = (Activity) context;
                if (!activity.isFinishing()) {
                    activity.finish();
                }
            }
        }
    }

    /**
     * 获取web返回事件
     *
     * @return
     */
    private OnClickListener getWebBackClickListener() {
        return view -> {
//            if (vWebView.canGoBack()) {
//                vWebView.goBack();
//            } else {//if (quickClickUtil.clickRecord(view.getId()))
                pop(view.getContext());
          //  }

        };
    }

    /**
     * 获取web退出事件
     *
     * @return
     */
    private OnClickListener getWebPopClickListener() {
        return view -> {
//            if (quickClickUtil.clickRecord(view.getId())) {
            Fragment fragment = ViewEx.getFragment(this);
            if (fragment != null) {
                FragmentEx.pop(fragment);
            } else {
                pop(view.getContext());
            }
//            }
        };
    }

//    /**
//     * 绑定webView
//     *
//     * @param web
//     */
//    public void bindWebView(final ZRWebView web) {
//        vWebView = web;
//        setBackClickListener(getWebBackClickListener());
//        final View pop = getImageView(popResId == 0 ? R.mipmap.ic_pop : popResId);
//        setLeftView(pop);
//        web.setUpdateVisitedHistortyListener((view, url, isReload) -> onWebHistoryChange(web, pop));
//        onWebHistoryChange(web, pop);
//    }

    /**
     * web历史记录变化
     *
     * @param web
     * @param btn
     */
    private void onWebHistoryChange(WebView web, View btn) {
        if (web.canGoBack()) {
            btn.setOnClickListener(getWebPopClickListener());
            btn.setVisibility(View.VISIBLE);
        } else {
            btn.setOnClickListener(null);
            btn.setVisibility(View.GONE);
        }
    }

    /**
     * 设置title
     *
     * @param title
     */
    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            if (vTitleView != null) {
                removeView(vTitleView);
                vTitleView = null;
                layoutTitleView();
            }
            return;
        }
        if (vTitleView == null) {
            setTitleView(createTitleView());
        }
        if (vTitleView instanceof ITopbarTitle) {
            ((ITopbarTitle) vTitleView).setTitle(title);
        } else if (vTitleView instanceof TextView) {
            ((TextView) vTitleView).setText(title);
        }
    }

    /**
     * 设置副title
     *
     * @param subTitle
     */
    public void setSubTitle(CharSequence subTitle) {
        if (TextUtils.isEmpty(subTitle)) {
            if (vSubTitleView != null) {
                removeView(vSubTitleView);
                vSubTitleView = null;
                layoutTitleView();
            }
            return;
        }
        if (vSubTitleView == null) {
            setSubTitleView(getSubTitleView());
        }
        if (vSubTitleView instanceof ITopbarTitle) {
            ((ITopbarTitle) vSubTitleView).setTitle(subTitle);
        } else if (vSubTitleView instanceof TextView) {
            ((TextView) vSubTitleView).setText(subTitle);
        }
    }

    /**
     * 添加返回键
     */
    public void addBackView(int resId) {
        if (vBackView == null) {
            setBackView(getImageView(resId == 0 ? R.mipmap.ic_back : resId));
            setBackClickListener(getDefBackClickListener());
        }
    }

    /**
     * 移除返回键
     */
    public void removeBackView() {
        if (vBackView != null) {
            removeView(vBackView);
            vBackView = null;
            layoutLeftView();
        }
    }


    public View getBackView() {
        return vBackView;
    }

    /**
     * 设置titleView
     *
     * @param v
     */
    public void setTitleView(View v) {
        if (addView(vTitleView, v, 0)) {
            vTitleView = v;
            layoutTitleView();
            if (isAttachedToWindow()) {
                titleContentSizeChange();
            }
        }
    }

    public View getTitleView() {
        return vTitleView;
    }

    /**
     * 设置副title
     *
     * @param v
     */
    public void setSubTitleView(View v) {
        if (addView(vSubTitleView, v)) {
            vSubTitleView = v;
            layoutTitleView();
        }
    }

    /**
     * 设置副标题颜色
     *
     * @param color
     */
    public void setSubTitleTextColor(@ColorInt int color) {
        if (vSubTitleView instanceof TextView) {
            ((TextView) vSubTitleView).setTextColor(color);
        }
    }

    /**
     * 设置返回btn
     *
     * @param v
     */
    public void setBackView(View v) {
        if (addView(vBackView, v)) {
            vBackView = v;
            layoutLeftView();
        }
    }

    /**
     * 设置左侧view
     *
     * @param v
     */
    public void setLeftView(View v) {
        if (addView(vLeftView, v)) {
            vLeftView = v;
            layoutLeftView();
        }
    }

    /**
     * 添加右侧view
     *
     * @param vs
     */
    public void addRightView(View... vs) {
        if (vs != null) {
            List<View> his = getRightViews();
            boolean change = false;
            for (View v : vs) {
                if (v != null && !his.contains(v)) {
                    if (mRightClickListenr != null) {
                        setRightButtonClickListener(v);
                    }
                    his.add(v);
                    change = true;
                }
            }
            if (change) {
                layoutRightView();
            }
        }
    }

    /**
     * 添加右侧view
     *
     * @param position
     * @param v
     */
    public void addRightView(int position, View v) {
        List<View> his = getRightViews();
        if (!his.contains(v)) {
            his.add(position, v);
            if (mRightClickListenr != null) {
                setRightButtonClickListener(v);
            }
            layoutRightView();
        }
    }

    /**
     * 移除右侧view
     *
     * @param position
     */
    public void removeRightView(int position) {
        List<View> his = getRightViews();
        if (position < his.size() && position >= 0) {
            View v = his.remove(position);
            removeView(v);
            v.setOnClickListener(null);
            layoutRightView();
        }
    }

    /**
     * 移除右侧view
     *
     * @param vs
     */
    public void removeRightView(View... vs) {
        if (vs != null) {
            List<View> his = getRightViews();
            for (View v : vs) {
                if (v != null) {
                    his.remove(v);
                    removeView(v);
                    v.setOnClickListener(null);
                }
            }
            layoutRightView();
        }
    }

    public void removeAllRightView() {
        List<View> his = getRightViews();
        for (View v : his) {
            removeView(v);
            v.setOnClickListener(null);
        }
        his.clear();
        layoutRightView();
    }

    public View getRightView(int index) {
        return getRightViews().get(index);
    }

    /**
     * 获取右侧view集合
     *
     * @return
     */
    protected synchronized List<View> getRightViews() {
        if (mRightViews == null) {
            mRightViews = new ArrayList<>();
        }
        return mRightViews;
    }


    /**
     * 比较并添加view
     *
     * @param oldView
     * @param newView
     * @return
     */
    private boolean addView(View oldView, View newView) {
        return addView(oldView, newView, -1);
    }

    /**
     * 比较并添加view
     *
     * @param oldView
     * @param newView
     * @return
     */
    private boolean addView(View oldView, View newView, int position) {
        if (oldView == newView) return false;
        removeView(oldView);
        if (newView == null) return false;
        addView(newView, position);
        return true;
    }

    /**
     * 布局右侧view
     */
    private void layoutRightView() {
        List<View> views = getRightViews();
        int preID = ConstraintSet.PARENT_ID;
        int strIndex = views.size() - 1;
        for (int i = strIndex; i >= 0; i--) {
            View v = views.get(i);
            if (v.getId() == NO_ID || this.findViewById(v.getId()) == null) {
                addView(v);
            }
            int curID = v.getId();
            ConstraintLayout constraintLayout = this;
            ConstraintSet set = new ConstraintSet();
            set.clone(constraintLayout);
            if (i == strIndex) {
                int margin = rightPadding - v.getPaddingRight();
                set.connect(curID, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, margin < 0 ? 0 : margin);
            } else {
                set.connect(curID, ConstraintSet.RIGHT, preID, ConstraintSet.LEFT, 0);
            }
            set.connect(curID, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            set.connect(curID, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            set.applyTo(constraintLayout);
            preID = curID;
        }
        layoutTitleView();
    }

    /**
     * 布局左则view
     */
    private void layoutLeftView() {
        View vBack = vBackView;
        if (vBack != null) {
            ConstraintLayout constraintLayout = this;
            ConstraintSet set = new ConstraintSet();
            set.clone(constraintLayout);
            int margin = leftPadding - vBack.getPaddingLeft();
            set.connect(vBack.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, margin < 0 ? 0 : margin);
            set.connect(vBack.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            set.connect(vBack.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            set.applyTo(constraintLayout);
        }
        View vLeft = vLeftView;
        if (vLeft != null) {
            ConstraintLayout constraintLayout = this;
            ConstraintSet set = new ConstraintSet();
            set.clone(constraintLayout);
            if (vBack != null) {
                set.connect(vLeft.getId(), ConstraintSet.LEFT, vBack.getId(), ConstraintSet.RIGHT, 0);
            } else {
                int margin = leftPadding - vLeft.getPaddingLeft();
                set.connect(vLeft.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, margin < 0 ? 0 : margin);
            }
            set.connect(vLeft.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            set.connect(vLeft.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            set.applyTo(constraintLayout);
        }
        layoutTitleView();
    }

    /**
     * 布局title
     */
    private void layoutTitleView() {
        View title = vTitleView;
        View subTitle = vSubTitleView;
        if (title != null) {
            int oldPadding = title.getPaddingLeft();
            int leftBtnWidth = getLeftBtnWidth();
            int rightBtnWidth = getRightBtnWidth();
            final int padding = Math.max(leftBtnWidth, rightBtnWidth);
            title.setPadding(padding, title.getPaddingTop(), padding, title.getPaddingBottom());
            ConstraintLayout constraintLayout = this;
            ConstraintSet set = new ConstraintSet();
            set.clone(constraintLayout);
            int titleId = title.getId();
            set.connect(titleId, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
            set.connect(titleId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            set.connect(titleId, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
            if (subTitle != null) {
                set.setVerticalChainStyle(titleId, ConstraintSet.CHAIN_PACKED);
                set.connect(titleId, ConstraintSet.BOTTOM, subTitle.getId(), ConstraintSet.TOP);
            } else {
                set.setVerticalChainStyle(titleId, ConstraintSet.CHAIN_SPREAD);
                set.connect(titleId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            }
            set.applyTo(constraintLayout);
            if (oldPadding != padding) {
                titleContentSizeChange();
            }
        }
        if (subTitle != null) {
            ConstraintLayout constraintLayout = this;
            ConstraintSet set = new ConstraintSet();
            set.clone(constraintLayout);
            int subTitleId = subTitle.getId();
            set.connect(subTitleId, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
            if (title != null) {
                set.connect(subTitleId, ConstraintSet.TOP, title.getId(), ConstraintSet.BOTTOM);
            } else {
                set.connect(subTitleId, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
            }
            set.connect(subTitleId, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
            set.connect(subTitleId, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
            set.applyTo(constraintLayout);
        }
    }

    /**
     * 通知title 可显示区域大小变化
     */
    private void titleContentSizeChange() {
        if (vTitleView != null) {
            int maxW = getMaxTitleContentWidth();
            onTitleContentSizeChange(maxW, getHeight());
        }
    }

    /**
     * title可显示区域大小变化
     */
    protected void onTitleContentSizeChange(int width, int hight) {

    }

    /**
     * 获取title可显示区域宽
     *
     * @return
     */
    public int getMaxTitleContentWidth() {
        int w = getWidth() == 0 ? getResources().getDisplayMetrics().widthPixels : getWidth();
        w = w - getPaddingLeft() - getPaddingRight();
        return vTitleView != null ? w - vTitleView.getPaddingLeft() - vTitleView.getPaddingRight() : w;
    }

    /**
     * 获取右侧btn总宽度
     *
     * @return
     */
    private int getRightBtnWidth() {
        int w = 0;
        List<View> vs = getRightViews();
        for (View v : vs) {
            w += getWidthWrapContent(v);
        }
        return w > 0 ? w + rightPadding - btnPadding : w;
    }

    /**
     * 获取左侧btn总宽度
     *
     * @return
     */
    private int getLeftBtnWidth() {
        int w = 0;
        if (vBackView != null) {
            w += getWidthWrapContent(vBackView);
        }
        if (vLeftView != null) {
            w += getWidthWrapContent(vLeftView);
        }
        return w > 0 ? w + leftPadding - btnPadding : w;
    }

    /**
     * 获取自适应宽度view的宽
     *
     * @param v
     * @return
     */
    private int getWidthWrapContent(View v) {
        int vw = v.getWidth();
        if (vw == 0) {
            int measureWidth = View.MeasureSpec.makeMeasureSpec((1 << 30) - 1, View.MeasureSpec.AT_MOST);
            int measureHeight = View.MeasureSpec.makeMeasureSpec((1 << 30) - 1, View.MeasureSpec.AT_MOST);
            v.measure(measureWidth, measureHeight);
            vw = v.getMeasuredWidth();
        }
        return vw;
    }

    /**
     * 获取titleView
     *
     * @return
     */
    public View createTitleView() {
        TextView tv = new TextView(getContext(), null, R.style.TextStyle);
        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.main_content_text_color));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setMaxLines(1);
        ViewEXKt.sansSerifMedium(tv);
        LayoutParams lp = new LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        return tv;
    }

    /**
     * 获取副titleView
     *
     * @return
     */
    protected View getSubTitleView() {
        TextView tv = new TextView(getContext(), null, R.style.TextStyle);
        tv.setTextColor(ContextCompat.getColor(getContext(), R.color.subtitle_text_color));
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setMaxLines(1);
        TextPaint tp = tv.getPaint();
        tp.setFakeBoldText(true);
        LayoutParams lp = new LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tv.setLayoutParams(lp);
        return tv;
    }

    /**
     * 获取图像btn显示控件
     *
     * @return
     */
    public ImageView getImageView() {
        return new TopbarImageView(getContext());
    }

    /**
     * 获取图像btn显示控件
     *
     * @return
     */
    public ImageView getImageView(@DrawableRes final int resId) {
        final ImageView iv = getImageView();
        skinViewFunctionList.add(() -> {
            iv.setImageResource(resId);
            return null;
        });
        iv.setImageResource(resId);
        return iv;
    }

    /**
     * 获取文字btn显示控件
     *
     * @return
     */
    public TextView getTextView() {
        TextView tv = new TextView(getContext());
        tv.setPadding(btnPadding, 0, btnPadding, 0);
        tv.setGravity(Gravity.CENTER);
        if (rightTextColor == 0) {
            tv.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.selector_topbar_btn_textcolor));
        } else {
            tv.setTextColor(rightTextColor);
        }
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, 0));
        return tv;
    }

    /**
     * 获取文字btn显示控件
     *
     * @return
     */
    public TextView getTextView(final int resId) {
        final TextView tv = getTextView();
        tv.setText(ResourceKt.text(resId));
        skinViewFunctionList.add(() -> {
            tv.setText(ResourceKt.text(resId));
            if (rightTextColor == 0) {
                tv.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.selector_topbar_btn_textcolor));
            } else {
                tv.setTextColor(rightTextColor);
            }
            return null;
        });
        return tv;
    }

    /**
     * 设置title点击事件
     *
     * @param listener
     */
    public void setTitleClickListener(OnClickListener listener) {
        if (vTitleView != null) {
            vTitleView.setOnClickListener(listener);
        }
    }

    /**
     * 设置副标题点击事件
     *
     * @param listener
     */
    public void setSubTitleClickListener(OnClickListener listener) {
        if (vSubTitleView != null) {
            vSubTitleView.setOnClickListener(listener);
        }
    }

    /**
     * 设置返回点击事件
     *
     * @param l
     */
    public void setBackClickListener(OnClickListener l) {
        if (vBackView != null) vBackView.setOnClickListener(l);
    }

    /**
     * 设置左侧点击事件
     *
     * @param l
     */
    public void setLifeViewClickListener(OnClickListener l) {
        if (vLeftView != null) vLeftView.setOnClickListener(l);
    }

    /**
     * 设置右侧btn点击事件监听
     * 此监听方式适用于btn位置，数量不会改变的情况
     * 如有动态变更情况，点击事件逻辑外部维护
     *
     * @param listener
     */
    public void setOnRightButtonClickListener(OnButtonClickListener listener) {
        mRightClickListenr = listener;
        List<View> vs = getRightViews();
        for (View v : vs) {
            setRightButtonClickListener(v);
        }
    }

    /**
     * 设置右侧btn监听
     *
     * @param v
     */
    private void setRightButtonClickListener(View v) {
        if (mRightClickListenr == null) {
            v.setOnClickListener(null);
        } else {
            v.setOnClickListener(view -> {
                int resid = 0;
                if (view instanceof TopbarImageView) {
                    resid = ((TopbarImageView) view).getResId();
                }
                mRightClickListenr.onButtonClick(view, getRightViews().indexOf(view), resid);
            });
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child.getId() == NO_ID) {
            child.setId(View.generateViewId());
            child.setSaveEnabled(false);
        }
        super.addView(child, index, params);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        if (vWebView != null) {
//            vWebView.setUpdateVisitedHistortyListener(null);
//        }
    }

    /**
     * topBar图像btn显示ImageView
     */
    class TopbarImageView extends AppCompatImageView {

        private int resId;

        public TopbarImageView(Context context) {
            super(context);
            init();
        }

        public TopbarImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public TopbarImageView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        /**
         * 获取显示的资源id,只在通过setImageResource方法设置显示图像，方法获取到的才有效
         *
         * @return
         */
        public int getResId() {
            return resId;
        }

        private void init() {
            setAdjustViewBounds(true);
            int topBottoPadding = (maxHeight - defIconHight) / 2;
            setPadding(btnPadding, topBottoPadding, btnPadding, topBottoPadding);
            setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, maxHeight));
        }

        @Override
        public void setImageResource(int resId) {
            this.resId = resId;
            Drawable drawable = ContextCompat.getDrawable(getContext(), resId);
            setImageDrawable(drawable);
        }

        @Override
        public void setImageDrawable(@Nullable Drawable drawable) {
            int right = (int) (defIconHight * 1f / drawable.getMinimumHeight() * drawable.getMinimumWidth());
            drawable.setBounds(0, 0, right, defIconHight);
            super.setImageDrawable(drawable);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            Drawable drawable = getDrawable();
            int width;
            if (drawable != null) {
                width = (int) (defIconHight * 1f / drawable.getMinimumHeight() * drawable.getMinimumWidth());
            } else {
                width = defIconHight;
            }
            setMeasuredDimension(width + btnPadding + btnPadding, maxHeight);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            boolean t = super.onTouchEvent(event);
            if (t) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        setAlpha(0.5f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_OUTSIDE:
                    case MotionEvent.ACTION_CANCEL:
                        setAlpha(1.0f);
                        break;
                }
            }
            return t;
        }
    }

    public interface ITopbarTitle {
        void setTitle(CharSequence title);
    }

    public interface OnButtonClickListener {

        /**
         * tabBar点击事件
         *
         * @param v
         * @param position
         */
        void onButtonClick(View v, int position, int resid);
    }

}
