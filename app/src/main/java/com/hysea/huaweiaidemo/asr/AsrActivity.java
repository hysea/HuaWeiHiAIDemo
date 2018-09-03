package com.hysea.huaweiaidemo.asr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.huawei.hiai.asr.AsrConstants;
import com.huawei.hiai.asr.AsrError;
import com.huawei.hiai.asr.AsrListener;
import com.huawei.hiai.asr.AsrRecognizer;
import com.hysea.huaweiaidemo.BaseActivity;
import com.hysea.huaweiaidemo.R;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.util.List;

/**
 * 语音识别
 *
 * @author hysea
 */
public class AsrActivity extends BaseActivity {


    private AsrRecognizer mAsrRecognizer;
    private MyAsrListener mAsrListener = new MyAsrListener();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_asr;
    }

    @Override
    protected void init() {
        initEngine(AsrConstants.ASR_SRC_TYPE_FILE);
    }


    public void openAudio(View view) {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofAudio())
                .selectionMode(PictureConfig.SINGLE)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }


    /**
     * 初始化引擎
     *
     * @param srcType：配置语音的输入来源 ASR_SRC_TYPE_FILE：语音输入来自文件；ASR_SRC_TYPE_RECORD：语音输入来自mic录入；
     */
    private void initEngine(int srcType) {
        if (mAsrRecognizer == null) {
            // 创建引擎
            mAsrRecognizer = AsrRecognizer.createAsrRecognizer(this);
        }
        Intent initIntent = new Intent();
        initIntent.putExtra(AsrConstants.ASR_AUDIO_SRC_TYPE, srcType);
        mAsrRecognizer.init(initIntent, mAsrListener);
    }

    /**
     * 开始识别
     *
     * @param srcType  源类型
     * @param filePath 文件路径
     */
    private void startListening(int srcType, String filePath) {
        Log.d(TAG, "startListening() " + "src_type:" + srcType);
        Intent intent = new Intent();
        if (srcType == AsrConstants.ASR_SRC_TYPE_FILE) {
            intent.putExtra(AsrConstants.ASR_SRC_FILE, filePath);
        }
        if (mAsrRecognizer != null) {
            mAsrRecognizer.startListening(intent);
        }
    }

    /**
     * 停止识别
     */
    private void stopListening() {
        // 停止识别
        if (mAsrRecognizer != null) {
            mAsrRecognizer.stopListening();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    // 图片、视频、音频选择结果回调
                    List<LocalMedia> list = PictureSelector.obtainMultipleResult(data);
                    if (list != null && list.size() > 0) {
                        String filePath = list.get(0).getPath();
                        startListening(AsrConstants.ASR_SRC_TYPE_FILE, filePath);
                    }


            }
        }
    }


    private class MyAsrListener implements AsrListener {

        @Override
        public void onInit(Bundle params) {
            Log.d(TAG, "onInit() called with: params = [" + params + "]");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d(TAG, "onBeginningOfSpeech() called");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
            Log.d(TAG, "onRmsChanged() called with: rmsdB = [" + rmsdB + "]");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.d(TAG, "onBufferReceived() called with: buffer = [" + buffer + "]");
        }

        @Override
        public void onEndOfSpeech() {
            Log.d(TAG, "onEndOfSpeech: ");
        }

        @Override
        public void onError(int error) {
            Log.d(TAG, "onError() called with: error = [" + error + "]");
            if (error == AsrError.SUCCESS) {
                return;
            }

            if (error == AsrError.ERROR_CLIENT_INSUFFICIENT_PERMISSIONS) {
                showToast("请在设置中打开麦克风权限!");
            }
        }

        @Override
        public void onResults(Bundle results) {
            Log.d(TAG, "onResults() called with: results = [" + results + "]");
//            endTime = getTimeMillis();
//            waitTime = endTime - startTime;
            getOnResult(results, AsrConstants.RESULTS_RECOGNITION);
//
//            stopListening();
//            if (isAutoTest) {
//                resultList.add(pathList.get(count) + "\t" + mResult);
//                Log.d(TAG, "isAutoTest: " + waitTime + "count :" + count);
//                if (count == fileTotalCount - 1) {
//                    resultList.add("\n\nwaittime:\t" + waitTime + "ms");
//                    mHandler.sendEmptyMessage(WRITE_RESULT_SD);
//                    Log.d(TAG, "waitTime: " + waitTime + "count :" + count);
//                    count = 0;
//                } else {
//                    Log.d(TAG, "isAutoTest: else" + waitTime + "count :" + count);
//                    count += 1;
//                    mHandler.sendEmptyMessageDelayed(NEXT_FILE_TEST, 1000);
//                }
//            } else {
//                startRecord.setEnabled(true);
//            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.d(TAG, "onPartialResults() called with: partialResults = [" + partialResults + "]");
            getOnResult(partialResults, AsrConstants.RESULTS_PARTIAL);
        }

        @Override
        public void onEnd() {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.d(TAG, "onEvent() called with: eventType = [" + eventType + "], params = [" + params + "]");
//            switch (eventType) {
//                case AsrEvent.EVENT_PERMISSION_RESULT:
//                    int result = params.getInt(AsrEvent.EVENT_KEY_PERMISSION_RESULT, PackageManager.PERMISSION_DENIED);
//                    if (result != PackageManager.PERMISSION_GRANTED) {
//                        reset();
//                    }
//                default:
//                    return;
//            }
        }

        private String getOnResult(Bundle partialResults, String key) {
            Log.d(TAG, "getOnResult() called with: getOnResult = [" + partialResults + "]");
            String json = partialResults.getString(key);
            Log.i(TAG, "result json : " + json);
            final StringBuilder sb = new StringBuilder();
//            try {
//                JSONObject result = new JSONObject(json);
//                JSONArray items = result.getJSONArray("result");
//                for (int i = 0; i < items.length(); i++) {
//                    String word = items.getJSONObject(i).getString("word");
//                    double confidences = items.getJSONObject(i).getDouble("confidence");
//                    sb.append(word);
//                    Log.d(TAG, "asr_engine: result str " + word);
//                    Log.d(TAG, "asr_engine: confidence " + String.valueOf(confidences));
//                }
//                Log.d(TAG, "getOnResult: " + sb.toString());
//                showResult.setText(sb.toString());
//            } catch (JSONException exp) {
//                Log.w(TAG, "JSONException: " + exp.toString());
//            }
            return sb.toString();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 取消识别
        mAsrRecognizer.cancel();
    }

    @Override
    protected void onDestroy() {
        // 销毁引擎
        if (mAsrRecognizer != null) {
            mAsrRecognizer.destroy();
            mAsrRecognizer = null;
        }
        super.onDestroy();
    }
}
