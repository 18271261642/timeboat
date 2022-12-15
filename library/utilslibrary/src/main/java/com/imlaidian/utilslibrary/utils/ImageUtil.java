package com.imlaidian.utilslibrary.utils;

/**
 * Created by zbo on 16/6/7.
 */

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.imlaidian.utilslibrary.UtilsApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class ImageUtil {
    private static final String TAG = "ImageUtil" ;
    final static int limitMaxWidth = 960;
    final static int limitMaxHeight = 960;// 1136
    final static int pictureSaveQuality = 80;


    public final static int REQUEST_CODE_GETIMAGE_BYSDCARD = 100 ;
    public final static int REQUEST_CODE_GETIMAGE_BYCROP = 10;
    public final static int REQUEST_CODE_GETIMAGE_BYCAMERA =15;


    /**
     * 获取图片路径
     */
    public static String getImagePath(Uri uri, Activity context) {
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

            int columIndex = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
            String ImagePath = cursor.getString(columIndex);
            cursor.close();

            return ImagePath;
        }

        return uri.toString();
    }

    static Bitmap bitmap = null;

    public static Bitmap loadPicasaImageFromGalley(final Uri uri,
                                                   final Activity context) {
        String[] projection = { MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME };
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            int columIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME);
            if (columIndex != -1) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            bitmap = android.provider.MediaStore.Images.Media
                                    .getBitmap(context.getContentResolver(),
                                            uri);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }

            cursor.close();
            return bitmap;
        }

        return null;
    }

    public static Bitmap loadImgThumbnail(String filePath, int w, int h) {
        Bitmap bitmap = getBitmapByPath(filePath);

        return zoomBitmap(bitmap, w, h);
    }


    /**
     * 判断当前Url是否标准的content://样式，如果不是，则返回绝对路径
     */
    public final static String SDCARD_MNT = "/mnt/sdcard";
    public final static String SDCARD = "/sdcard";

    public static String getAbsolutePathFromNoStandardUri(Uri mUri) {
        String filePath = null;

        String mUriString = mUri.toString();
        mUriString = Uri.decode(mUriString);

        String pre1 = "file://" + SDCARD + File.separator;
        String pre2 = "file://" + SDCARD_MNT + File.separator;

        if (mUriString.startsWith(pre1)) {
            filePath = Environment.getExternalStorageDirectory().getPath()
                    + File.separator + mUriString.substring(pre1.length());
        } else if (mUriString.startsWith(pre2)) {
            filePath = Environment.getExternalStorageDirectory().getPath()
                    + File.separator + mUriString.substring(pre2.length());
        }

        return filePath;
    }


    /**
     * 通过uri获取文件的绝对路径
     */
    @SuppressWarnings("deprecation")
    public static String getAbsoluteImagePath(Activity context, Uri uri) {
        String imagePath = "";
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.managedQuery(uri, proj, // Which columns to
                // return
                null, // WHERE clause; which rows to return (all rows)
                null, // WHERE clause selection arguments (none)
                null); // Order-by clause (ascending by name)

        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                imagePath = cursor.getString(column_index);
            }
        }

        return imagePath;
    }


    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input)) {
            return true;
        }

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }

        return true;
    }

    /**
     * 获取文件扩展名
     */
    public static String getFileFormat(String fileName) {
        if (isEmpty(fileName)) {
            return "";
        }

        int point = fileName.lastIndexOf('.');
        return fileName.substring(point + 1);
    }

    //压缩图片大小
    public static Bitmap compressBitmap(Bitmap bitmap, double w, double h) {
        Bitmap newbmp = null;

        if (bitmap != null) {
            float width = bitmap.getWidth();
            float height = bitmap.getHeight();

            // 相差不大 不进行缩放
            double diffWidth = Math.abs(width -w);
            double diffHeight= Math.abs(height -h);
            if( diffWidth != diffHeight && diffWidth < 60  &&  diffHeight < 60){
                return  bitmap ;
            }

            float scaleWidth = (width / (float) w);
            float scaleHeight = ( height / (float)  h);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] bitmapArray = baos.toByteArray();

            BitmapFactory.Options opts = new BitmapFactory.Options();
            //设置采样率，成倍数减少像素点。inSamleSize = 2 总像素点是原图像的1/4
            opts.inSampleSize = Math.max(2, Math.min((int)scaleWidth ,(int)scaleHeight));
            opts.inJustDecodeBounds = false;
            Bitmap compressBitmap = BitmapFactory.decodeByteArray(bitmapArray, 0 ,bitmapArray.length, opts);
            int compressWidth = compressBitmap.getWidth() ;
            int compressHigh = compressBitmap.getHeight() ;
            LogUtil.d(TAG , "compressWidth =" +compressWidth + ",compressHigh=" + compressHigh);
            Matrix matrix = new Matrix();
            float pressWidth = ((float) w / compressWidth);
            float pressHeight = ((float) h / compressHigh);
            matrix.postScale(pressWidth, pressHeight);
            newbmp = Bitmap.createBitmap(compressBitmap, 0, 0, compressWidth, compressHigh, matrix, true);

        }


        return newbmp;
    }



    /**
     * 放大缩小图片
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, double w, double h) {
        Bitmap newbmp = null;

            if (bitmap != null) {
                float width = bitmap.getWidth();
                float height = bitmap.getHeight();

                // 相差不大 不进行缩放
                double diffWidth = Math.abs(width -w);
                double diffHeight= Math.abs(height -h);
                float scaleWidth = ((float) w / width);
                float scaleHeight = ((float) h / height);
                if( diffWidth != diffHeight && diffWidth < 60  &&  diffHeight < 60){
                    return  bitmap ;
                }

                Matrix matrix = new Matrix();

                try {
                    matrix.postScale(scaleWidth, scaleHeight);
                    newbmp = Bitmap.createBitmap(bitmap, 0, 0, (int)width, (int)height, matrix, true);

                }catch (OutOfMemoryError error){
                    LogUtil.d(TAG , "zoomBitmap oom=" + error.getLocalizedMessage());
                     scaleWidth = (width / (float) w);
                     scaleHeight = ( height / (float)  h);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    try {
                        baos.flush();
                        baos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    byte[] bitmapArray = baos.toByteArray();

                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    //设置采样率，成倍数减少像素点。inSamleSize = 2 总像素点是原图像的1/4
                    opts.inSampleSize = Math.max(2, Math.min((int)scaleWidth ,(int)scaleHeight));
                    opts.inJustDecodeBounds = false;
                    Bitmap compressBitmap = BitmapFactory.decodeByteArray(bitmapArray, 0 ,bitmapArray.length, opts);
                    int compressWidth = compressBitmap.getWidth() ;
                    int compressHigh = compressBitmap.getHeight() ;
                    LogUtil.d(TAG , "compressWidth =" +compressWidth + ",compressHigh=" + compressHigh);
                    Matrix matrixCompress = new Matrix();
                    float pressWidth = ((float) w / compressWidth);
                    float pressHeight = ((float) h / compressHigh);
                    matrixCompress.postScale(pressWidth, pressHeight);
                    newbmp = Bitmap.createBitmap(compressBitmap, 0, 0, compressWidth, compressHigh, matrixCompress, true);
                }catch (Exception e){

                    e.printStackTrace();
                }
            }
        return newbmp;
    }




    public static Drawable compressDrableByResId(int resId) {
        BitmapDrawable bd = null ;
        Context context = UtilsApplication.getInstance().getApplicationContext() ;
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        try {
            opt.inSampleSize = 3 ;
            InputStream is = context.getResources().openRawResource(resId);
            Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
            bd = new BitmapDrawable(context.getResources(), bm);
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (OutOfMemoryError error){
            error.printStackTrace();
            LogUtil.d(TAG , "readBitMap OutOfMemoryError =" + error.getLocalizedMessage());
        }

        if(bd ==null){
            bd=  (BitmapDrawable) context.getResources().getDrawable(resId);
        }
        return bd;
    }

    public static Drawable readBitmapDrawable(int resId){
        Context context = UtilsApplication.getInstance().getApplicationContext() ;
        BitmapFactory.Options opt = new BitmapFactory.Options();
//        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        BitmapDrawable bd = null ;
        try {

            InputStream is = context.getResources().openRawResource(resId);
            Bitmap bm = BitmapFactory.decodeStream(is, null, opt);

            bd = new BitmapDrawable(context.getResources(), bm);

            is.close();
        } catch (IOException e) {
            e.printStackTrace();

        }catch (OutOfMemoryError error){
            error.printStackTrace();
            LogUtil.d(TAG , "readBitmapDrawable OutOfMemoryError =" + error.getLocalizedMessage());
            opt.inPreferredConfig = Bitmap.Config.RGB_565;
            opt.inSampleSize = 2 ;
            InputStream is = context.getResources().openRawResource(resId);
            Bitmap bm = BitmapFactory.decodeStream(is, null, opt);
            bd = new BitmapDrawable(context.getResources(), bm);
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(bd ==null){
            bd=  (BitmapDrawable) context.getResources().getDrawable(resId);
        }
        return bd;
    }


    /**
     * 获取bitmap
     */
    public static Bitmap getBitmapByPath(String filePath) {
        return getBitmapByPath(filePath, null);
    }

    public static Bitmap getBitmapByPath(String filePath,
                                         BitmapFactory.Options opts) {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            File file = new File(filePath);
            fis = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fis, null, opts);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }

        return bitmap;
    }

    /**
     * 获取bitmap
     */
    public static Bitmap getBitmapByFile(File file) {
        FileInputStream fis = null;
        Bitmap bitmap = null;
        try {
            fis = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return bitmap;
    }




    /**
     * 获得文件名
     */
    public static String getFileNameFromPath(String path) {
        String fn = "";
        int pos = path.lastIndexOf("/");
        if (pos > 0) {
            fn = path.substring(pos+1); //此处加1 是为了不要“/”
        }
        return fn;
    }

    /**
     * 是否远程地址
     */
    public static boolean isRemoteMediaPath(String path) {
        boolean isRemote = false;
        if (path != null && path.startsWith("http")) {
            isRemote = true;
        }
        return isRemote;
    }

    /**
     * 压缩图片
     */
    public static boolean compressBitmap(String filePath, String toFilePath) {
        boolean compress = false;
        FileOutputStream fos = null;
        try {
            Bitmap bitmap = null;
            try {
                bitmap = getSmallBitmap(filePath, limitMaxWidth, limitMaxHeight);// decodeUriAsBitmap(Uri.parse(filePath));
                LogUtil.e(TAG ,"compress compressBitmap! bitmap=" + bitmap);
            } catch (OutOfMemoryError e) {
                LogUtil.e(TAG ,e.toString());
                e.printStackTrace();
                return compress;
            }
            if (null == bitmap) {
                LogUtil.e(TAG ,"compress err!");
                return compress;
            }
            File file = new File(toFilePath);
            fos = new FileOutputStream(file);
            compress = bitmap.compress(Bitmap.CompressFormat.JPEG, pictureSaveQuality,
                    fos);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
                bitmap = null; // recycle()是个比较漫长的过程，设为null，然后在最后调用System.gc()，效果能好很多
            }
            System.gc();
            LogUtil.e(TAG ,"compress compressBitmap!222");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
        return compress;
    }

    /**
     * 需要验证此方法是否能够加载大图片
     */
    public static Bitmap decodeUriAsBitmap(Uri uri) {
        Context context = UtilsApplication.getInstance().getApplicationContext();
        if (context == null || uri == null)
            return null;

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver()
                    .openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    /**
     * 获得规定尺寸的图片信息
     */
    public static BitmapFactory.Options getUploadBitmapInfo(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, limitMaxWidth,
                limitMaxHeight);

        return options;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath, int width, int height)
            throws OutOfMemoryError {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    // 计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        int yRatio = (int) Math.ceil(height / reqWidth);
        int xRatio = (int) Math.ceil(width / reqHeight);
        if (yRatio > 1 || xRatio > 1) {
            if (yRatio > xRatio) {
                inSampleSize = yRatio;
            } else {
                inSampleSize = xRatio;
            }
        }

        return inSampleSize;
    }

    public static int calculateVideoInSampleSize(int layoutWidth , int layoutHeight , int videoWidth ,int videoHeight){
         Context mContext  = UtilsApplication.getInstance().getApplicationContext();
        int inSampleSize = 1;

        int yRatio = (int) Math.ceil(layoutHeight / videoWidth);
        int xRatio = (int) Math.ceil(layoutWidth / videoHeight);

        float max;
        if (mContext.getResources().getConfiguration().orientation== ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            //竖屏模式下按视频宽度计算放大倍数值
            max = Math.max((float) videoWidth / (float) layoutWidth,(float) videoHeight / (float) layoutHeight);
        } else{
            //横屏模式下按视频高度计算放大倍数值
            max = Math.max(((float) videoWidth/(float) layoutHeight),(float) videoHeight/(float) layoutWidth);
        }

        //视频宽高分别/最大倍数值 计算出放大后的视频尺寸
        videoWidth = (int) Math.ceil((float) videoWidth / max);
        videoHeight = (int) Math.ceil((float) videoHeight / max);


        return inSampleSize;
    }

    /**
     * 加载本地图片
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();

        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 建立对应 bitmap
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        // 建立对应 bitmap 的画布
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        // 把 drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    /** 水平方向模糊度 */
    private static float hRadius = 10;
    /** 竖直方向模糊度 */
    private static float vRadius = 10;
    /** 模糊迭代度 */
    private static int iterations = 7;

    /**
     * 高斯模糊处理
     *
     * @param bmp
     * @return
     */
    public static Drawable boxBlurFilter(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < iterations; i++) {
            blur(inPixels, outPixels, width, height, hRadius);
            blur(outPixels, inPixels, height, width, vRadius);
        }
        blurFractional(inPixels, outPixels, width, height, hRadius);
        blurFractional(outPixels, inPixels, height, width, vRadius);
        bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
        Drawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }

    private static void blur(int[] in, int[] out, int width, int height,
                             float radius) {
        int widthMinus1 = width - 1;
        int r = (int) radius;
        int tableSize = 2 * r + 1;
        int divide[] = new int[256 * tableSize];

        for (int i = 0; i < 256 * tableSize; i++)
            divide[i] = i / tableSize;

        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;
            int ta = 0, tr = 0, tg = 0, tb = 0;

            for (int i = -r; i <= r; i++) {
                int rgb = in[inIndex + clamp(i, 0, width - 1)];
                ta += (rgb >> 24) & 0xff;
                tr += (rgb >> 16) & 0xff;
                tg += (rgb >> 8) & 0xff;
                tb += rgb & 0xff;
            }

            for (int x = 0; x < width; x++) {
                out[outIndex] = (divide[ta] << 24) | (divide[tr] << 16)
                        | (divide[tg] << 8) | divide[tb];

                int i1 = x + r + 1;
                if (i1 > widthMinus1)
                    i1 = widthMinus1;
                int i2 = x - r;
                if (i2 < 0)
                    i2 = 0;
                int rgb1 = in[inIndex + i1];
                int rgb2 = in[inIndex + i2];

                ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
                tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
                tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
                tb += (rgb1 & 0xff) - (rgb2 & 0xff);
                outIndex += height;
            }
            inIndex += width;
        }
    }

    private static void blurFractional(int[] in, int[] out, int width,
                                       int height, float radius) {
        radius -= (int) radius;
        float f = 1.0f / (1 + 2 * radius);
        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;

            out[outIndex] = in[0];
            outIndex += height;
            for (int x = 1; x < width - 1; x++) {
                int i = inIndex + x;
                int rgb1 = in[i - 1];
                int rgb2 = in[i];
                int rgb3 = in[i + 1];

                int a1 = (rgb1 >> 24) & 0xff;
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;
                int a2 = (rgb2 >> 24) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = rgb2 & 0xff;
                int a3 = (rgb3 >> 24) & 0xff;
                int r3 = (rgb3 >> 16) & 0xff;
                int g3 = (rgb3 >> 8) & 0xff;
                int b3 = rgb3 & 0xff;
                a1 = a2 + (int) ((a1 + a3) * radius);
                r1 = r2 + (int) ((r1 + r3) * radius);
                g1 = g2 + (int) ((g1 + g3) * radius);
                b1 = b2 + (int) ((b1 + b3) * radius);
                a1 *= f;
                r1 *= f;
                g1 *= f;
                b1 *= f;
                out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
                outIndex += height;
            }
            out[outIndex] = in[width - 1];
            inIndex += width;
        }
    }

    private static int clamp(int x, int a, int b) {
        return (x < a) ? a : (x > b) ? b : x;
    }
}

