package com.zhuorui.securities.pickerview.date;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

import com.zhuorui.securities.pickerview.IWheelData;
import com.zhuorui.securities.pickerview.R;
import com.zhuorui.securities.pickerview.WheelPicker;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 月份选择器
 * Created by ycuwq on 17-12-28.
 */
public class MonthPicker extends WheelPicker<MonthPicker.MonthData> {

    private static final int MAX_MONTH = 12;
    private static final int MIN_MONTH = 1;

    private int mSelectedMonth;

    private OnMonthSelectedListener mOnMonthSelectedListener;

    private int mYear;
    private long mMaxDate, mMinDate;
    private int mMaxYear, mMinYear;
    private int mMinMonth = MIN_MONTH;
    private int mMaxMonth = MAX_MONTH;

    public MonthPicker(Context context) {
        this(context, null);
    }

    public MonthPicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MonthPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setItemMaximumWidthText("00");

        Calendar.getInstance().clear();
        mSelectedMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        updateMonth();
        setSelectedMonth(mSelectedMonth, false);
        setOnWheelChangeListener(new OnWheelChangeListener<MonthData>() {
            @Override
            public void onWheelSelected(MonthData item, int position) {
                mSelectedMonth = item.getItemData();
                if (mOnMonthSelectedListener != null) {
                    mOnMonthSelectedListener.onMonthSelected(mSelectedMonth);
                }
            }
        });
    }

    public void updateMonth() {
        List<MonthData> list = new ArrayList<>();
        for (int i = mMinMonth; i <= mMaxMonth; i++) {
            list.add(new MonthData(i));
        }
        setDataList(list);
    }

    public void setMaxDate(long date) {
        mMaxDate = date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        mMaxYear = calendar.get(Calendar.YEAR);
    }

    public void setMinDate(long date) {
        mMinDate = date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date);
        mMinYear = calendar.get(Calendar.YEAR);
    }


    public void setYear(int year) {
        mYear = year;
        mMinMonth = MIN_MONTH;
        mMaxMonth = MAX_MONTH;
        if (mMaxDate != 0 && mMaxYear == year) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mMaxDate);
            mMaxMonth = calendar.get(Calendar.MONTH) + 1;

        }
        if (mMinDate != 0 && mMinYear == year) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(mMinDate);
            mMinMonth = calendar.get(Calendar.MONTH) + 1;

        }
        updateMonth();
        if (mSelectedMonth > mMaxMonth) {
            setSelectedMonth(mMaxMonth, false);
        } else if (mSelectedMonth < mMinMonth) {
            setSelectedMonth(mMinMonth, false);
        } else {
            setSelectedMonth(mSelectedMonth, false);
        }
    }

    public int getSelectedMonth() {
        return mSelectedMonth;
    }

    public void setSelectedMonth(int selectedMonth) {
        setSelectedMonth(selectedMonth, true);
    }

    public void setSelectedMonth(int selectedMonth, boolean smoothScroll) {

        setCurrentPosition(selectedMonth - mMinMonth, smoothScroll);
    }

    public void setOnMonthSelectedListener(OnMonthSelectedListener onMonthSelectedListener) {
        mOnMonthSelectedListener = onMonthSelectedListener;
    }

    public interface OnMonthSelectedListener {
        void onMonthSelected(int month);
    }

    class MonthData implements IWheelData<Integer> {

        private final Integer data;
        NumberFormat numberFormat;

        public MonthData(Integer data) {
            this.data = data;
            numberFormat = NumberFormat.getNumberInstance();
            numberFormat.setMinimumIntegerDigits(2);
        }

        /**
         * 获取需要显示的数据
         *
         * @return
         */
        @Override
        public String getItemText() {
            return numberFormat.format(data) + getContext().getString(R.string.month);
        }

        /**
         * 获取需要返回选择的数据
         *
         * @return
         */
        @Override
        public Integer getItemData() {
            return data;
        }
    }

}
