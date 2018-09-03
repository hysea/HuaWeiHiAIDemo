package com.hysea.huaweiaidemo;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.View;

import com.hysea.huaweiaidemo.asr.AsrActivity;
import com.hysea.huaweiaidemo.scene.SceneActivity;

/**
 * 华为HiAI Engine测试demo
 */
public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        tipDialog("提示", R.string.mobile_environment);
    }

    /**
     * 场景识别
     */
    public void toScene(View view) {
        if (isSupportHiAI()) {
            Intent intent = new Intent(this, SceneActivity.class);
            startActivity(intent);
        } else {
            showToast(R.string.no_support_hiai);
        }
    }


    /**
     * 语音识别
     */
    public void toAsr(View view) {
        if (isSupportHiAI()) {
            Intent intent = new Intent(this, AsrActivity.class);
            startActivity(intent);
        } else {
            showToast(R.string.no_support_hiai);
        }
    }


    /**
     * 是否支持HiAI
     */
    private boolean isSupportHiAI() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo("com.huawei.hiai", 0);
            return packageInfo != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
