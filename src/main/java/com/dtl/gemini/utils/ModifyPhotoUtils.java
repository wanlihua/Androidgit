package com.dtl.gemini.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.dtl.gemini.R;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.widget.BottomMenuDialog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

/**
 * 大图灵
 * 2019/9/18
 * 选择图片
 **/
public class ModifyPhotoUtils {
    public static final int MY_CALL_CIMERA = 1; // 相机权限
    public static final String USER_IMAGE_NAME = "image.png";
    public static final String USER_CROP_IMAGE_NAME = "temporary.png";
    public static Uri imageUriFromCamera;
    public static Uri cropImageUri;
    public static final int GET_IMAGE_BY_CAMERA_U = 5001;
    public static final int GET_IMAGE_BY_GALLERY_U = 5002;
    public static final int CROP_IMAGE_U = 5003;

    static String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA,};

    /**
     * 选择上传方式
     */
    public static void modifyUserPhoto(Activity context) {
        BottomMenuDialog dialog = new BottomMenuDialog(context);
        //拍照
        dialog.setConfirmListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检测是否有相机和读写文件权限
                DataUtil.initPermission(context, permissions);
                getPicFromCarema(context);
                dialog.dismiss();
            }
        });
        //相册
        dialog.setMiddleListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUtil.initPermission(context, permissions);
                getPicFromGallery(context);
                dialog.dismiss();
            }
        });
        //取消
        dialog.setCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    static String permission = Manifest.permission.CAMERA;

    /**
     * 拍照
     */
    public static void getPicFromCarema(Activity context) {

        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, Manifest.permission.READ_CONTACTS)) {
                //权限已有
                setCapture(context);
            } else {
                //没有权限，申请一下
                ActivityCompat.requestPermissions(context,
                        new String[]{permission},
                        1);
            }
        } else {
            setCapture(context);
        }
    }

    private static void setCapture(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {  // 或者 android.os.Build.VERSION_CODES.KITKAT这个常量的值是19
            setPic(context, true);
        } else {
            ModifyPhotoUtils.imageUriFromCamera = createImagePathUri(context);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, ModifyPhotoUtils.imageUriFromCamera);
            context.startActivityForResult(intent, ModifyPhotoUtils.GET_IMAGE_BY_CAMERA_U);
        }
    }

    private static void setPic(Activity context, boolean bln) {
        if (bln) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = createImagePathFile(context);
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            /*
             * 这里就是高版本需要注意的，需用使用FileProvider来获取Uri，同时需要注意getUriForFile
             * 方法第二个参数要与AndroidManifest.xml中provider的里面的属性authorities的值一致
             * */
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            ModifyPhotoUtils.imageUriFromCamera = FileProvider.getUriForFile(context,
                    Constant.FILEPROVIDER, photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, ModifyPhotoUtils.imageUriFromCamera);
            context.startActivityForResult(intent, ModifyPhotoUtils.GET_IMAGE_BY_CAMERA_U);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.photo_error), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 相册
     */
    public static void getPicFromGallery(Activity context) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        context.startActivityForResult(intent, ModifyPhotoUtils.GET_IMAGE_BY_GALLERY_U);
    }

    /**
     * 拍照<19文件
     */
    public static File createImagePathFile(Activity activity) {
        //文件目录可以根据自己的需要自行定义
        Uri imageFilePath;
        File file = new File(activity.getExternalCacheDir(), ModifyPhotoUtils.USER_IMAGE_NAME);
        imageFilePath = Uri.fromFile(file);
        return file;
    }

    /**
     * 拍照>19文件
     */
    public static Uri createImagePathUri(Activity activity) {
        //文件目录可以根据自己的需要自行定义
        Uri imageFilePath;
        File file = new File(activity.getExternalCacheDir(), ModifyPhotoUtils.USER_IMAGE_NAME);
        imageFilePath = Uri.fromFile(file);
        return imageFilePath;
    }

    /**
     * 裁剪
     */
    public static void cropImage(Activity context, Uri imageUri, double aspect, int outputX, int outputY, int return_flag) {
        File file = new File(context.getExternalCacheDir(), USER_CROP_IMAGE_NAME);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //高版本一定要加上这两句话，做一下临时的Uri
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            FileProvider.getUriForFile(context, Constant.FILEPROVIDER, file);
        }
        cropImageUri = Uri.fromFile(file);
        intent.setDataAndType(imageUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("circleCrop", "true");
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        if (aspect < 1) {
            intent.putExtra("aspectX", aspect);
            intent.putExtra("aspectY", aspect);
        } else {
            String Brand = Build.MANUFACTURER;
            if (Brand.equals("HUAWEI")) {
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
            } else {
                intent.putExtra("aspectX", aspect);
                intent.putExtra("aspectY", aspect);
            }
        }
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边

        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImageUri);
        context.startActivityForResult(intent, return_flag);
    }

    /**
     * 转化为bitmap
     */
    public static Bitmap GetBitmap(String path, int w, int h) {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int) scale;
        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(
                BitmapFactory.decodeFile(path, opts));
        if (weak != null && weak.get() != null)
            bitmap = Bitmap.createScaledBitmap(weak.get(), w, h, true);
        return bitmap;
    }

    /**
     * 将Bitmap转换成文件
     * 保存文件
     */
    public static File saveFile(Bitmap bm, String path, String fileName) throws IOException {
        File dirFile = new File(path);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File myCaptureFile = new File(path, fileName);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return myCaptureFile;
    }

}
