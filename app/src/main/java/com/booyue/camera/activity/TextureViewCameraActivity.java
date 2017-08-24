package com.booyue.camera.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.booyue.camera.R;
import com.booyue.camera.camera.CameraInterface;
import com.booyue.camera.camera.CameraSurfaceView;
import com.booyue.camera.camera.CameraTextureView;
import com.booyue.camera.util.DisplayUtil;
import com.booyue.camera.util.LogUtil;

public class TextureViewCameraActivity extends BaseActivity implements CameraInterface.CamOpenOverCallback {

    private static final String TAG = SurefaceViewCameraActivity.class.getSimpleName() + "--->";
    CameraTextureView surfaceView = null;
    ImageButton shutterBtn;
    ImageButton openCamera;
    float previewRate = -1f;
    private Thread openThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO Auto-generated method stub

        setContentView(R.layout.activity_texture_view_camera);
        initUI();
        initViewParams();
        checkPermisson(Manifest.permission.CAMERA, new SurefaceViewCameraActivity.PermissonCallback() {
            @Override
            public void permissonSuccess() {
                openCamera();
            }

            @Override
            public void permissonFail() {

            }
        });

        shutterBtn.setOnClickListener(new TextureViewCameraActivity.BtnListeners());
        openCamera.setOnClickListener(new TextureViewCameraActivity.BtnListeners());
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.camera, menu);
//        return true;
//    }

    private void initUI() {
        surfaceView = (CameraTextureView) findViewById(R.id.camera_surfaceview);
        shutterBtn = (ImageButton) findViewById(R.id.btn_shutter);
        openCamera = (ImageButton) findViewById(R.id.btn_open_camera);

    }

    private void initViewParams() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) surfaceView.getLayoutParams();
        Point p = DisplayUtil.getScreenMetrics(this);
        params.width = p.x;
        params.height = p.y;
        previewRate = DisplayUtil.getScreenRate(this); //默认全屏的比例预览
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
        SurfaceTexture holder = surfaceView.getSurfaceTexture();
        if (holder != null) {
            CameraInterface.getInstance().doStartPreview(holder, previewRate);
        }
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
                    checkPermisson(Manifest.permission.CAMERA, new SurefaceViewCameraActivity.PermissonCallback() {
                        @Override
                        public void permissonSuccess() {
                            openCamera();
                        }

                        @Override
                        public void permissonFail() {

                        }
                    });
                    break;
                default:
                    break;
            }
        }
    }

    public void openCamera() {
        openThread = new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                CameraInterface.getInstance().doOpenCamera(TextureViewCameraActivity.this);
            }
        };
        openThread.start();
    }


}
