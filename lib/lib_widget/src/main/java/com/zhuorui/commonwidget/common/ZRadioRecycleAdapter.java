package com.zhuorui.commonwidget.common;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zhuorui.commonwidget.R;
import com.zhuorui.securities.base2app.ex.TextFont;

import base2app.adapter.BaseListAdapter;
import base2app.ex.ResourceKt;

public class ZRadioRecycleAdapter extends BaseListAdapter<String> {

    private final int itemHeight;
    private final int itemWidth;

    private final boolean isAvgMode;

    public ZRadioRecycleAdapter(int itemWidth, int itemHeight, boolean isAvgMode) {
        this.itemHeight = itemHeight;
        this.itemWidth = itemWidth;
        this.isAvgMode = isAvgMode;
    }

    private int mSelectedItem = -1;


    @NonNull
    @Override
    protected RecyclerView.ViewHolder createViewHolderByParent(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflateView(parent, R.layout.item_radio));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onClickItem(int absolutePsition, int bindingPosition, int dataPostion, String item, View itemView) {
        mSelectedItem = dataPostion;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).bind(getItem(position), mSelectedItem == position);
    }

    public class ViewHolder extends ListItemViewHolder<String> {

        private final RadioButton radioButton;

        public ViewHolder(View v) {
            super(v, true, false);
            radioButton = v.findViewById(R.id.radio_rb);
        }

        @Override
        public void bind(String item, int position) {
            //重写过onBindViewHolder,可以不实现
        }

        public void bind(String item, boolean checked) {
            radioButton.setChecked(checked);
            if (checked) {
                radioButton.setTypeface(TextFont.INSTANCE.getSAN_SERIf_MEDIUM());
            } else {
                radioButton.setTypeface(Typeface.DEFAULT);
            }
            if (isAvgMode) {
                radioButton.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, itemHeight));
            } else {
                radioButton.setLayoutParams(new RecyclerView.LayoutParams(itemWidth, itemHeight));
            }
            radioButton.setText(item);
            radioButton.setBackground(ResourceKt.drawable(R.drawable.select_radio_bg));
            radioButton.setTextColor(ResourceKt.colorState(R.drawable.select_radio_text_color));
        }

    }

    public void selectedDeflut(int position) {
        mSelectedItem = position;
    }

    public int getSelectItem() {
        return mSelectedItem;
    }

}
