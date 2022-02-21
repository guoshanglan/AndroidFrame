/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zrlib.matisse.ui.widget;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.core.content.ContextCompat;
import com.zrlib.matisse.R;
import com.zrlib.matisse.intermal.entity.Album;
import com.zrlib.matisse.intermal.entity.SelectionSpec;
import com.zrlib.matisse.intermal.utils.Platform;

import base2app.ex.PixelExKt;


public class AlbumsSpinner {

    private static final float MAX_SHOWN_COUNT = 7.5f;
    private CursorAdapter mAdapter;
    private TextView mSelected;
    private final ListPopupWindow mListPopupWindow;
    private AdapterView.OnItemSelectedListener mOnItemSelectedListener;

    public AlbumsSpinner(@NonNull Context context) {
        context.setTheme(SelectionSpec.getInstance().themeId);
        mListPopupWindow = new ListPopupWindow(context, null, R.attr.matisse_listPopupWindowStyle);
        mListPopupWindow.setModal(true);
        mListPopupWindow.setContentWidth(context.getResources().getDisplayMetrics().widthPixels);
        mListPopupWindow.setOnItemClickListener((parent, view, position, id) -> {
            AlbumsSpinner.this.onItemSelected(parent.getContext(), position);
            if (mOnItemSelectedListener != null) {
                mOnItemSelectedListener.onItemSelected(parent, view, position, id);
            }
        });
        mListPopupWindow.setOnDismissListener(() -> updateDrawables(false));
    }

    private void updateDrawables(boolean show) {
        Drawable right = ContextCompat.getDrawable(mSelected.getContext(), show ? R.mipmap.ic_pic_select_up : R.mipmap.ic_pic_select_down);
//        TypedArray ta = mSelected.getContext().getTheme().obtainStyledAttributes(new int[]{R.attr.album_element_color});
//        int color = ta.getColor(0, 0);
//        ta.recycle();
//        right.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        right.setBounds(0, 0, (int) PixelExKt.dp2px(11f), (int) PixelExKt.dp2px(6f));
        mSelected.setCompoundDrawables(null, null, right, null);
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        mOnItemSelectedListener = listener;
    }

    public void setSelection(Context context, int position) {
        mListPopupWindow.setSelection(position);
        onItemSelected(context, position);
    }

    private void onItemSelected(Context context, int position) {
        mListPopupWindow.dismiss();
        Cursor cursor = mAdapter.getCursor();
        cursor.moveToPosition(position);
        Album album = Album.valueOf(cursor);
        String displayName = album.getDisplayName(context);
        if (mSelected.getVisibility() == View.VISIBLE) {
            mSelected.setText(displayName);
        } else {
            if (Platform.hasICS()) {
                mSelected.setAlpha(0.0f);
                mSelected.setVisibility(View.VISIBLE);
                mSelected.setText(displayName);
                mSelected.animate().alpha(1.0f).setDuration(context.getResources().getInteger(
                        android.R.integer.config_longAnimTime)).start();
            } else {
                mSelected.setVisibility(View.VISIBLE);
                mSelected.setText(displayName);
            }

        }
    }

    public void setAdapter(CursorAdapter adapter) {
        mListPopupWindow.setAdapter(adapter);
        mAdapter = adapter;
    }

    public void setSelectedTextView(TextView textView) {
        mSelected = textView;
        mSelected.setCompoundDrawablePadding((int) PixelExKt.dp2px(4f));
        updateDrawables(false);
        mSelected.setVisibility(View.GONE);
        mSelected.setOnClickListener(v -> {
            int itemHeight = v.getResources().getDimensionPixelSize(R.dimen.matisse_album_item_height);
            int tbPadding = (int) PixelExKt.dp2px(5f);
            int h = mAdapter.getCount() > MAX_SHOWN_COUNT ? (int) (itemHeight * MAX_SHOWN_COUNT) : itemHeight * mAdapter.getCount();
            mListPopupWindow.setHeight(h + tbPadding + tbPadding);
            mListPopupWindow.show();
            mListPopupWindow.getListView().setPadding(0, tbPadding, 0, tbPadding);
            updateDrawables(true);
        });
        mSelected.setOnTouchListener(mListPopupWindow.createDragToOpenListener(mSelected));
    }

    public void setPopupAnchorView(View view) {
        mListPopupWindow.setAnchorView(view);
    }

}
