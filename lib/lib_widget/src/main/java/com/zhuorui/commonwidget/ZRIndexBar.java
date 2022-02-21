package com.zhuorui.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.zhuorui.commonwidget.impl.IIndexBarDataHelper;
import com.zhuorui.commonwidget.impl.IndexBarDataHelperImpl;
import com.zhuorui.commonwidget.model.BaseIndexPinyinBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import base2app.ex.PixelExKt;
import base2app.ex.ResourceKt;

/**
 * @date 2020/7/23 18:00
 * @desc 索引右侧边栏
 */
public class ZRIndexBar extends View {

    //#在最后面（默认的数据源）
    public static String[] INDEX_STRING = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z", "#"};
    //是否需要根据实际的数据来生成索引数据源（例如 只有 A B C 三种tag，那么索引栏就 A B C 三项）
    private boolean isNeedRealIndex;
    //索引数据源
    private List<String> indexDatas;

    //View的宽高
    private int width, height;
    //每个index区域的高度
    private int gapHeight;
    private Paint mPaint;
    //手指按下时的背景色
    private int pressedBackground;

    //汉语->拼音，拼音->tag
    private IIndexBarDataHelper indexDataHelper;

    private List<? extends BaseIndexPinyinBean> sourceData;//Adapter的数据源
    private LinearLayoutManager layoutManager;
    private int choosePosition = 0;//选择的下标
    private TextView indexDialog;
    private Paint.FontMetrics fontMetrics;
    private onIndexPressedListener onIndexPressedListener;

    public void setBaseIndexTag(String baseIndexTag) {
        for (int i = 0; i < indexDatas.size(); i++) {
            if (baseIndexTag.equals(indexDatas.get(i))) {
                choosePosition = i;
                invalidate();
                return;
            }
        }
    }

    public ZRIndexBar(Context context) {
        this(context, null);
    }

