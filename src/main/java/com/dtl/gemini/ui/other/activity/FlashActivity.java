package com.dtl.gemini.ui.other.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.widget.TextView;
import android.widget.Toast;

import com.dtl.gemini.MainActivity;
import com.dtl.gemini.PunkApplication;
import com.dtl.gemini.R;
import com.dtl.gemini.base.BaseAppActivity;
import com.dtl.gemini.db.LanguageUtils;
import com.dtl.gemini.model.MData;
import com.dtl.gemini.utils.AndroidUtil;
import com.dtl.gemini.utils.DataUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.NonNull;

import butterknife.Bind;

/**
 * 启动页
 */
public class FlashActivity extends BaseAppActivity {
    @Bind(R.id.app_versionName)
    TextView appVersionName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_flash;
    }

    @Override
    public void initPresenter() {
    }

    @Override
    public void initView() {
        appVersionName.setText(getResources().getString(R.string.version_name) + ": " + AndroidUtil.getVersionName(PunkApplication.getAppContext()));
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startToMain();
                        }
                    });
                } catch (Exception e) {
                }
            }
        }.start();

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            int i = 0;
            for (String pms : permissions) {
                if (pms.equals(Manifest.permission.READ_PHONE_STATE) && grantResults[i] == -1) {
                    Toast.makeText(this, getResources().getString(R.string.not_authorization), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }

                if (pms.equals(Manifest.permission.ACCESS_FINE_LOCATION) && grantResults[i] == -1) {
                    Toast.makeText(this, getResources().getString(R.string.not_authorization), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }

                if (pms.equals(Manifest.permission.ACCESS_COARSE_LOCATION) && grantResults[i] == -1) {
                    Toast.makeText(this, getResources().getString(R.string.not_authorization), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }

                if (pms.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[i] == -1) {
                    Toast.makeText(this, getResources().getString(R.string.not_authorization), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }

                if (pms.equals(Manifest.permission.READ_EXTERNAL_STORAGE) && grantResults[i] == -1) {
                    Toast.makeText(this, getResources().getString(R.string.not_authorization), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }
                if (pms.equals(Manifest.permission.SYSTEM_ALERT_WINDOW) && grantResults[i] == -1) {
                    Toast.makeText(this, getResources().getString(R.string.not_authorization), Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                }
                i++;
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startToMain() {
        String language = DataUtil.returnLanguuage(this);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Configuration configuration = LanguageUtils.returnConfigration(FlashActivity.this, language);
        getResources().updateConfiguration(configuration, metrics);
        LanguageUtils.init(this).delParameter();
        LanguageUtils.init(this).setParameter(language);
        Intent intent = null;
//        if (StoreUtils.init(this).getLoginUser() != null && StoreUtils.init(this).getToken() != null) {
            intent = new Intent(this, MainActivity.class);
//        } else {
//            intent = new Intent(this, LoginActivity.class);
//        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    @Override
    public void event(MData mData) {

    }

}
