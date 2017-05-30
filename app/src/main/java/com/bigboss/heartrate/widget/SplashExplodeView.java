package com.bigboss.heartrate.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.bigboss.heartrate.fastheartrate.R;

/**
 * Created by BOY on 2017/5/31.
 */

public class SplashExplodeView extends View {

    private static final int LARGER_CIRCLE = 0;

    private int mHeight;
    private int mWidth;
    private Paint mPaint;
    private float mRadius;
    private float curRadius;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LARGER_CIRCLE:
                    curRadius = (float)(1.0f - Math.pow((1.0f - curRadius), 1.13));
                    invalidate();
                    break;
            }
        }
    };

    public SplashExplodeView(Context context) {
        super(context);
        init();
    }

    public SplashExplodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SplashExplodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SplashExplodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void init(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(getResources().getColor(R.color.colorRed));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        mRadius = (float)Math.sqrt(mWidth*mWidth+mHeight*mHeight)+100;
        curRadius = 0.01f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(0,0,curRadius*mRadius,mPaint);
        if (curRadius < mRadius){
            mHandler.sendEmptyMessageDelayed(LARGER_CIRCLE, 6);
        }
    }

    public void start(){

    }
}
