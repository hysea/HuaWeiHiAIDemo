package com.hysea.huaweiaidemo.ocr;

import android.graphics.Bitmap;
import android.util.Log;

import com.huawei.hiai.vision.common.ConnectionCallback;
import com.huawei.hiai.vision.common.VisionBase;
import com.huawei.hiai.vision.text.TextDetector;
import com.huawei.hiai.vision.visionkit.common.Frame;
import com.huawei.hiai.vision.visionkit.text.Text;
import com.hysea.huaweiaidemo.BaseActivity;

import org.json.JSONObject;

/**
 * OCR识别
 */
public class OcrActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Override
    protected void init() {
        initVisionBase();
    }

    private void initTextDetector() {
        TextDetector textDetector = new TextDetector(this);

        Frame frame = new Frame();
        Bitmap bitmap = null;
        frame.setBitmap(bitmap);
        JSONObject json = textDetector.detect(frame, null);
        Log.d(TAG,"json : " + json);
        Text text = textDetector.convertResult(json);
    }
}
