package com.xb.xblibrary.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.xb.xblibrary.R;
import com.xb.xblibrary.camera.CameraManager;
import com.xb.xblibrary.decoding.CaptureActivityHandler;
import com.xb.xblibrary.decoding.InactivityTimer;
import com.xb.xblibrary.decoding.IsChineseOrNot;
import com.xb.xblibrary.decoding.RGBLuminanceSource;
import com.xb.xblibrary.utils.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by Administrator on 2018/4/20.
 */

public abstract class BaseScanActivity  extends Activity implements SurfaceHolder.Callback {
    private final String TAG="QRScanActivity";
    private final int REQUEST_CODE=1000;
    //光线感应
    private SensorManager sm;
    private Sensor ligthSensor;
    float lightdata=0;

    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private SurfaceView surfaceView;
    private boolean hasSurface=false;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    private SharedPreferences mPreferences;
    private int zoomTo=-1;
    private String photo_path="";

    private Boolean inittrue=false;

    /* Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(intiLayout());
        initView();
        mPreferences = getSharedPreferences("APPSETTING", Context.MODE_PRIVATE);
        mPreferences.edit();

        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        ligthSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        sm.registerListener(new MySensorListener(), ligthSensor, SensorManager.SENSOR_DELAY_NORMAL);
        try {
            Intent intent = BaseScanActivity.this.getIntent();
            zoomTo = intent.getExtras().getInt("zoomto");
        } catch (Exception e) {
            zoomTo=-1;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int c = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i != PackageManager.PERMISSION_GRANTED||c != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(BaseScanActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, 1);
            }
        }
        CameraManager.init(getApplication());
        surfaceView=findViewById(initSurfaceView());
        viewfinderView=findViewById(initViewfinderView());
        if(surfaceView==null||viewfinderView==null) {
            Log.e("BaseScanActivity", "BaseScanActivity init fail");
            finish();
        }
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    /**
     * 设置布局
     *
     * @return 页面ID
     */
    public abstract int intiLayout();

    /**
     * 初始化布局
     */
    public abstract void initView();

    /**
     * 初始化预览控件
     * @return SurfaceView控件ID
     */
    public abstract int initSurfaceView();

    /**
     * 初始化扫描控件
     * @return ViewfinderView控件ID
     */
    public abstract int initViewfinderView();

    public void OpenAndCloseLight(){
        CameraManager.get().flashHandler();
    }

