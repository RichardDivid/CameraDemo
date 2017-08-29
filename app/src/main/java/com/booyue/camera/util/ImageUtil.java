package com.booyue.camera.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.widget.TabHost;

/**
 * Created by Administrator on 2017/8/22.
 * 图片工具类，对图片做一些旋转处理
 */

public class ImageUtil {
    private static final String TAG = ImageUtil.class.getSimpleName();

    /**
     * 旋转Bitmap
     *
     * @param b            原始图片
     * @param rotateDegree 旋转的角度
     * @return 旋转之后的图片
     */
    public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(rotateDegree);
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);
        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
//        Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
        return rotaBitmap;
    }

    /**
     * 两张图片合成之后返回的图片
     *
     * @param src  第一层原始图片
     * @param dest 第二层原始图片
     * @return 合成的bitmap图片
     */
    public static Bitmap conpoundBitmap(Bitmap src, Bitmap dest) {
        LogUtil.d(TAG + "合成图片");
//        Bitmap.createBitmap(src,src.getWidth(),src.getHeight(),null);
        int width = src.getWidth();
        int height = src.getHeight();
//        int width1 = dest.getWidth();
//        int height1 = dest.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);//创建一个新的和SRC长度宽度一样的位图
        Canvas canvas = new Canvas(bitmap);
//        Bitmap srcbitmap = lessenBitmap(src, width1, height1);
//        Bitmap bitmap1 = lessenBitmap(dest, width1, height1);
        Rect rect = new Rect(0,0,src.getWidth(),src.getHeight());
        RectF rectF = new RectF(30,30,src.getWidth() - 30,src.getHeight() - 30);

//        canvas.drawBitmap(src, 50, 50, null);//在 0，0坐标开始画入src
        canvas.drawBitmap(src,rect,rectF,null);
        canvas.drawBitmap(dest, 0, 0, null);//再画dest
        canvas.save(Canvas.ALL_SAVE_FLAG);//保存
        canvas.save();
        canvas.restore();//存储
        return bitmap;
    }

    /**
     * 返回压缩过后的图片
     *
     * @param src        原图片
     * @param destWidth  目标图片的宽
     * @param destHeight 目标图片的高
     * @return 压缩过后的图片
     */
    public static Bitmap lessenBitmap(Bitmap src, float destWidth, float destHeight) {
        int width = src.getWidth();
        int height = src.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(destWidth, destHeight);
        Bitmap resizeBitmap = Bitmap.createBitmap(src, 0, 0, width, height, matrix, true);//直接按照矩阵的比例把原图画进去
        return resizeBitmap;
    }
}
