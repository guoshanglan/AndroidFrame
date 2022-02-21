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

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.zrlib.matisse.R;
import com.zrlib.matisse.intermal.entity.Item;
import com.zrlib.matisse.intermal.entity.SelectionSpec;
import com.zrlib.matisse.intermal.utils.PhotoMetadataUtils;
import com.zrlib.matisse.listener.OnFragmentInteractionListener;
import com.zrlib.matisse.ui.widget.PreviewView;

import base2app.ex.ResourceKt;

public class PreviewItemFragment extends Fragment {

    private static final String ARGS_ITEM = "args_item";
    private OnFragmentInteractionListener mListener;
    private PreviewView scaleImageView;


    public static PreviewItemFragment newInstance(Item item) {
        PreviewItemFragment fragment = new PreviewItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_ITEM, item);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.matisse_fragment_preview_item, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Item item = getArguments().getParcelable(ARGS_ITEM);
        if (item == null) {
            return;
        }
        FrameLayout fl = (FrameLayout) view;
        if (item.isGif()) {
            ImageView image = new ImageView(view.getContext());
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.CENTER;
            fl.addView(image, lp);
            Point size = PhotoMetadataUtils.getBitmapSize(item.getContentUri(), getActivity());
            SelectionSpec.getInstance().imageEngine.loadGifImage(getContext(), size.x, size.y, image, item.getContentUri());
        } else {
            scaleImageView = new PreviewView(view.getContext());
            fl.addView(scaleImageView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            scaleImageView.setImage(ImageSource.uri(item.getContentUri()));

        }
        if (item.isVideo()) {
            ImageView videoPlayButton = new ImageView(view.getContext());
            int wh = (int) (view.getResources().getDisplayMetrics().density * 48f);
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(wh, wh);
            lp.gravity = Gravity.CENTER;
            fl.addView(videoPlayButton, lp);
            videoPlayButton.setVisibility(View.VISIBLE);
            videoPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(item.uri, "video/*");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(), ResourceKt.text(R.string.matisse_no_app_that_supports_video_preview), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        if (scaleImageView != null){
            FrameLayout fl = (FrameLayout) getView();
            fl.removeView(scaleImageView);
            scaleImageView = null;
        }
        super.onDestroyView();
    }

    public void resetView() {
        if (scaleImageView != null) {
            scaleImageView.reset();
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
