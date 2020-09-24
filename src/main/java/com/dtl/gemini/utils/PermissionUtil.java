package com.dtl.gemini.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

public class PermissionUtil {

    private static final String TAG = "PermissionUtil";

    public static String[] neededPermission = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    public static void initPermission(Activity context) {
        ArrayList<String> permission = new ArrayList<>();
        for (int i = 0; i < PermissionUtil.neededPermission.length; i++) {
            if (!PermissionUtil.checkPermissionGrantStatus(context, PermissionUtil.neededPermission[i])) {
                permission.add(PermissionUtil.neededPermission[i]);
            }
        }
        PermissionUtil.requestPermissions(context, permission, 1);
    }

    public static boolean requestPermissions(Activity activity, ArrayList<String> permissionList, int requestCode) {

        Log.i(TAG, "requestPermissions: " + permissionList.size());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        int i = 0;
        String[] permissionArray = new String[permissionList.size()];
        for (String each : permissionList) {
            permissionArray[i++] = each;
        }
        if (permissionArray.length != 0) {
            activity.requestPermissions(permissionArray, requestCode);
        }
        return true;
    }

    public static boolean checkPermissionGrantStatus(Context context, String permission) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "checkPermissionGrantStatus: " + permission);
            return true;
        } else {
            Log.i(TAG, "checkPermissionGrantStatus: " + permission);
            return false;
        }
    }

}
