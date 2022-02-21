package com.zrlib.permission.request.fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.zrlib.permission.Constants;
import com.zrlib.permission.PermissionTools;
import com.zrlib.permission.bean.Permission;
import com.zrlib.permission.bean.Special;
import com.zrlib.permission.callbcak.GoAppDetailCallBack;
import com.zrlib.permission.callbcak.RequestPermissionListener;
import com.zrlib.permission.callbcak.SpecialPermissionListener;
import com.zrlib.permission.checker.SpecialChecker;
import com.zrlib.permission.debug.PermissionDebug;
import com.zrlib.permission.request.IPermissionActions;

import static android.os.Build.VERSION_CODES.M;

/**
 * @author cd5160866
 */
public class PermissionSupportFragment extends Fragment implements IPermissionActions {

    private static final String TAG = PermissionSupportFragment.class.getSimpleName();

    private RequestPermissionListener runtimeListener;

    private SpecialPermissionListener specialListener;

    private Special specialToRequest;

    private GoAppDetailCallBack goAppDetailCallBack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @TargetApi(M)
    @Override
    public void requestPermissions(String[] permissions, RequestPermissionListener listener) {
        requestPermissions(permissions, Constants.REQUEST_CODE_PERMISSION);
        this.runtimeListener = listener;
    }

    @TargetApi(M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permission[] permissionResults = new Permission[permissions.length];
        //some specific rom will provide a null array
        if (null == permissionResults || null == grantResults) {
            clearListener();
            return;
        }
        if (requestCode == Constants.REQUEST_CODE_PERMISSION) {
            for (int i = 0; i < permissions.length; ++i) {
                Permission permission = new Permission(permissions[i], grantResults[i], this.shouldShowRequestPermissionRationale(permissions[i]));
                permissionResults[i] = permission;
            }
        }
        if (null != runtimeListener && PermissionTools.isActivityAvailable(getActivity())) {
            runtimeListener.onPermissionResult(permissionResults);
        }
        clearListener();
    }

    @Override
    public void requestSpecialPermission(Special permission, SpecialPermissionListener listener) {
        this.specialListener = listener;
        this.specialToRequest = permission;
        Intent intent = PermissionTools.getSpecialPermissionIntent(getActivity(), permission);
        if (null == intent) {
            PermissionDebug.w(TAG, "create intent failed");
            clearListener();
            return;
        }
        try {
            startActivityForResult(intent, Constants.REQUEST_CODE_PERMISSION_SPECIAL);
        } catch (Exception e) {
            e.printStackTrace();
            PermissionDebug.e(TAG, e.toString());
            clearListener();
        }
    }

    @Override
    public void goAppDetail(@Nullable GoAppDetailCallBack callBack) {
        this.goAppDetailCallBack = callBack;
        Intent intent = PermissionTools.getAppManageIntent(getActivity());
        if (null == intent) {
            PermissionDebug.w(TAG, "create intent failed");
            return;
        }
        startActivityForResult(intent, Constants.REQUEST_CODE_APPLICATION_SETTINGS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Activity activity = getActivity();
        if (!PermissionTools.isActivityAvailable(activity)) {
            clearListener();
            return;
        }
        if (requestCode == Constants.REQUEST_CODE_PERMISSION_SPECIAL && null != specialToRequest && null != specialListener) {
            boolean result = new SpecialChecker(activity, specialToRequest).check();
            if (result) {
                specialListener.onGranted(specialToRequest);
            } else {
                specialListener.onDenied(specialToRequest);
            }
            clearListener();
            return;
        }
        if (requestCode == Constants.REQUEST_CODE_APPLICATION_SETTINGS && null != goAppDetailCallBack) {
            goAppDetailCallBack.onBackFromAppDetail(data);
        }
        clearListener();
    }

    private void clearListener(){
        runtimeListener = null;
        specialListener = null;
        goAppDetailCallBack = null;
    }
}
