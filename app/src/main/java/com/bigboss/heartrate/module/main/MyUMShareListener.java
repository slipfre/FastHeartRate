package com.bigboss.heartrate.module.main;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

public class MyUMShareListener implements UMShareListener {

    private Context mContext;

    public MyUMShareListener(Context context) {
        mContext = context;
    }

    @Override
    public void onStart(SHARE_MEDIA platform) {
        //分享开始的回调
        System.out.println("sfdgsgdgfdghkjgkj");
    }

    @Override
    public void onResult(SHARE_MEDIA platform) {
        Log.d("plat","platform"+platform);

        Toast.makeText(mContext, platform + " 分享成功啦", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onError(SHARE_MEDIA platform, Throwable t) {
        Toast.makeText(mContext,platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
        if(t!=null){
            Log.d("throw","throw:"+t.getMessage());
        }
    }

    @Override
    public void onCancel(SHARE_MEDIA platform) {
        Toast.makeText(mContext,platform + " 分享取消了", Toast.LENGTH_SHORT).show();
    }
}
