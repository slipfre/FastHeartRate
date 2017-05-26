package com.bigboss.heartrate.module.splash;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bigboss.heartrate.app.BaseActivity;
import com.bigboss.heartrate.fastheartrate.R;
import com.bigboss.heartrate.module.main.MainActivity;
import com.bigboss.heartrate.widget.CameraPreviewView;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

/**
 * Created by BOY on 2017/5/26.
 */

public class SplashActivity extends BaseActivity {

    private static final int REQUEST_CODE_FOR_CAMERA = 0;
    private static final int PERMISSION_DENY = 1;
    private static final int REQUEST_EXTERNAL_STORAGE = 2;

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //分享开始的回调
            System.out.println("sfdgsgdgfdghkjgkj");
        }
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("plat","platform"+platform);

            Toast.makeText(SplashActivity.this, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(SplashActivity.this,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
            if(t!=null){
                Log.d("throw","throw:"+t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(SplashActivity.this,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PERMISSION_DENY:
                    finish();
            }
        }
    };

    @Override
    protected void initViews() {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_splash;
    }

    @Override
    protected void doAfterInitView() {
        if (Build.VERSION.SDK_INT >= 23) {
            int cameraPermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
            int writeExternalStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (cameraPermission != PackageManager.PERMISSION_GRANTED && writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_FOR_CAMERA);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
            } else if (cameraPermission != PackageManager.PERMISSION_GRANTED && writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_FOR_CAMERA);
            } else if (cameraPermission == PackageManager.PERMISSION_GRANTED && writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
            } else if (cameraPermission == PackageManager.PERMISSION_GRANTED && writeExternalStoragePermission != PackageManager.PERMISSION_GRANTED){
//                goMain();
            }
        } else {
//            goMain();
        }
    }

    @Override
    public int getStatusBarType() {
        return BaseActivity.TRANSPARENT_STATUS_BAR;
    }

    public void test(View view){
        UMImage image = new UMImage(SplashActivity.this, R.drawable.umeng_socialize_qzone);//资源文件

        new ShareAction(SplashActivity.this).withText("爆炸了")
                .withMedia(image).setPlatform(SHARE_MEDIA.QQ)
                .setCallback(umShareListener)
                .share();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_FOR_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    goMain();
                } else {
                    Toast.makeText(SplashActivity.this, "CAMERA permission Denied", Toast.LENGTH_SHORT)
                            .show();
                    mHandler.sendEmptyMessageDelayed(PERMISSION_DENY, 1000);
                }
                break;
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    goMain();
                } else {
                    Toast.makeText(SplashActivity.this, "CAMERA permission Denied", Toast.LENGTH_SHORT)
                            .show();
                    mHandler.sendEmptyMessageDelayed(PERMISSION_DENY, 1000);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void goMain() {
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }
}
