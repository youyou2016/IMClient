package com.liangzhenyou.imclient.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by youyou on 2016/4/7.
 */
public class FileUtils {
    private static final String TAG = "FileUtils";

    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
    }

    public static String getExternalFilesDir() {
        return mContext.getExternalFilesDir(null).toString();
    }

    public static String encodeBase64FromFile(String path) {

        Log.d(TAG, path);

        File file = new File(path);
        FileInputStream inputFile = null;
        byte[] buffer = new byte[(int) file.length()];
        try {
            inputFile = new FileInputStream(file);
            inputFile.read(buffer);
            inputFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(buffer, Base64.DEFAULT);
    }

    public static void base64ToFile(String base64Code, String targetPath) {
        FileOutputStream out = null;
        try {
            byte[] buffer = Base64.decode(base64Code, Base64.DEFAULT);
            out = new FileOutputStream(targetPath);
            out.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] bitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


}
