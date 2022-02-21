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
package com.zrlib.matisse.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.zhuorui.commonwidget.ZRLoadingView;
import com.zhuorui.securities.base2app.glide.ZRGlide;
import com.zrlib.matisse.R;
import com.zrlib.matisse.listener.OnFragmentInteractionListener;
import com.zrlib.matisse.ui.widget.PreviewView;

import java.io.File;

import base2app.util.JsonUtil;


public class PreviewPathItemFragment extends Fragment {

    private static final String ARGS_ITEM = "args_item";
    private OnFragmentInteractionListener mListener;
    private ImageView vPlaceholder;
    private PreviewView image;
    private ZRLoadingView vLoading;
    private final float y = 0;


    public static PreviewPathItemFragment newInstance(PreviewItem item) {
        PreviewPathItemFragment fragment = new PreviewPathItemFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_ITEM, JsonUtil.toJson(item));
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.matisse_fragment_preview_item, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final PreviewItem item = JsonUtil.fromJson(getArguments().getString(ARGS_ITEM), PreviewItem.class);
        vPlaceholder = new ImageView(view.getContext());
        FrameLayout.LayoutParams placeholderLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        placeholderLp.gravity = Gravity.CENTER;
        ((FrameLayout) view).addView(vPlaceholder, placeholderLp);
        if (item == null || TextUtils.isEmpty(item.path())) {
            if (item.errRes() != null && item.errRes() != 0) {
                vPlaceholder.setImageResource(item.errRes());
            } else {
                vPlaceholder.setImageResource(R.mipmap.ic_placeholder_2e2e2e);
            }
            vPlaceholder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onClick();
                    }
                }
            });
            return;
        }
        image = new PreviewView(view.getContext());
        image.setFitStart(true);

        ((FrameLayout) view).addView(image, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        image.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onClick();
            }
        });
        if (!item.path().startsWith("http")) {
            image.setImage(ImageSource.uri(item.path()));

            return;
        }
        ZRGlide.INSTANCE.with(image).download(item.path()).into(new CustomTarget<File>() {
            @Override
            public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                hideLoading();
                if (vPlaceholder != null) {
                    vPlaceholder.setImageResource(0);
                }
                if (image != null) {
                    image.setImage(ImageSource.uri(resource.getAbsolutePath()));
                }

            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
                hideLoading();
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                hideLoading();
                if (vPlaceholder != null) {
                    vPlaceholder.setImageResource(R.mipmap.ic_load_fair);
                }
                super.onLoadFailed(errorDrawable);
            }

            @Override
            public void onLoadStarted(@Nullable Drawable placeholder) {
                super.onLoadStarted(placeholder);
                showLoading();
            }
        });
    }

    @Override
    public void onDestroyView() {
        hideLoading();
        if (image != null) {
            image.setOnClickListener(null);
            image = null;
        }
        vPlaceholder = null;
        vLoading = null;
        super.onDestroyView();
    }


    private void hideLoading() {
        if (vLoading != null) {
            vLoading.stop();
            ViewParent parent = vLoading.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(vLoading);
            }
            vLoading = null;
        }

    }

    private void showLoading() {
        if (vLoading == null && getContext() != null) {
            vLoading = new ZRLoadingView(getContext());
            FrameLayout.LayoutParams loadLp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            loadLp.gravity = Gravity.CENTER;
            ((FrameLayout) getView()).addView(vLoading, loadLp);
        }
        if (vLoading != null)
            vLoading.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void resetView() {
        if (image != null) {
            image.reset();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) getParentFragment();
        } else if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        mListener = null;
        super.onDetach();
    }

}
