package com.dtl.gemini.utils;

import java.util.regex.Pattern;

/**
 * 正则
 *
 * @author DTL
 * @date 2020/4/14
 **/
public class RegularUtil {
    /**
     * @param username 昵称
     * @return
     */
    public static boolean valiUsername(String username) {
        //包含数字、字母（不区分大小写）、汉字的正则表达式，这里还限制了长度1~10
        String regex = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{1,10}$";
        return matches(regex, username);
    }

    /**
     * @param pwd 密码
     * @return
     */
    public static boolean valiPwd(String pwd) {
        //首字母，其它为字母或数字 16位
        String regex = "^[a-zA-Z][a-zA-Z0-9]{7,16}$";
        return matches(regex, pwd);
    }

    /**
     * @param pwd 资金密码
     * @return
     */
    public static boolean valiAssetPwd(String pwd) {
        //首字母，其它为字母或数字 16位
        String regex = "^[a-zA-Z][a-zA-Z0-9]{7,16}$";
        return matches(regex, pwd);
    }

    /**
     * @param regex      正则
     * @param inputValue 字符串
     * @return 符合返回true，不符合返回false
     */
    public static boolean matches(String regex, String inputValue) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(inputValue).matches();
    }
}
