package com.zhuorui.securities.pickerview.date;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

import com.zhuorui.securities.pickerview.IWheelData;
import com.zhuorui.securities.pickerview.R;
import com.zhuorui.securities.pickerview.WheelPicker;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * 日期选择
 * Created by ycuwq on 17-12-28.
 */
public class DayPicker extends WheelPicker<DayPicker.DayData> {

    private int mMinDay, mMaxDay;

    private int mSelectedDay;

    private int mYear, mMonth;
    private long mMaxDate, mMinDate;
    private boolean mIsSetMaxDate;
    private OnDaySelectedListener mOnDaySelectedListener;
    private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> dateMap = new HashMap<>();
    private List<Integer> sourceDateList = new ArrayList<>();

    public DayPicker(Context context) {
        this(context, null);
    }

    public DayPicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DayPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setItemMaximumWidthText("00");
        mMinDay = 1;
        mMaxDay = Calendar.getInstance().getActualMaximum(Calendar.DATE);
        updateDay();
        mSelectedDay = Calendar.getInstance().get(Calendar.DATE);
        setSelectedDay(mSelectedDay, false);
        setOnWheelChangeListener(new OnWheelChangeListener<DayData>() {
            @Override
            public void onWheelSelected(DayData item, int position) {
                mSelectedDay = item.getItemData();
                if (mOnDaySelectedListener != null) {
                    mOnDaySelectedListener.onDaySelected(mSelectedDay);
                }
            }
        });
    }


    public void setMonth(int year, int month) {
        mYear = year;
        mMonth = month;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mMaxDate);
        int maxYear = calendar.get(Calendar.YEAR);
        int maxMonth = calendar.get(Calendar.MONTH) + 1;
        int maxDay = calendar.get(Calendar.DAY_OF_MONTH);
        //如果不判断mIsSetMaxDate，则long 为0，则选择1970-01-01 时会有问题
        if (mIsSetMaxDate && maxYear == year && maxMonth == month) {
            mMaxDay = maxDay;
        } else {
            calendar.set(year, month - 1, 1);
            mMaxDay = calendar.getActualMaximum(Calendar.DATE);
        }
        Log.d(TAG, "setMonth: year:" + year + " month: " + month + " day:" + mMaxDay);
        calendar.setTimeInMillis(mMinDate);
        int minYear = calendar.get(Calendar.YEAR);
        int minMonth = calendar.get(Calendar.MONTH) + 1;
        int minDay = calendar.get(Calendar.DAY_OF_MONTH);
        if (minYear == year && minMonth == month) {
            mMinDay = minDay;
        } else {
            mMinDay = 1;
        }
        updateSourceDate();
        updateDay();
        if (mSelectedDay < mMinDay) {
            setSelectedDay(mMinDay, false);
        } else {
            setSelectedDay(Math.min(mSelectedDay, mMaxDay), false);
        }
    }

    public int getSelectedDay() {
        return mSelectedDay;
    }

    public void setSelectedDay(int selectedDay) {
        setSelectedDay(selectedDay, true);
    }

    public void setSelectedDay(int selectedDay, boolean smoothScroll) {
        this.mSelectedDay = selectedDay;
        setCurrentPosition(selectedDay - mMinDay, smoothScroll);
    }

    public void setMaxDate(long date) {
        mMaxDate = date;
        mIsSetMaxDate = true;
    }

    public void setMinDate(long date) {
        mMinDate = date;
    }

    public void setOnDaySelectedListener(OnDaySelectedListener onDaySelectedListener) {
        mOnDaySelectedListener = onDaySelectedListener;
    }

    private void updateDay() {
        List<DayData> list = new ArrayList<>();
        for (int i = mMinDay; i <= mMaxDay; i++) {
            if (sourceDateList == null || sourceDateList.isEmpty() || sourceDateList.contains(i)) {
                list.add(new DayData(i));
            }
        }
        setDataList(list);
    }

    public void setSourceDate(HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> dateMap) {
        this.dateMap = dateMap;
    }

    private void updateSourceDate() {
        if (dateMap.get(mYear) != null && dateMap.get(mYear).get(mMonth) != null) {
            sourceDateList = dateMap.get(mYear).get(mMonth);
        }
    }

    public interface OnDaySelectedListener {
        void onDaySelected(int day);
    }

    class DayData implements IWheelData<Integer> {

        private final Integer data;
        NumberFormat numberFormat;

        public DayData(Integer data) {
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
            return numberFormat.format(data) + getContext().getString(R.string.day);
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
