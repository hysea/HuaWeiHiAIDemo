package com.hysea.huaweiaidemo;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

    }

    /**
     * 场景识别
     */
    public void toScene(View view) {
        Intent intent = new Intent(this, SceneActivity.class);
        startActivity(intent);
//        if (isSupportHiAI()) {
//            Intent intent = new Intent(this, SceneActivity.class);
//            startActivity(intent);
//        } else {
//            showToast("该手机暂不支持华为HiAI引擎");
//        }
    }



    /**
     * 场景识别
     */
    public void toAsr(View view) {
        if (isSupportHiAI()) {
            Intent intent = new Intent(this, SceneActivity.class);
            startActivity(intent);
        } else {
            showToast("该手机暂不支持华为HiAI引擎");
        }
    }


    /**
     * 是否支持HiAI
     */
    private boolean isSupportHiAI() {
        PackageManager packageManager = getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo("com.huawei.hiai", 0);
            Log.i(TAG, "Engine versionName: " + packageInfo.versionName + " ,versionCode: " + packageInfo.getLongVersionCode());
            if (packageInfo.getLongVersionCode() <= 801000300) {
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }
}
