package com.zhuorui.commonwidget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;



import java.util.List;

import base2app.ex.PixelExKt;


/**
 * @date 2020/6/17 10:11
 * @desc 跑马灯View
 */
public class ZRMarqueeView extends SurfaceView implements SurfaceHolder.Callback {
    public Context mContext;
    private float mTextSize = 12; //字体大小
    private int mTextColor = Color.WHITE; //字体的颜色
    private boolean mIsRepeat;//是否重复滚动
    private int mStartPoint;// 开始滚动的位置  0是从最左面开始    1是从最末尾开始
    private int mDirection;//滚动方向 0 向左滚动   1向右滚动
    private int mSpeed;//滚动速度
    private SurfaceHolder surfaceHolder;
    private TextPaint mTextPaint;
    private MarqueeViewThread mThread;
    private String margueeString;
    private int textWidth = 0, textHeight = 0;
    public int currentX = 0;// 当前x的位置
    public int sepX = (int) PixelExKt.dp2px(0.5f);//每一步滚动的距离
    private OnMarqueeListener mOnMarqueeListener;
    public static final int ROLL_OVER = 100;
    private boolean firstRoll = true;

    public ZRMarqueeView(Context context) {
        this(context, null);
    }

    public ZRMarqueeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZRMarqueeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init(attrs, defStyleAttr);
    }

    private void init(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ZRMarqueeView, defStyleAttr, 0);
        mTextColor = a.getColor(R.styleable.ZRMarqueeView_mv_textColor, Color.WHITE);
        mTextSize = a.getFloat(R.styleable.ZRMarqueeView_mv_textSize, 12);
        mIsRepeat = a.getBoolean(R.styleable.ZRMarqueeView_mv_isRepeat, true);
        mStartPoint = a.getInt(R.styleable.ZRMarqueeView_mv_startPoint, 0);
        mDirection = a.getInt(R.styleable.ZRMarqueeView_mv_scroll_direction, 0);
        mSpeed = a.getInt(R.styleable.ZRMarqueeView_mv_speed, (int) PixelExKt.dp2px(0.5f));
        a.recycle();

        surfaceHolder = this.getHolder();
        surfaceHolder.addCallback(this);
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);
        setZOrderOnTop(true);//使surfaceview放到最顶层
        getHolder().setFormat(PixelFormat.TRANSLUCENT);//使窗口支持透明度
    }

    public void setText(List<String> msg) {
        if (msg == null || msg.isEmpty()) return;
        measurementsText(msg);
    }

    protected void measurementsText(List<String> contentList) {
        StringBuilder scrollContent = new StringBuilder();
        for (int i = 0; i < contentList.size(); i++) {
            String content = contentList.get(i);
            scrollContent.append(content);
            if (i != contentList.size() - 1) {
                scrollContent.append("    ");
            }
        }
        margueeString = scrollContent.toString();
        mTextPaint.setTextSize(PixelExKt.sp2px(mTextSize));
        mTextPaint.setColor(mTextColor);
        mTextPaint.setStrokeWidth(0.5f);
        textWidth = (int) mTextPaint.measureText(margueeString);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        textHeight = (int) fontMetrics.bottom;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        if (mStartPoint == 0 && firstRoll) {
            currentX = (int) PixelExKt.dp2px(20);
            firstRoll = false;
        } else {
            //减去左右两个图标的宽度
            currentX = width - (int) PixelExKt.dp2px(30) - (int) PixelExKt.dp2px(30);
        }
        startScroll();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        this.surfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mThread != null)
            mThread.isRun = true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        this.setVisibility(visibility);
    }

    /**
     * 开始滚动
     */
    public void startScroll() {
        if (mThread != null && mThread.isRun)
            return;
        mThread = new MarqueeViewThread();//创建一个绘图线程
        mThread.start();
    }

    /**
     * 停止滚动
     */
    public void stopScroll() {
        if (mThread != null) {
            mThread.isRun = false;
            mThread.interrupt();
        }
        mThread = null;
        surfaceHolder.removeCallback(this);
    }

    /**
     * 线程
     */
    class MarqueeViewThread extends Thread {

        public boolean isRun;//是否在运行

        public MarqueeViewThread() {
            isRun = true;
        }

        public void onDraw() {
            try {
                if (TextUtils.isEmpty(margueeString)) {
                    return;
                }
                Canvas canvas = surfaceHolder.lockCanvas();
                if (canvas == null) return;
                int paddingLeft = getPaddingLeft();
                int paddingTop = getPaddingTop();
                int paddingRight = getPaddingRight();
                int paddingBottom = getPaddingBottom();

                int contentWidth = getWidth() - paddingLeft - paddingRight;
                int contentHeight = getHeight() - paddingTop - paddingBottom;

                int centeYLine = paddingTop + contentHeight / 2;//中心线

                if (mDirection == 0) {//向左滚动
                    //单次滚动结束
                    if (currentX <= -textWidth) {
                        mHandler.sendEmptyMessage(ROLL_OVER);
                        currentX = contentWidth;
                    } else {
                        currentX -= sepX;
                    }
                } else {//  向右滚动
                    //单次滚动结束
                    if (currentX >= contentWidth) {
                        currentX = -textWidth;
                    } else {
                        currentX += sepX;
                    }
                }
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
                canvas.drawText(margueeString, currentX, centeYLine + (int) PixelExKt.dp2px(textHeight) / 2, mTextPaint);
                surfaceHolder.unlockCanvasAndPost(canvas);//结束锁定画图，并提交改变。
                int a = textWidth / margueeString.length();
                int b = a / sepX;
                int c = mSpeed / b == 0 ? 1 : mSpeed / b;
                Thread.sleep(c);//睡眠时间为移动的频率
                if (isRun) {
                    onDraw();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            onDraw();
        }
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == ROLL_OVER) {
                if (mOnMarqueeListener != null) {
                    mOnMarqueeListener.onRollOver();
                }
            }
        }
    };

    public void reset() {
        int contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        if (mStartPoint == 0)
            currentX = 0;
        else
            currentX = contentWidth;
    }


    /**
     * 滚动回调
     */
    public interface OnMarqueeListener {
        void onRollOver();//单次循环滚动完毕
    }

    public void setOnMarqueeListener(OnMarqueeListener onMarqueeListener) {
        this.mOnMarqueeListener = onMarqueeListener;
    }

}

