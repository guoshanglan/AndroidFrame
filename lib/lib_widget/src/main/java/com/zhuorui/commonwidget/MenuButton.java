package com.zhuorui.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.zhuorui.commonwidget.popwindow.MenuPopWindow;

/**
 * date   : 2020/5/15 13:34
 * desc   :
 */
public class MenuButton extends LinearLayout {

    private int menuPopWidth = 0;
    private int menuPopHight = 0;
    private float menuPopMaxItem = 0;
    private MenuPopWindow popWindow;
    private TextView vText;
    private ImageView vPeg;
    private int gravity;

    public MenuButton(Context context) {
        super(context);
        initView(null);
    }

    public MenuButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public MenuButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        float density = getResources().getDisplayMetrics().density;
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MenuButton);
        menuPopWidth = a.getDimensionPixelOffset(R.styleable.MenuButton_zr_popWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        menuPopHight = a.getDimensionPixelOffset(R.styleable.MenuButton_zr_popHight, ViewGroup.LayoutParams.WRAP_CONTENT);
        menuPopMaxItem = a.getFloat(R.styleable.MenuButton_zr_popMaxItem, menuPopMaxItem);
        int textColor = a.getColor(R.styleable.MenuButton_zr_textColor, getResources().getColor(R.color.label_selected_text_color));
        int textSize = a.getDimensionPixelOffset(R.styleable.MenuButton_zr_textSize, 0);
        int pegResid = a.getResourceId(R.styleable.MenuButton_zr_iconSrc, R.mipmap.ic_arrow_down_white);
        a.recycle();
        setPadding(getPaddingLeft() == 0 ? (int) density * 4 : getPaddingLeft(),
                getPaddingTop() == 0 ? (int) density * 3 : getPaddingTop(),
                getPaddingLeft() == 0 ? (int) density * 4 : getPaddingLeft(),
                getPaddingTop() == 0 ? (int) density * 2 : getPaddingTop());
        if (getBackground() == null)
            setBackgroundResource(R.drawable.background_menu_button);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        vText = new TextView(getContext());
        vText.setId(R.id.zr_menubutton_title);
        vText.setTextColor(textColor);
        if (textSize > 0) {
            vText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        } else {
            vText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
        }
        addView(vText, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        vPeg = new ImageView(getContext());
        vPeg.setImageResource(pegResid);
        int icW = (int) (density * 5);
        int icH = (int) (density * 3);
        LayoutParams lp = new LayoutParams(icW, icH);
        lp.leftMargin = (int) density * 2;
        addView(vPeg, lp);
    }

    public void setText(CharSequence text) {
        vText.setText(text);
    }

    public String getText() {
        return vText.getText().toString();
    }

    public TextView getTextView() {
        return vText;
    }

    public <T> void setOnMenuClickListener(@Nullable OnMenuClickListener<T> listener) {
        if (popWindow != null) {
            popWindow.setOnMenuSelectListener(null);
        }
        if (listener != null) {
            setOnClickListener(v -> showPop(gravity));
            String[] titles = listener.getShowTexts();
            T[] datas = listener.getDatas();
            popWindow = menuPopMaxItem > 0 ? new MenuPopWindow(getContext(), menuPopMaxItem, titles, datas) : new MenuPopWindow(getContext(), menuPopWidth, menuPopHight, titles, datas);
            popWindow.setOnMenuSelectListener(listener);
        } else {
            setOnClickListener(null);
            popWindow = null;
        }
    }

    public void removeOnMenuClickListener(){
        setOnClickListener(null);
        if (popWindow != null)
            popWindow.setOnMenuSelectListener(null);
        popWindow = null;
    }


    public View getPegView() {
        return vPeg;
    }

    public void sePegViewVisibility(int visibility) {
        vPeg.setVisibility(visibility);
    }

    public void showPop(int gravity) {
        if (popWindow != null) {
            popWindow.show(this, vPeg, gravity);
            String text = getText();
            if (!TextUtils.isEmpty(text)) {
                popWindow.setItemSelectedByTitle(text);
            }
        }
    }

    public void setPopWidth(int width) {
        menuPopWidth = width;
    }

    public void setPopHight(int hight) {
        menuPopHight = hight;
    }

    public void setPopMaxItem(float items) {
        menuPopMaxItem = items;
    }

    public void setPopGravity(int gravity) {
        this.gravity = gravity;
    }

    public interface OnMenuClickListener<T> extends MenuPopWindow.OnMenuSelectListener<T> {

        String[] getShowTexts();

        T[] getDatas();

    }
}
