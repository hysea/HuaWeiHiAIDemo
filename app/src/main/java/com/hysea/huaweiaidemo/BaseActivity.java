package com.hysea.huaweiaidemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.huawei.hiai.vision.common.ConnectionCallback;
import com.huawei.hiai.vision.common.VisionBase;

public abstract class BaseActivity extends AppCompatActivity {
    protected final String TAG = this.getClass().getSimpleName();
    protected Context mContext = this;

    protected boolean mIsConnected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mContext = this;
        init();
    }

    @LayoutRes
    protected abstract int getLayoutId();

    protected abstract void init();

    protected void tipDialog(String title, int resId) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(resId)
                .setPositiveButton("确定", null)
                .create()
                .show();
    }

    /**
     * 初始化VisionBase，连接服务
     */
    protected void initVisionBase() {
        VisionBase.init(this, new ConnectionCallback() {
            @Override
            public void onServiceConnect() {
                // 当与服务连接成功时，会调用此回调方法；
                // 可以在这里进行detector类的初始化、标记服务连接状态等
                showToast("HiAI服务连接成功");
                mIsConnected = true;
            }

            @Override
            public void onServiceDisconnect() {
                // 当与服务断开时，会调用此回调方法；
                // 可以选择在这里进行服务的重连，或者对异常进行处理；
                showToast("HiAI服务连接断开");
                mIsConnected = false;
            }
        });
    }

    protected void showToast(int resId) {
        showToast(getString(resId));
    }

    protected void showToast(String str) {
        Toast.makeText(mContext, str, Toast.LENGTH_SHORT).show();
    }
}
