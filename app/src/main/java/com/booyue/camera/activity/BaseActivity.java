package com.booyue.camera.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Administrator on 2017/8/22.
 */

public class BaseActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    /**
     * 检测拍照权限
     */
    private PermissonCallback listener;

    public void checkPermisson(String permission, PermissonCallback listener) {
        this.listener = listener;
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, 100);
        } else {
            if (listener != null) {
                listener.permissonSuccess();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == SurefaceViewCameraActivity.RESULT_OK) {
                if (listener != null) {
                    listener.permissonSuccess();
                }
            } else {
                if (listener != null) {
                    listener.permissonFail();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    interface PermissonCallback {
        void permissonSuccess();

        void permissonFail();
    }



}
