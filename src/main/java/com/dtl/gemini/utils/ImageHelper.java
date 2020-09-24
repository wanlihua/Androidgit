package com.dtl.gemini.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.dtl.gemini.R;
import com.dtl.gemini.constants.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

public class ImageHelper {

    ImageLoader imageLoader;
    DisplayImageOptions options;

    private static ImageHelper helper;

    private ImageHelper() {
        imageLoader = ImageLoader.getInstance();
        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.ic_launcher) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.ic_launcher) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//                .displayer(new RoundedBitmapDisplayer(1)) // 设置成圆角图片
                .build(); // 构建完成
    }

    public static ImageHelper init() {

        if (helper == null) {
            helper = new ImageHelper();
        }

        return helper;

    }

    public void showHeadImage(String path, ImageView iv) {
        path=path.trim();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.icon_default_head) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(R.mipmap.icon_default_head) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.mipmap.icon_default_head) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//                .displayer(new RoundedBitmapDisplayer(1)) // 设置成圆角图片
                .build(); // 构建完成
        if (path != null && !path.equals("null") && !path.equals(""))
            imageLoader.displayImage(path, iv, options);
    }


    /**
     * 加载图片
     *
     * @param context
     * @param imageRes 图片下载期间显示的图片
     * @param imageUrl 下载链接
     * @param imageIv  展示控件
     */
    public void showImage(Context context, int imageRes, String imageUrl, ImageView imageIv) {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(imageRes) // 设置图片下载期间显示的图片
                .showImageForEmptyUri(imageRes) // 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(imageRes) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
//                .displayer(new RoundedBitmapDisplayer(1)) // 设置成圆角图片
                .build(); // 构建完成
        imageUrl=imageUrl.trim();
//        String imageName = DataUtil.returnImageName(imageUrl);
//        String imagePath = context.getExternalCacheDir() + Constant.IMG_FILE;
//        File imageFile = new File(imagePath, imageName);
//        Bitmap bitmap = null;
//        if (imageFile.exists()) {
//            // 如果已经存在加载本地
//            bitmap = BitmapFactory.decodeFile(imagePath + imageName);
//        }
//        if (bitmap != null) {
//            imageIv.setImageBitmap(bitmap);
//        } else {
            imageLoader.displayImage(imageUrl, imageIv, options);
//            DataUtil.down(context, imageUrl, imagePath, imageName);
//        }
    }

    /**
     * 加载视频第一帧
     *
     * @param context
     * @param image
     * @param imageIv
     */
    public void showVideoImage(Context context, String image, ImageView imageIv) {
        String imageName = DataUtil.returnImageName(image);
        String imagePath = context.getExternalCacheDir() + Constant.IMG_FILE;
        File imageFile = new File(imagePath, imageName);
        Bitmap bitmap = null;
        if (imageFile.exists()) {
            // 如果已经存在,加载本地
            bitmap = BitmapFactory.decodeFile(imagePath + imageName);
        } else {
            bitmap = DataUtil.getNetVideoBitmap(image);
            ImageUtils.download(bitmap, imagePath, imageName);
        }
        imageIv.setImageBitmap(bitmap);
    }

    /**
     * 截取scrollview的屏幕
     **/
    public static Bitmap getScrollViewBitmap(Context context, ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        if (scrollView != null) {
            // 获取recyclerview实际高度
            for (int i = 0; i < scrollView.getChildCount(); i++) {
                h += scrollView.getChildAt(i).getHeight();
            }
            // 创建对应大小的bitmap
            bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                    Bitmap.Config.RGB_565);
            final Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(context.getResources().getColor(R.color.white));
            scrollView.draw(canvas);
        }
        return bitmap;
    }
}
