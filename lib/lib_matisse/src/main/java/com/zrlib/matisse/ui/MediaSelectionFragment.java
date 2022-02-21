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
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zrlib.matisse.R;
import com.zrlib.matisse.intermal.entity.Album;
import com.zrlib.matisse.intermal.entity.Item;
import com.zrlib.matisse.intermal.entity.SelectionSpec;
import com.zrlib.matisse.intermal.model.AlbumMediaCollection;
import com.zrlib.matisse.intermal.model.SelectedItemCollection;
import com.zrlib.matisse.intermal.utils.UIUtils;
import com.zrlib.matisse.ui.adapter.AlbumMediaAdapter;
import com.zrlib.matisse.ui.widget.MediaGridInset;

public class MediaSelectionFragment extends Fragment implements
        AlbumMediaCollection.AlbumMediaCallbacks, AlbumMediaAdapter.CheckStateListener,
        AlbumMediaAdapter.OnMediaClickListener {

    public static final String EXTRA_ALBUM = "extra_album";

    private AlbumMediaCollection mAlbumMediaCollection = new AlbumMediaCollection();
    private RecyclerView mRecyclerView;
    private AlbumMediaAdapter mAdapter;
    private SelectionProvider mSelectionProvider;
    private AlbumMediaAdapter.CheckStateListener mCheckStateListener;
    private AlbumMediaAdapter.OnMediaClickListener mOnMediaClickListener;
    private Album album;

    public static MediaSelectionFragment newInstance(Album album) {
        MediaSelectionFragment fragment = new MediaSelectionFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_ALBUM, album);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater = inflater.cloneInContext(new ContextThemeWrapper(getActivity(), SelectionSpec.getInstance().themeId));
        return inflater.inflate(R.layout.matisse_fragment_media_selection, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = view.findViewById(R.id.recyclerview);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (getParentFragment() instanceof SelectionProvider) {
            mSelectionProvider = (SelectionProvider) getParentFragment();
        } else {
            throw new IllegalStateException("Context must implement SelectionProvider.");
        }
        if (getParentFragment() instanceof AlbumMediaAdapter.CheckStateListener) {
            mCheckStateListener = (AlbumMediaAdapter.CheckStateListener) getParentFragment();
        }
        if (getParentFragment() instanceof AlbumMediaAdapter.OnMediaClickListener) {
            mOnMediaClickListener = (AlbumMediaAdapter.OnMediaClickListener) getParentFragment();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        album = getArguments().getParcelable(EXTRA_ALBUM);
        mAdapter = new AlbumMediaAdapter(requireContext(),
                mSelectionProvider.provideSelectedItemCollection(), mRecyclerView);
        if (getParentFragment() instanceof AlbumMediaAdapter.OnPhotoCapture) {
            mAdapter.setOnPhotoCapture((AlbumMediaAdapter.OnPhotoCapture) getParentFragment());
        }
        mAdapter.registerCheckStateListener(this);
        mAdapter.registerOnMediaClickListener(this);
        mRecyclerView.setHasFixedSize(true);

        int spanCount;
        SelectionSpec selectionSpec = SelectionSpec.getInstance();
        if (selectionSpec.gridExpectedSize > 0) {
            spanCount = UIUtils.spanCount(requireContext(), selectionSpec.gridExpectedSize);
        } else {
            spanCount = selectionSpec.spanCount;
        }
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), spanCount));

        int spacing = getResources().getDimensionPixelSize(R.dimen.matisse_media_grid_spacing);
        mRecyclerView.addItemDecoration(new MediaGridInset(spanCount, spacing, false));
        mRecyclerView.setAdapter(mAdapter);
        mAlbumMediaCollection.onCreate(this, this);
        mAlbumMediaCollection.load(album, selectionSpec.capture);

    }

    @Override
    public void onDestroy() {
        mAlbumMediaCollection.onDestroy();
        mAdapter.setOnPhotoCapture(null);
        mAdapter.unregisterCheckStateListener();
        mAdapter.unregisterOnMediaClickListener();
        super.onDestroy();
    }

    /**
     * 刷新
     */
    public void refreshAlbum() {
        if (mAlbumMediaCollection != null) {
            mAlbumMediaCollection.onDestroy();
        }
        mAlbumMediaCollection = new AlbumMediaCollection();
        mAlbumMediaCollection.onCreate(this, this);
        SelectionSpec selectionSpec = SelectionSpec.getInstance();
        mAlbumMediaCollection.load(album, selectionSpec.capture);
    }

    public void refreshMediaGrid() {
        mAdapter.notifyDataSetChanged();
    }

    public void refreshSelection() {
        mAdapter.refreshSelection();
    }

    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onAlbumMediaReset() {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onUpdate() {
        // notify outer Activity that check state changed
        if (mCheckStateListener != null) {
            mCheckStateListener.onUpdate();
        }
    }


    @Override
    public void onMediaClick(Album album, Item item, int adapterPosition) {
        if (mOnMediaClickListener != null) {
            mOnMediaClickListener.onMediaClick(getArguments().getParcelable(EXTRA_ALBUM),
                    item, adapterPosition);
        }
    }

    public interface SelectionProvider {
        SelectedItemCollection provideSelectedItemCollection();
    }
}
