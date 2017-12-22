package demo.face.comi.io.camerademogoogle.listener;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import demo.face.comi.io.camerademogoogle.camera.CameraConfigurationManager;


public class PreviewCallback implements Camera.PreviewCallback {

    private static final String TAG = "PreviewCallback";
    private final CameraConfigurationManager mConfigurationManager;
    private Handler mPreviewHandler;
    private int mPreviewMessage;
    public PreviewCallback(CameraConfigurationManager cameraConfigurationManager){
        mConfigurationManager = cameraConfigurationManager;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.e(TAG,"有数据输出");
        Point cameraResolution = mConfigurationManager.getCameraResolution();
        Handler thePreviewHandler = mPreviewHandler;
        if (cameraResolution != null && thePreviewHandler != null){
            Message message = thePreviewHandler.obtainMessage(mPreviewMessage, cameraResolution.x, cameraResolution.y,data);
            message.sendToTarget();
            mPreviewHandler = null;
        }else {
            Log.d(TAG, "onPreviewFrame: 获取到相机预览数据，但是没有Handler来处理");
        }

    }
}
