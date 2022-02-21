package com.zhuorui.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.zhuorui.commonwidget.impl.IZRTitleView;

import static base2app.ex.ResourceKt.text;

/**
 * date   : 2019-08-23 10:09
 * desc   :
 */
public class ZRTitleTextView extends FrameLayout implements View.OnFocusChangeListener, TextWatcher, IZRTitleView {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private TextView vTitle;
    public TextView vEt;
    private int mOrientation = VERTICAL;
    private boolean mTitleBaseline = false;
    private boolean mShowDivider = false;
    private boolean mRightIconVisible = false;
    private int mRightIconSrc = 0;
    private int mRightIconWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
    private int mRightIconHeight = ViewGroup.LayoutParams.WRAP_CONTENT;

    public ZRTitleTextView(Context context) {
        this(context, null);
    }

    public ZRTitleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZRTitleTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ZRTitleTextView);
        mOrientation = a.getInt(R.styleable.ZRTitleTextView_zr_ttextviewOrientation, mOrientation);
        mShowDivider = a.getBoolean(R.styleable.ZRTitleTextView_zr_dividerVisible, mShowDivider);
        mRightIconVisible = a.getBoolean(R.styleable.ZRTitleTextView_zr_iconVisible, mRightIconVisible);
        mRightIconWidth = a.getDimensionPixelOffset(R.styleable.ZRTitleTextView_zr_iconWidth, mRightIconWidth);
        mRightIconHeight = a.getDimensionPixelOffset(R.styleable.ZRTitleTextView_zr_iconHight, mRightIconHeight);
        mRightIconSrc = a.getResourceId(R.styleable.ZRTitleTextView_zr_iconSrc, mRightIconSrc);
        mTitleBaseline = a.getBoolean(R.styleable.ZRTitleTextView_zr_titleWidthBaseline, mTitleBaseline);
        String title = a.getString(R.styleable.ZRTitleTextView_zr_ttextviewTitle);
        String text = a.getString(R.styleable.ZRTitleTextView_zr_ttextviewText);
        String hiht = a.getString(R.styleable.ZRTitleTextView_zr_ttextviewHint);
        a.recycle();
        initView();
        orientationChange(title, text, TextUtils.isEmpty(hiht) ? String.format(text(R.string.pls_select), title) : hiht);
        if (mTitleBaseline) {
            post(() -> titleBasel());
        }
    }

    private void initView() {
        removeAllViews();
        inflate(getContext(), mOrientation == VERTICAL ? R.layout.layout_title_textview_vertical : R.layout.layout_title_textview_horizontal, this);
        vTitle = findViewById(R.id.tv_title);
        vEt = findViewById(R.id.et_edittext);
        vEt.setOnFocusChangeListener(this);
        vEt.addTextChangedListener(this);
    }

    private void orientationChange(String title, String text, String hint) {
        setTitle(title);
        setText(text);
        setHint(hint);
        setRightIcon();
        setDivider();
    }

    private void setDivider() {
        if (!mShowDivider) return;
        ConstraintLayout rootView = findViewById(R.id.root_view);
        View v = getDividerView(rootView.getChildCount());
        rootView.addView(v);
        ConstraintSet mConstraintSet = new ConstraintSet();
        mConstraintSet.clone(rootView);
        mConstraintSet.connect(v.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        mConstraintSet.connect(v.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        mConstraintSet.connect(v.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        mConstraintSet.applyTo(rootView);
    }

    private View getDividerView(int parentChildCount) {
        View v = new View(getContext());
        v.setId(parentChildCount + 1);
        v.setBackgroundColor(Color.parseColor("#CCCCCC"));
        int h = (int) (getResources().getDisplayMetrics().density * 0.6);
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(0, h);
        v.setLayoutParams(lp);
        return v;
    }

    private void titleBasel() {
        ViewParent parent = getParent();
        if (parent != null && parent instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) parent;
            for (int i = 0, l = group.getChildCount(); i < l; i++) {
                View v = group.getChildAt(i);
                if (v != ZRTitleTextView.this && v instanceof IZRTitleView) {
                    ((IZRTitleView) v).setTitleWidth(vTitle.getWidth());
                }
            }

        }
    }

    public void setTitleWidth(int width) {
        if (width > 0) {
            ViewGroup.LayoutParams params = vTitle.getLayoutParams();
            params.width = width;
            vTitle.setLayoutParams(params);
        }
    }

    private void setRightIcon() {
        ImageView vRightIcon = findViewById(R.id.iv_right_icon);
        if (vRightIcon == null) return;
        if (mRightIconVisible) {
            vRightIcon.setVisibility(VISIBLE);
            ViewGroup.LayoutParams params = vRightIcon.getLayoutParams();
            params.width = mRightIconWidth;
            params.height = mRightIconHeight;
            vRightIcon.setImageResource(mRightIconSrc);
        } else {
            vRightIcon.setVisibility(GONE);
        }

    }

    public void setHint(String hiht) {
        vEt.setHint(hiht);
    }

    public void setText(String text) {
        vEt.setText(text);
    }

    public void setTitle(String title) {
        vTitle.setText(title);
    }

    public void setOrientation(int orientation) {
        if (orientation == mOrientation) return;
        mOrientation = orientation;
        String title = vTitle != null ? vTitle.getText().toString() : "";
        String text = vEt != null ? vEt.getText().toString() : "";
        String hint = vEt != null ? vEt.getHint().toString() : "";
        initView();
        orientationChange(title, text, hint);
    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public String getText() {
        return vEt.getText().toString();
    }
}
