package com.booyue.camera.camera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;

import com.booyue.camera.util.CamParaUtil;
import com.booyue.camera.util.FileUtil;
import com.booyue.camera.util.ImageUtil;
import com.booyue.camera.util.LogUtil;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/8/22.
 */

public class CameraInterface {
    private static final String TAG = CameraInterface.class.getSimpleName() + "--->";
    private Camera mCamera;
    private Camera.Parameters mParams;
    private boolean isPreviewing = false;
    private float mPreviwRate = -1f;
    private static CameraInterface mCameraInterface;

    public interface CamOpenOverCallback {
        public void cameraHasOpened();
    }

    private CameraInterface() {

    }

    private CamOpenOverCallback mCallback;

    public static synchronized CameraInterface getInstance() {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface();
        }
        return mCameraInterface;
    }

    /**
     * 打开Camera
     *
     * @param callback
     */

    public void doOpenCamera(CamOpenOverCallback callback) {
//        if (mCallback == null && callback != null) {
//            mCallback = callback;
//        }
        LogUtil.d(TAG + "Camera open....");
        mCamera = Camera.open();
        LogUtil.d(TAG + "Camera open over....");
        if (callback != null) {
            callback.cameraHasOpened();
        }
    }

    /**
     * 开启预览
     *
     * @param holder      使用surfaceview预览
     * @param previewRate
     */
    public void doStartPreview(SurfaceHolder holder, float previewRate) {
        LogUtil.d(TAG + "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            initCamera(previewRate);
        }
    }

    /**
     * 使用TextureView预览Camera
     *
     * @param surface
     * @param previewRate
     */
    public void doStartPreview(SurfaceTexture surface, float previewRate) {
        LogUtil.d(TAG + "doStartPreview...");
        if (isPreviewing) {
            mCamera.stopPreview();
            return;
        }
        if (mCamera != null) {
            try {
                mCamera.setPreviewTexture(surface);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            initCamera(previewRate);
        }
    }

    /**
     * 初始化照相机
     *
     * @param previewRate
     */
    private void initCamera(float previewRate) {
        if (mCamera != null) {
            mParams = mCamera.getParameters();
            mParams.setPictureFormat(PixelFormat.JPEG);//设置拍照后存储的图片格式
            CamParaUtil.getInstance().printSupportPictureSize(mParams);
            CamParaUtil.getInstance().printSupportPreviewSize(mParams);
            //设置PreviewSize和PictureSize
            Camera.Size pictureSize = CamParaUtil.getInstance().getPropPictureSize(
                    mParams.getSupportedPictureSizes(), previewRate, 800);
            mParams.setPictureSize(pictureSize.width, pictureSize.height);
            Camera.Size previewSize = CamParaUtil.getInstance().getPropPreviewSize(
                    mParams.getSupportedPreviewSizes(), previewRate, 800);
            mParams.setPreviewSize(previewSize.width, previewSize.height);
            mCamera.setDisplayOrientation(90);
            CamParaUtil.getInstance().printSupportFocusMode(mParams);
            List<String> focusModes = mParams.getSupportedFocusModes();
            if (focusModes.contains("continuous-video")) {
                mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(mParams);
            mCamera.startPreview();//开启预览
            isPreviewing = true;
            mPreviwRate = previewRate;

            mParams = mCamera.getParameters(); //重新get一次
            LogUtil.d(TAG + "最终设置:PreviewSize--With = " + mParams.getPreviewSize().width
                    + "Height = " + mParams.getPreviewSize().height);
            LogUtil.d(TAG + "最终设置:PictureSize--With = " + mParams.getPictureSize().width
                    + "Height = " + mParams.getPictureSize().height);
        }
    }

    /**
     * 停止预览，释放Camera
     */
    public void doStopCamera() {
        if (null != mCamera) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            isPreviewing = false;
            mPreviwRate = -1f;
            mCamera.release();
            mCamera = null;
        }
    }


    /**
     * 拍照
     */
    public void doTakePicture() {
        if (isPreviewing && (mCamera != null)) {
            mCamera.takePicture(mShutterCallback, null, mJpegPictureCallback);
        }
    }

    /**
     * 为了实现拍照的快门声音及拍照保存照片需要下面三个回调变量
     */
    Camera.ShutterCallback mShutterCallback = new Camera.ShutterCallback() {
        //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
        public void onShutter() {
            // TODO Auto-generated method stub
            LogUtil.d(TAG + "myShutterCallback:onShutter...");

        }
    };
    /**
     * 拍摄的未压缩原数据的回调,可以为null
     */

    Camera.PictureCallback mRawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            LogUtil.d(TAG + "myRawCallback:onPictureTaken...");

        }
    };

    /**
     * 对jpeg图像数据的回调,最重要的一个回调
     */
    Camera.PictureCallback mJpegPictureCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            // TODO Auto-generated method stub
            LogUtil.d(TAG + "myJpegCallback:onPictureTaken...");
            Bitmap b = null;
            if (null != data) {
                b = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
                mCamera.stopPreview();
                isPreviewing = false;
            }
            //保存图片到sdcard
            if (null != b) {
                //设置FOCUS_MODE_CONTINUOUS_VIDEO)之后，myParam.set("rotation", 90)失效。
                //图片竟然不能旋转了，故这里要旋转下
                Bitmap rotaBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
                FileUtil.saveBitmap(rotaBitmap);
            }
            //再次进入预览
            mCamera.startPreview();
            isPreviewing = true;
        }
    };

    private int cameraPosition = 1;//0代表前置摄像头，1代表后置摄像头

    public void switchCameraFace(SurfaceHolder holder, float previewRate) {
        //切换前后摄像头
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if (cameraPosition == 1) {
                //现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    mCamera.stopPreview();//停掉原来摄像头的预览
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头
                    mCamera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        mCamera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                        initCamera(1.7778f);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
//                    mCamera.startPreview();//开始预览
                    cameraPosition = 0;
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    mCamera.stopPreview();//停掉原来摄像头的预览
                    mCamera.release();//释放资源
                    mCamera = null;//取消原来摄像头
                    mCamera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        mCamera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                        initCamera(1.7778f);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
//                    mCamera.startPreview();//开始预览
                    cameraPosition = 1;
                    break;
                }
            }

        }
    }
}
