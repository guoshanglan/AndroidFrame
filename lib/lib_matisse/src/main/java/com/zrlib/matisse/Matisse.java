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
package com.zrlib.matisse;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.SharedElementCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentEx;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Dest;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.fragment.FragmentNavigatorExtrasKt;
import androidx.transition.TransitionInflater;
import com.zrlib.matisse.ui.IPreviewItem;
import com.zrlib.matisse.ui.PreviewFragment;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import java.util.Set;

import base2app.BaseApplication;
import base2app.ui.activity.AbsActivity;
import kotlin.Pair;


/**
 * Entry for Matisse's media selection.
 */
public final class Matisse implements Navigator.Extras {

    private final WeakReference<Context> mContext;

    private Matisse(Context context) {
        mContext = new WeakReference<>(context);
    }

    private Matisse(Fragment fragment) {
        this(fragment.getContext());
    }

    /**
     * Start Matisse from an Activity.
     * <p>
     *
     * @return Matisse instance.
     */
    public static Matisse from(Context context) {
        return new Matisse(context);
    }

    /**
     * Start Matisse from a Fragment.
     * <p>
     * This Fragment's {@link Fragment#onActivityResult(int, int, Intent)} will be called when user
     * finishes selecting.
     *
     * @param fragment Fragment instance.
     * @return Matisse instance.
     */
    public static Matisse from(Fragment fragment) {
        return new Matisse(fragment);
    }

//    /**
//     * Obtain user selected media' {@link Uri} list in the starting Activity or Fragment.
//     */
//    public static List<Uri> obtainResult(Bundle data) {
//        return data.getParcelableArrayList(MatisseFragment.EXTRA_RESULT_SELECTION);
//    }

//    /**
//     * Obtain user selected media path list in the starting Activity or Fragment.
//     *
//     * @return User selected media path list.
//     */
//    public static List<String> obtainPathResult(Bundle data) {
//        return data.getStringArrayList(MatisseFragment.EXTRA_RESULT_SELECTION_PATH);
//    }

//    /**
//     * Obtain state whether user decide to use selected media in original
//     *
//     * @return Whether use original photo
//     */
//    public static boolean obtainOriginalState(Bundle data) {
//        return data.getBoolean(MatisseFragment.EXTRA_RESULT_ORIGINAL_ENABLE, false);
//    }

    /**
     * MIME types the selection constrains on.
     * <p>
     * Types not included in the set will still be shown in the grid but can't be chosen.
     *
     * @param mimeTypes MIME types set user can choose from.
     * @return {@link SelectionCreator} to build select specifications.
     * @see MimeType
     * @see SelectionCreator
     */
    public SelectionCreator choose(Set<MimeType> mimeTypes) {
        return this.choose(mimeTypes, true);
    }

    /**
     * MIME types the selection constrains on.
     * <p>
     * Types not included in the set will still be shown in the grid but can't be chosen.
     *
     * @param mimeTypes          MIME types set user can choose from.
     * @param mediaTypeExclusive Whether can choose images and videos at the same time during one single choosing
     *                           process. true corresponds to not being able to choose images and videos at the same
     *                           time, and false corresponds to being able to do this.
     * @return {@link SelectionCreator} to build select specifications.
     * @see MimeType
     * @see SelectionCreator
     */
    public SelectionCreator choose(Set<MimeType> mimeTypes, boolean mediaTypeExclusive) {
        return new SelectionCreator(this, mimeTypes, mediaTypeExclusive);
    }


