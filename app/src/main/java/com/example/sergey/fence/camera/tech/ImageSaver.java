package com.example.sergey.fence.camera.tech;


import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Sergey on 08.10.2017.
 */


public class ImageSaver {

    private static final String TAG = "ImageSaver";
    String subdir;
    public ImageSaver(String subdir)
    {
        this.subdir = subdir;
    }

    public void save(byte[] bytes)
    {
        Log.i(TAG, "save image");
        File file12 = getOutputMediaFile();
        OutputStream outputStream=null;
        try
        {
            outputStream = new FileOutputStream(file12);
            outputStream.write(bytes);
        }catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, "save failed", e);
        }finally {
            try {
                if (outputStream != null)
                    outputStream.close();
            }catch (Exception e){ Log.e(TAG, "save failed", e);}
        }
        Log.i(TAG, "save image finish");
    }

    private  File getOutputMediaFile() {
        /*File mediaStorageDir = new File(
                Environment.getExternalStorageDirectory(),
                "MyCameraApp"); */
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), subdir);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.i("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        Log.i(TAG, "MediaFile " + mediaFile.getAbsolutePath());
        return mediaFile;
    }
}
