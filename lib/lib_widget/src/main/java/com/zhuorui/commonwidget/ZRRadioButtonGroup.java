package com.zhuorui.commonwidget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.zhuorui.commonwidget.common.ZRadioRecycleAdapter;
import com.zhuorui.securties.skin.view.ZRSkinAble;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import base2app.adapter.BaseListAdapter;
import base2app.ex.PixelExKt;

/**
 * 支持多行多列的RadioGroup
 */
public class ZRRadioButtonGroup extends RecyclerView implements BaseListAdapter.OnClickItemCallback<String>, ZRSkinAble {

    private String lastSelectedItem;
    private ZRadioRecycleAdapter radioAdapter;

    private OnSelectedCallBack callBack;

    /**
     * 垂直间距
     */
    private int verticalSpace;

    /**
     * 水平间距
     */
    private int horizontalSpace;

    /**
     * item的高度
     */
    private int itemHeight;

    /**
     * 当ZRRadioButtonGroup设置的宽度为WRAP_CONTENT时生效，否则itemWidth随spanCount和屏幕宽度变化
     */
    private int itemWidth;

    /**
     * 一行显示几个
     */
    private int spanCount;

    /**
     * 是否为均分模式，如果是，那么设置的itemWidth将无效
     */
    private boolean isAvgMode = false;

    public ZRRadioButtonGroup(Context context) {
        this(context, null);
    }

    public ZRRadioButtonGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ZRRadioButtonGroup);
        verticalSpace = typedArray.getDimensionPixelOffset(R.styleable.ZRRadioButtonGroup_rbg_vertical_space,
                (int) PixelExKt.dp2px(10));
        horizontalSpace = typedArray.getDimensionPixelOffset(R.styleable.ZRRadioButtonGroup_rbg_horizontal_space,
                (int) PixelExKt.dp2px(10));
        itemHeight = typedArray.getDimensionPixelOffset(R.styleable.ZRRadioButtonGroup_rbg_item_height, (int) PixelExKt.dp2px(28));
        itemWidth = typedArray.getDimensionPixelSize(R.styleable.ZRRadioButtonGroup_rbg_item_width, (int) PixelExKt.dp2px(70));
        spanCount = typedArray.getInteger(R.styleable.ZRRadioButtonGroup_rbg_span_count, 4);
        isAvgMode = typedArray.getBoolean(R.styleable.ZRRadioButtonGroup_rbg_avg_mode, false);
        typedArray.recycle();
    }

    public void setData(boolean isLand, List<String> valueList) {
        if (radioAdapter == null) {
            if (getItemAnimator() instanceof SimpleItemAnimator) {
                ((SimpleItemAnimator) getItemAnimator()).setSupportsChangeAnimations(false);
            }
            radioAdapter = new ZRadioRecycleAdapter(itemWidth, itemHeight, isAvgMode);
            LayoutManager layoutManager;
            if (isLand) {
                spanCount = valueList.size();
            }
            layoutManager = new GridLayoutManager(getContext(), spanCount);
            addItemDecoration(new GridSpacingItemDecoration(spanCount, horizontalSpace, verticalSpace, false));
            setLayoutManager(layoutManager);
            setAdapter(radioAdapter);
            radioAdapter.setClickItemCallback(this);
        }
        radioAdapter.setItems(valueList);
    }

    @Override
    public void onClickItem(int pos, String item, View v) {
        lastSelectedItem = item;
        if (callBack != null) {
            callBack.onSelected(pos, item);
        }
    }

    @Override
    public void applyUIMode(@Nullable Resources resources) {
        if (radioAdapter != null) {
            radioAdapter.notifyDataSetChanged();
        }
    }

    public String getLast() {
        return lastSelectedItem;
    }

    public int getSelectedIndex() {
        if (radioAdapter != null) {
            return radioAdapter.getSelectItem();
        }
        return 0;
    }

    public void setDefaultSelected(int position) {
        if (radioAdapter != null) {
            radioAdapter.selectedDeflut(position);
            radioAdapter.notifyDataSetChanged();
        }
    }

    public void setOnSelectedCallBack(OnSelectedCallBack callBack) {
        this.callBack = callBack;
    }

    public interface OnSelectedCallBack {
        void onSelected(int position, String item);
    }

    public ZRadioRecycleAdapter getRadioAdapter() {
        return radioAdapter;
    }
}
