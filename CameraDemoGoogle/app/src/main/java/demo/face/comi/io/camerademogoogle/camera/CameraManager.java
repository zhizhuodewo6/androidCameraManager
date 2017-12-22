package demo.face.comi.io.camerademogoogle.camera;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import java.io.IOException;

import demo.face.comi.io.camerademogoogle.bean.CameraFacing;
import demo.face.comi.io.camerademogoogle.bean.OpenCamera;
import demo.face.comi.io.camerademogoogle.bean.OpenCameraInterface;
import demo.face.comi.io.camerademogoogle.listener.PreviewCallback;


/**
 * 相机管理器
 */
public class CameraManager {
    private CameraConfigurationManager mConfigurationManager;
    private PreviewCallback mPreviewCallback;

    private OpenCamera mCamera;
    private boolean mInitialized ;//是否初始化
    private boolean mIsPreviewing;

    public CameraManager(Context context){
        mConfigurationManager = new CameraConfigurationManager(context);
        mPreviewCallback = new PreviewCallback(mConfigurationManager);
    }

    /**
     * 打开相机，并设置相机需要设置的参数。可以指定前后摄像头，若指定的摄像头没找到，则会打开后置摄像头，或者打开失败
     * @throws IOException
     */
    public synchronized void openCamera(CameraFacing cameraFacing) throws IOException {
        OpenCamera theCamera = mCamera;
        //没有打开过摄像头
        if (theCamera == null){
            theCamera = OpenCameraInterface.open(cameraFacing);//获得前置摄像头OpenCamera
            if (theCamera == null){
                throw new IOException("相机无法打开");
            }
            mCamera = theCamera;
        }
        Camera rawCamera = mCamera.getCamera();//得到相机
        Camera.Parameters parameters = rawCamera.getParameters();//得到相机参数
        if (!mInitialized){
            mInitialized = true;
            mConfigurationManager.initFromCamera(theCamera,parameters);//根据相机参数初始configuraion
        }
        mConfigurationManager.setCameraParameters(rawCamera);//设置相机参数
    }

    /**
     * 判断相机是否打开
     * @return
     */
    public synchronized boolean isOpen(){
        return mCamera != null;
    }

    /**
     * 关闭相机
     */
    public synchronized void closeCamera(){
        if (mCamera != null){
            mCamera.getCamera().release();
            mCamera = null;
        }
    }

    public synchronized void setPreviewDisplay(SurfaceHolder holder){
        if(mCamera!=null){
            try {
                mCamera.getCamera().setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 相机开始在surfaceView投影
     */
    public synchronized void startPreview(){
        OpenCamera theCamera = mCamera;
        if (theCamera != null && !mIsPreviewing){
            theCamera.getCamera().startPreview();
            mIsPreviewing = true;
            //TODO :detectFaceOneTime autoFocus
        }
    }

    /**
     * 相机停止投影
     */
    public synchronized void stopPreview(){
        if (mCamera != null && mIsPreviewing){
//            mCamera.getCamera().setOneShotPreviewCallback(null); //若回调一次预览数据，则可以关闭
            mCamera.getCamera().stopPreview();
            mIsPreviewing = false;
        }
    }

    /**
     * 请求画面框架
     * 回调一次预览帧数据
     */
    public synchronized void requestPreviewFrame(){
        OpenCamera theCamera = mCamera;
        if(theCamera != null && mIsPreviewing){
//            theCamera.getCamera().setOneShotPreviewCallback(mPreviewCallback);//回调一次预览帧数据
            theCamera.getCamera().setPreviewCallback(mPreviewCallback);
        }
    }

    public CameraConfigurationManager getConfigurationManager() {
        return mConfigurationManager;
    }
}
