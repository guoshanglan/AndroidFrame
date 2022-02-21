package com.zhuorui.commonwidget;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.LOCAL_VARIABLE;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.CLASS;

import android.content.Context;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zhuorui.commonwidget.adapter.IZRStateView;
import com.zhuorui.commonwidget.adapter.OnClickRetryLoadingListener;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.ref.SoftReference;
import java.util.Map;

import base2app.ex.ResourceKt;


/**
 * @date 2020/5/9 10:34
 * @desc 多状态页面切换View, 控制显示加载中，加载失败，和空数据,其他自行扩展
 */
public class ZRMultiStatePageView extends FrameLayout implements IZRStateView {


    public static final int LOADING = 0;

    public static final int FAULT = 1;

    public static final int SUCCESS = 2;

    @IntDef({LOADING, FAULT, SUCCESS})
    @Retention(CLASS)
    @Target({METHOD, PARAMETER, FIELD, LOCAL_VARIABLE, ANNOTATION_TYPE, PACKAGE})
    public @interface ZRLoadState {

    }

    /**
     * 状态帧
     */
    private ZRMultiStateFrame mFrame = ZRMultiStateFrame.Companion.createContentFrame();

    /**
     * 状态View
     */
    private final ArrayMap<Integer, SoftReference<View>> mStateViewMap = new ArrayMap<>();

    /**
     * 最小高度
     */
    private int mMiniPageHeight;

    /**
     * 是否自动清理状态view
     */
    private boolean isAutoClearStateView = true; // 是否自动清理状态view

    public ZRMultiStatePageView(@NonNull Context context) {
        this(context, null);
    }

