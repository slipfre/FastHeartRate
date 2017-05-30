package com.bigboss.heartrate.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.bigboss.heartrate.fastheartrate.R;

import java.util.LinkedList;
import java.util.List;

public class CardiogView extends View {

    private int mHeight;
    private int mWidth;
    private float mDistanceX;
    private int mColor;
    private float mStartY;
    private float mEndY;
    private float mDistanceY;
    private List<Float> points;
    private Paint mPaint;
    private Paint mPointPaint;

    public CardiogView(Context context) {
        super(context);
    }

    //AttributeSet属性集合
    public CardiogView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.CardiogView);
        mColor = mTypedArray.getColor(R.styleable.CardiogView_linecolor, getResources().getColor(R.color.colorPrimary));
        mDistanceX = mTypedArray.getDimension(R.styleable.CardiogView_distanceX, 10.0f);
        mStartY = mTypedArray.getFloat(R.styleable.CardiogView_startY, -100.0f);
        mEndY = mTypedArray.getFloat(R.styleable.CardiogView_endY, 100.0f);
        init();
    }

    public CardiogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public CardiogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs);
    }

    private void init(){
        mPaint = new Paint();
        PathEffect mPathEffect = new CornerPathEffect(2.0f);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(5.0f);
        mPointPaint = new Paint();
        mPointPaint.setColor(getResources().getColor(R.color.colorAccent));
        mPointPaint.setStyle(Paint.Style.STROKE);
        mPointPaint.setStrokeWidth(5.0f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        mDistanceY = calculateDistanceY(mHeight);
        points = initPoints(w);
    }

    private float calculateDistanceY(float height){
        return height/Math.abs(mEndY - mStartY);                                   //mEndY   mStartY   坐标范围    函数返回Y值间距的长度
    }

    private List<Float> initPoints(float width){
        int size = (int) (width / mDistanceX);
        float initial = (mStartY + mEndY) / 2;
        List<Float> points = new LinkedList<Float>();
        for (int i = 0; i < size; i++){
            points.add(initial);
        }
        return points;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(0, mHeight);
        canvas.scale(1.0f, -1.0f);
        Path path = new Path();
        float x = 0;
        float y = (points.get(0) - mStartY)*mDistanceY;
        path.moveTo(x, y);
        for (int i = 1; i < points.size(); i++) {
            x += mDistanceX;
            y = (points.get(i) - mStartY)*mDistanceY;
            path.lineTo(x, y);
            canvas.drawCircle(x, y, 6.0f, mPointPaint);
        }
        canvas.drawPath(path, mPaint);
    }

    public void putPoint(float y){
        points.remove(0);
        points.add(y);
        invalidate();
    }
}
