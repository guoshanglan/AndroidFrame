
package com.zhuorui.commonwidget.adapter;

import static com.zhuorui.commonwidget.adapter.IZRStateView.CONTENT;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuorui.commonwidget.ZRMultiStateFrame;
import com.zhuorui.commonwidget.ZRMultiStatePageView;

import org.jetbrains.annotations.NotNull;

import base2app.adapter.BaseListAdapter;

/**
 * date   : 2020/6/11 14:10
 * desc   : 带状态RecyclerView Adapter
 */
public abstract class ZRStateAdapter<T> extends BaseListAdapter<T> implements IZRStateAdapter {

    /**
     * 状态view类型
     */
    protected final int TYPE_STATE_VIEW = -999;

    /**
     * /**
     * 是否显示状态view
     */
    private boolean mShowStateView = true;

    /**
     * 锁定Adapter设置状态view权限，锁定adapter不能设置手动设置的状态view的所有属性
     * true:Adapter不能设置，false:可以设置手动设置的状态view
     */
    public boolean isLockStateSetPermissions() {
        return false;
    }

    /**
     * adapter是否添加到RecyclerView
     */
    private boolean mAttachedToRecycler = false;

    /**
     * 状态View控制器
     */
    protected final StateController mStateController = new StateController(isLockStateSetPermissions());

    private boolean mOutSetStateView = false;

    @Override
    public int getItemCount() {
        int itemCount = super.getItemCount();
        return itemCount <= 0 && showStateItem() ? 1 : itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (super.getItemCount() <= 0 && showStateItem()) {
            return TYPE_STATE_VIEW;
        } else {
            int type = getItemViewType(position, getItemIndex(position));
            if (type == TYPE_STATE_VIEW) {
                throw new RuntimeException(TYPE_STATE_VIEW + "is the STATE_VIEW_TYPE,Cannot be used externally");
            }
            return type;
        }
    }

    protected boolean showStateItem() {
        return (mShowStateView && mOutSetStateView) || (mShowStateView && mStateController.mFrame != null && mStateController.mFrame.getState() != CONTENT);
    }

