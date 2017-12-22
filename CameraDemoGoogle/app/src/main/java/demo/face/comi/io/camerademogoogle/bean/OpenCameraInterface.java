package demo.face.comi.io.camerademogoogle.bean;

import android.hardware.Camera;

/**
 * 打开摄像头接口
 *
 */
public class OpenCameraInterface {
    private static final String TAG = "OpenCameraInterface";

    private OpenCameraInterface(){}

    public static final int NO_REQUEST_CAMERA = -1;

    /**
     * 通过cameraId打开一个摄像头
     * @param cameraId
     * @return
     */
    public static OpenCamera open(int cameraId){
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras == 0){
            return null;
        }
        if (cameraId >=0 && cameraId < numberOfCameras){
            Camera camera = Camera.open(cameraId);
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(cameraId,cameraInfo);
            return new OpenCamera(cameraId,camera,CameraFacing.values()[cameraInfo.facing],cameraInfo.orientation);

        }
        return open();
    }

    /**
     * 打开默认的摄像头0
     * @return
     */
    public static OpenCamera open(){
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras == 0){
            return null;
        }

        Camera camera = Camera.open(0);
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(0,cameraInfo);
        return new OpenCamera(0,camera,CameraFacing.values()[cameraInfo.facing],cameraInfo.orientation);
    }

    /**
     * 通过摄像头脸方法打开指定的摄像头
     * @param cameraFacing
     * @return
     */
    public static OpenCamera open(CameraFacing cameraFacing){
        int numberOfCameras = Camera.getNumberOfCameras();
        if (numberOfCameras == 0){
            return null;
        }

        int rawFacing  = cameraFacing == CameraFacing.BACK ?
                Camera.CameraInfo.CAMERA_FACING_BACK :
                Camera.CameraInfo.CAMERA_FACING_FRONT;

        int index = 0;
        while (index < numberOfCameras){//通过摄像头facing信息，找到对应摄像头
            Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
            Camera.getCameraInfo(index,cameraInfo);
            if (cameraInfo.facing == rawFacing){
                Camera camera = Camera.open(index);
                return new OpenCamera(index,camera,cameraFacing,cameraInfo.orientation);
            }
            index++;
        }

        return open();//未找到对应的摄像头，返回默认的摄像头
    }


}
