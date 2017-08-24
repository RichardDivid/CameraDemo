package com.booyue.camera.util;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Logger;

/**
 * Created by Administrator on 2017/8/22.
 */

public class FileUtil {
    private static final  String TAG = "FileUtil";
    private static final File parentPath = Environment.getExternalStorageDirectory();
    private static   String storagePath = "";
    private static final String DST_FOLDER_NAME = "PlayCamera";

    /**初始化保存路径
     * @return
     */
    private static String initPath(){
        if(storagePath.equals("")){
            storagePath = parentPath.getAbsolutePath()+"/" + DST_FOLDER_NAME;
            LogUtil.d(TAG + "storage = " + storagePath);
            File f = new File(storagePath);
            if(!f.exists()){
                f.mkdir();
            }
        }
        return storagePath;
    }

    /**保存Bitmap到sdcard
     * @param b
     */
    public static void saveBitmap(Bitmap b){
//        String path = initPath();
//        long dataTake = System.currentTimeMillis();
//        String jpegName = path + "/" + dataTake +".jpg";
//        LogUtil.d(TAG + "saveBitmap:jpegName = " + jpegName);
//        try {
//            FileOutputStream fout = new FileOutputStream(jpegName);
//            BufferedOutputStream bos = new BufferedOutputStream(fout);
//            b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
//            bos.flush();
//            bos.close();
//            LogUtil.d(TAG + "saveBitmap成功");
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            LogUtil.d(TAG + "saveBitmap:失败");
//            e.printStackTrace();
//        }
        String path = initPath();
        long dataTake = System.currentTimeMillis();
        String jpegName = path + "/" + dataTake + ".jpg";
        LogUtil.d(TAG + "saveBitmap : jpegName = " + jpegName);
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG,100,bos);
            bos.flush();
            bos.close();
            LogUtil.d(TAG + "savebitmap 成功");
        } catch (Exception e) {
            LogUtil.d(TAG + "saveBitmap 失败");
            e.printStackTrace();
        }

    }
}