    @NonNull
    @Override
    @CallSuper
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_STATE_VIEW) {
            return new StateViewHolder(parent.getContext());
        } else {
            return super.onCreateViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (mShowStateView && holder instanceof StateViewHolder) {
            ((StateViewHolder) holder).bind(mStateController);
        } else {
            super.onBindViewHolder(holder, position);
        }
    }

    /**
     * 获取item类型
     *
     * @param position  adapter中的位置
     * @param itemIndex 数据集合中的类型
     * @return 数据对应的item类型
     */
    public int getItemViewType(int position, int itemIndex) {
        return 0;
    }

    /**
     * 是否显示状态View
     */
    public boolean isShowStateView() {
        return mShowStateView;
    }

    @CallSuper
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mAttachedToRecycler = true;
    }

    @CallSuper
    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        mAttachedToRecycler = false;
    }

    /**
     * 设置是否显示状态View
     */
    @CallSuper
    public void setShowStateView(boolean show) {
        if (mShowStateView != show) {
            mShowStateView = show;
            if (super.getItemCount() <= 0 && mAttachedToRecycler) {
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 设置自定义状态View
     *
     * @param stateView 状态View
     */
    public void setStateView(View stateView) {
        setStateView(stateView, isLockStateSetPermissions());
    }

    /**
     * 设置自定义状态View
     *
     * @param stateView               状态View
     * @param lockStateSetPermissions 是否锁定adapter对状态view的修改权限
     */
    public void setStateView(View stateView, boolean lockStateSetPermissions) {
        mOutSetStateView = true;
        mShowStateView = true;
        mStateController.setStateView(stateView, lockStateSetPermissions);
    }

    @Override
    public void setFrame(@NotNull ZRMultiStateFrame frame) {
        setFrame(frame, true);
    }

    public void setFrame(@NotNull ZRMultiStateFrame frame, boolean notifyChanged) {
        boolean oldShow = showStateItem();
        mStateController.setFrame(checkFrameEmpty(frame));
        if (notifyChanged && super.getItemCount() <= 0 && oldShow != showStateItem()) {
            notifyDataSetChanged();
        }
    }

    @Override
    public void setStateMinimumHeight(int minimunHeight) {
        mStateController.setStateMinimumHeight(minimunHeight);
    }

    @Override
    public int getStateMinimumHeight() {
        return mStateController.getStateMinimumHeight();
    }

    public void setStateMaxHeight(int maxHeight) {
        mStateController.setMaxHeight(maxHeight);
    }

    /**
     * 检查空状态数据
     */
    protected ZRMultiStateFrame checkFrameEmpty(ZRMultiStateFrame frame) {
        if (frame.getState() == IZRStateView.EMPTY) {
            int iconRes = getEmptyIcon();
            if (iconRes != 0 && frame.getIconResId() == null) {
                frame.setIconResId(iconRes);
            }
            CharSequence msg = getEmptyMassge();
            if (msg != null && frame.getTipsText() == null) {
                frame.setTipsText(msg);
            }
        }
        return frame;
    }

    /**
     * adapter默认空占位文字
     */
    protected CharSequence getEmptyMassge() {
        return null;
    }

    /**
     * adapter默认空占位icon
     *
     * @return 资源ID
     */
    protected int getEmptyIcon() {
        return 0;
    }

    static class StateViewHolder extends RecyclerView.ViewHolder implements IListItemViewHolder2 {

        public StateController mController;

        public StateViewHolder(@NonNull Context context) {
            super(getRooView(context));
        }


        private static View getRooView(Context context) {
            FrameLayout itemView = new FrameLayout(context);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
            return itemView;
        }

        public void bind(StateController controller) {
            controller.refreshStateView();
            mController = controller;
            if (controller.mMaxHeight != 0) {
                itemView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, controller.mMaxHeight));
            }
        }

        @Override
        public void attached() {
            if (mController != null) {
                mController.bind((ViewGroup) itemView);
            }
        }

        /**
         * item从界面移除
         */
        @Override
        public void detached() {
            if (mController != null) {
                mController.unBind();
            }
        }

        /**
         * item复用回收
         */
        @Override
        public void recycled() {
            mController = null;
        }
    }

    static class StateController implements IZRStateAdapter {

        private View mView;
        private ViewGroup vParent;
        private int mMiniHeight = 0;
        private int mMaxHeight = 0;
        private ZRMultiStateFrame mFrame;
        private IZRStateView mStateView;
        /**
         * 锁定Adapter内部更新状态view
         * true :不允许更新
         */
        private boolean mLockRefreshStateView;

        StateController(boolean lock) {
            this.mLockRefreshStateView = lock;
        }

        public void unBind() {
            vParent = null;
            if (mView != null && mView.getParent() != null) {
                ((ViewGroup) mView.getParent()).removeView(mView);
            }
        }

        public void bind(ViewGroup parent) {
            if (vParent != null && vParent != parent) {
                vParent.removeView(mView);
            }
            this.vParent = parent;
            if (mView == null) {
                ZRMultiStatePageView defV = new ZRMultiStatePageView(parent.getContext());
                mView = defV;
                mStateView = defV;
            }
            addView();
            refreshStateView();
        }

        private void refreshStateView() {
            if (!mLockRefreshStateView) {
                if (mStateView != null && mFrame != null) {
                    mStateView.setFrame(mFrame);
                }
                if (mView != null && mView.getMinimumHeight() != mMiniHeight) {
                    mView.setMinimumHeight(mMiniHeight);
                }
            }
        }

        private void addView() {
            if (vParent != null && mView != null && mView.getParent() == null) {
                FrameLayout.LayoutParams lp;
                ViewGroup.LayoutParams vLP = mView.getLayoutParams();
                if (vLP == null) {
                    lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                } else if (vLP instanceof FrameLayout.LayoutParams) {
                    lp = (FrameLayout.LayoutParams) vLP;
                } else {
                    lp = new FrameLayout.LayoutParams(vLP.width, vLP.height);
                }
                lp.gravity = Gravity.CENTER;
                vParent.addView(mView, lp);
            }
        }

        public void setStateView(@NonNull View view, boolean lock) {
            this.mLockRefreshStateView = lock;
            if (vParent != null && mView != null) {
                vParent.removeView(mView);
            }
            this.mView = view;
            this.mStateView = view instanceof IZRStateView ? (IZRStateView) view : null;
            addView();
            refreshStateView();
        }

        @Override
        public void setFrame(@NonNull ZRMultiStateFrame frame) {
            this.mFrame = frame;
            if (mStateView != null) {
                mStateView.setFrame(mFrame);
            }
        }

        @Override
        public void setStateMinimumHeight(int minimunHeight) {
            this.mMiniHeight = minimunHeight;
            if (mView != null) {
                mView.setMinimumHeight(minimunHeight);
            }
        }

        public void setMaxHeight(int mMaxHeight) {
            this.mMaxHeight = mMaxHeight;
        }

        @Override
        public int getStateMinimumHeight() {
            return mMiniHeight;
        }

    }

}
