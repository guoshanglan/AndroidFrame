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
package com.zrlib.matisse.intermal.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;
import androidx.fragment.app.Fragment;

import com.zrlib.matisse.intermal.entity.CaptureStrategy;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MediaStoreCompat {

    private final String FILE_DIRECTORY = Environment.DIRECTORY_PICTURES;
    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;
    private CaptureStrategy mCaptureStrategy;
    private Uri mCurrentPhotoUri;
    private String mCurrentPhotoPath;

    public MediaStoreCompat(Activity activity) {
        mContext = new WeakReference<>(activity);
        mFragment = null;
    }

    public MediaStoreCompat(Fragment fragment) {
        mContext = new WeakReference<>(fragment.getActivity());
        mFragment = new WeakReference<>(fragment);
    }

    /**
     * Checks whether the device has a camera feature or not.
     *
     * @param context a context to check for camera feature.
     * @return true if the device has a camera feature. false otherwise.
     */
    public static boolean hasCameraFeature(Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }

    public void setCaptureStrategy(CaptureStrategy strategy) {
        mCaptureStrategy = strategy;
    }

    public void dispatchCaptureIntent(Context context, int requestCode) {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (captureIntent.resolveActivity(context.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile == null) return;

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                List<ResolveInfo> resInfoList = context.getPackageManager()
                        .queryIntentActivities(captureIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    context.grantUriPermission(packageName, mCurrentPhotoUri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            }

            mCurrentPhotoPath = photoFile.getPath();
            mCurrentPhotoUri = FileProvider.getUriForFile(mContext.get(), mCaptureStrategy.authority, photoFile);
            //????????????
            Uri uri;
            //?????????????????????ContentValues???
            ContentValues contentValues = new ContentValues();
            //???????????????
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, photoFile.getName());
            //??????????????????
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG");
            //??????Android Q???????????????
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //android Q???????????????DATA???????????????RELATIVE_PATH?????????RELATIVE_PATH?????????????????????????????????
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, FILE_DIRECTORY);
            } else {
                //Android Q????????????
                contentValues.put(MediaStore.Images.Media.DATA, photoFile.getPath());
            }
            //??????insert??????????????????????????????????????????
            //EXTERNAL_CONTENT_URI????????????????????????????????????
            uri = mContext.get().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            //????????????uri?????????????????????????????????
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            if (mFragment != null) {
                mFragment.get().startActivityForResult(captureIntent, requestCode);
            } else {
                mContext.get().startActivityForResult(captureIntent, requestCode);
            }
        }
    }


    public boolean createCutterImageFile() {
        File photoFile = null;
        try {
            photoFile = createImageFile();
            mCurrentPhotoPath = photoFile.getAbsolutePath();
            mCurrentPhotoUri = FileProvider.getUriForFile(mContext.get(), mCaptureStrategy.authority, photoFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return photoFile != null;
    }


    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = String.format("JPEG_%s.jpg", timeStamp);
        File storageDir;
        if (mCaptureStrategy.isPublic) {
            storageDir = Environment.getExternalStoragePublicDirectory(FILE_DIRECTORY);
            if (!storageDir.exists()) storageDir.mkdirs();
        } else {
            storageDir = mContext.get().getExternalFilesDir(FILE_DIRECTORY);
        }
        if (mCaptureStrategy.directory != null) {
            storageDir = new File(storageDir, mCaptureStrategy.directory);
            if (!storageDir.exists()) storageDir.mkdirs();
        }

        // Avoid joining path components manually
        File tempFile = new File(storageDir, imageFileName);

        // Handle the situation that user's external storage is not ready
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }

        return tempFile;
    }

    public Uri getCurrentPhotoUri() {
        return mCurrentPhotoUri;
    }

    public String getCurrentPhotoPath() {
        return mCurrentPhotoPath;
    }
}
