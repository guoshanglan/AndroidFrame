package com.zhuorui.commonwidget.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.InternalClassics;
import com.scwang.smartrefresh.layout.util.SmartUtil;
import com.zhuorui.commonwidget.R;

/**
 * 经典上拉底部组件
 * Created by SCWANG on 2017/5/28.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ClassicsFooter extends InternalClassics<ClassicsFooter> implements RefreshFooter {

    public static String REFRESH_FOOTER_PULLING = null;//"上拉加载更多";
    public static String REFRESH_FOOTER_RELEASE = null;//"释放立即加载";
    public static String REFRESH_FOOTER_LOADING = null;//"正在加载...";
    public static String REFRESH_FOOTER_REFRESHING = null;//"正在刷新...";
    public static String REFRESH_FOOTER_FINISH = null;//"加载完成";
    public static String REFRESH_FOOTER_FAILED = null;//"加载失败";
    public static String REFRESH_FOOTER_NOTHING = null;//"没有更多数据了";

    protected String mTextPulling;//"上拉加载更多";
    protected String mTextRelease;//"释放立即加载";
    protected String mTextLoading;//"正在加载...";
    protected String mTextRefreshing;//"正在刷新...";
    protected String mTextFinish;//"加载完成";
    protected String mTextFailed;//"加载失败";
    protected String mTextNothing;//"没有更多数据了";

    protected boolean mNoMoreData = false;

    //<editor-fold desc="LinearLayout">
    public ClassicsFooter(Context context) {
        this(context, null);
    }

    public ClassicsFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClassicsFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View.inflate(context, R.layout.app_layout_classics_footer, this);

        final View thisView = this;
        mArrowView = thisView.findViewById(R.id.srl_classics_arrow);
        mProgressView = thisView.findViewById(R.id.srl_classics_progress);

        mTitleText = thisView.findViewById(R.id.srl_classics_title);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClassicsFooter);

        mFinishDuration = ta.getInt(R.styleable.ClassicsFooter_srlFinishDuration, mFinishDuration);
        mSpinnerStyle = SpinnerStyle.values[ta.getInt(R.styleable.ClassicsFooter_srlClassicsSpinnerStyle, mSpinnerStyle.ordinal)];

        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextSizeTitle)) {
            mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_PX, ta.getDimensionPixelSize(R.styleable.ClassicsFooter_srlTextSizeTitle, SmartUtil.dp2px(16)));
        }

        if (ta.hasValue(R.styleable.ClassicsFooter_srlPrimaryColor)) {
            super.setPrimaryColor(ta.getColor(R.styleable.ClassicsFooter_srlPrimaryColor, 0));
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlAccentColor)) {
            super.setAccentColor(ta.getColor(R.styleable.ClassicsFooter_srlAccentColor, 0));
        }

        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextPulling)) {
            mTextPulling = ta.getString(R.styleable.ClassicsFooter_srlTextPulling);
        } else if (REFRESH_FOOTER_PULLING != null) {
            mTextPulling = REFRESH_FOOTER_PULLING;
        } else {
            mTextPulling = context.getString(R.string.srl_footer_pulling);
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextRelease)) {
            mTextRelease = ta.getString(R.styleable.ClassicsFooter_srlTextRelease);
        } else if (REFRESH_FOOTER_RELEASE != null) {
            mTextRelease = REFRESH_FOOTER_RELEASE;
        } else {
            mTextRelease = context.getString(R.string.srl_footer_release);
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextLoading)) {
            mTextLoading = ta.getString(R.styleable.ClassicsFooter_srlTextLoading);
        } else if (REFRESH_FOOTER_LOADING != null) {
            mTextLoading = REFRESH_FOOTER_LOADING;
        } else {
            mTextLoading = context.getString(R.string.srl_footer_loading);
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextRefreshing)) {
            mTextRefreshing = ta.getString(R.styleable.ClassicsFooter_srlTextRefreshing);
        } else if (REFRESH_FOOTER_REFRESHING != null) {
            mTextRefreshing = REFRESH_FOOTER_REFRESHING;
        } else {
            mTextRefreshing = context.getString(R.string.srl_footer_refreshing);
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextFinish)) {
            mTextFinish = ta.getString(R.styleable.ClassicsFooter_srlTextFinish);
        } else if (REFRESH_FOOTER_FINISH != null) {
            mTextFinish = REFRESH_FOOTER_FINISH;
        } else {
            mTextFinish = context.getString(R.string.srl_footer_finish);
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextFailed)) {
            mTextFailed = ta.getString(R.styleable.ClassicsFooter_srlTextFailed);
        } else if (REFRESH_FOOTER_FAILED != null) {
            mTextFailed = REFRESH_FOOTER_FAILED;
        } else {
            mTextFailed = context.getString(R.string.srl_footer_failed);
        }
        if (ta.hasValue(R.styleable.ClassicsFooter_srlTextNothing)) {
            mTextNothing = ta.getString(R.styleable.ClassicsFooter_srlTextNothing);
        } else if (REFRESH_FOOTER_NOTHING != null) {
            mTextNothing = REFRESH_FOOTER_NOTHING;
        } else {
            mTextNothing = context.getString(R.string.srl_footer_nothing);
        }

        ta.recycle();

        mTitleText.setText(thisView.isInEditMode() ? mTextLoading : mTextPulling);

    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        if (!mNoMoreData) {
            super.onStartAnimator(refreshLayout, height, maxDragHeight);
        }
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        if (!mNoMoreData) {
            mTitleText.setText(success ? mTextFinish : mTextFailed);
            return super.onFinish(layout, success);
        }
        return 0;
    }

    /**
     * ClassicsFooter 在(SpinnerStyle.FixedBehind)时才有主题色
     */
    @Override
    @Deprecated
    public void setPrimaryColors(@ColorInt int... colors) {
        if (mSpinnerStyle == SpinnerStyle.FixedBehind) {
            super.setPrimaryColors(colors);
        }
    }

    /**
     * 设置数据全部加载完成，将不能再次触发加载功能
     */
    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        if (mNoMoreData != noMoreData) {
            mNoMoreData = noMoreData;
            if (noMoreData) {
                mTitleText.setText(mTextNothing);
            } else {
                mTitleText.setText(mTextPulling);
            }
        }
        return true;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        if (!mNoMoreData) {
            switch (newState) {
                case None:
                case PullUpToLoad:
                    mTitleText.setText(mTextPulling);
                    break;
                case Loading:
                case LoadReleased:
                    mTitleText.setText(mTextLoading);
                    break;
                case ReleaseToLoad:
                    mTitleText.setText(mTextRelease);
                    break;
                case Refreshing:
                    mTitleText.setText(mTextRefreshing);
                    break;
            }
        }
    }
}