package com.bigboss.heartrate.module.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bigboss.heartrate.app.BaseActivity;
import com.bigboss.heartrate.fastheartrate.R;
import com.bigboss.heartrate.widget.CameraPreviewView;
import com.bigboss.heartrate.widget.CardiogView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.view.LineChartView;

import static com.bigboss.heartrate.util.CameraHelper.decodeYUV420StoGreyRGB;
import static com.bigboss.heartrate.util.CameraHelper.decodeYUV420StoGreyRGBandGetAvgGrey;
import static com.bigboss.heartrate.util.CameraHelper.decodeYUV420StoGreyRGBandRotate;

public class MainActivity extends BaseActivity {

    private CameraPreviewView mCameraPreviewView;
    private Camera mCamera;
    private ImageView mImageView;
    private CardiogView mCardiogView;

    @Override
    public int getStatusBarType() {
        return BaseActivity.TRANSPARENT_STATUS_BAR;
    }

    @Override
    public void initViews() {
        mCameraPreviewView = (CameraPreviewView) findViewById(R.id.camerapreviewview);
        mCameraPreviewView.setPreviewCallback(new MyPreviewCallback());
        mImageView = (ImageView) findViewById(R.id.iv_imageview);
        mCardiogView = (CardiogView) findViewById(R.id.cardiogview);
    }

    @Override
    protected void doAfterInitView() {

    }

    public void startMeasure(View view){
        mCameraPreviewView.openCameraFlashMode();
    }

    public void share(View view){
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

    class MyPreviewCallback implements Camera.PreviewCallback{

        private int[] mRgb;
        private Matrix matrix;
        private int count = 0;

        public MyPreviewCallback() {
            matrix = new Matrix();
            matrix.postRotate(90);
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            int width = camera.getParameters().getPreviewSize().width;
            int height = camera.getParameters().getPreviewSize().height;

            if (mRgb == null){
                mRgb = new int[width*height];
                mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }

            int avggrey = decodeYUV420StoGreyRGBandGetAvgGrey(mRgb, data, width, height);
            Bitmap bm = Bitmap.createBitmap(mRgb, width, height, Bitmap.Config.ARGB_8888);
            mImageView.setImageBitmap(bm);
            mCardiogView.putPoint(avggrey);
            count++;
            if (count == 100){
                System.out.println("100");
                count = 0;
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }
}
