package com.dtl.gemini.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dtl.gemini.R;
import com.dtl.gemini.common.base.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 大图灵
 * 2019/10/16
 **/
public class ShareUtils {
    public Context context;

    public ShareUtils(Context context) {
        this.context = context;
    }

    public static View shareFriend(Context context, String username, Bitmap bitmap) {
        int width = 500;     // 屏幕宽度（像素）
        int height = 580;
        View view = LayoutInflater.from(context).inflate(R.layout.share_friend_item, null, false);
        ImageView imageView = view.findViewById(R.id.share_friend_item_rcode);
        TextView usernameTv = view.findViewById(R.id.share_friend_item_username);
        imageView.setImageBitmap(bitmap);
        usernameTv.setText(username);
        // 整个View的大小 参数是左上角 和右下角的坐标
        view.layout(0, 0, width, height);
        //UNSPECIFIED(未指定)EXACTLY(完全)AT_MOST(至多)
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST);
        /** 当然，measure完后，并不会实际改变View的尺寸，需要调用View.layout方法去进行布局。
         * 按示例调用layout函数后，View的大小将会变成你想要设置成的大小。
         */
        view.measure(measuredWidth, measuredHeight);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        //v.layout(0, 0, v.getWidth(), v.getMeasuredHeight());
        return view;
    }

    //然后View和其内部的子View都具有了实际大小，也就是完成了布局，相当与添加到了界面上。接着就可以创建位图并在上面绘制了：
    private void layoutView(View v, int width, int height, int type) {
        // 整个View的大小 参数是左上角 和右下角的坐标
        v.layout(0, 0, width, height);
        //UNSPECIFIED(未指定)EXACTLY(完全)AT_MOST(至多)
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST);
        /** 当然，measure完后，并不会实际改变View的尺寸，需要调用View.layout方法去进行布局。
         * 按示例调用layout函数后，View的大小将会变成你想要设置成的大小。
         */
        v.measure(measuredWidth, measuredHeight);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        //v.layout(0, 0, v.getWidth(), v.getMeasuredHeight());
        viewSaveToImage(v, type);
    }

    @SuppressLint("ResourceAsColor")
    private void viewSaveToImage(View view, int type) {
        // 把一个View转换成图片
        Bitmap cachebmp = loadBitmapFromView(view);
        FileOutputStream fos;
        String imagePath = "";
        try {
            // 判断手机设备是否有SD卡
            boolean isHasSDCard = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);
            if (isHasSDCard) {
                // SD卡根目录
                String filePath = com.dtl.gemini.utils.DataUtil.getFileRoot(context) + File.separator
                        + "share_" + System.currentTimeMillis() + ".png";
                File file = new File(filePath);
                fos = new FileOutputStream(file);
                imagePath = file.getAbsolutePath();
            } else {
                throw new Exception("创建文件失败!");
            }
            cachebmp.compress(Bitmap.CompressFormat.PNG, 100, fos);

            fos.flush();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (type == 1) {
            BaseActivity activity = (BaseActivity) context;
            activity.shareImg(imagePath);
        } else if (type == 2) {
//            MainActivity activity = (MainActivity) context;
//            activity.shareImg(imagePath);
        }
        view.destroyDrawingCache();
    }

    @SuppressLint("ResourceAsColor")
    public static Bitmap loadBitmapFromView(View v) {
        v.setDrawingCacheEnabled(true);
        v.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        v.setDrawingCacheBackgroundColor(R.color.white);
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(R.color.white);
        /** 如果不设置canvas画布为白色，则生成透明 */
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }
}
