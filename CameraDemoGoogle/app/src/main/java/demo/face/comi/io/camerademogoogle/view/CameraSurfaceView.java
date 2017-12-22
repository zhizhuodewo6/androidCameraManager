package demo.face.comi.io.camerademogoogle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import demo.face.comi.io.camerademogoogle.camera.CameraManager;


public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraSurfaceView";
    private Context mContext;
    private SurfaceHolder mHolder;

    private CameraManager mCameraManager;

    //private FaceDetector mFaceDetector;

    //private RecognizeCallback mRecognizeCallback;

    //private DetectThread mDetectThread;
    //private RecognizeThread mRecognizeThread;
    //private String mGroupId;

    private boolean mAutoStartDetect;
    private boolean mHasSurface;
    //private boolean mAutoStartRecognize;

    private boolean mAutoStartLivDetect;


    public CameraSurfaceView(Context context) {
        super(context);
        init(context);
    }

    public CameraSurfaceView(Context context,CameraManager cameraManager) {
        super(context);
        init(context);
        mCameraManager=cameraManager;
    }
    public CameraSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        //mFaceDetector = new FaceDetector(context);

    }

    /**
     * SurfaceHolder.Callback接口函数
     * surface已经准备完毕，可以开始创建camera
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated: ");
        if (holder == null) {
            Log.w(TAG, "surfaceCreated: SurfaceHolder == null");
            return;
        }

        if (!mHasSurface) {
            mHasSurface = true;
            initCameraViewHandler(holder);
            mHolder=holder;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged: ");

        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCameraManager.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            initCameraViewHandler(mHolder);

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed: ");
        mHasSurface = false;
    }



    /**
     * 初始化CameraViewHandler，并且打开相机开始投影
     */
    private void initCameraViewHandler(SurfaceHolder holder) {
        if (mCameraManager == null) {
            Log.e(TAG, "initCameraViewHandler:CameraManager == null");
            return;
        }
        //mDetectThread = new DetectThread(mFaceDetector);
        //mRecognizeThread = new RecognizeThread();
        mCameraManager.setPreviewDisplay(holder);
        mCameraManager.startPreview();
        mCameraManager.requestPreviewFrame();
        //if (mAutoStartDetect && !mDetectThread.isAlive()){
        //    mDetectThread.detectFaceOneTime();
        //    mCameraViewHandler.detectFaceOneTime();
        //}
        //
        //if (mAutoStartRecognize && !mRecognizeThread.isAlive()){
        //    mRecognizeThread.detectFaceOneTime();
        //}
    }

    /**
     * 重新启动surfaceView
     */
    public void onResume() {
        SurfaceHolder holder = getHolder();

        if (mHasSurface) {//若
            //初始化
            initCameraViewHandler(holder);
        } else {
            holder.addCallback(this);
        }
    }

    /**
     * 暂停SurfaceView
     */
    public void onPause() {
        mCameraManager.closeCamera();
        if (!mHasSurface){
            getHolder().removeCallback(this);
            Log.d(TAG, "onPause: 移除Callback");
        }
         mCameraManager = null;
    }


}
