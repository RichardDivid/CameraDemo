package com.booyue.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.booyue.camera.activity.OpenGLActivity;
import com.booyue.camera.activity.SurefaceViewCameraActivity;
import com.booyue.camera.activity.TextureViewCameraActivity;

/**
 * Created by Administrator on 2017/8/22.
 */

public class MainActivity extends AppCompatActivity {

    private Button surfaceView;
    private Button textureView;
    private Button glSurfaceView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        surfaceView = (Button) findViewById(R.id.surfaceView);
        textureView = (Button) findViewById(R.id.textureView);
        glSurfaceView = (Button) findViewById(R.id.glsurfaceview);

        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterActivity(SurefaceViewCameraActivity.class);
            }
        });
        textureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterActivity(TextureViewCameraActivity.class);
            }
        });
        glSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterActivity(OpenGLActivity.class);
            }
        });
    }

    private void enterActivity(Class<?> clzz) {
        Intent intent = new Intent(this, clzz);
        startActivity(intent);
    }
}
