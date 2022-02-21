package com.zhuorui.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.zhuorui.commonwidget.model.SortStatus;

import base2app.ex.PixelExKt;


/**
 * @date 2020/4/24 17:13
 * @desc 排序View封装
 */
public class FastSortView extends LinearLayout implements View.OnClickListener {

    private final int sortNormalIcon;
    private final int sortUpIcon;
    private final int sortDownIcon;

    private SortStatus sortStatus;

    private final TextView tvSortTitle;

    private final ImageView imgSortIcon;

    private FastSortView[] fastSortViews;

    /**
     * 排序模式：标准模式,包含三种状态UP,DOWN,NORMAL
     */
    private final static int FAST_SORT_MODE_STANDARD = 1;
    /**
     * 排序模式：upDown模式,包含两种状态UP,DOWN
     */
    private final static int FAST_SORT_MODE_UP_DOWN = 2;

    private final int mSortMode;

    public FastSortView(Context context) {
        this(context, null);
    }

    public FastSortView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @Override
    public void onClick(View v) {
        if (fastSortViews != null) {
            for (FastSortView fastSortView : fastSortViews) {
                if (fastSortView != null && fastSortView != this) {
                    fastSortView.resetSortIcon();
                }
            }
        }
        if (mSortMode == FAST_SORT_MODE_STANDARD) {
            //标准模式
            if (sortStatus == SortStatus.NORMAL) {
                sortStatus = SortStatus.DOWN;
                imgSortIcon.setImageResource(sortDownIcon);
            } else if (sortStatus == SortStatus.DOWN) {
                sortStatus = SortStatus.UP;
                imgSortIcon.setImageResource(sortUpIcon);
            } else {
                sortStatus = SortStatus.NORMAL;
                imgSortIcon.setImageResource(sortNormalIcon);
            }
        } else if (mSortMode == FAST_SORT_MODE_UP_DOWN) {
            //上下模式
            if (sortStatus == SortStatus.DOWN) {
                sortStatus = SortStatus.UP;
                imgSortIcon.setImageResource(sortUpIcon);
            } else {
                sortStatus = SortStatus.DOWN;
                imgSortIcon.setImageResource(sortDownIcon);
            }
        }
        if (onFastSortListener != null) {
            onFastSortListener.onFastSort(sortStatus);
        }
    }

    public interface OnFastSortListener {
        void onFastSort(SortStatus sortStatus);
    }

    private OnFastSortListener onFastSortListener;

    public void setOnFastSortListener(OnFastSortListener onFastSortListener) {
        this.onFastSortListener = onFastSortListener;
    }




    public FastSortView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(LinearLayout.HORIZONTAL);
        inflate(context, R.layout.layout_fast_sort_view, this);

        imgSortIcon = findViewById(R.id.img_sort_icon);
        tvSortTitle = findViewById(R.id.tv_sort_title);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FastSortView);

        mSortMode = typedArray.getInteger(R.styleable.FastSortView_fsv_sort_mode, FAST_SORT_MODE_UP_DOWN);

        int gravity = typedArray.getInt(R.styleable.FastSortView_fsv_gravity, 1);
        if (gravity == 1) {
            //居中
            setGravity(Gravity.CENTER);
        } else if (gravity == 2) {
            //靠左居中
            setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        } else {
            //靠右居中
            setGravity(Gravity.CENTER_VERTICAL | Gravity.END);
        }


        String title = typedArray.getString(R.styleable.FastSortView_fsv_title);
        int titleColor = typedArray.getColor(R.styleable.FastSortView_fsv_title_color, ContextCompat.getColor(context, R.color.subtitle_text_color));
        int titleSize = typedArray.getDimensionPixelSize(R.styleable.FastSortView_fsv_title_size, (int) PixelExKt.sp2px(12));

        tvSortTitle.setText(title);
        tvSortTitle.setTextColor(titleColor);
        tvSortTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);

        sortNormalIcon = typedArray.getResourceId(R.styleable.FastSortView_fsv_normal_icon, R.mipmap.ic_sort);
        sortUpIcon = typedArray.getResourceId(R.styleable.FastSortView_fsv_up_icon, R.mipmap.ic_sort_up);
        sortDownIcon = typedArray.getResourceId(R.styleable.FastSortView_fsv_down_icon, R.mipmap.ic_sort_down);

        int sortIconWidth = typedArray.getDimensionPixelSize(R.styleable.FastSortView_fsv_sort_icon_width, (int) PixelExKt.dp2px(6));
        int sortIconHeight = typedArray.getDimensionPixelSize(R.styleable.FastSortView_fsv_sort_icon_height, (int) PixelExKt.dp2px(10));

        int sortIconMarginLeft = typedArray.getDimensionPixelSize(R.styleable.FastSortView_fsv_icon_margin_left, (int) PixelExKt.dp2px(5));

        LinearLayout.LayoutParams layoutParams;

        if (sortIconHeight == 0 && sortIconWidth == 0) {
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        } else {
            layoutParams = new LinearLayout.LayoutParams(sortIconWidth, sortIconHeight);
        }
        layoutParams.leftMargin = sortIconMarginLeft;
        imgSortIcon.setLayoutParams(layoutParams);


        int defaultSortState = typedArray.getInt(R.styleable.FastSortView_fsv_sort_status, SortStatus.NORMAL.getValue());
        //设置默认图标
        if (defaultSortState == SortStatus.NORMAL.getValue()) {
            sortStatus = SortStatus.NORMAL;
            imgSortIcon.setImageResource(sortNormalIcon);
        } else if (defaultSortState == SortStatus.UP.getValue()) {
            sortStatus = SortStatus.UP;
            imgSortIcon.setImageResource(sortUpIcon);
        } else {
            sortStatus = SortStatus.DOWN;
            imgSortIcon.setImageResource(sortDownIcon);
        }

        this.setOnClickListener(this);
        typedArray.recycle();
    }


    /**
     * 重置
     */
    private void resetSortIcon() {
        this.sortStatus = SortStatus.NORMAL;
        imgSortIcon.setImageResource(sortNormalIcon);
    }


    /**
     * 外部设置sortTile
     */
    public void setSortTitle(String sortTitle) {
        tvSortTitle.setText(sortTitle);
    }

    public void showSortButton(boolean isShowSort) {
        imgSortIcon.setVisibility(isShowSort ? View.VISIBLE : View.GONE);
    }

    public FastSortView attachGroupFastSortView(FastSortView... fastSortViews) {
        this.fastSortViews = fastSortViews;
        return this;
    }


    /**
     * 根据最新排序状态更新排序图标
     */
    public void updateSortIconBySortStatus(SortStatus sortStatus) {
        if (fastSortViews != null) {
            for (FastSortView fastSortView : fastSortViews) {
                if (fastSortView != null) {
                    fastSortView.resetSortIcon();
                }
            }
        }
        this.sortStatus = sortStatus;
        if (sortStatus == SortStatus.NORMAL) {
            imgSortIcon.setImageResource(sortNormalIcon);
        } else if (sortStatus == SortStatus.UP) {
            imgSortIcon.setImageResource(sortUpIcon);
        } else {
            imgSortIcon.setImageResource(sortDownIcon);
        }
    }
}