    public ZRMultiStatePageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZRMultiStatePageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMiniPageHeight = getMinimumHeight();
    }

    @Override
    public void setMinimumHeight(int minHeight) {
        mMiniPageHeight = minHeight;
        controlPageHeight(mFrame.getState() != IZRStateView.CONTENT);
    }

    private void controlPageHeight(boolean isExactly) {
        if (mMiniPageHeight > 0 && isExactly) {
            if (getMinimumHeight() != mMiniPageHeight) {
                super.setMinimumHeight(mMiniPageHeight);
            }
        } else if (getMinimumHeight() != 0) {
            super.setMinimumHeight(0);
        }
    }

    /**
     * 设置状态帧
     *
     * @param frame 状态数据
     */
    @Override
    public void setFrame(@NonNull ZRMultiStateFrame frame) {
        if (frame == mFrame) return;
        ZRMultiStateFrame oldFrame = mFrame;
        mFrame = frame;
        if (oldFrame.getState() == IZRStateView.CUSTOM) {
            removeView(oldFrame.getCustomView());
        }
        if (oldFrame.getState() != frame.getState() || oldFrame.getBaseStyleRes() != frame.getBaseStyleRes()) {
            for (Map.Entry<Integer, SoftReference<View>> entry : mStateViewMap.entrySet()) {
                removeView(entry.getValue().get());
            }
            mStateViewMap.clear();
        }
        setState();
    }

    @NotNull
    @Override
    public View getView() {
        return this;
    }

    /**
     * 显示加载View
     */
    public void showLoadingView(@NonNull OnStartLoadingListener onStartLoadingListener) {
        setFrame(ZRMultiStateFrame.Companion.createLoadingFrame());
        onStartLoadingListener.onStartLoading();
    }

    /**
     * 显示空View
     */
    public void showEmptyView(@NonNull CharSequence emptyTips) {
        ZRMultiStateFrame frame = ZRMultiStateFrame.Companion.createEmptyFrame();
        frame.setTipsText(emptyTips);
        setFrame(frame);
    }

    /**
     * 显示自定义占位
     */
    public void showCustomView(View customView) {
        if (customView == null) {
            showContent();
        } else {
            setFrame(ZRMultiStateFrame.Companion.createCustomFrame(customView));
        }
    }

    /**
     * 显示加载失败的View
     */
    public void showFailureView(final OnClickRetryLoadingListener onClickRetryLoadingListener) {
        ZRMultiStateFrame frame = ZRMultiStateFrame.Companion.createFailFrame();
        frame.setOnButtonClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onClickRetryLoadingListener.onClickRetryLoading();
            }
        });
        setFrame(frame);
    }

    /**
     * 显示数据,移除所有占位
     */
    public void showContent() {
        setFrame(ZRMultiStateFrame.Companion.createContentFrame());
    }

    private LayoutParams getAddViewLayoutParams() {
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        lp.gravity = Gravity.CENTER;
        return lp;
    }


    public void setAutoClearStateView(boolean autoClearStateView) {
        isAutoClearStateView = autoClearStateView;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (isAutoClearStateView && mFrame.getState() != IZRStateView.CONTENT) {
            setState();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (isAutoClearStateView) {
            removeViewInLayout(mFrame.getCustomView());
            for (Map.Entry<Integer, SoftReference<View>> entry : mStateViewMap.entrySet()) {
                removeViewInLayout(entry.getValue().get());
            }
            mStateViewMap.clear();
        }
    }

    private void setState() {
        if (isAutoClearStateView && !isAttachedToWindow()) return;
        ZRMultiStateFrame frame = mFrame;
        int state = frame.getState();
        SoftReference<View> sr = mStateViewMap.get(state);
        View v = sr != null ? sr.get() : null;
        switch (state) {
            case IZRStateView.LAODING:
                ZRLoadingView loadingView;
                if (v != null) {
                    loadingView = (ZRLoadingView) v;
                } else {
                    loadingView = new ZRLoadingView(getContext());
                    mStateViewMap.put(state, new SoftReference<View>(loadingView));
                }
                if (loadingView.getParent() == null) {
                    addView(loadingView, getAddViewLayoutParams());
                }
                if (frame.getTipsText() != null) {
                    loadingView.setMessage(frame.getTipsText());
                } else {
                    loadingView.setMessage(ResourceKt.text(R.string.load_state_default));
                }
                loadingView.start();
                controlPageHeight(true);
                break;
            case IZRStateView.EMPTY:
            case IZRStateView.FAIL:
                ZRPlaceholderView vPlaceholder;
                if (v != null) {
                    vPlaceholder = (ZRPlaceholderView) v;
                } else {
                    vPlaceholder = new ZRPlaceholderView(getContext(), frame.getBaseStyleRes());
                    mStateViewMap.put(state, new SoftReference(vPlaceholder));
                }
                if (frame.getIconResId() != null) {
                    vPlaceholder.setIcon(frame.getIconResId());
                }
                if (frame.getTitleText() != null) {
                    vPlaceholder.setTipsText(frame.getTitleText());
                }
                if (frame.getTipsText() != null) {
                    vPlaceholder.setTipsText(frame.getTipsText());
                }
                if (frame.getButtonText() != null) {
                    vPlaceholder.setButtonText(frame.getButtonText());
                }
                if (frame.getStateBtnStyle() != null){
                    vPlaceholder.setStateBtnStyle(frame.getStateBtnStyle());
                }
                OnClickListener buttonClick = frame.getOnButtonClickListener();
                if (buttonClick != null && state == IZRStateView.FAIL) {
                    vPlaceholder.setButtonCLickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OnClickListener buttonClick = mFrame.getOnButtonClickListener();
                            if (buttonClick != null) {
                                buttonClick.onClick(v);
                            }
                            if (ZRMultiStatePageView.this.isAttachedToWindow()
                                    && mFrame.getState() == IZRStateView.FAIL) {
                                setFrame(ZRMultiStateFrame.Companion.createLoadingFrame());
                            }
                        }
                    });
                } else {
                    vPlaceholder.setButtonCLickListener(buttonClick);
                }
                if (vPlaceholder.getParent() == null) {
                    addView(vPlaceholder, getAddViewLayoutParams());
                }
                controlPageHeight(true);
                break;
            case IZRStateView.CUSTOM:
                v = frame.getCustomView();
                if (v != null) {
                    if (v.getParent() == null) {
                        addView(v, getAddViewLayoutParams());
                    }
                    controlPageHeight(true);
                    break;
                }
            case IZRStateView.CONTENT:
            default:
                controlPageHeight(false);
                break;
        }
    }

    public interface OnStartLoadingListener {
        void onStartLoading();
    }


}