    public void ScanPicQR(){
        Intent innerIntent = new Intent(); // "android.intent.action.GET_CONTENT"
        if (Build.VERSION.SDK_INT < 19) {
            innerIntent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            innerIntent.setAction(Intent.ACTION_PICK);
        }
        innerIntent.setType("image/*");
        Intent wrapperIntent = Intent.createChooser(innerIntent, "选择二维码图片");
        BaseScanActivity.this.startActivityForResult(wrapperIntent, REQUEST_CODE);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        if(surfaceView!=null&&viewfinderView!=null){
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            if (hasSurface) {
                initCamera(surfaceHolder);
            } else {
                surfaceHolder.addCallback(this);
                surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            }

            decodeFormats = null;
            characterSet = "ISO-8859-1";//null;

            playBeep = true;
            AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
            if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
                playBeep = false;
            }
            initBeepSound();
            vibrate = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(selectedImage,  filePathColumn, null, null, null);
                    if(cursor!=null)
                    {
                        if (cursor.moveToFirst()) {
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            photo_path = cursor.getString(column_index);
                            if (photo_path == null) {
                                photo_path = Utils.getPath(getApplicationContext(),data.getData());
                            }
                        }
                        cursor.close();
                    }
                    else
                    {
                        photo_path = Utils.getPath(getApplicationContext(),data.getData());//selectedImage.getPath();
                    }
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Result result = scanningImage(photo_path);
                            // String result = decode(photo_path);
                            if (result == null) {
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(), "识别失败！可能需要要先对图片进行处理。裁剪剔除多余部分！", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            } else {
                                handleDecode(result,null);
                            }
                        }
                    }).start();
                    break;
            }
        }
    }

    /**
     * @param result 扫描结果
     * @param barcode  扫描的图片
     */
    public void handleDecode(Result result, Bitmap barcode) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        String resultString = result.getText();
        if (resultString.equals("")) {
            Toast.makeText(BaseScanActivity.this, "Scan failed!", Toast.LENGTH_SHORT).show();
        }else {
            String scan_result="";
            try {
                scan_result=new String(resultString.getBytes("ISO-8859-1"),"UTF-8");
                if(IsChineseOrNot.isSpecialCharacter(scan_result))
                {
                    scan_result=new String(resultString.getBytes("ISO-8859-1"),"GB2312");
                }
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("name", result.getBarcodeFormat().name());
            bundle.putString("result", scan_result);
            //bundle.putParcelable("bitmap", barcode);
            resultIntent.putExtras(bundle);
            this.setResult(RESULT_OK, resultIntent);
        }
        BaseScanActivity.this.finish();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(BaseScanActivity.this, decodeFormats,characterSet);
            handler.setZoom(zoomTo);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 200: {
                if (ContextCompat.checkSelfPermission(BaseScanActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    CameraManager.init(getApplication());
                }
            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

        Boolean mlight=mPreferences.getBoolean("mylight",false);
        if(mlight)
            CameraManager.get().flashHandler();
        else
        {
            if(lightdata<6)
            {
                CameraManager.get().flashHandler();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }


    protected Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        // DecodeHintType 和EncodeHintType
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "ISO-8859-1"); // 设置二维码内容的编码
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false; // 先获取原大小
        Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);

        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        scanBitmap=convertToBlackWhite(scanBitmap);
        RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result result=null;
        try {
            result= reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            Log.e(TAG,e.toString()+"---------"+e.getMessage());
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        if(result==null)
            result=recognition(1,path);
        return result;
    }

    private Result recognition(int i, String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false; // 先获取原大小
        Bitmap tempbitmap = BitmapFactory.decodeFile(path, options);
        Matrix matrix = new Matrix();
        float width=tempbitmap.getWidth()/i;
        float height=tempbitmap.getHeight()/i;
        matrix.setScale(width/tempbitmap.getWidth(), height/tempbitmap.getHeight());
        tempbitmap = Bitmap.createBitmap(tempbitmap, 0, 0, tempbitmap.getWidth(),
                tempbitmap.getHeight(), matrix, true);
        tempbitmap=convertToBlackWhite(tempbitmap);
        Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
        hints.put(DecodeHintType.CHARACTER_SET, "ISO-8859-1"); // 设置二维码内容的编码
        RGBLuminanceSource source = new RGBLuminanceSource(tempbitmap);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        Result result=null;
        try {
            result= reader.decode(bitmap1, hints);
        } catch (NotFoundException e) {
            Log.e(TAG,e.toString()+"---------"+e.getMessage());
            e.printStackTrace();
        } catch (ChecksumException e) {
            Log.e(TAG,e.toString()+"---------"+e.getMessage());
            e.printStackTrace();
        } catch (FormatException e) {
            Log.e(TAG,e.toString()+"---------"+e.getMessage());
            e.printStackTrace();
        }
        if(result==null) {
            source=null;
            bitmap1=null;
            tempbitmap=null;
            reader=null;
            i=i*2;
            if(i>16)
                return null;
            else
                return recognition(i, path);
        }
        else
            return result;
    }

    /**
     * 将彩色图转换为纯黑白二色
     * @param bmp 位图
     * @return 返回转换好的位图
     */
    public static Bitmap convertToBlackWhite(Bitmap bmp) {
        int width = bmp.getWidth(); // 获取位图的宽
        int height = bmp.getHeight(); // 获取位图的高
        int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组

        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                //分离三原色
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                //转化成灰度像素
                grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        //新建图片
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        //设置图片数据
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return newBmp;
    }


    private String recode(String str) {
        String formart = "";
        try {
            boolean ISO = Charset.forName("ISO-8859-1").newEncoder().canEncode(str);
            if (ISO) {
                formart = new String(str.getBytes("ISO-8859-1"), "GB2312");
            } else {
                formart = str;
            }
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return formart;
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    public class MySensorListener implements SensorEventListener {
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        public void onSensorChanged(SensorEvent event) {
            float lux = event.values[0];
            lightdata=lux;
        }

    }
}
