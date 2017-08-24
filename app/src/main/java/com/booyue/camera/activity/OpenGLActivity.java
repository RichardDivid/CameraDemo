package com.booyue.camera.activity;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Administrator on 2017/8/23.
 */

public class OpenGLActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        GLSurfaceView glSurfaceView = new GLSurfaceView(this);
        glSurfaceView.setRenderer(new OpenGlRender());
        setContentView(glSurfaceView);
    }

    class OpenGlRender implements GLSurfaceView.Renderer {
        private Square square;

        //画面创建 在这里我们主要进行一些初始化工作，比如对透视进行修正、设置清屏所用颜色等。
        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            // 黑色背景
            gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
            // 启用阴影平滑（不是必须的）
            gl.glShadeModel(GL10.GL_SMOOTH);
            // 设置深度缓存
            gl.glClearDepthf(1.0f);
            // 启用深度测试
            gl.glEnable(GL10.GL_DEPTH_TEST);
            // 所作深度测试的类型
            gl.glDepthFunc(GL10.GL_LEQUAL);
            // 对透视进行修正
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
            square = new Square();
        }

        //画面改变  当设备水平或者垂直变化时调用此方法，设置新的显示比例
        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            // 设置画面的大小
            gl.glViewport(0, 0, width, height);
            // 设置投影矩阵
            gl.glMatrixMode(GL10.GL_PROJECTION);
            // 重置投影矩阵
            gl.glLoadIdentity();
            // 设置画面比例
            GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
            // 选择模型观察矩阵
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            // 重置模型观察矩阵
            gl.glLoadIdentity();
        }

        //画面绘制
        @Override
        public void onDrawFrame(GL10 gl) {
            // 清除屏幕和深度缓存
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            gl.glTranslatef(0, 0, -4);
            // 绘制正方形
            square.draw(gl);
            // 重置当前的模型观察矩阵
            gl.glLoadIdentity();
        }
    }

    class Square {
        // 顶点坐标数组
        private float vertices[] = {
                -1.0f, 1.0f, 0.0f,  // 0, 左上
                -1.0f, -1.0f, 0.0f, // 1, 左下
                1.0f, -1.0f, 0.0f, // 2, 右下
                1.0f, 1.0f, 0.0f,  // 3, 右上
        };
        // 连接规则
        private short[] indices = {0, 1, 2, 0, 2, 3};
        // 顶点缓存
        private FloatBuffer vertexBuffer;
        // 索引缓存
        private ShortBuffer indexBuffer;


        public Square() {
            // 一个float为4 bytes, 因此要乘以4
            ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
            vbb.order(ByteOrder.nativeOrder());
            vertexBuffer = vbb.asFloatBuffer();
            vertexBuffer.put(vertices);
            vertexBuffer.position(0);
            // short类型同理
            ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
            ibb.order(ByteOrder.nativeOrder());
            indexBuffer = ibb.asShortBuffer();
            indexBuffer.put(indices);
            indexBuffer.position(0);
        }

        /**
         * 绘制正方形到屏幕
         * @param gl
         */
        public void draw(GL10 gl) {
            // 逆时针环绕
            gl.glFrontFace(GL10.GL_CCW);
            // 开启剔除功能
            gl.glEnable(GL10.GL_CULL_FACE);
            // 剔除背面
            gl.glCullFace(GL10.GL_BACK);
            // 开启顶点缓存写入功能
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            // 设置顶点
            // size:每个顶点有几个数指描述。
            // type:数组中每个顶点的坐标类型。
            // stride:数组中每个顶点间的间隔，步长（字节位移）。
            // pointer:存储着每个顶点的坐标值。初始值为0
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
            gl.glDrawElements(GL10.GL_TRIANGLES, indices.length, GL10.GL_UNSIGNED_SHORT, indexBuffer);
            // 关闭各个功能
            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
            gl.glDisable(GL10.GL_CULL_FACE);
        }
    }
}
