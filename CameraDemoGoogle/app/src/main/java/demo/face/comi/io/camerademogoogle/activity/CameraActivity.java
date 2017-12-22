package demo.face.comi.io.camerademogoogle.activity;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.IOException;

import demo.face.comi.io.camerademogoogle.R;
import demo.face.comi.io.camerademogoogle.bean.CameraFacing;
import demo.face.comi.io.camerademogoogle.camera.CameraManager;
import demo.face.comi.io.camerademogoogle.camera.CameraUtils;
import demo.face.comi.io.camerademogoogle.view.CameraSurfaceView;

/**
 * Created by xijie on 2017/12/15.
 */

public class CameraActivity extends Activity {
    private final static String TAG="CameraActivity";

    private FrameLayout camera_preview;
    private CameraSurfaceView cameraSurfaceView;
    private CameraManager mCameraManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        camera_preview = (FrameLayout) findViewById(R.id.camera_preview);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG,"onResume...");
        if(CameraUtils.checkCameraHardware(this)){
            openCamera();//需要在子线程中操作
            relayout();
            cameraSurfaceView.onResume();
        }else{
            Toast.makeText(this,"该手机不支持摄像头！",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG,"onPause...");
        cameraSurfaceView.onPause();
    }

    /**
     * 初始化相机，主要是打开相机，并设置相机的相关参数，并将holder设置为相机的展示平台
     */
    private void openCamera() {
        mCameraManager = new CameraManager(this);
        if (mCameraManager.isOpen()) {
            Log.w(TAG, "surfaceCreated: 相机已经被打开了");
            return;
        }
        try {
            mCameraManager.openCamera(CameraFacing.FRONT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 设置界面展示大小
     */
    private void relayout() {
        // Create our Preview view and set it as the content of our activity.
        cameraSurfaceView = new CameraSurfaceView(this,mCameraManager);
        Point previewSizeOnScreen = mCameraManager.getConfigurationManager().getPreviewSizeOnScreen();//相机预览尺寸
        Point screentPoint=mCameraManager.getConfigurationManager().getScreenResolution();//自己展示相机预览控件所能设置最大值
        Point point = CameraUtils.calculateViewSize(previewSizeOnScreen, screentPoint);
        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(point.x,point.y);
        layoutParams.gravity= Gravity.CENTER;
        cameraSurfaceView.setLayoutParams(layoutParams);
        camera_preview.addView(cameraSurfaceView);
    }
}
