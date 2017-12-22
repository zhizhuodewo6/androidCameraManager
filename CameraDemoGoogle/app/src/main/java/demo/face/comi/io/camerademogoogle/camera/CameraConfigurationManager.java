package demo.face.comi.io.camerademogoogle.camera;

import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import demo.face.comi.io.camerademogoogle.bean.CameraFacing;
import demo.face.comi.io.camerademogoogle.bean.OpenCamera;

/**
 * 通过相机与手机屏幕参数，初始化参数
 */
public class CameraConfigurationManager {
    private static final String TAG = "ConfigurationManager";
    private static final int MAX_SIZE_OF_MIN_EDGE = 500;
    private Context mContext;
    private int mRotationFromDisplayToCamera;// set the camera direction according to the screen direction
    private int mNeedRotation;
    private Point mScreenResolution;// the screen resolution
    private Point mCameraResolution;// the camera resolution according to mBestPreviewSize
    private Point mBestPreviewSize;//the calculated preview size the most suitable size
    private Point mPreviewSizeOnScreen;// final preview size according to screen orientaion and mBestPreviewSize
    private int mPreviewFormat;// preview camera format
    private float mPreviewToScreenRatio;// the ratio of preview size to sreen size
    private float mCalcScaleRatio ; //mBestPreviewSize minimum side /  500

    public CameraConfigurationManager(Context context) {
        mContext = context;
    }

      /**
     * init preview size、preview format and ratio
     * @param camera
     * @param parameters
     */
    public void initFromCamera(OpenCamera camera, Camera.Parameters parameters) {
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        int displayRotation = display.getRotation();//得到手机屏幕旋转方向，返回的值，正是用户需要补偿的方向， 竖屏为0，横屏为1，竖屏 高比宽大
        int rotationToDisplay ;
        switch (displayRotation){//屏幕旋转是逆时针方向
            case Surface.ROTATION_0:
                rotationToDisplay = 0;
                break;
            case Surface.ROTATION_90:
                rotationToDisplay = 90;
                break;
            case Surface.ROTATION_180://这个值不可能出现
                rotationToDisplay = 180;
                break;
            case Surface.ROTATION_270:
                rotationToDisplay = 270;
                break;
            default:
                if (displayRotation % 90 == 0){//是90度的整数倍
                    rotationToDisplay = (360+displayRotation) %360;
                }else {
                    throw new IllegalArgumentException("rotation: " + displayRotation);
                }
        }
        int rotationToCamera = camera.getOrientation();//得到相机的方向，这个值只影响拍照的返回图片方法，对于摄像不起作用
        if (camera.getCameraFacing() == CameraFacing.FRONT){//前置摄像头，需经过处理。例如：后置摄像头是90，那前置摄像头就是270
            rotationToCamera = (360-rotationToCamera) %360;
        }

        mRotationFromDisplayToCamera = (360 + rotationToCamera - rotationToDisplay) % 360;//????

        if (camera.getCameraFacing() == CameraFacing.FRONT){
            mNeedRotation = (360-mRotationFromDisplayToCamera) %360;
        }else {
            mNeedRotation = mRotationFromDisplayToCamera;
        }

        Point theScreenResolution = new Point();
        display.getSize(theScreenResolution);//得到屏幕的尺寸，单位是像素
        mScreenResolution = theScreenResolution;

        mBestPreviewSize = CameraUtils.findBestPreviewSizeValue(parameters,theScreenResolution);//通过相机尺寸、屏幕尺寸来得到最好的展示尺寸，此尺寸为相机的
        mCameraResolution = new Point(mBestPreviewSize);

        boolean isScreenPortrait = mScreenResolution.x < mScreenResolution.y;
        boolean isPreviewSizePortrait = mBestPreviewSize.x < mBestPreviewSize.y;
        if (isScreenPortrait == isPreviewSizePortrait){//相机与屏幕一个方向，则使用相机尺寸
            mPreviewSizeOnScreen = mBestPreviewSize;
        }else {
            mPreviewSizeOnScreen = new Point(mBestPreviewSize.y,mBestPreviewSize.x);//否则翻个
        }
        mPreviewFormat = CameraUtils.findAvailablePreviewFormat(parameters, ImageFormat.NV21, ImageFormat.YUY2);//查询相机是否支持ImageFormat.NV21或者ImageFormat.YUY2格式
        mPreviewToScreenRatio = (float)mPreviewSizeOnScreen.x/mScreenResolution.x;//相机预览大小与屏幕大小的比例
        mCalcScaleRatio = Math.min(mBestPreviewSize.x,mBestPreviewSize.y)/MAX_SIZE_OF_MIN_EDGE;
        if(mCalcScaleRatio==0){
            mCalcScaleRatio=1;
        }
    }

    /**
     * camera set previewSize、previewFormat、cameraParamter
     * @param camera
     */
    public void setCameraParameters(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(mBestPreviewSize.x,mBestPreviewSize.y);
        if (mPreviewFormat != -1){
            parameters.setPreviewFormat(mPreviewFormat);//设置相机预览格式
        }
        CameraUtils.setFocus(parameters,true,false,true);//设置相机对焦模式
        CameraUtils.setBarcodeSceneMode(parameters,Camera.Parameters.SCENE_MODE_BARCODE);//设置相机场景模式
        CameraUtils.setBestPreviewFPS(parameters);//设置相机帧数
        CameraUtils.setAntiBanding(parameters);//设置防牛顿环配置 太专业不太懂。哈哈
        camera.setParameters(parameters);
        camera.setDisplayOrientation(mRotationFromDisplayToCamera);//设置预览方向，可以让本身横屏的相机展示出来竖屏，正方向的肖像
        //reset mBestPreviewSize according to afterParameter，prevent the above setting from invalidation 为了防止上述previewSzie设置失效，因为有可能设置的值，相机不支持。
        Camera.Parameters afterParameter = camera.getParameters();
        Camera.Size afterSize = afterParameter.getPreviewSize();
        if (afterSize != null && (mBestPreviewSize.x != afterSize.width
                                || mBestPreviewSize.y != afterSize.height)){
            mBestPreviewSize.x = afterSize.width;
            mBestPreviewSize.y = afterSize.height;
        }
    }

    public int getRotationFromDisplayToCamera() {
        return mRotationFromDisplayToCamera;
    }

    public int getNeedRotation() {
        return mNeedRotation;
    }

    public Point getScreenResolution() {
        return mScreenResolution;
    }

    public Point getBestPreviewSize() {
        return mBestPreviewSize;
    }

    public Point getPreviewSizeOnScreen() {
        return mPreviewSizeOnScreen;
    }

    public int getPreviewFormat() {
        return mPreviewFormat;
    }

    public float getPreviewToScreenRatio() {
        return mPreviewToScreenRatio;
    }

    public float getCalcScaleRatio(){
        return mCalcScaleRatio;
    }

    public Point getCameraResolution() {
        return mCameraResolution;
    }
}
