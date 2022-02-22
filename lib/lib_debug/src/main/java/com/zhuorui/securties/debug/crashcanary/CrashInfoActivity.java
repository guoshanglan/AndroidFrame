package com.zhuorui.securties.debug.crashcanary;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;


import com.zhuorui.securties.debug.R;
import com.zhuorui.securties.debug.crashcanary.crash.db.CrashBean;

import base2app.ui.activity.AbsActivity;
import base2app.util.StatusBarUtil;

public class CrashInfoActivity extends AbsActivity {

    private ListView mListView;
    private ScrollView mScrollView;
    private Button mBtn;
    private TextView mTextView;
    private CrashItemAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        showListView();

    }

    private void showBtnClick() {
        DialogInterface.OnClickListener okListener = (dialogInterface, i) -> {
            CrashCanary.getCrashDao().deleteALL();
            listAdapter.cancel();
        };
        new AlertDialog.Builder(CrashInfoActivity.this)
                .setTitle(getString(R.string.crash_canary_delete_all))
                .setMessage(getString(R.string.crash_canary_message))
                .setPositiveButton(getString(R.string.crash_canary_yes), okListener)
                .setNegativeButton(getString(R.string.crash_canary_no), null)
                .show();
    }


    private void initView() {
        int barHeight = StatusBarUtil.getStatusBarHeight(this);
        View titleBar = findViewById(R.id.title_bar);
        LinearLayoutCompat.LayoutParams layoutParams = ((LinearLayoutCompat.LayoutParams) titleBar.getLayoutParams());
        layoutParams.topMargin = barHeight;
        titleBar.setLayoutParams(layoutParams);
        mTextView = findViewById(R.id.crash_canary_detail_tv);
        mScrollView = findViewById(R.id.crash_canary_sv);
        mListView = findViewById(R.id.crash_canary_list);
        listAdapter = new CrashItemAdapter(CrashInfoActivity.this);
        mListView.setAdapter(listAdapter);
        mBtn = findViewById(R.id.crash_canary_btn);
        mBtn.setOnClickListener(v -> showBtnClick());
        mListView.setOnItemClickListener((parent, view, position, id) -> hideListView(position));
        findViewById(R.id.back).setOnClickListener(v -> finish());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            showListView();
        }
        return true;

    }

    @Override
    public void onBackPressed() {
        if (mListView.getVisibility() == View.VISIBLE) {
            super.onBackPressed();
        } else {
            showListView();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        listAdapter.refresh(CrashCanary.getCrashDao().queryAll());
    }

    private void showListView() {
        mListView.setVisibility(View.VISIBLE);
        mBtn.setVisibility(View.VISIBLE);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        setTitle(String.format(getResources().getString(R.string.crash_canary_title), getApplicationContext().getPackageName()));
        mScrollView.setVisibility(View.GONE);
        mTextView.setClickable(false);
    }

    private void hideListView(int position) {
        final CrashBean crashBean = CrashCanary.getCrashDao().queryCrashBeanById(listAdapter.getCount() - position);
        mListView.setVisibility(View.INVISIBLE);
        mBtn.setVisibility(View.INVISIBLE);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(crashBean.getNormalError());
        mScrollView.setVisibility(View.VISIBLE);
        mTextView.setClickable(true);
        mTextView.setOnLongClickListener(v -> {
            listAdapter.onLongClick(crashBean.getDetailedError());
            return true;
        });
        setText(crashBean.getDetailedError());
    }

    public static final String EXCEPTION = "Exception";


    private void setText(String param) {
        mTextView.setText("");
        String[] strings = param.split("\n");
        for (String str : strings) {
            if (str == null) {
                continue;
            }
            if (str.contains(EXCEPTION)) {
                mTextView.append(getClause(str, getResources().getColor(R.color.crash_canary_error)));
            } else if (str.contains(getPackageName())) {
                mTextView.append(getClause(str, getResources().getColor(R.color.crash_canary_warn)));
            } else {
                mTextView.append(str);
            }
            mTextView.append("\n");
        }

    }

    private SpannableString getClause(final String param, final int color) {
        SpannableString spanString = new SpannableString(param);
        spanString.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View view) {

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(color);
                ds.setUnderlineText(false);
            }
        }, 0, param.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spanString;
    }

    @Override
    protected int getAcContentRootViewId() {
        return R.id.root_layout;
    }

    @Override
    protected int getLayout() {
        return R.layout.crash_canary_activity_crash_info;
    }
}
