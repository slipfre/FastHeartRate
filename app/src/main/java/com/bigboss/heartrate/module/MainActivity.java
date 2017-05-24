package com.bigboss.heartrate.module;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.bigboss.heartrate.app.BaseActivity;
import com.bigboss.heartrate.fastheartrate.R;
import com.bigboss.heartrate.widget.CameraPreviewView;

public class MainActivity extends BaseActivity {

    // CAMERA权限请求码
    private static final int REQUEST_CODE_FOR_CAMERA = 0;

    private CameraPreviewView mCameraPreviewView;
    private Camera mCamera;

    @Override
    public int getStatusBarType() {
        return BaseActivity.TRANSPARENT_STATUS_BAR;
    }

    @Override
    public void initViews() {
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_FOR_CAMERA);
            }else{
                mCameraPreviewView = (CameraPreviewView) findViewById(R.id.camerapreviewview);
            }
        }else{
            mCameraPreviewView = (CameraPreviewView) findViewById(R.id.camerapreviewview);
        }
    }

    @Override
    protected void doAfterInitView() {

    }

    @Override
    public int getContentViewID() {
        return R.layout.activity_main;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_FOR_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mCameraPreviewView = (CameraPreviewView) findViewById(R.id.camerapreviewview);
                } else {
                    Toast.makeText(MainActivity.this, "CAMERA permission Denied", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
