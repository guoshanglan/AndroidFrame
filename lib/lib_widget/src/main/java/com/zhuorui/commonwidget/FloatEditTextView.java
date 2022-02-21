package com.zhuorui.commonwidget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatEditText;

import java.util.regex.Pattern;

/**
 * Date: 2019/10/15
 * Desc:提醒设置Edittext
 * */
public class FloatEditTextView extends AppCompatEditText implements TextWatcher {
    private String lastNumber;
    private Context context;
    private int integer_part;    //整数部分
    private int decimal_part;    //小数部分
    public FloatEditTextView(Context context) {
        this(context,null);
    }

    public FloatEditTextView(Context context, AttributeSet attrs) {
        this(context,attrs,0);
    }

    public FloatEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context =context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FloatEditTextView);
        setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        integer_part =typedArray.getInteger(R.styleable.FloatEditTextView_integer_part,1);
        decimal_part =typedArray.getInteger(R.styleable.FloatEditTextView_decimal_part,1);
        if(integer_part>0&&decimal_part>0) {
            addTextChangedListener(this);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s,  int start, int count, int after) {
        lastNumber = s.toString();
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String temp = s.toString();
        int posDot = temp.indexOf(".");
        if (Pattern.matches("^[0-9]{1,"+integer_part+"}(\\.[0-9]{1,"+decimal_part+"})?$",temp)) {
            if(temp.startsWith("0")&&!temp.equals("0")){
                if(posDot>1||posDot<0){
                    setText(temp.substring(1));
                    setSelection(getText().length());
                }
            }
        }else {
            if (temp.endsWith(".")) {
                if (temp.length() == 1) {
                    setText("0.0");
                    return;
                }
                if (temp.length() < lastNumber.length()) {
                    String str = temp.substring(0, temp.length() - 1);
                    setText(str);
                    setSelection(getText().length());
                    return;
                } else {
                    return;
                }
            } else if (temp.startsWith(".")) {
                setText(temp.substring(1, getText().length()));
                return;
            } else {
                if (!TextUtils.isEmpty(temp)&&temp!=lastNumber) {
                    setText(lastNumber);
                    setSelection(getText().length());
                }
            }
        }
    }

    /**
     * 输入框编辑状态
     */
    public void setEditable(boolean flag) {
        setFocusable(flag);
        setFocusableInTouchMode(flag);
    }

}