    public ZRIndexBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZRIndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public void setIndexDialog(TextView indexDialog) {
        this.indexDialog = indexDialog;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        int textSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics());//默认的TextSize
        pressedBackground = Color.TRANSPARENT;//默认按下是透明
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ZRIndexBar, defStyleAttr, 0);
        int n = typedArray.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = typedArray.getIndex(i);
            if (attr == R.styleable.ZRIndexBar_indexBarTextSize) {
                textSize = typedArray.getDimensionPixelSize(attr, textSize);
            } else if (attr == R.styleable.ZRIndexBar_indexBarPressBackground) {
                pressedBackground = typedArray.getColor(attr, pressedBackground);
            }
        }
        typedArray.recycle();

        initIndexData();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize);
        mPaint.setColor(ResourceKt.color(R.color.main_content_text_color));
        //获得画笔的FontMetrics，用来计算baseLine。因为drawText的y坐标，代表的是绘制的文字的baseLine的位置
        fontMetrics = mPaint.getFontMetrics();
        //设置index触摸监听器
        setOnIndexPressedListener(new onIndexPressedListener() {
            @Override
            public void onIndexPressed(int index, String text) {
                //滑动Rv
                if (layoutManager != null) {
                    int position = getPosByTag(text);
                    if (position != -1) {
                        if (indexDialog != null) {
                            indexDialog.setText(text);
                            //获得画笔的FontMetrics，用来计算baseLine。因为drawText的y坐标，代表的是绘制的文字的baseLine的位置
                            int baseline = (int) ((gapHeight - fontMetrics.bottom - fontMetrics.top) / 2);//计算出在每格index区域，竖直居中的baseLine值
                            indexDialog.setVisibility(VISIBLE);
                            indexDialog.setY(gapHeight * (choosePosition - 1) + baseline + getTop());
                        }
                        layoutManager.scrollToPositionWithOffset(position, 0);
                    }
                }
            }

            @Override
            public void onMotionEventEnd() {
                if (indexDialog != null) {
                    //手指放开300ms后消失
                    indexDialog.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            indexDialog.setVisibility(INVISIBLE);
                        }
                    }, 300);
                }
            }
        });

        indexDataHelper = new IndexBarDataHelperImpl();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //得到合适宽度：
        Rect indexBounds = new Rect();//存放每个绘制的index的Rect区域
        String index;//每个要绘制的index内容
        for (int i = 0; i < indexDatas.size(); i++) {
            index = indexDatas.get(i);
            mPaint.getTextBounds(index, 0, index.length(), indexBounds);//测量计算文字所在矩形，可以得到宽高
        }
        //最终测量出来的宽高
        int measureHeight = (int) (indexDatas.size() * indexBounds.height() * 2.5);
        setMeasuredDimension((int) PixelExKt.dp2px(30f), measureHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int t = getPaddingTop();//top的基准点(支持padding)
        String index;//每个要绘制的index内容
        int baseline = (int) ((gapHeight - fontMetrics.bottom - fontMetrics.top) / 2);//计算出在每格index区域，竖直居中的baseLine值
        for (int i = 0; i < indexDatas.size(); i++) {
            index = indexDatas.get(i);
            mPaint.setColor(ResourceKt.color(R.color.main_content_text_color));
            canvas.drawText(index, width / 2 - mPaint.measureText(index) / 2, t + gapHeight * i + baseline, mPaint);//调用drawText，居中显示绘制index
        }
        if (indexDatas.size() > 0) {
            mPaint.setColor(ResourceKt.color(R.color.brand_main_color));
            canvas.drawCircle((float) (width / 2), t + gapHeight * choosePosition + baseline - PixelExKt.dp2px(14) / 3f, 22, mPaint);
            mPaint.setColor(Color.parseColor("#FFFFFF"));
            canvas.drawText(indexDatas.get(choosePosition), width / 2 - mPaint.measureText(indexDatas.get(choosePosition)) / 2, t + gapHeight * choosePosition + baseline, mPaint);//调用drawText，居中显示绘制index
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setBackgroundColor(pressedBackground);//手指按下时背景变色
                //注意这里没有break，因为down时，也要计算落点 回调监听器
                if (indexDialog != null) indexDialog.setVisibility(INVISIBLE);
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                //通过计算判断落点在哪个区域：
                int pressI = (int) ((y - getPaddingTop()) / gapHeight);
                //边界处理（在手指move时，有可能已经移出边界，防止越界）
                if (pressI < 0) {
                    pressI = 0;
                } else if (pressI >= indexDatas.size()) {
                    pressI = indexDatas.size() - 1;
                }
                //回调监听器
                if (null != onIndexPressedListener && pressI > -1 && pressI < indexDatas.size()) {
                    onIndexPressedListener.onIndexPressed(pressI, indexDatas.get(pressI));
                    choosePosition = pressI;
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            default:
                setBackgroundResource(android.R.color.transparent);//手指抬起时背景恢复透明
                //回调监听器
                if (null != onIndexPressedListener) {
                    onIndexPressedListener.onMotionEventEnd();
                }
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        if (null == indexDatas || indexDatas.isEmpty()) return;
        computeGapHeight();
    }

    /**
     * 当前被按下的index的监听器
     */
    public interface onIndexPressedListener {
        void onIndexPressed(int index, String text);//当某个Index被按下

        void onMotionEventEnd();//当触摸事件结束（UP CANCEL）
    }

    public void setOnIndexPressedListener(onIndexPressedListener onIndexPressedListener) {
        this.onIndexPressedListener = onIndexPressedListener;
    }

    /**
     * 一定要在设置数据源{@link #setSourceData(List)}之前调用
     *
     * @param needRealIndex
     * @return
     */
    public ZRIndexBar setNeedRealIndex(boolean needRealIndex) {
        isNeedRealIndex = needRealIndex;
        initIndexData();
        return this;
    }

    private void initIndexData() {
        if (isNeedRealIndex) {
            indexDatas = new ArrayList<>();
        } else {
            indexDatas = Arrays.asList(INDEX_STRING);
        }
    }

    public ZRIndexBar setSourceData(List<? extends BaseIndexPinyinBean> sourceData) {
        this.sourceData = sourceData;
        initSourceData();//对数据源进行初始化
        return this;
    }

    public ZRIndexBar setLayoutManager(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
        return this;
    }

    /**
     * 初始化原始数据源，并取出索引数据源
     *
     * @return
     */
    private void initSourceData() {
        if (null == sourceData || sourceData.isEmpty()) {
            return;
        }
        indexDataHelper.sortSourceDatas(sourceData);
        if (isNeedRealIndex) {
            indexDataHelper.getSortedIndexDatas(sourceData, indexDatas);
            computeGapHeight();
        }
    }

    /**
     * 以下情况调用：
     * 1 在数据源改变
     * 2 控件size改变时
     * 计算gapHeight
     */
    private void computeGapHeight() {
        gapHeight = (height - getPaddingTop() - getPaddingBottom()) / indexDatas.size();
    }

    /**
     * 根据传入的pos返回tag
     *
     * @param tag
     * @return
     */
    private int getPosByTag(String tag) {
        if (null == sourceData || sourceData.isEmpty()) {
            return -1;
        }
        if (TextUtils.isEmpty(tag)) {
            return -1;
        }
        for (int i = 0; i < sourceData.size(); i++) {
            if (tag.equals(sourceData.get(i).getBaseIndexTag())) {
                return i;
            }
        }
        return -1;
    }

}
