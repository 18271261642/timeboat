package net.sgztech.timeboat.ui.newui;


import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import com.blala.blalable.BleConstant;
import com.blala.blalable.listener.WriteBackDataListener;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraException;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraOptions;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Mode;
import com.otaliastudios.cameraview.controls.Preview;

import net.sgztech.timeboat.R;
import net.sgztech.timeboat.util.DisplayUtils;
import net.sgztech.timeboat.util.ToastUitlKt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * 相机页面
 *
 * @author 中庸
 * @date 2016/5/20
 */

public class CamaraActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = CamaraActivity.class.getSimpleName();

    CameraView cameraView;
    //是否正在拍照
    private boolean isTakingPhoto;


    ImageView ivSwitch;
    ImageView iv_back;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara2);

        initView();

        initData();

    }


    private void initView() {
        ivSwitch = findViewById(R.id.iv_switch);
        iv_back = findViewById(R.id.iv_back);
        cameraView = findViewById(R.id.camera);
        cameraView.setLifecycleOwner(this);
        cameraView.addCameraListener(new Listener());


        ivSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCamera();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private class Listener extends CameraListener {
        @Override
        public void onCameraOpened(@NonNull CameraOptions options) {
           /* Option frameRate = new Option.PreviewFrameRate();
            frameRate.set(camera, 20);
            camera.getVideoSize();*/
            Log.e(TAG, "onCameraOpened");
        }

        @Override
        public void onCameraError(@NonNull CameraException exception) {
            super.onCameraError(exception);
            Log.e(TAG, "onCameraError");
            //message("Got CameraException #" + exception.getReason(), true);
        }

        @Override
        public void onPictureTaken(@NonNull PictureResult result) {
            super.onPictureTaken(result);
            isTakeSuccess = true;
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    isTakeSuccess = true;
//                }
//            }, 500);
            Log.e(TAG, "onPictureTaken");

            Message message = handler.obtainMessage();
            message.what = 0x01;
            message.obj = result;
            handler.sendMessageDelayed(message, 500);
            if (cameraView.isTakingVideo()) {
                // message("Captured while taking video. Size=" + result.getSize(), false);
                return;
            }
        }

    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0x01) {
                PictureResult pictureResult = (PictureResult) msg.obj;
                try {
                    pictureResult.toBitmap(DisplayUtils.getScreenWidth(CamaraActivity.this), DisplayUtils.getScreenHeight(CamaraActivity.this), new BitmapCallback() {
                        @Override
                        public void onBitmapReady(Bitmap bitmap) {
                            saveBitmap(bitmap, System.currentTimeMillis() + "");
                        }
                    });
                } catch (UnsupportedOperationException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    Long mCaptureTime;

    //开始拍照
    private void capturePicture() {
        if (cameraView.getMode() == Mode.VIDEO) {
            return;
        }
        if (cameraView.isTakingPicture()) return;
        mCaptureTime = System.currentTimeMillis();
        //message("Capturing picture...", false);

        cameraView.takePicture();
    }

    private void capturePictureSnapshot() {
        if (cameraView.isTakingPicture()) return;
        if (cameraView.getPreview() != Preview.GL_SURFACE) {
            //message("Picture snapshots are only allowed with the GL_SURFACE preview.", true);
            return;
        }
        mCaptureTime = System.currentTimeMillis();
        // message("Capturing picture snapshot...", false);
        cameraView.takePictureSnapshot();
    }


    private void initData() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BleConstant.COMM_BROADCAST_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        // verifyPermission(new String[]{Manifest.permission.CAMERA});
        Log.e("mainService", "6666");
    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null)
                return;

            if (action.equals(BleConstant.COMM_BROADCAST_ACTION)) {
                int[] value = intent.getIntArrayExtra(BleConstant.COMM_BROADCAST_KEY);
                if (value[0] == 1) { //开始拍照
                    capturePicture();
                }
            }
        }
    };


    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    boolean isTakeSuccess = true;


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.surfaceView:
//                break;
        }
    }


    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }


    private int rotateDegress;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }


    /*
     * 保存文件，文件名为当前日期
     */
    public boolean saveBitmap(Bitmap bitmap, String bitName) {
        String fileName;
        File file;
        String brand = Build.BRAND;
        brand = brand.toLowerCase();
        if (brand.toLowerCase().equals("xiaomi")) { // 小米手机brand.equals("xiaomi")
            fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + bitName;
        } else if (brand.equalsIgnoreCase("Huawei")) {
            fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + bitName;
        } else { // Meizu 、Oppo
            fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/" + bitName;
        }
//        fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + bitName;
        if (Build.VERSION.SDK_INT >= 29) {
//            boolean isTrue = saveSignImage(bitName, bitmap);
            saveSignImage(bitName, bitmap);
            return true;
//            file= getPrivateAlbumStorageDir(NewPeoActivity.this, bitName,brand);
//            return isTrue;
        } else {
            Log.v("saveBitmap brand", "" + brand);
            file = new File(fileName);
        }
        if (file.exists()) {
            file.delete();
        }


        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                out.flush();
                out.close();
                // 插入图库
                if (Build.VERSION.SDK_INT >= 29) {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                    Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                } else {
                    MediaStore.Images.Media.insertImage(this.getContentResolver(), file.getAbsolutePath(), bitName, null);
                }

                ToastUitlKt.showSnackbarShort(CamaraActivity.this, "保存成功!");

            }
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", "FileNotFoundException:" + e.getMessage().toString());
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            Log.e("IOException", "IOException:" + e.getMessage().toString());
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            Log.e("IOException", "IOException:" + e.getMessage().toString());
            e.printStackTrace();
            return false;

// 发送广播，通知刷新图库的显示

        }
//        if(Build.VERSION.SDK_INT >= 29){
//            copyPrivateToDownload(this,file.getAbsolutePath(),bitName);
//        }
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));

        return true;

    }

    private void toggleCamera() {
        if (cameraView.isTakingPicture() || cameraView.isTakingVideo()) return;
        switch (cameraView.toggleFacing()) {
            case BACK:
                //message("Switched to back camera!", false);
                break;

            case FRONT:
                //message("Switched to front camera!", false);
                break;
        }
    }

    //将文件保存到公共的媒体文件夹
//这里的filepath不是绝对路径，而是某个媒体文件夹下的子路径，和沙盒子文件夹类似
//这里的filename单纯的指文件名，不包含路径
    public void saveSignImage(/*String filePath,*/String fileName, Bitmap bitmap) {
        try {
            //设置保存参数到ContentValues中
            ContentValues contentValues = new ContentValues();
            //设置文件名
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            //兼容Android Q和以下版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                //android Q中不再使用DATA字段，而用RELATIVE_PATH代替
                //RELATIVE_PATH是相对路径不是绝对路径
                //DCIM是系统文件夹，关于系统文件夹可以到系统自带的文件管理器中查看，不可以写没存在的名字
                contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/");
                //contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Music/signImage");
            } else {
                contentValues.put(MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath());
            }
            //设置文件类型
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/JPEG");
            //执行insert操作，向系统文件夹中添加文件
            //EXTERNAL_CONTENT_URI代表外部存储器，该值不变
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            if (uri != null) {
                //若生成了uri，则表示该文件添加成功
                //使用流将内容写入该uri中即可
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                    outputStream.flush();
                    outputStream.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }


}
