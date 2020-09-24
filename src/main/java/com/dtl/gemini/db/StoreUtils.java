package com.dtl.gemini.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.dtl.gemini.model.User;

public class StoreUtils {


    private static StoreUtils utils;

    Context context;
    SharedPreferences preferences;
    SharedPreferences userSp;

    private StoreUtils(Context context) {
        this.context = context;
        preferences = context.getSharedPreferences("store", Context.MODE_PRIVATE);
        userSp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
    }

    public static StoreUtils init(Context context) {
        if (utils == null) {
            utils = new StoreUtils(context);
        }
        return utils;
    }

    public void setParameter(String key, String value) {
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(key, value);
        edit.commit();
    }

    public String getParameter(String key) {
        return preferences.getString(key, null);
    }

    /***
     * 检查是否为第一次,如果是修改为不是
     * @param key
     * @param value
     */
    public boolean checkFirst(String key, String value) {
        String v = preferences.getString(key, null);
        if (v == null || !v.equals(value)) {
            SharedPreferences.Editor edit = preferences.edit();
            edit.putString(key, value);
            edit.commit();
            return true;
        }
        return false;
    }

    public void setToken(String token) {
        SharedPreferences.Editor edit = userSp.edit();
        edit.putString("token", token);
        edit.commit();
    }

    public String getToken() {
        return userSp.getString("token", null);
    }

    public void setRongToken(String token) {
        SharedPreferences.Editor edit = userSp.edit();
        edit.putString("rongToken", token);
        edit.commit();
    }

    public String getRongToken() {
        return userSp.getString("rongToken", null);
    }

    public void storeUser(User user) {
        SharedPreferences.Editor edit = userSp.edit();
        if (user.getUsername() != null) edit.putString("curr_username", user.getUsername());
        if (user.getHeadUrl() != null)
            edit.putString("curr_head_url", user.getHeadUrl().toString());
        if (user.getCreateDateTime() != null)
            edit.putString("curr_createdatetime", user.getCreateDateTime());
        if (user.getLastLoginDateTime() != null)
            edit.putString("curr_lastlogindatetime", user.getLastLoginDateTime());
        if (user.getToken() != null)
            edit.putString("token", user.getToken());
        if (user.getPhone() != null)
            edit.putString("curr_phone", user.getPhone());
        if (user.getGrade() != null)
            edit.putString("curr_grade", user.getGrade());
        if (user.getDirectPushNum() != null)
            edit.putInt("curr_direct_push_num", user.getDirectPushNum());
        if (user.getInvitationCode() != null)
            edit.putString("curr_invitation_code", user.getInvitationCode());
        if (user.getRegisterInvitationCode() != null)
            edit.putString("curr_register_invitation_code", user.getRegisterInvitationCode());
        edit.commit();
    }

    public void storeUserHead(String head) {
        if (head != null) {
            SharedPreferences.Editor edit = userSp.edit();
            edit.putString("curr_head_url", head);
            edit.commit();
        }
    }

    public void storeUsername(String username) {
        if (username != null && !username.equals("")) {
            SharedPreferences.Editor edit = userSp.edit();
            edit.putString("curr_username", username);
            edit.commit();
        }
    }

    public User getLoginUser() {
        String name = userSp.getString("curr_username", null);
        if (name != null && !name.equals("")) {
            User user = new User();
            user.setUsername(name);
            user.setHeadUrl(userSp.getString("curr_head_url", null));
            user.setCreateDateTime(userSp.getString("curr_createdatetime", null));
            user.setLastLoginDateTime(userSp.getString("curr_lastlogindatetime", null));
            user.setToken(userSp.getString("token", null));
            user.setPhone(userSp.getString("curr_phone", null));
            user.setGrade(userSp.getString("curr_grade", null));
            user.setDirectPushNum(userSp.getInt("curr_direct_push_num", 0));
            user.setInvitationCode(userSp.getString("curr_invitation_code", null));
            user.setRegisterInvitationCode(userSp.getString("curr_register_invitation_code", null));
            return user;
        } else {
            return null;
        }
    }

    public void logout() {
        SharedPreferences.Editor edit = userSp.edit();
        edit.clear();
        edit.commit();
    }

}
