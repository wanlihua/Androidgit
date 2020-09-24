package com.dtl.gemini.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dtl.gemini.MainActivity;
import com.dtl.gemini.R;
import com.dtl.gemini.api.Api;
import com.dtl.gemini.common.baseapp.AppManager;
import com.dtl.gemini.constants.Constant;
import com.dtl.gemini.db.LanguageUtils;
import com.dtl.gemini.db.StoreUtils;
import com.dtl.gemini.enums.AppUserGradeEnum;
import com.dtl.gemini.enums.CfdTypeEnum;
import com.dtl.gemini.ui.me.activity.PurchaseLevelActivity;
import com.dtl.gemini.ui.me.activity.ShareActivity;
import com.dtl.gemini.ui.other.activity.LoginActivity;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.BitmapCallback;
import com.lzy.okgo.callback.FileCallback;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import okhttp3.Call;
import okhttp3.Response;

import static com.dtl.gemini.enums.AppUserGradeEnum.*;

/**
 * 大图灵
 * 2019/10/16
 **/
@SuppressLint("StringFormatInvalid")
public class DataUtil {
    public static Date urlDate = null;

    /**
     * 指定格式时间和当前时间比较
     *
     * @param time       时间
     * @param timeFormat 时间格式
     * @return
     */
    public static boolean returnTimeBefor(String time, String timeFormat) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        String dates = format.format(date);
        if (time.compareTo(dates) < 0) {//表示time小于dates
            return true;
        } else {
            return false;
        }
    }

    /**
     * 时间戳转换到指定格式时间
     *
     * @param time       时间戳
     * @param timeFormat 指定格式
     * @return
     */
    public static String returnLongToTime(Object time, String timeFormat) {
        String date = "";
        if (time != null && !time.equals("")) {
            String times = time + "";
            BigDecimal bd = new BigDecimal(times);
            Date currentTime = new Date(bd.longValue());
            SimpleDateFormat format = new SimpleDateFormat(timeFormat);
            date = format.format(currentTime);
        }
        return date;
    }

    /**
     * 时间戳转换到指定格式时间
     *
     * @param time       时间戳
     * @param timeFormat 指定格式
     * @return
     */
    public static String returnLongToTimes(Object time, String timeFormat) {
        String date = "";
        if (time != null && !time.equals("")) {
            long times = Long.parseLong(time + "") * 1000;//直接是时间戳
            Date currentTime = new Date(times);
            SimpleDateFormat format = new SimpleDateFormat(timeFormat);
            date = format.format(currentTime);
        }
        return date;
    }

    /**
     * 指定格式时间转换为指定格式时间
     *
     * @param time           时间
     * @param timeFromFormat 时间格式
     * @param timeToFormat   指定格式
     * @return
     */
    public static String returnToTime(String time, String timeFromFormat, String timeToFormat) {
        String date = "";
        if (time != null && !time.equals("")) {
            if (time.length() < 12) {
                date = time;
            } else {
                try {
                    if (time != null && !time.equals("")) {
                        String tzId = TimeZone.getDefault().getID();
                        TimeZone tz = TimeZone.getTimeZone(tzId);
                        Date currentTime = null;
                        SimpleDateFormat formatter = new SimpleDateFormat(timeFromFormat);
                        formatter.setTimeZone(tz);
                        try {
                            currentTime = formatter.parse(time);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        SimpleDateFormat format = new SimpleDateFormat(timeToFormat);
                        date = format.format(currentTime);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    date = time;
                }
            }
        }
        return date;
    }

    /**
     * 指定格式时间转换为时间戳
     *
     * @param time       时间
     * @param timeFormat 指定格式
     * @return
     */
    public static long returnTimeToLong(String time, String timeFormat) {
        long date = 0;
        if (time != null && !time.equals("")) {
            String tzId = TimeZone.getDefault().getID();
            TimeZone tz = TimeZone.getTimeZone(tzId);
            Date currentTime = null;
            SimpleDateFormat formatter = new SimpleDateFormat(timeFormat);
            formatter.setTimeZone(tz);
            try {
                currentTime = formatter.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long timer = currentTime.getTime();
            date = timer;
        }
        return date;
    }

    /**
     * 当前时间加n天返回指定格式时间
     *
     * @param day        天数
     * @param timeFormat 指定格式
     * @return
     */
    public static String returnIsTimeAddDay(int day, String timeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);//+1今天的时间加一天
        date = calendar.getTime();
        String dates = format.format(date);
        return dates;
    }

    /**
     * 当前时间加n分钟返回指定格式时间
     *
     * @param minute     分钟
     * @param timeFormat 指定格式
     * @return
     */
    public static String returnIsTimeAddMinute(int minute, String timeFormat) {
        SimpleDateFormat format = new SimpleDateFormat(timeFormat);
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);
        date = calendar.getTime();
        String dates = format.format(date);
        return dates;
    }

    /**
     * 指定格式时间加n月返回指定格式时间
     *
     * @param time           时间
     * @param month          月数
     * @param timeFromFormat 时间格式
     * @param timeToFormat   指定格式
     * @return
     */
    public static String returnTimeAddMonth(String time, int month, String timeFromFormat, String timeToFormat) {
        String dates = "";
        if (time != null && !time.equals("")) {
            SimpleDateFormat format = new SimpleDateFormat(timeToFormat);
            String tzId = TimeZone.getDefault().getID();
            TimeZone tz = TimeZone.getTimeZone(tzId);
            Date currentTime = null;
            SimpleDateFormat formatter = new SimpleDateFormat(timeFromFormat);
            formatter.setTimeZone(tz);
            try {
                currentTime = formatter.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTime);
            calendar.add(Calendar.MONTH, month);//+1今天的时间加一天
            currentTime = calendar.getTime();
            dates = format.format(currentTime);
        }
        return dates;
    }

    /**
     * 指定格式时间加n天返回指定格式时间
     *
     * @param time           时间
     * @param day            天数
     * @param timeFromFormat 时间格式
     * @param timeToFormat   指定格式
     * @return
     */
    public static String returnTimeAddDay(String time, int day, String timeFromFormat, String timeToFormat) {
        String dates = "";
        if (time != null && !time.equals("")) {
            SimpleDateFormat format = new SimpleDateFormat(timeToFormat);
            String tzId = TimeZone.getDefault().getID();
            TimeZone tz = TimeZone.getTimeZone(tzId);
            Date currentTime = null;
            SimpleDateFormat formatter = new SimpleDateFormat(timeFromFormat);
            formatter.setTimeZone(tz);
            try {
                currentTime = formatter.parse(time);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTime);
            calendar.add(Calendar.DAY_OF_MONTH, day);//+1今天的时间加一天
            currentTime = calendar.getTime();
            dates = format.format(currentTime);
        }
        return dates;
    }

    /**
     * 判断指定格式时间是哪天或返回指定格式时间
     *
     * @param datetime       时间
     * @param timeFromFormat 时间格式
     * @param timeToFormat   指定格式
     * @return
     */
    public static String returnTimeIsDay(Context context, String datetime, String timeFromFormat, String timeToFormat) {
        String time = "";
        Date currentTime = new Date();//currentTime就是系统当前时间
        String tzId = TimeZone.getDefault().getID();
        TimeZone tz = TimeZone.getTimeZone(tzId);
        Date currdate = null;
        SimpleDateFormat formatter = new SimpleDateFormat(timeFromFormat);
        formatter.setTimeZone(tz);
        try {
            currdate = formatter.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat format = new SimpleDateFormat(timeToFormat);
        String enddate = format.format(currdate);
        long timer = currentTime.getTime() - currdate.getTime();
        long i = timer / 1000 / 60;
        if (i <= 0) {
            if (timer / 1000 <= 0)
                time = "1" + context.getResources().getString(R.string.is_day_status1);
            else
                time = timer / 1000 + context.getResources().getString(R.string.is_day_status1);
        } else if (i > 0 && i < 60) {
            time = i + context.getResources().getString(R.string.is_day_status2);
        } else if (i >= 60 && i < 60 * 24) {
            time = i / 60 + context.getResources().getString(R.string.is_day_status3);
        } else if (i >= 60 * 24 && i < 60 * 48) {
            time = context.getResources().getString(R.string.is_day_status4);
        } else if (i >= 60 * 48 && i < 60 * 72) {
            time = context.getResources().getString(R.string.is_day_status5);
        } else if (i >= 60 * 72) {
            time = enddate;
        }
        return time;
    }

    /**
     * 当前时间减去指定格式时间
     *
     * @param datetime       时间
     * @param timeFromFormat 时间格式
     * @return
     */
    public static long returnIsTimeSubtractTime(String datetime, String timeFromFormat) {
        urlDate = new Date();
        returnServerTime();
        String tzId = TimeZone.getDefault().getID();
        TimeZone tz = TimeZone.getTimeZone(tzId);
        Date currdate = null;
        SimpleDateFormat formatter = new SimpleDateFormat(timeFromFormat);
        formatter.setTimeZone(tz);
        try {
            currdate = formatter.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timer = urlDate.getTime() - currdate.getTime();
        return timer;
    }

    /**
     * 指定格式时间和当前时间相差多少分钟
     *
     * @param datetime       时间
     * @param timeFromFormat 时间格式
     * @return
     */
    public static int returnTimeSubtractIsTime(String datetime, String timeFromFormat) {
        urlDate = new Date();
        returnServerTime();
        String tzId = TimeZone.getDefault().getID();
        TimeZone tz = TimeZone.getTimeZone(tzId);
        Date currdate = null;
        SimpleDateFormat formatter = new SimpleDateFormat(timeFromFormat);
        formatter.setTimeZone(tz);
        try {
            currdate = formatter.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long timer = currdate.getTime() - urlDate.getTime();
        return (int) timer / 1000 / 60;
    }

    /**
     * 获取网络时间
     */
    public static void returnServerTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                URL url = null;//取得资源对象
                try {
                    url = new URL("http://www.baidu.com");
                    //url = new URL("http://www.ntsc.ac.cn");//中国科学院国家授时中心
                    //url = new URL("http://www.bjtime.cn");
                    URLConnection uc = url.openConnection();//生成连接对象
                    uc.connect(); //发出连接
                    long ld = uc.getDate(); //取得网站日期时间
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(ld);
                    final String format = formatter.format(calendar.getTime());
                    urlDate = formatter.parse(format);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /*
     * 小数点保留两位
     * */
    public static String doubleToString(double num) {
        //使用0.00不足位补0，#.##仅保留有效位
        return new DecimalFormat("0.00").format(num);
    }

    /**
     * 获取视频第一帧
     */
    /**
     * 服务器返回url，通过url去获取视频的第一帧
     * Android 原生给我们提供了一个MediaMetadataRetriever类
     * 提供了获取url视频第一帧的方法,返回Bitmap对象
     *
     * @param videoUrl
     * @return
     */
    public static Bitmap getNetVideoBitmap(String videoUrl) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据url获取缩略图
            retriever.setDataSource(videoUrl, new HashMap());
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }

    public static Bitmap getVideoBitmap(String videoUrl) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据url获取缩略图
            retriever.setDataSource(videoUrl);
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }


    // 两次点击按钮之间的点击间隔不能少于500毫秒
    private static final int MIN_CLICK_DELAY_TIME = 500;
    private static long lastClickTime;

    public static boolean isFastClick() {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= MIN_CLICK_DELAY_TIME) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }


    //文件存储根目录
    public static String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }

        return context.getFilesDir().getAbsolutePath();
    }

    private static String[] num = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};

    private static String[] unit = {"", "十", "百", "千", "万", "十", "百", "千", "亿"};

    private static String[] result;

    public static String transfer(String input) {
        String out = "";
        result = new String[input.length()];
        int length = result.length;
        for (int i = 0; i < length; i++) {
            result[i] = String.valueOf(input.charAt(i));
        }
        for (int i = 0; i < length; i++) {
            int back;
            if (!result[i].equals("0")) {
                back = length - i - 1;
                out += num[Integer.parseInt(result[i])];
                out += unit[back];
            } else {
                //最后一位不考虑
                if (i == (length - 1)) {
                    if (length > 4 && result[length - 1].equals("0") && result[length - 2].equals("0") && result[length - 3].equals("0") && result[length - 4].equals("0")) {
                        out += unit[4];
                    }
                } else {
                    //九位数，千万，百万，十万，万位都为0，则不加“万”
                    if (length == 9 && result[1].equals("0") && result[2].equals("0") && result[3].equals("0") && result[4].equals("0")) {

                    } else {
                        //大于万位，连着的两个数不为0，万位等于0则加上“万”
                        if (length > 4 && !result[i + 1].equals("0") && result[length - 5].equals("0")) {
                            out += unit[4];
                        }
                    }
                    //万位之后的零显示
                    if (i == length - 4 && !result[i + 1].equals("0")) {
                        out += num[0];
                    }
                }
            }
        }
        return out;
    }

    /*
     *亿/万转换
     * */
    public static String transfers(Context context, double number) {
        String result = "";
        if (number > 10000 || number == 10000) {
            if (number > 100000000 || number == 100000000) {
                result = numberTwo(number / 100000000.00) + context.getResources().getString(R.string.billion);
            } else {
                result = numberTwo(number / 10000.00) + context.getResources().getString(R.string.thousand);
            }
        } else {
            result = numberTwo(number);
        }
        return result;
    }

    /*
     *数量千转换
     * */
    public static String transferK(double number) {
        String result = "";
        if (number > 1000 || number == 1000) {
            result = numberTwo(number / 1000.00) + "k";
        } else {
            result = numberFour(number);
        }
        return result;
    }

    /*
     *小数点不保留
     * */
    public static String numberZero(double number) {
        NumberFormat format = NumberFormat.getInstance();
        format.setRoundingMode(RoundingMode.DOWN);
        format.setMaximumFractionDigits(0);//setMaximumFractionDigits(int) 设置数值的小数部分允许的最大位数。
        return format.format(number).replaceAll(",", "");
    }

    public static String numberZeroUp(double number) {
        NumberFormat format = NumberFormat.getInstance();
        format.setRoundingMode(RoundingMode.CEILING);
        format.setMaximumFractionDigits(0);//setMaximumFractionDigits(int) 设置数值的小数部分允许的最大位数。
        return format.format(number).replaceAll(",", "");
    }

    public static String numberOneHelpUp(double number) {
        NumberFormat format = NumberFormat.getInstance();
        format.setRoundingMode(RoundingMode.HALF_UP);
        format.setMaximumFractionDigits(1);//setMaximumFractionDigits(int) 设置数值的小数部分允许的最大位数。
        return format.format(number).replaceAll(",", "");
    }

    /*
     *小数点保留两位
     * */
    public static String numberTwo(double number) {
        NumberFormat format = NumberFormat.getInstance();
        format.setRoundingMode(RoundingMode.DOWN);
        format.setMaximumFractionDigits(2);//setMaximumFractionDigits(int) 设置数值的小数部分允许的最大位数。
        return format.format(number).replaceAll(",", "");
    }

    /*
     *小数点保留两位
     * */
    public static String numberTwoUp(double number) {
        NumberFormat format = NumberFormat.getInstance();
        format.setRoundingMode(RoundingMode.UP);
        format.setMaximumFractionDigits(2);//setMaximumFractionDigits(int) 设置数值的小数部分允许的最大位数。
        return format.format(number).replaceAll(",", "");
    }

    /*
     *小数点保留三位
     * */
    public static String numberThree(double number) {
        NumberFormat format = NumberFormat.getInstance();
        format.setRoundingMode(RoundingMode.DOWN);
        format.setMaximumFractionDigits(3);//setMaximumFractionDigits(int) 设置数值的小数部分允许的最大位数。
        return format.format(number).replaceAll(",", "");
    }

    public static String numberThrees(double number) {
        NumberFormat format = NumberFormat.getInstance();
        format.setRoundingMode(RoundingMode.HALF_UP);
        format.setMaximumFractionDigits(3);//setMaximumFractionDigits(int) 设置数值的小数部分允许的最大位数。
        return format.format(number).replaceAll(",", "");
    }

    /*
     *小数点保留四位
     * */
    public static String numberFour(double number) {
        NumberFormat format = NumberFormat.getInstance();
        format.setRoundingMode(RoundingMode.DOWN);
        format.setMaximumFractionDigits(4);//setMaximumFractionDigits(int) 设置数值的小数部分允许的最大位数。
        return format.format(number).replaceAll(",", "");
    }

    /*
     *小数点保留四位
     * */
    public static String numberFours(double number) {
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(4);//setMaximumFractionDigits(int) 设置数值的小数部分允许的最大位数。
        return format.format(number).replaceAll(",", "");
    }

    public static String numberFourUp(double number) {
        NumberFormat format = NumberFormat.getInstance();
        format.setRoundingMode(RoundingMode.UP);
        format.setMaximumFractionDigits(4);//setMaximumFractionDigits(int) 设置数值的小数部分允许的最大位数。
        return format.format(number).replaceAll(",", "");
    }

    /*
     *小数点保留六位
     * */
    public static String numberSix(double number) {
        NumberFormat format = NumberFormat.getInstance();
        format.setRoundingMode(RoundingMode.DOWN);
        format.setMaximumFractionDigits(6);//setMaximumFractionDigits(int) 设置数值的小数部分允许的最大位数。
        return format.format(number).replaceAll(",", "");
    }

    /*
     *小数点保留八位
     * */
    public static String doubleEight(double number) {
        String str = "";
        DecimalFormat df = null;
        if (number > 0)
            df = new DecimalFormat("###.00000000");
        else
            df = new DecimalFormat("#0.00000000");
        str = df.format(number).replaceAll(",", "");
        if (str.indexOf(".") == 0)
            str = "0" + str;
        return str;
    }

    /*
     *小数点保留四位
     * */
    public static String doubleFour(double number) {
        String str = "";
        DecimalFormat df = null;
        if (number > 0)
            df = new DecimalFormat("###.0000");
        else
            df = new DecimalFormat("#0.0000");
        df.setRoundingMode(RoundingMode.DOWN);
        str = df.format(number).replaceAll(",", "");
        if (str.indexOf(".") == 0)
            str = "0" + str;
        return str;
    }

    /*
     *小数点两位
     * */
    public static String doubleTwo(double number) {
        String str = "";
        DecimalFormat df = null;
        if (number > 0)
            df = new DecimalFormat("###.00");
        else
            df = new DecimalFormat("#0.00");
        df.setRoundingMode(RoundingMode.DOWN);
        str = df.format(number).replaceAll(",", "");
        if (str.indexOf(".") == 0)
            str = "0" + str;
        return str;
    }

    /*
     *小数点3位
     * */
    public static String doubleThree(double number) {
        String str = "";
        DecimalFormat df = null;
        if (number > 0)
            df = new DecimalFormat("###.000");
        else
            df = new DecimalFormat("#0.000");
        df.setRoundingMode(RoundingMode.DOWN);
        str = df.format(number).replaceAll(",", "");
        if (str.indexOf(".") == 0)
            str = "0" + str;
        return str;
    }

    /**
     * @param currency
     * @param price
     * @return
     */
    public static String returnCurrencyDouble(String currency, double price) {
        currency = toUpper(currency);
        String prices = doubleFour(price);//价格
        if (currency.equals("BTC") || currency.equals("ETH") || currency.equals("LTC")) {
            prices = DataUtil.doubleTwo(price);
        } else if (currency.equals("GPCOIN")) {
            prices = DataUtil.doubleThree(price);
        }
        return prices;
    }

    /**
     * 大于零小于一百万
     */
    public static boolean isDoubleEt(double amount) {
        boolean status = true;
        if (amount == 0.00 || amount > 1000000.00) {
            status = false;
        }
        return status;
    }

    /**
     * 小数点后有几位
     */
    public static int isDoubleEts(String amount) {
        String str = amount + "";
        int index = str.indexOf(".");
        int length = 0;
        if (index > 0) {
            String strs = str.substring(index, str.length());
            Log.e("小数点后位置", strs);
            length = strs.length() - 1;
        }
        return (length);
    }

    public static int dipToPix(Context context, float dip) {
        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics());
        return size;
    }

    //判断微信是否可用
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        // 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        // 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * Md5字符加密
     */
    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    @SuppressLint("ResourceAsColor")
    public static String photoDown(Context context, Bitmap cachebmp, String filePath) {

        FileOutputStream fos;
        String imagePath = "";
        try {
            // 判断手机设备是否有SD卡
            boolean isHasSDCard = Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED);
            if (isHasSDCard) {
                // SD卡根目录
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
        return imagePath;
    }


    /**
     * Bitmap图片切换圆型
     */
    public static Bitmap createCircleBitmap(Bitmap resource) {
        //获取图片的宽度
        int width = resource.getWidth();
        Paint paint = new Paint();
        //设置抗锯齿
        paint.setAntiAlias(true);

        //创建一个与原bitmap一样宽度的正方形bitmap
        Bitmap circleBitmap = Bitmap.createBitmap(width, width, Bitmap.Config.ARGB_8888);
        //以该bitmap为低创建一块画布
        Canvas canvas = new Canvas(circleBitmap);
        //以（width/2, width/2）为圆心，width/2为半径画一个圆
        canvas.drawCircle(width / 2, width / 2, width / 2, paint);

        //设置画笔为取交集模式
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //裁剪图片
        canvas.drawBitmap(resource, 0, 0, paint);

        return circleBitmap;
    }

    /**
     * Bitmap图片切换圆角
     * roundPx圆角的角度值float;
     */
    public static Bitmap getOvalBitmap(Bitmap bitmap) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

//        final int color = 0xff424242;
        final int color = 0xffffffff;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth() * 2, bitmap.getHeight() * 2);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        paint.setStrokeWidth(6f);

        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap bitmaps = null;

    public static Bitmap returnBitMap(Context context, final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkGo.get(url)//
                        .tag(context)//
                        .execute(new BitmapCallback() {
                            @Override
                            public void onSuccess(Bitmap bitmap, Call call, Response response) {
                                // bitmap 即为返回的图片数据
                                if (bitmap != null) {
                                    bitmaps = bitmap;
                                }
                            }
                        });
            }
        }).start();
        return bitmaps;
    }

    private static final int MY_PERMISSIONS = 3;

    /**
     * 申请权限
     */
    public static void initPermission(Activity activity, String[] permissions) {
        List<String> mPermissionList = new ArrayList<>();
        mPermissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if (mPermissionList.isEmpty()) {//未授予的权限为空，表示都授予了
        } else {//请求权限方法
            permissions = mPermissionList.toArray(new String[mPermissionList.size()]);//将List转为数组
            ActivityCompat.requestPermissions(activity, permissions, MY_PERMISSIONS);
        }
    }

    /**
     * 申请权限
     */
    public static boolean isPermission(Activity activity, String[] permissions) {
        boolean status = false;
        List<String> mPermissionList = new ArrayList<>();
        mPermissionList.clear();
        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(activity, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                mPermissionList.add(permissions[i]);
            }
        }
        if (mPermissionList.isEmpty()) //未授予的权限为空，表示都授予了
            status = true;
        else
            status = false;
        return status;
    }

    /**
     * 判断字符串是否是数字
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("^[+]?(([0-9]+)([.]([0-9]+))?|(([0-9]+)[.]([0-9]+))?)$");
//        Log.e("判断字符串是否是数字",pattern.matcher(str).matches()+"");
        return pattern.matcher(str).matches();
    }

    /**
     * 字母转换为大写
     */
    public static String toUpper(String str) {
        return str.toUpperCase();
    }

    /**
     * 字母转换为小写
     */
    public static String toLower(String str) {
        return str.toLowerCase();
    }

    /**
     * EditText输入框，输入浮点数字校验。整数最多8位，小数点后保留2位。
     */
    public static void checkEditInputNumberTwo(EditText editText) {
        String str = editText.getText().toString();
        if (str != null && !str.isEmpty()) {
            if (str.charAt(0) == '.') {         //首位输入.则忽略
                editText.setText("");
            } else if (str.charAt(0) == '0') {     //首位输入0，则第二位必须是.其他忽略
                if (str.length() >= 2) {
                    if (str.charAt(1) != '.') {
                        editText.setText("0");
                        editText.setSelection(1);
                    } else {                      //首位输入0，第二位输入.保留小数点后2位
                        if (str.length() > 4) {
                            str = str.substring(0, 4);
                            editText.setText(str);
                            editText.setSelection(4);
                        }
                    }
                }
            } else {          //首位不是0，也不是.
                if (!str.contains(".")) {  //不含小数
                    if (str.length() > 8) {//保留8位整数
                        str = str.substring(0, 8);
                        editText.setText(str);
                        editText.setSelection(8);
                    }
                } else {  //含小数   且必定长度在8以内
                    int index = str.indexOf(".") + 3;
                    if (str.length() > index) {
                        str = str.substring(0, index);
                        editText.setText(str);
                        editText.setSelection(index);
                    }
                }
            }
        }
    }

    /**
     * EditText输入框，输入浮点数字校验。整数最多8位，小数点后保留3位。
     */
    public static void checkEditInputNumberThree(EditText editText) {
        String str = editText.getText().toString();
        if (str != null && !str.isEmpty()) {
            if (str.charAt(0) == '.') {         //首位输入.则忽略
                editText.setText("");
            } else if (str.charAt(0) == '0') {     //首位输入0，则第二位必须是.其他忽略
                if (str.length() >= 2) {
                    if (str.charAt(1) != '.') {
                        editText.setText("0");
                        editText.setSelection(1);
                    } else {                      //首位输入0，第二位输入.保留小数点后2位
                        if (str.length() > 5) {
                            str = str.substring(0, 5);
                            editText.setText(str);
                            editText.setSelection(5);
                        }
                    }
                }
            } else {          //首位不是0，也不是.
                if (!str.contains(".")) {  //不含小数
                    if (str.length() > 8) {//保留8位整数
                        str = str.substring(0, 8);
                        editText.setText(str);
                        editText.setSelection(8);
                    }
                } else {  //含小数   且必定长度在8以内
                    int index = str.indexOf(".") + 4;
                    if (str.length() > index) {
                        str = str.substring(0, index);
                        editText.setText(str);
                        editText.setSelection(index);
                    }
                }
            }
        }
    }

    /**
     * EditText输入框，输入浮点数字校验。整数最多8位，小数点后保留4位。
     */
    public static void checkEditInputNumber(EditText editText) {
        String str = editText.getText().toString();
        if (str != null && !str.isEmpty()) {
            if (str.charAt(0) == '.') {         //首位输入.则忽略
                editText.setText("");
            } else if (str.charAt(0) == '0') {     //首位输入0，则第二位必须是.其他忽略
                if (str.length() >= 2) {
                    if (str.charAt(1) != '.') {
                        editText.setText("0");
                        editText.setSelection(1);
                    } else {                      //首位输入0，第二位输入.保留小数点后4位
                        if (str.length() > 6) {
                            str = str.substring(0, 6);
                            editText.setText(str);
                            editText.setSelection(6);
                        }
                    }
                }
            } else {          //首位不是0，也不是.
                if (!str.contains(".")) {  //不含小数
                    if (str.length() > 8) {//保留8位整数
                        str = str.substring(0, 8);
                        editText.setText(str);
                        editText.setSelection(8);
                    }
                } else {  //含小数   且必定长度在8以内
                    int index = str.indexOf(".") + 5;
                    if (str.length() > index) {
                        str = str.substring(0, index);
                        editText.setText(str);
                        editText.setSelection(index);
                    }
                }
            }
        }
    }

    public static void checkEditInputNumber(String currency, EditText editText) {
        if (editText != null) {
            if (currency.equals("GPCOIN"))
                checkEditInputNumberThree(editText);
            else
                checkEditInputNumber(editText);
        }
    }

    public static void hideJianPan(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
        if (isOpen && editText != null)
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0); //强制隐藏键盘
    }

    public static void setFocusTv(TextView tv) {
        if (tv != null) {
            tv.setFocusable(true);
            tv.setFocusableInTouchMode(true);
            tv.requestFocus();
            tv.requestFocusFromTouch();
        }
    }

    public static void setFocusIv(ImageView iv) {
        if (iv != null) {
            iv.setFocusable(true);
            iv.setFocusableInTouchMode(true);
            iv.requestFocus();
            iv.requestFocusFromTouch();
        }
    }

    @SuppressLint("NewApi")
    public static String returnLanguage(Context context) {
//        getLocale().toLanguageTag()
        String language = Locale.CHINA.toLanguageTag();
        String lan = returnLanguuage(context);
        if (lan != null && lan.equals("ko")) {
            language = Locale.KOREA.toLanguageTag();
        } else if (lan != null && lan.equals("zh")) {
            language = Locale.CHINA.toLanguageTag();
        } else if (lan != null && lan.equals("en")) {
            language = Locale.ENGLISH.toLanguageTag();
        } else if (lan != null && lan.equals("ja")) {
            language = Locale.JAPAN.toLanguageTag();
        }
//        Log.e("language","语言====="+language);
        return language;
    }

    public static void setLanguage(Context context) {
        if (context != null) {
            String language = LanguageUtils.init(context).getParameter();
            if (language != null) {
                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                Configuration configuration = LanguageUtils.returnConfigration(context, language);
                context.getResources().updateConfiguration(configuration, metrics);
            } else {
                LanguageUtils.init(context).setParameter("zh");
            }
        }
    }

    public static String returnLanguuagePrice(Context context, String prices) {
        String price = numberTwo(Double.parseDouble(prices));
        if (context != null) {
            String language = LanguageUtils.init(context).getParameter();
            if (language != null) {
                if (language.equals("ko")) {
                    price = numberTwo(Double.parseDouble(prices) / MainActivity.usd * MainActivity.krw);
                } else if (language.equals("ja")) {
                    price = numberTwo(Double.parseDouble(prices) / MainActivity.usd * MainActivity.jpy);
                } else if (language.equals("en")) {
                    price = numberTwo(Double.parseDouble(prices) / MainActivity.usd);
                } else if (language.equals("zh")) {
                    price = numberTwo(Double.parseDouble(prices));
                }
            }
        }
        return price;
    }

    public static String returnLanguuagePriceFour(Context context, String prices) {
        String price = numberFour(Double.parseDouble(prices));
        if (context != null) {
            String language = LanguageUtils.init(context).getParameter();
            if (language != null) {
                if (language.equals("ko")) {
                    price = numberFour(Double.parseDouble(prices) / MainActivity.usd * MainActivity.krw);
                } else if (language.equals("ja")) {
                    price = numberTwo(Double.parseDouble(prices) / MainActivity.usd * MainActivity.jpy);
                } else if (language.equals("en")) {
                    price = numberTwo(Double.parseDouble(prices) / MainActivity.usd);
                } else if (language.equals("zh")) {
                    price = numberTwo(Double.parseDouble(prices));
                }
            }
        }
        return price;
    }

    public static String returnLanguuagePrice1(Context context, String currncy, String prices) {
        String price = "￥" + transfers(context, Double.parseDouble(returnCurrencyDouble(currncy, Double.parseDouble(prices))));
        if (context != null) {
            String language = LanguageUtils.init(context).getParameter();
            if (language != null) {
                if (language.equals("ko")) {
                    price = "₩" + transfers(context, Double.parseDouble(returnCurrencyDouble(currncy, Double.parseDouble(prices) / MainActivity.usd * MainActivity.krw)));
                } else if (language.equals("ja")) {
                    price = "JPY￥" + transfers(context, Double.parseDouble(returnCurrencyDouble(currncy, Double.parseDouble(prices) / MainActivity.usd * MainActivity.jpy)));
                } else if (language.equals("en")) {
                    price = "$" + transfers(context, Double.parseDouble(returnCurrencyDouble(currncy, Double.parseDouble(prices) / MainActivity.usd)));
                } else if (language.equals("zh")) {
                    price = "￥" + transfers(context, Double.parseDouble(returnCurrencyDouble(currncy, Double.parseDouble(prices))));
                }
            }
        }
        return price;
    }

    public static String returnLanguuagePrice2(Context context, String prices) {
        String price = "≈￥" + transfers(context, Double.parseDouble(numberTwo(Double.parseDouble(prices))));
        if (context != null) {
            String language = LanguageUtils.init(context).getParameter();
            if (language != null) {
                if (language.equals("ko")) {
                    price = "≈₩" + transfers(context, Double.parseDouble(numberTwo(Double.parseDouble(prices) / MainActivity.usd * MainActivity.krw)));
                } else if (language.equals("ja")) {
                    price = "≈JPY￥" + transfers(context, Double.parseDouble(numberTwo(Double.parseDouble(prices) / MainActivity.usd * MainActivity.jpy)));
                } else if (language.equals("en")) {
                    price = "≈$" + transfers(context, Double.parseDouble(numberTwo(Double.parseDouble(prices) / MainActivity.usd)));
                } else if (language.equals("zh")) {
                    price = "≈￥" + transfers(context, Double.parseDouble(numberTwo(Double.parseDouble(prices))));
                }
            }
        }
        return price;
    }

    public static String returnLanguuage(Context context) {
        setLanguage(context);
        return LanguageUtils.init(context).getParameter();
    }

    public static String returnLanguuageName(Context context) {
        String language = returnLanguuage(context);
        String languageName = "简体中文";
        if (language.equals("zh")) {
            languageName = "简体中文";
        } else if (language.equals("en")) {
            languageName = "English";
        } else if (language.equals("it")) {
            languageName = "Italiano";
        } else if (language.equals("es")) {
            languageName = "Español";
        } else if (language.equals("de")) {
            languageName = "Deutsch";
        } else if (language.equals("nl")) {
            languageName = "Nederlands";
        } else if (language.equals("ar")) {
            languageName = "العربية";
        } else if (language.equals("pt_br")) {
            languageName = "Português(Brasil)";
        } else if (language.equals("ko")) {
            languageName = "한국어";
        } else if (language.equals("fr")) {
            languageName = "Français";
        } else if (language.equals("ru")) {
            languageName = "Русский";
        } else if (language.equals("ja")) {
            languageName = "日本語";
        }
        return languageName;
    }

    public static void setTokenIcon(ImageView image, String currency) {
        if (image != null && currency != null) {
            currency = toUpper(currency);
            if (currency.equals("BTC")) {
                image.setImageResource(R.mipmap.icon_wallet_btc);
            } else if (currency.equals("ETH")) {
                image.setImageResource(R.mipmap.icon_wallet_eth);
            } else if (currency.equals("USDT")) {
                image.setImageResource(R.mipmap.icon_wallet_usdt);
            } else if (currency.equals("EOS")) {
                image.setImageResource(R.mipmap.icon_wallet_eos);
            } else if (currency.equals("LTC")) {
                image.setImageResource(R.mipmap.icon_wallet_ltc);
            } else if (currency.equals("XRP")) {
                image.setImageResource(R.mipmap.icon_wallet_xrp);
            } else if (currency.equals("BCH")) {
                image.setImageResource(R.mipmap.icon_wallet_bch);
            } else if (currency.equals("ETC")) {
                image.setImageResource(R.mipmap.icon_wallet_etc);
            } else if (currency.equals("BSV")) {
                image.setImageResource(R.mipmap.icon_wallet_bsv);
            }
        }
    }

    public static void stopRefersh(SwipeRefreshLayout refreshLayout) {
        if (refreshLayout != null)
            refreshLayout.setRefreshing(false);
    }

    public static void stopRefersh(SmartRefreshLayout refersh) {
        if (refersh != null) {
            refersh.finishRefresh();
            refersh.finishLoadMore();
        }
    }

    public static void setRefershTheme(Context context, SmartRefreshLayout refreshLayout) {
        if (refreshLayout != null) {
//            refreshLayout.setRefreshHeader(new WaterDropHeader(context));//苹果
            refreshLayout.setRefreshHeader(new MaterialHeader(context));//Swi
            refreshLayout.setRefreshFooter(new ClassicsFooter(context));
        }
    }

    public static void setRefersh(SmartRefreshLayout refersh) {
        if (refersh != null) {
            refersh.setEnableRefresh(true);
            refersh.setEnableLoadMore(true);
            refersh.setDisableContentWhenRefresh(true);
            refersh.setDisableContentWhenLoading(true);
            refersh.setEnableLoadMoreWhenContentNotFull(false);
        }
    }

    public static void setRefershNoLoad(SmartRefreshLayout refersh) {
        if (refersh != null) {
            refersh.setEnableRefresh(true);
            refersh.setEnableLoadMore(false);
            refersh.setDisableContentWhenRefresh(true);
//            refersh.setDisableContentWhenLoading(true);
//            refersh.setEnableLoadMoreWhenContentNotFull(false);
        }
    }

    public static void noLogin(Context context) {
        loginFailure();
    }

    public static void noLogin(Context context, String message) {
        if (message.equals("未登录") || message.equals("登录失效")) {
            loginFailure();
        } else {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 未登录或登录失效
     */
    public static void loginFailure() {
        MobclickAgent.onProfileSignOff();
        StoreUtils.init(AppManager.getAppManager().currentActivity()).logout();
        AppManager.getAppManager().currentActivity().finish();
        LoginActivity.startAction(AppManager.getAppManager().currentActivity(), Constant.FAILURE);
    }

    public static void startActivity(Context context, Class activity) {
        if (StoreUtils.init(context).getLoginUser() != null) {
            context.startActivity(new Intent(context, activity));
        } else {
            loginFailure();
        }
    }

    public static void startActivity(Context context, Class activity, Bundle bundle) {
        if (StoreUtils.init(context).getLoginUser() != null) {
            Intent intent = new Intent(context, activity);
            intent.putExtra(Constant.BUNDLE, bundle);
            context.startActivity(intent);
        } else {
            loginFailure();
        }
    }

    public static String returnNoNullHint(Context context, String str) {
        String hint = "";
        if (context != null) {
            String noNullHint = context.getResources().getString(R.string.no_null_hint);
            hint = noNullHint + str;
        }
        return hint;
    }

    public static String returnNumberNoHint(Context context, String str) {
        String hint = "";
        if (context != null) {
//            hint = String.format(noNullHint, str);
            hint = context.getString(R.string.number_no_hint, str);
        }
        return hint;
    }

    /**
     * @param context
     * @param phone   手机号
     * @return
     */
    public static String returnSendCodeHint(Context context, String phone) {
        String hint = "";
        if (context != null) {
            hint = context.getString(R.string.send_sms_code_hint, phone);
        }
        return hint;
    }

    public static int setTokenId(String currency, int position) {
        int tokenId = position + 20;
        if (currency.equals("ETH")) {
            tokenId = 15;
        } else if (currency.equals("USDT")) {
            tokenId = 16;
        } else if (currency.equals("BTC")) {
            tokenId = 17;
        } else if (currency.equals("EOS")) {
            tokenId = 18;
        } else if (currency.equals("LTC")) {
            tokenId = 19;
        } else if (currency.equals("XRP")) {
            tokenId = 20;
        } else if (currency.equals("BCH")) {
            tokenId = 21;
        } else if (currency.equals("BSV")) {
            tokenId = 22;
        } else if (currency.equals("ETC")) {
            tokenId = 23;
        } else if (currency.equals("BNB")) {
            tokenId = 24;
        } else if (currency.equals("OKB")) {
            tokenId = 25;
        }
        return tokenId;
    }

    /**
     * 安装Apk
     */
    public static void installApk(Context context, String apkFilePath, String apkFileName) {
        File installFile = new File(apkFilePath, apkFileName);
        if (installFile != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // 给目标应用一个临时授权
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context, Constant.FILEPROVIDER, installFile);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(installFile), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        }
    }

    public static String returnPhoneHint(String phone) {
        String phones = phone;
        if (phones != null && !phones.isEmpty()) {
            String before = phone.substring(0, 3);
            String after = phone.substring(phone.length() - 4, phone.length());
            phones = before + "****" + after;
        }
        return phones;
    }

    public static double returnMaxNumber(List<Double> list) {
        double sum = list.get(0);//假设第一个元素是最大值
        //for循环遍历数组中元素，每次循环跟数组索引为0的元素比较大小
        for (double d : list) {
            if (sum < d) {//数组中的元素跟sum比较，比sum大就把它赋值给sum作为新的比较值
                sum = d;
            }
        }
        return sum;
    }

    public static String returnImageName(String imgUrl) {
        int indexOf = imgUrl.lastIndexOf("/");
        int lastIndexOf = imgUrl.lastIndexOf(".");
        if (lastIndexOf <= indexOf)
            lastIndexOf = imgUrl.length();
        return imgUrl.substring(indexOf, lastIndexOf) + ".png";
    }

    public static String returnVideoName(String videoUrl) {
        int indexOf = videoUrl.lastIndexOf("/");
        int lastIndexOf = videoUrl.lastIndexOf(".");
        if (lastIndexOf <= indexOf)
            lastIndexOf = videoUrl.length();
        return videoUrl.substring(indexOf, lastIndexOf) + ".mp4";
    }

    static int code = 404;

    public static int down(Context context, String url, String filePath, String name) {

        try {
            Api.getInstance().downLoadFile(context, url, new FileCallback(filePath, name) {

                @Override
                public void downloadProgress(long currentSize, long totalSize, float progres, long networkSpeed) {
                    super.downloadProgress(currentSize, totalSize, progres, networkSpeed);
                }

                @Override
                public void onSuccess(File file, Call call, Response response) {
                    code = 200;
                }

                @Override
                public void onError(Call call, Response response, Exception e) {
                    super.onError(call, response, e);
                    code = 404;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code;
    }

    public static void copyString(Context context, TextView tv) {
        // 将文本内容放到系统剪贴板里。
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(tv.getText().toString().trim());
        Toast.makeText(context, context.getResources().getString(R.string.copy_ok), Toast.LENGTH_SHORT).show();
    }

    public static void copyString(Context context, String tv) {
        // 将文本内容放到系统剪贴板里。
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cm.setText(tv.trim());
        Toast.makeText(context, context.getResources().getString(R.string.copy_ok), Toast.LENGTH_SHORT).show();
    }

    public static void setUserGrade(String grade, ImageView imageView) {
        if (imageView != null) {
            if (grade.equals(STAR_ZERO.name())) {
                imageView.setImageResource(R.mipmap.icon_grade_zero);
            } else if (grade.equals(STAR_ONE.name())) {
                imageView.setImageResource(R.mipmap.icon_grade_one);
            } else if (grade.equals(STAR_TWO.name())) {
                imageView.setImageResource(R.mipmap.icon_grade_two);
            } else if (grade.equals(STAR_THREE.name())) {
                imageView.setImageResource(R.mipmap.icon_grade_three);
            } else if (grade.equals(STAR_FOUR.name())) {
                imageView.setImageResource(R.mipmap.icon_grade_four);
            } else if (grade.equals(STAR_FIVE.name())) {
                imageView.setImageResource(R.mipmap.icon_grade_five);
            } else if (grade.equals(STAR_SIX.name())) {
                imageView.setImageResource(R.mipmap.icon_grade_six);
            }
        }
    }

    public static CfdTypeEnum getCfdTypeEnum(String type) {
        CfdTypeEnum cfdTypeEnum = CfdTypeEnum.DOUBLE;
        if (type != null && !type.equals("") && Integer.parseInt(type) == CfdTypeEnum.FREE.grade) {
            cfdTypeEnum = CfdTypeEnum.FREE;
        }
        return cfdTypeEnum;
    }
}
