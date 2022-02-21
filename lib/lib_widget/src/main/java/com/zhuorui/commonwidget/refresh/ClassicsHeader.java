package com.zhuorui.commonwidget.refresh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.internal.ArrowDrawable;
import com.scwang.smartrefresh.layout.internal.InternalClassics;
import com.scwang.smartrefresh.layout.internal.ProgressDrawable;
import com.zhuorui.commonwidget.R;

import java.util.Date;
import java.util.List;

/**
 * 经典下拉头部
 * Created by SCWANG on 2017/5/28.
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ClassicsHeader extends InternalClassics<ClassicsHeader> implements RefreshHeader {

    public static final int ID_TEXT_UPDATE = R.id.srl_classics_update;

    public static String REFRESH_HEADER_PULLING = null;//"下拉可以刷新";
    public static String REFRESH_HEADER_REFRESHING = null;//"正在刷新...";
    public static String REFRESH_HEADER_LOADING = null;//"正在加载...";
    public static String REFRESH_HEADER_RELEASE = null;//"释放立即刷新";
    public static String REFRESH_HEADER_FINISH = null;//"刷新完成";
    public static String REFRESH_HEADER_FAILED = null;//"刷新失败";
    public static String REFRESH_HEADER_UPDATE = null;//"上次更新 M-d HH:mm";
    public static String REFRESH_HEADER_SECONDARY = null;//"释放进入二楼";

    protected String KEY_LAST_UPDATE_TIME = "LAST_UPDATE_TIME";

    protected Date mLastTime;
    protected TextView mLastUpdateText;
    protected SharedPreferences mShared;

    protected String mTextPulling;//"下拉可以刷新";
    protected String mTextRefreshing;//"正在刷新...";
    protected String mTextLoading;//"正在加载...";
    protected String mTextRelease;//"释放立即刷新";
    protected String mTextFinish;//"刷新完成";
    protected String mTextFailed;//"刷新失败";
    protected String mTextSecondary;//"释放进入二楼";

    public ClassicsHeader(Context context) {
        this(context, null);
    }

    public ClassicsHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClassicsHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        View.inflate(context, R.layout.app_layout_classics_header, this);

        final View thisView = this;
        final View arrowView = mArrowView = thisView.findViewById(R.id.srl_classics_arrow);
        final View progressView = mProgressView = thisView.findViewById(R.id.srl_classics_progress);

        mTitleText = thisView.findViewById(R.id.srl_classics_title);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ClassicsHeader);

        mFinishDuration = ta.getInt(R.styleable.ClassicsHeader_srlFinishDuration, mFinishDuration);
        mSpinnerStyle = SpinnerStyle.values[ta.getInt(R.styleable.ClassicsHeader_srlClassicsSpinnerStyle, mSpinnerStyle.ordinal)];

        if (ta.hasValue(R.styleable.ClassicsHeader_srlDrawableArrow)) {
            mArrowView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsHeader_srlDrawableArrow));
        } else if (mArrowView.getDrawable() == null) {
            mArrowDrawable = new ArrowDrawable();
            mArrowDrawable.setColor(0xff666666);
            mArrowView.setImageDrawable(mArrowDrawable);
        }

        if (ta.hasValue(R.styleable.ClassicsHeader_srlDrawableProgress)) {
            mProgressView.setImageDrawable(ta.getDrawable(R.styleable.ClassicsHeader_srlDrawableProgress));
        } else if (mProgressView.getDrawable() == null) {
            mProgressDrawable = new ProgressDrawable();
            mProgressDrawable.setColor(0xff666666);
            mProgressView.setImageDrawable(mProgressDrawable);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlPrimaryColor)) {
            super.setPrimaryColor(ta.getColor(R.styleable.ClassicsHeader_srlPrimaryColor, 0));
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlAccentColor)) {
            setAccentColor(ta.getColor(R.styleable.ClassicsHeader_srlAccentColor, 0));
        }

        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextPulling)) {
            mTextPulling = ta.getString(R.styleable.ClassicsHeader_srlTextPulling);
        } else if (REFRESH_HEADER_PULLING != null) {
            mTextPulling = REFRESH_HEADER_PULLING;
        } else {
            mTextPulling = context.getString(R.string.srl_header_pulling);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextLoading)) {
            mTextLoading = ta.getString(R.styleable.ClassicsHeader_srlTextLoading);
        } else if (REFRESH_HEADER_LOADING != null) {
            mTextLoading = REFRESH_HEADER_LOADING;
        } else {
            mTextLoading = context.getString(R.string.srl_header_loading);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextRelease)) {
            mTextRelease = ta.getString(R.styleable.ClassicsHeader_srlTextRelease);
        } else if (REFRESH_HEADER_RELEASE != null) {
            mTextRelease = REFRESH_HEADER_RELEASE;
        } else {
            mTextRelease = context.getString(R.string.srl_header_release);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextFinish)) {
            mTextFinish = ta.getString(R.styleable.ClassicsHeader_srlTextFinish);
        } else if (REFRESH_HEADER_FINISH != null) {
            mTextFinish = REFRESH_HEADER_FINISH;
        } else {
            mTextFinish = context.getString(R.string.srl_header_finish);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextFailed)) {
            mTextFailed = ta.getString(R.styleable.ClassicsHeader_srlTextFailed);
        } else if (REFRESH_HEADER_FAILED != null) {
            mTextFailed = REFRESH_HEADER_FAILED;
        } else {
            mTextFailed = context.getString(R.string.srl_header_failed);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextSecondary)) {
            mTextSecondary = ta.getString(R.styleable.ClassicsHeader_srlTextSecondary);
        } else if (REFRESH_HEADER_SECONDARY != null) {
            mTextSecondary = REFRESH_HEADER_SECONDARY;
        } else {
            mTextSecondary = context.getString(R.string.srl_header_secondary);
        }
        if (ta.hasValue(R.styleable.ClassicsHeader_srlTextRefreshing)) {
            mTextRefreshing = ta.getString(R.styleable.ClassicsHeader_srlTextRefreshing);
        } else if (REFRESH_HEADER_REFRESHING != null) {
            mTextRefreshing = REFRESH_HEADER_REFRESHING;
        } else {
            mTextRefreshing = context.getString(R.string.srl_header_refreshing);
        }
        ta.recycle();

        progressView.animate().setInterpolator(null);
        mTitleText.setText(thisView.isInEditMode() ? mTextRefreshing : mTextPulling);

        if (thisView.isInEditMode()) {
            arrowView.setVisibility(GONE);
        } else {
            progressView.setVisibility(GONE);
        }

        try {//try 不能删除-否则会出现兼容性问题
            if (context instanceof FragmentActivity) {
                FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
                if (manager != null) {
                    @SuppressLint("RestrictedApi")
                    List<Fragment> fragments = manager.getFragments();
                    if (fragments != null && fragments.size() > 0) {
                        return;
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        KEY_LAST_UPDATE_TIME += context.getClass().getName();
        mShared = context.getSharedPreferences("ClassicsHeader", Context.MODE_PRIVATE);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout layout, boolean success) {
        if (success) {
            mTitleText.setText(mTextFinish);
        } else {
            mTitleText.setText(mTextFailed);
        }
        return super.onFinish(layout, success);//延迟500毫秒之后再弹回
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        final View arrowView = mArrowView;
        final View updateView = mLastUpdateText;
        switch (newState) {
            case None:

            case PullDownToRefresh:
                mTitleText.setText(mTextPulling);
                arrowView.setVisibility(VISIBLE);
                arrowView.animate().rotation(0);
                break;
            case Refreshing:
            case RefreshReleased:
                mTitleText.setText(mTextRefreshing);
                arrowView.setVisibility(GONE);
                break;
            case ReleaseToRefresh:
                mTitleText.setText(mTextRelease);
                arrowView.animate().rotation(180);
                break;
            case ReleaseToTwoLevel:
                mTitleText.setText(mTextSecondary);
                arrowView.animate().rotation(0);
                break;
            case Loading:
                arrowView.setVisibility(GONE);
                mTitleText.setText(mTextLoading);
                break;
        }
    }

    public ClassicsHeader setAccentColor(@ColorInt int accentColor) {
        mLastUpdateText.setTextColor(accentColor & 0x00ffffff | 0xcc000000);
        return super.setAccentColor(accentColor);
    }
}