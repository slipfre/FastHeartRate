package com.bigboss.heartrate.module.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.view.View;
import android.widget.ImageView;

import com.bigboss.heartrate.app.BaseActivity;
import com.bigboss.heartrate.fastheartrate.R;
import com.bigboss.heartrate.widget.CameraPreviewView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import static com.bigboss.heartrate.util.CameraHelper.decodeYUV420SPnooffset;
import static com.bigboss.heartrate.util.CameraHelper.transformYUV420SPtoGreyScale;

public class MainActivity extends BaseActivity {

    private CameraPreviewView mCameraPreviewView;
    private Camera mCamera;
    private ImageView mImageView;

    @Override
    public int getStatusBarType() {
        return BaseActivity.TRANSPARENT_STATUS_BAR;
    }

    @Override
    public void initViews() {
        mCameraPreviewView = (CameraPreviewView) findViewById(R.id.camerapreviewview);
        mCameraPreviewView.setPreviewCallback(new MyPreviewCallback());
        mImageView = (ImageView) findViewById(R.id.iv_imageview);
    }

    @Override
    protected void doAfterInitView() {

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

        public MyPreviewCallback() {
            matrix = new Matrix();
            matrix.postRotate(90);
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            int width = camera.getParameters().getPreviewSize().width;
            int height = camera.getParameters().getPreviewSize().height;

            if (mRgb == null)
                mRgb = new int[width*height];

            transformYUV420SPtoGreyScale(data, width, height);
            decodeYUV420SPnooffset(mRgb, data, width, height);
            Bitmap bm = Bitmap.createBitmap(mRgb, width, height, Bitmap.Config.ARGB_8888);
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            mImageView.setImageBitmap(bm);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }
}
