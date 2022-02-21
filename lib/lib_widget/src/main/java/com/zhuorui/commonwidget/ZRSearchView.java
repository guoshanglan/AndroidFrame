package com.zhuorui.commonwidget;

import static android.view.ViewEx.hideSoftInput;
import static android.view.ViewEx.showSoftInput;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewEx;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import base2app.ex.PixelExKt;


/**
 * date   : 2019-09-11 15:52
 * desc   : 搜索框控件
 */
public class ZRSearchView extends LinearLayout implements TextWatcher, View.OnClickListener, TextView.OnEditorActionListener {

    private final EditText vEt;
    private final ImageView vClear;
    private OnKeyChangeListener ml;
    private String lastKey;

    private final Runnable runnable = () -> onKeyChange(getText());

    public ZRSearchView(Context context) {
        this(context, null);
    }

    public ZRSearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZRSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (getMinimumHeight() == 0){
            setMinimumHeight((int) PixelExKt.dp2px(35f));
        }
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ZRSearchView);
        Drawable leftIcon = a.getDrawable(R.styleable.ZRSearchView_zr_left_icon);
        int clearIcon = a.getResourceId(R.styleable.ZRSearchView_zr_clear_icon, 0);
        int background = a.getResourceId(R.styleable.ZRSearchView_zr_background, 0);
        int textColor = a.getColor(R.styleable.ZRSearchView_zr_textColor, 0);
        int hintColor = a.getColor(R.styleable.ZRSearchView_zr_hintColor, 0);
        float textSize = a.getDimension(R.styleable.ZRSearchView_zr_textSize, -1);
        String hint = a.getString(R.styleable.ZRSearchView_zr_hint);
        a.recycle();
        inflate(context, R.layout.layout_search_view, this);
        vEt = findViewById(R.id.search_view_et);
        vClear = findViewById(R.id.search_view_clear);
        vEt.setHint(hint);
        if (textColor != 0) {
            vEt.setTextColor(textColor);
        }
        if (hintColor != 0) {
            vEt.setHintTextColor(hintColor);
        }
        if (textSize != -1.0) vEt.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        vEt.addTextChangedListener(this);
        vEt.setOnEditorActionListener(this);
        int h = (int) (getResources().getDisplayMetrics().density * 16);
        int w = leftIcon.getIntrinsicWidth() / leftIcon.getIntrinsicHeight() * h;
        leftIcon.setBounds(0, 0, w, h);
        vEt.setCompoundDrawables(leftIcon, null, null, null);
        vClear.setOnClickListener(this);
        vClear.setImageResource(clearIcon);
        setBackgroundResource(background);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (TextUtils.isEmpty(editable)) {
            vClear.setVisibility(GONE);
        } else {
            vClear.setVisibility(VISIBLE);
        }
        //延迟通知文字变化，留出文字输入间runnable隙
        removeCallbacks(runnable);
        postDelayed(runnable, TextUtils.isEmpty(editable) ? 0 : 400);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(runnable);
    }

    @Override
    public void onClick(View view) {
        if (view == vClear) {
            vEt.setText("");
            ViewEx.showSoftInput(vEt);
        }
    }


    public EditText getEditText() {
        return vEt;
    }

    public void setKey(@NonNull String key) {
        setText(key);
        onKeyChange(key);
    }

    public String getText() {
        return vEt.getText().toString();
    }

    public void setText(@NonNull String text) {
        vEt.setText(text);
        vEt.setSelection(text.length());
    }

    public void setOnKeyChangeListener(OnKeyChangeListener l) {
        ml = l;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            //点击搜索的时候隐藏软键盘
            hideKeyboard();
            onActionSearch(getText());
            return true;
        }

        return false;
    }

    /**
     * 隐藏软键盘
     */
    public void hideKeyboard() {
        hideSoftInput(vEt);
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public void showSoftInputFromWindow() {
        showSoftInput(vEt);
    }

    private void onKeyChange(String key) {
        if (ml != null && !TextUtils.equals(key, lastKey)) {
            lastKey = key;
            ml.onKeyChange(key);
        }
    }

    private void onActionSearch(String key) {
        if (ml != null) {
            ml.onActionSearch(key);
        }
    }

    public interface OnKeyChangeListener {

        /**
         * 搜索文字输入变化
         *
         * @param key
         */
        void onKeyChange(String key);

        /**
         * 键盘搜索
         *
         * @param key
         */
        void onActionSearch(String key);
    }
}
