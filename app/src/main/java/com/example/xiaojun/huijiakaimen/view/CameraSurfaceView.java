package com.example.xiaojun.huijiakaimen.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.xiaojun.huijiakaimen.MyAppLaction;
import com.example.xiaojun.huijiakaimen.utils.FileUtil;
import com.tzutalin.dlib.VisionDetRet;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/9/8.
 */

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback {

    private static final String TAG = "CameraSurfaceView";
    private Context mContext;
    private SurfaceHolder holder;
    private Camera mCamera;
    private int mScreenWidth;
    private int mScreenHeight;
    private Bitmap bmp2=null;
    private static boolean isTrue4;
    private CameraBack cameraBack=null;


    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        isTrue4=true;
        getScreenMetrix(context);
        initView();
    }
    public void setCallBack(CameraBack callBack){
        this.cameraBack=callBack;
    }

    private void getScreenMetrix(Context context) {
        WindowManager WM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        WM.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
    }

    private void initView() {
        holder = getHolder();//获得surfaceHolder引用
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//设置类型
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i(TAG, "surfaceCreated");
        if (mCamera == null) {
            mCamera = Camera.open(1);//开启相机
            try {

                mCamera.setPreviewDisplay(holder);//摄像头画面显示在Surface上
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i(TAG, "surfaceChanged");

        setCameraParams(mCamera,width,height);

        mCamera.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("dddddddddddd", "surfaceDestroyed");

        if (!bmp2.isRecycled()) {
            bmp2.recycle();
        }
        bmp2 = null;

        holder.removeCallback(this);
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();//停止预览
        mCamera.lock();
        mCamera.release();//释放相机资源
        mCamera = null;
        holder = null;
        System.gc();

    }

    public void setStartPZ(){
        isTrue4=true;
    }
    public void stopView(){
        mCamera.stopPreview();
    }

    @Override
    public void onAutoFocus(boolean success, Camera Camera) {
        if (success) {
            Log.i(TAG, "onAutoFocus success="+success);
        }
    }

    private void setCameraParams(Camera camera, int width, int height) {
        Log.i(TAG,"setCameraParams  width="+width+"  height="+height);
        Camera.Parameters parameters = mCamera.getParameters();
        // 获取摄像头支持的PictureSize列表
        List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
        for (Camera.Size size : pictureSizeList) {
            Log.i(TAG, "pictureSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        /**从列表中选取合适的分辨率*/
        Camera.Size picSize = getProperSize(pictureSizeList, ((float) height / width));
        if (null == picSize) {
            Log.i(TAG, "null == picSize");
            picSize = parameters.getPictureSize();
        }
        Log.i(TAG, "picSize.width=" + picSize.width + "  picSize.height=" + picSize.height);
        // 根据选出的PictureSize重新设置SurfaceView大小
        float w = picSize.width;
        float h = picSize.height;
        parameters.setPictureSize(picSize.width,picSize.height);
        this.setLayoutParams(new FrameLayout.LayoutParams((int) (height*(h/w)), height));

        // 获取摄像头支持的PreviewSize列表
        List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();

        for (Camera.Size size : previewSizeList) {
            Log.i(TAG, "previewSizeList size.width=" + size.width + "  size.height=" + size.height);
        }
        Camera.Size preSize = getProperSize(previewSizeList, ((float) height) / width);
        if (null != preSize) {
            Log.i(TAG, "preSize.width=" + preSize.width + "  preSize.height=" + preSize.height);
            parameters.setPreviewSize(preSize.width, preSize.height);
        }

        parameters.setJpegQuality(100); // 设置照片质量
        if (parameters.getSupportedFocusModes().contains(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 连续对焦模式
        }
        mCamera.cancelAutoFocus();//自动对焦。
        mCamera.setDisplayOrientation(90);// 设置PreviewDisplay的方向，效果就是将捕获的画面旋转多少度显示
        mCamera.setParameters(parameters);

        mCamera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {

                Camera.Size size = camera.getParameters().getPreviewSize();
                try{
                    YuvImage image = new YuvImage(data, ImageFormat.NV21, size.width, size.height, null);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compressToJpeg(new android.graphics.Rect(0, 0, size.width, size.height), 100, stream);



                    bmp2 = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                    Log.d("CameraSurfaceView", "bmp2.getWidth():" + bmp2.getWidth());
                    Log.d("CameraSurfaceView", "bmp2.getHeight():" + bmp2.getHeight());
                    //旋转图片
                    Matrix matrix = new Matrix();
                    matrix.postRotate(270);
                    bmp2 = Bitmap.createBitmap(bmp2, 0, 0, bmp2.getWidth(), bmp2.getHeight(), matrix, true);


                    if (isTrue4) {
                        isTrue4=false;

                        List<VisionDetRet>  results = MyAppLaction.mFaceDet.detect(bmp2);

                        if (results!=null) {

                            int s = results.size();
                            Log.d("CameraSurfaceView", "s:" + s);
                           // VisionDetRet face;
                            if (s > 0) {
//                                if (s > count - 1) {
//
//                                    face = results.get(count - 1);
//
//                                } else {

                                //    face = results.get(0);

                             //   }

//                                int xx = 0;
//                                int yy = 0;
//                                int xx2 = 0;
//                                int yy2 = 0;
//                                int ww = bmp2.getWidth();
//                                int hh = bmp2.getHeight();
//                                if (face.getRight() - 150 >= 0) {
//                                    xx = face.getRight() - 150;
//                                } else {
//                                    xx = 0;
//                                }
//                                if (face.getTop() - 160 >= 0) {
//                                    yy = face.getTop() - 160;
//                                } else {
//                                    yy = 0;
//                                }
//                                if (xx + 200 <= ww) {
//                                    xx2 = 200;
//                                } else {
//                                    xx2 = ww - xx;
//                                }
//                                if (yy + 300 <= hh) {
//                                    yy2 = 300;
//                                } else {
//                                    yy2 = hh - yy;
//                                }
//
//
//                                Bitmap bitmap = Bitmap.createBitmap(bmp2, xx, yy, xx2, yy2);

//                                Message message = Message.obtain();
//                                message.what = MESSAGE_QR_SUCCESS;
//                                message.obj = bmp2;
//                                mHandler2.sendMessage(message);


                                String fn = "bbbb.jpg";
                                FileUtil.isExists(FileUtil.PATH, fn);
                                saveBitmap2File2(bmp2, FileUtil.SDPATH + File.separator + FileUtil.PATH + File.separator + fn, 100);

                            } else {
                                isTrue4 = true;
                            }

//                                    if (isTrue4) {
//                                        isTrue4=false;
//                                        Log.d("InFoActivity3", "获取图片2222");
//
//                                        Bitmap bmpf = bmp2.copy(Bitmap.Config.RGB_565, true);
                            //返回识别的人脸数
                            //	int faceCount = new FaceDetector(bmpf.getWidth(), bmpf.getHeight(), 1).findFaces(bmpf, facess);
                            //	FaceDetector faceCount2 = new FaceDetector(bmpf.getWidth(), bmpf.getHeight(), 2);

//                                        myFace = new FaceDetector.Face[numberOfFace];       //分配人脸数组空间
//                                        myFaceDetect = new FaceDetector(bmpf.getWidth(), bmpf.getHeight(), numberOfFace);
//                                        numberOfFaceDetected = myFaceDetect.findFaces(bmpf, myFace);    //FaceDetector 构造实例并解析人脸
//
//                                        if (numberOfFaceDetected > 0) {
//
//                                            FaceDetector.Face face;
//                                            if (numberOfFaceDetected > count - 1) {
//                                                face = myFace[count - 1];
//
//                                            } else {
//                                                face = myFace[0];
//
//                                            }
//
//                                            PointF pointF = new PointF();
//                                            face.getMidPoint(pointF);
//                                            Log.d("InFoActivity2", "pointF.x:" + pointF.x);
//                                            Log.d("InFoActivity2", "pointF.y:" + pointF.y);
//
//                                            //  myEyesDistance = (int)face.eyesDistance();
//
//                                            int xx = 0;
//                                            int yy = 0;
//                                            int xx2 = 0;
//                                            int yy2 = 0;
//
//                                            if ((int) pointF.x - 200 >= 0) {
//                                                xx = (int) pointF.x - 200;
//                                            } else {
//                                                xx = 0;
//                                            }
//                                            if ((int) pointF.y - 200 >= 0) {
//                                                yy = (int) pointF.y - 200;
//                                            } else {
//                                                yy = 0;
//                                            }
//                                            if (xx + 350 >= bmp2.getWidth()) {
//                                                xx2 = bmp2.getWidth() - xx;
//                                                Log.d("fff", "xxxxxxxxxx:" + xx2);
//                                            } else {
//                                                xx2 = 350;
//                                            }
//                                            if (yy + 350 >= bmp2.getHeight()) {
//                                                yy2 = bmp2.getHeight() - yy;
//                                                Log.d("fff", "yyyyyyyyy:" + yy2);
//                                            } else {
//                                                yy2 = 350;
//                                            }
//                                            Log.d("InFoActivity2", "xx:" + xx);
//                                            Log.d("InFoActivity2", "yy:" + yy);
//                                            Log.d("InFoActivity2", "xx2:" + xx2);
//                                            Log.d("InFoActivity2", "yy2:" + yy2);
//
//                                            Bitmap bitmap = Bitmap.createBitmap(bmp2, xx, yy, xx2, yy2);
//
//                                            //  Bitmap bitmap = Bitmap.createBitmap(bitmapBig,0,0,bitmapBig.getWidth(),bitmapBig.getHeight());
//
//                                            Message message = Message.obtain();
//                                            message.what = MESSAGE_QR_SUCCESS;
//                                            message.obj = bitmap;
//                                            mHandler2.sendMessage(message);
//
//
//                                            String fn = "bbbb.jpg";
//                                            FileUtil.isExists(FileUtil.PATH, fn);
//                                            saveBitmap2File2(bitmap, FileUtil.SDPATH + File.separator + FileUtil.PATH + File.separator + fn, 100);
//
//                                        }else {
//                                            isTrue4=true;
//                                        }
//
//                                        bmpf.recycle();
//                                        bmpf = null;

                        }

                    }
                    stream.close();

                }catch(Exception ex){
                    Log.e("Sys","Error:"+ex.getMessage());
                }
            }
        });

    }

    /**
     * 从列表中选取合适的分辨率
     * 默认w:h = 4:3
     * <p>注意：这里的w对应屏幕的height
     *            h对应屏幕的width<p/>
     */
    private Camera.Size getProperSize(List<Camera.Size> pictureSizeList, float screenRatio) {
        Log.i(TAG, "screenRatio=" + screenRatio);
        Camera.Size result = null;
        for (Camera.Size size : pictureSizeList) {
            float currentRatio = ((float) size.width) / size.height;
            if (currentRatio - screenRatio == 0) {
                result = size;
                break;
            }
        }

        if (null == result) {
            for (Camera.Size size : pictureSizeList) {
                float curRatio = ((float) size.width) / size.height;
                if (curRatio == 4f / 3) {// 默认w:h = 4:3
                    result = size;
                    break;
                }
            }
        }

        return result;
    }
    public  void saveBitmap2File2(Bitmap bm, final String path, int quality) {
        try {

            if (null == bm) {
                Log.d("InFoActivity", "回收|空");
                return ;
            }

            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();

           cameraBack.Back(path);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {

			if (!bm.isRecycled()) {
				bm.recycle();
			}
            bm = null;
        }
    }



}