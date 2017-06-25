package com.bigboss.heartrate.module.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.SystemClock;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigboss.heartrate.app.BaseActivity;
import com.bigboss.heartrate.fastheartrate.R;
import com.bigboss.heartrate.util.Complex;
import com.bigboss.heartrate.util.FFT;
import com.bigboss.heartrate.util.RateCalculate;
import com.bigboss.heartrate.widget.CameraPreviewView;
import com.bigboss.heartrate.widget.CardiogView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import static com.bigboss.heartrate.util.CameraHelper.calculateAvgGrey;
import static com.bigboss.heartrate.util.CameraHelper.decodeYUV420StoGreyRGBandGetAvgGrey;

public class MainActivity extends BaseActivity {

    private CameraPreviewView mCameraPreviewView;
    private Camera mCamera;
//    private ImageView mImageView;
    private CardiogView mCardiogView;
    private boolean started = false;
    private TextView mTv_heartrate;

    @Override
    public int getStatusBarType() {
        return BaseActivity.TRANSPARENT_STATUS_BAR;
    }

    @Override
    public void initViews() {
        mCameraPreviewView = (CameraPreviewView) findViewById(R.id.camerapreviewview);
        mCameraPreviewView.setPreviewCallback(new MyPreviewCallback());
//        mImageView = (ImageView) findViewById(R.id.iv_imageview);
        mCardiogView = (CardiogView) findViewById(R.id.cardiogview);

        mCameraPreviewView.openCameraFlashMode();
        mTv_heartrate = (TextView) findViewById(R.id.tv_heartrate);
    }

    @Override
    protected void doAfterInitView() {
    }

    public void startMeasure(View view) {
//        mCameraPreviewView.openCameraFlashMode();
        started = !started;
    }

    public void share(View view) {
        UMImage image = new UMImage(MainActivity.this, R.drawable.umeng_socialize_qq);//资源文件

        new ShareAction(MainActivity.this)
                .setPlatform(SHARE_MEDIA.QQ)
                .withText("爆炸了")
                .withMedia(image)
                .setCallback(new MyUMShareListener(getApplicationContext()))
                .share();
    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_main;
    }

    class MyPreviewCallback implements Camera.PreviewCallback {

        private int[] mRgb;
        private Matrix matrix;
        private int count = 0;
        Complex[] avggreyArray;
        private long mStartTime;
        private long mEndTime;
        private int frameCount;

        public MyPreviewCallback() {
            matrix = new Matrix();
            matrix.postRotate(90);
            frameCount = 128;
            avggreyArray = new Complex[frameCount];
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {

            int width = camera.getParameters().getPreviewSize().width;
            int height = camera.getParameters().getPreviewSize().height;

            if (mRgb == null) {
                mRgb = new int[width * height];
//                mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }

            float avggrey = calculateAvgGrey(data, width, height);
//            Bitmap bm = Bitmap.createBitmap(mRgb, width, height, Bitmap.Config.ARGB_8888);
//            mImageView.setImageBitmap(bm);

            if (started == true) {
                mCardiogView.putPoint(avggrey);
                //新加，把取到的灰度值散点放到数组中，用于后续的FFT处理
                avggreyArray[count] = new Complex((double)avggrey, 0.0);

                if (count == 0){
                    mStartTime = SystemClock.uptimeMillis();
                }

                count++;

                if (count == frameCount) {
                    mEndTime = SystemClock.uptimeMillis();
                    double samplingRate = 1000.0/((mEndTime-mStartTime)/(double)frameCount);
                    Complex[] rateArray = FFT.fft(avggreyArray);
                    int maxNum = 1;
                    double max = rateArray[1].abs();
                    for (int i = 2; i < rateArray.length/2; i++) {
                        double absnum = rateArray[i].abs();
                        System.out.println();
                        if (absnum > max){
                            max = absnum;
                            maxNum = i;
                        }
                    }
//                    FFT.show(rateArray, "result:");
//                    StringBuffer buffer = new StringBuffer("");
//                    for (Complex c:avggreyArray
//                         ) {
//                        buffer.append(c.getRe() + " ");
//                    }
                    int heartrate = (int)(60*(double)(samplingRate*maxNum/frameCount));
                    if (heartrate >= 40 && heartrate <= 200)
                        mTv_heartrate.setText("心率:" + heartrate + "次/分  (•̀ᴗ•́)و ̑̑ ");
                    else
                        mTv_heartrate.setText("心率:" + " 手指要放好啊 ( ＿ ＿)ノ｜扶墙");
//                    System.out.println(buffer);
//                    System.out.println("SamplingRate:" + samplingRate);
                    count = 0;
                }
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }
}
