package com.booyue.camera.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.booyue.camera.R;
import com.booyue.camera.camera.CameraInterface;
import com.booyue.camera.camera.CameraSurfaceView;
import com.booyue.camera.util.DisplayUtil;
import com.booyue.camera.util.FileUtil;
import com.booyue.camera.util.ImageUtil;
import com.booyue.camera.util.LogUtil;

import java.util.logging.Logger;

public class SurefaceViewCameraActivity extends BaseActivity implements CameraInterface.CamOpenOverCallback {
    private static final String TAG = SurefaceViewCameraActivity.class.getSimpleName() + "--->";
    CameraSurfaceView surfaceView = null;
    ImageButton shutterBtn;
    ImageButton openCamera;
    float previewRate = -1f;
    private Thread openThread;
    private ImageButton ibSwitchCameraFace;
    private SurfaceHolder holder;

    private int[] images = new int[]{R.mipmap.all_audio_album_bg, R.mipmap.all_audio_single_bg, R.mipmap.all_video_single_bg};
    private ImageButton ibReplaceBg;
    private FrameLayout frameLayout;
    private ImageView ivCompound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO Auto-generated method stub
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//拍照过程屏幕一直处于高亮
        //设置手机屏幕朝向，一共有7种
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        //SCREEN_ORIENTATION_BEHIND： 继承Activity堆栈中当前Activity下面的那个Activity的方向
        //SCREEN_ORIENTATION_LANDSCAPE： 横屏(风景照) ，显示时宽度大于高度
        //SCREEN_ORIENTATION_PORTRAIT： 竖屏 (肖像照) ， 显示时高度大于宽度
        //SCREEN_ORIENTATION_SENSOR  由重力感应器来决定屏幕的朝向,它取决于用户如何持有设备,当设备被旋转时方向会随之在横屏与竖屏之间变化
        //SCREEN_ORIENTATION_NOSENSOR： 忽略物理感应器——即显示方向与物理感应器无关，不管用户如何旋转设备显示方向都不会随着改变("unspecified"设置除外)
        //SCREEN_ORIENTATION_UNSPECIFIED： 未指定，此为默认值，由Android系统自己选择适当的方向，选择策略视具体设备的配置情况而定，因此不同的设备会有不同的方向选择
        //SCREEN_ORIENTATION_USER： 用户当前的首选方向

        setContentView(R.layout.activity_camera);
        initUI();
        initViewParams();
        checkPermisson(Manifest.permission.CAMERA, new PermissonCallback() {
            @Override
            public void permissonSuccess() {
                openCamera();
            }

            @Override
            public void permissonFail() {

            }
        });

        shutterBtn.setOnClickListener(new BtnListeners());
        openCamera.setOnClickListener(new BtnListeners());
        ibSwitchCameraFace.setOnClickListener(new BtnListeners());
        ibReplaceBg.setOnClickListener(new BtnListeners());
        /**
         * 用于监听拍照之后图片保存成功之后的回调
         */
        FileUtil.setSaveBitmapListener(new FileUtil.SaveBitmap() {
            @Override
            public void onSaveBitmapComplete() {
                Bitmap srcBitmap = BitmapFactory.decodeFile(FileUtil.jpegName);
                float scaleY = (ivCompound.getHeight()) / (float) srcBitmap.getHeight();
                float scaleX = (ivCompound.getWidth()) / (float) srcBitmap.getWidth();
                float scale = scaleX >= scaleY ? scaleX : scaleY;
                Bitmap bitmap1 = ImageUtil.lessenBitmap(srcBitmap, scaleX, scaleY);

                Bitmap layerBitmap = BitmapFactory.decodeResource(getResources(), images[currentPosition]);
                float scaleY1 = (ivCompound.getHeight()) / (float) layerBitmap.getHeight();
                float scaleX1 = (ivCompound.getWidth()) / (float) layerBitmap.getWidth();
                float scale1 = scaleX1 >= scaleY1 ? scaleX1 : scaleY1;
                Bitmap bitmap2 = ImageUtil.lessenBitmap(layerBitmap, scaleX1, scaleY1);

                Bitmap bitmap = ImageUtil.conpoundBitmap(bitmap1, bitmap2);
                ivCompound.setImageBitmap(bitmap);
            }
        });
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.camera, menu);
//        return true;
//    }

    private void initUI() {
        ivCompound = (ImageView) findViewById(R.id.iv_compund_picture);
        frameLayout = (FrameLayout) findViewById(R.id.fl);
        surfaceView = (CameraSurfaceView) findViewById(R.id.camera_surfaceview);
        shutterBtn = (ImageButton) findViewById(R.id.btn_shutter);
        openCamera = (ImageButton) findViewById(R.id.btn_open_camera);
        ibReplaceBg = (ImageButton) findViewById(R.id.btn_replace_bg);
        ibSwitchCameraFace = (ImageButton) findViewById(R.id.btn_switch_camera_face);


    }

    private void initViewParams() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
//        Point p = DisplayUtil.getScreenMetrics(this);
//        params.width = p.x;
//        params.height = p.y;
        previewRate = DisplayUtil.getScreenRate(this); //默认全屏的比例预览
        LogUtil.d(TAG + "previewRate = " + previewRate);
        surfaceView.setLayoutParams(params);

        //手动设置拍照ImageButton的大小为120dip×120dip,原图片大小是64×64
        ViewGroup.LayoutParams p2 = shutterBtn.getLayoutParams();
        p2.width = DisplayUtil.dip2px(this, 80);
        p2.height = DisplayUtil.dip2px(this, 80);
        shutterBtn.setLayoutParams(p2);

    }

    @Override
    public void cameraHasOpened() {
        // TODO Auto-generated method stub
        LogUtil.d(TAG + "cameraHasOpened()");
        holder = surfaceView.getSurfaceHolder();
        CameraInterface.getInstance().doStartPreview(holder, previewRate);
    }

    private class BtnListeners implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.btn_shutter:
                    checkPermisson(Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissonCallback() {
                        @Override
                        public void permissonSuccess() {
                            CameraInterface.getInstance().doTakePicture();
                        }

                        @Override
                        public void permissonFail() {

                        }
                    });
                    break;
                case R.id.btn_open_camera:
                    checkPermisson(Manifest.permission.CAMERA, new PermissonCallback() {
                        @Override
                        public void permissonSuccess() {
                            openCamera();
                        }

                        @Override
                        public void permissonFail() {

                        }
                    });
                    break;
                case R.id.btn_switch_camera_face:
                    CameraInterface.getInstance().switchCameraFace(holder, previewRate);
                    break;
                case R.id.btn_replace_bg:
                    if (currentPosition == 0) {
                        frameLayout.setBackgroundResource(images[1]);
                        currentPosition = 1;
                    } else if (currentPosition == 1) {
                        frameLayout.setBackgroundResource(images[2]);
                        currentPosition = 2;
                    } else if (currentPosition == 2) {
                        frameLayout.setBackgroundResource(images[0]);
                        currentPosition = 0;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private int currentPosition = 0;

    public void openCamera() {
        openThread = new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                CameraInterface.getInstance().doOpenCamera(SurefaceViewCameraActivity.this);
            }
        };
        openThread.start();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
