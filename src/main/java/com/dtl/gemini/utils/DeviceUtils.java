package com.dtl.gemini.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

/**
 * 类说明
 * 设备信息获取工具类
 */
public class DeviceUtils {

    private static TelephonyManager getTelephonyManager(Context ctx) {
        if (ctx == null) {
            return null;
        }
        return (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * @Title: getDeviceId 
     * @Description: TODO(获取设备ID) 
     * @param ctx
     * @return    
     * String    返回类型 
     * @date 2014年1月16日 下午1:30:12
     */
    public static String getDeviceId(Context ctx) {
        TelephonyManager tm = getTelephonyManager(ctx);
        if (tm == null) {
            return "";
        }
        @SuppressLint("MissingPermission") String deviceId = tm == null ? "" : tm.getDeviceId();
        if (!TextUtils.isEmpty(deviceId)) {
            return deviceId;
        }
        //虚拟一个设备id
        deviceId = UUID.randomUUID().toString();
        if (deviceId != null && deviceId.length() > 32) {
            deviceId = deviceId.substring(0, 32);
        }
        return deviceId;
    }

//    /**
//     * @Title: getDeviceId
//     * @Description: TODO(获取设备操作系统版本)
//     * @param ctx
//     * @return
//     * String    返回类型
//     * @date 2014年1月16日 下午1:30:12
//     */
//    public static String getOSVersion(Context ctx) {
//    	TelephonyManager tm = getTelephonyManager(ctx);
//    	if (tm == null) {
//    		return "";
//    	}
//    	String osVersion = tm == null ? "" : tm.getDeviceSoftwareVersion();
//    	return osVersion;
//    }

    // -1：无网络 1：wifi 2：2g 3：3g 4：4g
    public static int getCurrentNetType(Context context) {
        int type = -1;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return type;
        }
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return type;
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            type = 1;
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int subType = info.getSubtype();
            if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS
                    || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                type = 2;
            } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_A
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                type = 3;
            } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {
                type = 4;
            }
        }
        return type;
    }

    /**
     * @Title: getServiceProvider 
     * @Description: TODO(获取当前设备运营商类型 1：移动、2：电信、3：联通、4：其他) 
     * @param ctx
     * @return    
     * int    返回类型 
     * @date 2014年1月16日 下午1:37:27
     */
    public static int getServiceProvider(Context ctx) {
        TelephonyManager tm = getTelephonyManager(ctx);
        if (tm == null) {
            return 4;
        }
        String networkOperator = tm.getNetworkOperator();
        if (networkOperator == null || ("").equals(networkOperator)) {
            return 4;
        }
        String mnc = networkOperator.substring(3);
        // 中移动的mnc id为00
        int type = -1;
        if (("00").equals(mnc)) {
            type = 1;
        } else if (("01").equals(mnc)) { // 中联通的mnc id为01
            type = 3;
        } else if (("03").equals(mnc)) { // 中电信的mnc id为03
            type = 2;
        }
        return type;
    }

    /**
     * @Title: getModel 
     * @Description: TODO(获取设备型号) 
     * @return    
     * String    返回类型 
     * @date 2014年1月16日 下午1:36:47
     */
    public static String getModel() {
        return Build.MODEL;
    }

    /**
     * @Title: getBrand
     * @Description: TODO(获取手机厂商 )
     * @return
     * String    返回类型
     * @date 2014年1月16日 下午1:36:47
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /***
     * @Title: getIPAddress
     * @Description: TODO(获取ip 地址 )
     * @param context
     * @return
     */
    public static String getIPAddress(Context context) {
        NetworkInfo info = ((ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    /**
     * 将得到的int类型的IP转换为String类型     *
     * @param ip
     * @return
     */
    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }

    /**
     * 获取当前手机系统版本号
     *
     * @return  系统版本号
     */
    public static String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取ip地址方法
     * @return
     */
    public static String GetHostIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
                        .hasMoreElements();) {
                    InetAddress inetAddress = ipAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        //if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet6Address) {
                        return inetAddress.getHostAddress().toString();
                    }

                }
            }
        } catch (SocketException ex) {
        } catch (Exception e) {
        }
        return null;
    }
}
