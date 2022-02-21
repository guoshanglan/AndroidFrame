package com.zhuorui.commonwidget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuorui.commonwidget.adapter.HeaderFooterAdapter;
import com.zhuorui.commonwidget.adapter.ZRStateAdapter;

/**
 * author : liuwei
 * e-mail : vsanliu@foxmail.com
 * date   : 2019-12-20 09:12
 * desc   : 自带空数据占位图
 */
public class ZRRecyclerView extends RecyclerView {

    private float mDefStateMiniHeight;//状态view默认高度
    private float mOutHeaderHight = 0;//RecyclerView 外部title 高度
    private float mInHeaderHight = 0;//RecyclerView 内部header 高度

    public ZRRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public ZRRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZRRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDefStateMiniHeight = getResources().getDisplayMetrics().density * 300;
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        clearAdapters();
        initAdapter(adapter);
        super.setAdapter(adapter);
    }

    private void initAdapter(Adapter adapter) {
        if (adapter instanceof HeaderFooterAdapter) {
            initHeaderFooterAdapter((HeaderFooterAdapter) adapter);
        } else if (adapter instanceof ZRStateAdapter) {
            initStateAdapter((ZRStateAdapter) adapter);
        }
    }

    /**
     * 设置adapter到MergeAdapter
     *
     * @param adapters
     */
    public void setAdapter(Adapter... adapters) {
        if (adapters != null && adapters.length > 0) {
            for (Adapter adapter : adapters) {
                initAdapter(adapter);
            }
//            setAdapter(new MergeAdapter(adapters));
        } else {
            setAdapter((Adapter) null);
        }
    }

//    /**
//     * 添加adapter到MergeAdapter
//     *
//     * @param adapter
//     * @return
//     */
//    public boolean addAdapter(Adapter adapter) {
//        return addAdapter(-1, adapter);
//    }

//    /**
//     * 添加adapter到MergeAdapter指定位置
//     *
//     * @param adapter
//     * @return
//     */
//    public boolean addAdapter(int position, Adapter adapter) {
//        MergeAdapter mergeAdapter = getInternalMergeAdapter();
//        if (mergeAdapter == null) {
//            initAdapter(adapter);
//            mergeAdapter = new MergeAdapter(adapter);
//            super.setAdapter(mergeAdapter);
//            return true;
//        } else {
//            int size = mergeAdapter.getAdapters().size();
//            if (position < 0 || position >= size) {
//                position = size;
//            }
//            initAdapter(adapter);
//            return mergeAdapter.addAdapter(position, adapter);
//        }
//    }

    /**
     * 移除MergeAdapter中adapter
     *
     * @param adapter
     */
    public void remove(Adapter adapter) {
//        MergeAdapter mergeAdapter = getInternalMergeAdapter();
//        if (mergeAdapter != null) {
//            mergeAdapter.removeAdapter(adapter);
//        }
    }

//    /**
//     * 获取设置的MergeAdapter
//     *
//     * @return
//     */
//    private MergeAdapter getInternalMergeAdapter() {
//        Adapter adapter = getAdapter();
//        if (adapter instanceof MergeAdapter) {
//            return (MergeAdapter) adapter;
//        }
//        return null;
//    }

    /**
     * 清除MergeAdapter中adapter
     */
    public void clearAdapters() {
//        MergeAdapter mergeAdapter = getInternalMergeAdapter();
//        if (mergeAdapter != null) {
//            for (Adapter adapter : mergeAdapter.getAdapters()) {
//                mergeAdapter.removeAdapter(adapter);
//            }
//        }
    }

    private void initStateAdapter(ZRStateAdapter adapter) {
        if (!adapter.isLockStateSetPermissions() && adapter.getStateMinimumHeight() <= 0) {
            adapter.setStateMinimumHeight(getMiniHight());
        }
    }

    private void initHeaderFooterAdapter(final HeaderFooterAdapter headerFooterAdapter) {
        if (!headerFooterAdapter.isLockStateSetPermissions() && headerFooterAdapter.getStateMinimumHeight() <= 0) {
            headerFooterAdapter.setStateMinimumHeight(getMiniHight());
            final View headerView = headerFooterAdapter.getHeaderView();
            if (headerView != null) {
                headerView.post(new Runnable() {
                    @Override
                    public void run() {
                        mInHeaderHight = headerView.getHeight();
                        headerFooterAdapter.setStateMinimumHeight(getMiniHight());
                    }
                });
            }
        }

    }

    /**
     * RecyclerView外部头部高度
     *
     * @param height
     */
    public void setOutHeaderHeight(@Px int height) {
        mOutHeaderHight = height;
        setEmptyMiniHeight(getMiniHight());
    }

    private int getMiniHight() {
        return (int) (mDefStateMiniHeight - mOutHeaderHight - mInHeaderHight);
    }

    public void setEmptyMiniHeight(int miniHeight) {
        HeaderFooterAdapter adapter = getHFAdapter();
        if (adapter != null) {
            adapter.setStateMinimumHeight(miniHeight);
        }
    }

    private HeaderFooterAdapter getHFAdapter() {
        Adapter adapter = getAdapter();
        if (null != adapter && adapter instanceof HeaderFooterAdapter) {
            return (HeaderFooterAdapter) adapter;
        }
        return null;
    }

}