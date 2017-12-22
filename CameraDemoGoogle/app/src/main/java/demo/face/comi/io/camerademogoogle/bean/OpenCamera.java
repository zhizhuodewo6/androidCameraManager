package demo.face.comi.io.camerademogoogle.bean;

import android.hardware.Camera;

/**
 * 相机信息bean
 */
public class OpenCamera {
    private final int mIndex;//摄像头在系统中的所引
    private final Camera mCamera;
    private final CameraFacing mCameraFacing;//前后摄像头
    private final int mOrientation;//摄像头方向，这个值影响Camera.PictureCallback返回的图片方向

    public OpenCamera(int index, Camera camera, CameraFacing cameraFacing, int orientation) {
        mIndex = index;
        mCamera = camera;
        mCameraFacing = cameraFacing;
        mOrientation = orientation;
    }

    public int getIndex() {
        return mIndex;
    }

    public Camera getCamera() {
        return mCamera;
    }

    public CameraFacing getCameraFacing() {
        return mCameraFacing;
    }

    public int getOrientation() {
        return mOrientation;
    }

    @Override
    public String toString() {
        return "Camera Info, index:" + mIndex + " facing:" + mCameraFacing
                + " orientation:" + mOrientation;

    }
}
