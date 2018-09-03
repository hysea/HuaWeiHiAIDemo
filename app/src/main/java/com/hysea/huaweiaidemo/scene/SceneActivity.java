package com.hysea.huaweiaidemo.scene;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huawei.hiai.vision.common.ConnectionCallback;
import com.huawei.hiai.vision.common.VisionBase;
import com.huawei.hiai.vision.image.detector.SceneDetector;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.image.detector.Scene;
import com.hysea.huaweiaidemo.BaseActivity;
import com.hysea.huaweiaidemo.R;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

/**
 * 场景识别
 */
public class SceneActivity extends BaseActivity {
    // 场景类型字段
    private static final String UNKNOWN = "Unknown";
    private static final String UN_SUPPORT = "UnSupport";
    private static final String BEACH = "沙滩";
    private static final String BLUE_SKY = "蓝天";
    private static final String SUNSET = "落日";
    private static final String FOOD = "食物";
    private static final String FLOWER = "花卉";
    private static final String GREEN_PLANT = "雪景";
    private static final String SNOW = "雪景";
    private static final String NIGHT = "夜晚";
    private static final String TEXT = "文字";
    private static final String STAGE = "舞台";
    private static final String CAT = "猫";
    private static final String DOG = "狗";
    private static final String FIREWORK = "焰火";
    private static final String OVERCAST = "阴天";
    private static final String FALLEN = "落叶";
    private static final String PANDA = "熊猫";
    private static final String CAR = "汽车";
    private static final String OLD_BUILDINGS = "旧建筑";
    private static final String BICYCLE = "自行车";
    private static final String WATERFALL = "瀑布";

    private static final int MSG_SERVICE_CONNECTED = 1;
    private static final int MSG_SERVICE_DISCONNECTED = 2;
    private static final int MSG_SCENE = 3;
    private static final int MSG_SHOW_SCENE = 4;


    // 场景数组，必须按如下排序
    private String mSceneArr[] = {
            UNKNOWN, UN_SUPPORT, BEACH, BLUE_SKY, SUNSET, FOOD, FLOWER, GREEN_PLANT, SNOW, NIGHT,
            TEXT, STAGE, CAT, DOG, FIREWORK, OVERCAST, FALLEN, PANDA, CAR, OLD_BUILDINGS, BICYCLE, WATERFALL};


    @BindView(R.id.iv_show)
    ImageView mIvShow;
    @BindView(R.id.tv_show_result)
    TextView mTvShowResult;

    private Handler mSceneHandler;
    private SceneHandlerThread mSceneHandlerThread;
    private SceneDetector mSceneDetector;
    private Bitmap mBitmap;
    private String result = "";
    private RxPermissions mPermissions;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SERVICE_CONNECTED:
                    mIsConnected = true;
                    showToast("HiAI服务连接成功");
                    mSceneDetector = new SceneDetector(mContext);
                    break;
                case MSG_SERVICE_DISCONNECTED:
                    mIsConnected = false;
                    showToast("HiAI服务连接断开");
                    break;
                case MSG_SHOW_SCENE:
                    mTvShowResult.setText(result);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_scene;
    }

    @Override
    protected void init() {
        ButterKnife.bind(this);
        mSceneHandlerThread = new SceneHandlerThread();
        mSceneHandlerThread.start();
        mSceneHandler = new Handler(mSceneHandlerThread.getLooper(), mSceneHandlerThread);
        initVisionBase();
        mPermissions = new RxPermissions(this);
    }

    protected void initVisionBase() {
        VisionBase.init(this, new ConnectionCallback() {
            @Override
            public void onServiceConnect() {
                // 当与服务连接成功时，会调用此回调方法；
                // 可以在这里进行detector类的初始化、标记服务连接状态等
                Log.i(TAG, "current thread : " + Thread.currentThread().getName());
                mHandler.sendEmptyMessage(MSG_SERVICE_CONNECTED);
            }

            @Override
            public void onServiceDisconnect() {
                // 当与服务断开时，会调用此回调方法；
                // 可以选择在这里进行服务的重连，或者对异常进行处理；
                mHandler.sendEmptyMessage(MSG_SERVICE_DISCONNECTED);
            }
        });
    }

    /**
     * 拍照
     */
    @SuppressLint("CheckResult")
    public void openCamera(View view) {
        mPermissions.request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) {
                        if (granted) {
                            if (mIsConnected) {
                                PictureSelector.create(SceneActivity.this)
                                        .openCamera(PictureMimeType.ofImage())
                                        .forResult(PictureConfig.CHOOSE_REQUEST);
                            } else {
                                showToast("HiAI服务连接失败，请重新尝试");
                            }
                        }
                    }
                });

    }

    /**
     * 拍照
     */
    @SuppressLint("CheckResult")
    public void openGallery(View view) {
        mPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean granted) {
                        if (granted) {
                            if (mIsConnected) {
                                PictureSelector.create(SceneActivity.this)
                                        .openGallery(PictureMimeType.ofImage())
                                        .isCamera(false)
                                        .selectionMode(PictureConfig.SINGLE)
                                        .forResult(PictureConfig.CHOOSE_REQUEST);
                            } else {
                                showToast("HiAI服务连接失败，请重新尝试");
                            }
                        }
                    }
                });


    }

    public void openSceneType(View view) {
        tipDialog("场景类型", R.string.scene_desc);
    }

    public void openUseScene(View view) {
        tipDialog("应用场景", R.string.use_scene);
    }


    private class SceneHandlerThread extends HandlerThread implements Handler.Callback {
        public SceneHandlerThread() {
            super("SceneHandlerThread");
        }

        @Override
        public boolean handleMessage(Message msg) {
            // 构造Frame对象
            Frame frame = new Frame();
            frame.setBitmap(mBitmap);
            switch (msg.what) {
                case MSG_SCENE:
                    long startTime = System.currentTimeMillis();
                    // 进行场景检测
                    JSONObject obj = mSceneDetector.detect(frame, null);
                    if (obj == null) {
                        result = "error";
                    }
                    // 获取Java类形式的结果
                    Scene scene = mSceneDetector.convertResult(obj);
                    if (scene == null) {
                        break;
                    }
                    // 获取识别出来的场景类型
                    result = "result : " + getScene(scene.getType());
                    long end = System.currentTimeMillis();
                    Log.i(TAG, "scene need time:" + (end - startTime)); // 148ms
                    mHandler.sendEmptyMessage(MSG_SHOW_SCENE);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    /**
     * 获取场景
     *
     * @param type 场景类型
     */
    private String getScene(int type) {
        return mSceneArr[type];
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    List<LocalMedia> list = PictureSelector.obtainMultipleResult(data);
                    if (list != null && list.size() > 0) {
                        String filePath = list.get(0).getPath();
                        Log.i(TAG, "filePath : " + filePath);
                        Glide.with(mContext).load(filePath).into(mIvShow);
                        mBitmap = BitmapFactory.decodeFile(filePath);
                        if (mIsConnected) {
                            mSceneHandler.sendEmptyMessage(MSG_SCENE);
                        }
                    }
                    break;
            }
        }
    }


    @Override
    protected void onDestroy() {
        // 释放资源
        mSceneDetector.release();
        mHandler.removeCallbacksAndMessages(null);
        mSceneHandlerThread.quit();
        super.onDestroy();
    }
}
