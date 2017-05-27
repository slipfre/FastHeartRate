package com.bigboss.heartrate.module.splash;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import com.bigboss.heartrate.app.BaseActivity;
import com.bigboss.heartrate.fastheartrate.R;
import com.bigboss.heartrate.module.main.MainActivity;


public class SplashActivity extends BaseActivity {

    private static final int REQUEST_CODE_FOR_PERMISSION = 0;
    private static final int PERMISSION_DENY = 1;

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
            String[] permissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (checkandrequestPermissions(permissions, REQUEST_CODE_FOR_PERMISSION))
                goMain();
        } else {
            goMain();
        }
    }

    @Override
    public int getStatusBarType() {
        return BaseActivity.TRANSPARENT_STATUS_BAR;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_FOR_PERMISSION:
                if (getUngrantedPermissions(permissions).length == 0)
                    goMain();
                else
                    mHandler.sendEmptyMessageDelayed(PERMISSION_DENY, 1000);
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
}
