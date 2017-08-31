package com.rayject.table.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageUtils {
    public final static int QUALITY_LOW = 0; //Use provided width/height to decode bitmap
    public final static int QUALITY_HIGH = 1; //Use actual width/height to decode bitmap

    public static Bitmap getBitmap(byte[] data) {
        try {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();

            BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(newOpts, true);

            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length, newOpts);
            return bm;
        } catch (Exception ee) {
            ee.printStackTrace();
            return null;
        }
    }

    public static Bitmap getBitmap(byte[] data, int width, int height, int quality) {
        try {
            BitmapFactory.Options newOpts = new BitmapFactory.Options();
            newOpts.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, newOpts);
            int origWidth = newOpts.outWidth;
            int origHeight = newOpts.outHeight;

            newOpts = new BitmapFactory.Options();
            int maxLength = 400;
            maxLength = Math.min(width, height);
            int scale = 1;
            if (origHeight > maxLength || origWidth > maxLength) {
                scale = (int) Math.pow(2, (int) Math.round(Math.log(maxLength
                        / (double) Math.max(origHeight, origWidth))
                        / Math.log(0.5)));
            }
            if (quality == QUALITY_HIGH) {
                newOpts.inSampleSize = 1;
            } else {
                newOpts.inSampleSize = scale;
            }

            BitmapFactory.Options.class.getField("inNativeAlloc").setBoolean(newOpts, true);

            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length, newOpts);
            //TODO: createScaledBitmap can cause OOM.
            return bm;
//			Bitmap scaleBm = Bitmap.createScaledBitmap(bm, width, height, true);
//			bm.recycle();
//			return scaleBm;
        } catch (Exception ee) {
            return null;
        }
    }

    // 保存到sdcard
    public static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
