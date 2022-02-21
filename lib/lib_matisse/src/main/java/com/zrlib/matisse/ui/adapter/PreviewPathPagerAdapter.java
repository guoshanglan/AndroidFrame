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
package com.zrlib.matisse.ui.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.zrlib.matisse.ui.PreviewItem;
import com.zrlib.matisse.ui.PreviewPathItemFragment;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PreviewPathPagerAdapter extends FragmentStateAdapter {

    private ArrayList<PreviewItem> mItems = new ArrayList<>();


    public PreviewPathPagerAdapter(@NonNull @NotNull Fragment fragment) {
        super(fragment);
    }


    public PreviewItem getMediaItem(int position) {
        return mItems.get(position);
    }

    public void addAll(List<PreviewItem> items) {
        mItems.addAll(items);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        return PreviewPathItemFragment.newInstance(mItems.get(position));
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

}