    /**
     * 查看社区和主帖的列表大图
     *
     * @param clickView 点击的view
     * @param item      具体的图片
     * @param path      图片数组
     * @param callback  回调界面的预览图
     */
    public void previewListPicture(View clickView, IPreviewItem item, List<IPreviewItem> path, GetPreViewCallback callback) {

        AbsActivity topActivity = BaseApplication.Companion.getBaseApplication().getTopActivity();
        if (topActivity == null) return;
        Fragment topFragment = topActivity.getTopFragment();
        if (topFragment == null) return;
        topFragment.getParentFragmentManager().registerFragmentLifecycleCallbacks(new MyFragmentLifecycleCallbacks(topFragment, item, callback), false);
        topFragment.setExitTransition(TransitionInflater.from(topFragment.requireContext())
                .inflateTransition(R.transition.matisse_transition));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Pair sharedElements = new Pair(clickView, getContext().getString(R.string.matisse_image_transition));
            FragmentNavigator.Extras extras = FragmentNavigatorExtrasKt.FragmentNavigatorExtras(sharedElements);
            Fragment fragment = PreviewFragment.Companion.newInstance(item, path, 0);
            Dest dest = new Dest.Builder(fragment.getClass().getName()).setArguments(fragment.getArguments()).setExtras(extras).clearAnim().build();
            FragmentEx.startFragment(topFragment, dest);
        } else {
            Fragment fragment = PreviewFragment.Companion.newInstance(item, path, 0);
            FragmentEx.startFragment(topFragment, fragment.getClass(), fragment.getArguments());
        }
    }

    private static class MSharedElementCallback extends SharedElementCallback {

        private final WeakReference<PreviewFragment> fragment;
        private final GetPreViewCallback callback;
        private final IPreviewItem item;


        public MSharedElementCallback(PreviewFragment fragment, IPreviewItem item, GetPreViewCallback callback) {
            this.fragment = new WeakReference<>(fragment);
            this.callback = callback;
            this.item = item;

        }


        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            PreviewFragment previewFragment = fragment.get();
            if (previewFragment == null) return;
            View view = callback.getPreView(previewFragment.getPosition());
            if (view != null) {
                names.add(item.path());
                sharedElements.put(item.path(), view);
            } else {
                names.clear();
                sharedElements.clear();
            }
        }

    }

    /**
     * 监听fragment生命周期callback
     */
    private static class MyFragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {
        private final long topFragmentHashCode;  //topfragment的hashcode值

        private final WeakReference<Fragment> fragment;
        private final IPreviewItem item;  //具体图片对象
        private final GetPreViewCallback callback;  //获取特定的view


        public MyFragmentLifecycleCallbacks(Fragment fragment, IPreviewItem item, GetPreViewCallback callback) {
            this.fragment = new WeakReference<>(fragment);
            topFragmentHashCode = fragment.hashCode();
            this.item = item;
            this.callback = callback;
        }

        @Override
        public void onFragmentCreated(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
            super.onFragmentCreated(fm, f, savedInstanceState);
            if (f instanceof PreviewFragment) {
                fragment.get().setExitSharedElementCallback(new MSharedElementCallback((PreviewFragment) f, item, callback));
            }

        }

        @Override
        public void onFragmentResumed(@NonNull @NotNull FragmentManager fm, @NonNull @NotNull Fragment f) {
            super.onFragmentResumed(fm, f);
            if (f.hashCode() == topFragmentHashCode) {
                f.getParentFragmentManager().unregisterFragmentLifecycleCallbacks(this);
            }
        }

        @Override
        public void onFragmentDestroyed(@NonNull FragmentManager fm, @NonNull Fragment f) {
            super.onFragmentDestroyed(fm, f);
            if (fragment.get().getExitTransition() != null) {//将设置的元素共享退出动画重置
                fragment.get().setExitTransition(null);
            }

        }
    }

    /**
     * 查看普通大图
     */
    public void previewPicture(final View clickView, IPreviewItem item, final List<IPreviewItem> path, int errRes) {
        AbsActivity topActivity = BaseApplication.Companion.getBaseApplication().getTopActivity();
        if (topActivity == null) return;
        final Fragment topFragment = topActivity.getTopFragment();
        if (topFragment == null) return;
        topFragment.setExitTransition(TransitionInflater.from(topFragment.requireContext())
                .inflateTransition(R.transition.matisse_transition));
        PreviewFragment fragment;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            fragment = PreviewFragment.Companion.newInstance(item, path, errRes);

            topFragment.setExitSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    sharedElements.clear();
                    sharedElements.put(names.get(0), clickView);
                    if (topFragment.getExitTransition() != null) {
                        topFragment.setExitTransition(null);
                    }
                }

            });


            Pair sharedElements = new Pair(clickView, getContext().getString(R.string.matisse_image_transition));

            FragmentNavigator.Extras extras = FragmentNavigatorExtrasKt.FragmentNavigatorExtras(sharedElements);

            Dest dest = new Dest.Builder(fragment.getClass().getName()).setArguments(fragment.getArguments()).setExtras(extras).build();
            FragmentEx.startFragment(topFragment, dest);

        } else {
            fragment = PreviewFragment.Companion.newInstance(item, path, errRes);
            FragmentEx.startFragment(topFragment, fragment.getClass(), fragment.getArguments());
        }
    }


    public Context getContext() {
        return mContext.get();
    }

    /**
     * 接口获取对应位置的缩略图
     */
    public interface GetPreViewCallback {
        /**
         * @return 获取对应位置的缩略图
         */
        View getPreView(int position);
    }


    /**
     * 接口回调当前viewpager滑动到第几个
     */
    public interface SelectedPositionListener {
        /**
         * @return 获取页面上的下标
         */
        int getPosition();
    }
}