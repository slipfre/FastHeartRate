### 一、实现原理
在心脏泵出血液的同时，我们指间的血液容量会变大，此时指尖对光的吸收能力比较强，如果用打开手机的闪光灯，用手指抵着摄像头的话，会发现此时得到的指尖图像比较暗。随着心脏的持续搏动，指尖的血液容量会随着心脏周期性变化，指尖对光线的吸收能力也会呈现周期性的变化。

利用手机的闪光灯和摄像头和指尖对光照吸收能力的变化规律，通过捕获一系列的指尖图像，分析这些图像的明暗变化可以得到被测者的心率。

最终实现效果如下：

<div align=center><img src="https://github.com/WoHohohoho/FastHeartRate/blob/master/media/example.gif"/></div>

### 二、主要内容
1.	操作手机的摄像头和闪光灯获得指尖图像
2.	分析得到图像，获得每一帧图像的平均灰度值
3.	实现一个展示灰度值的实时曲线图
4.	通过快速傅里叶变换，将得到的一系列灰度值进行傅里叶变换得到心率

### 三、实现过程
#### 3.1 操作手机的摄像头和闪光灯获得指尖图像
将摄像头捕获到的图像显示出来，只需要通过Android的Camera类库取得对应的图像数据，然后通过ImageVIew显示图像即可。这里需要的是动态的图像，所以摄像头每捕获一帧，就将该帧发送给ImageView显示即可。

但Android中，提供了一种更好的方式，可以通过SurfaceView加Camera的方式来显示图像。在前一种方式中，ImageView在摄像头每捕获一帧时，就要刷新一次，这非常的耗时，因为刷新操作和其他的操作都是在一个线程进行的，这很容易引起应用的卡顿。而使用SurfaceVIew的时，SurfaceView的绘制是在单独的一个线程中进行的，这可以有效的提高应用的用户体验。

<div align=center><img src="https://github.com/WoHohohoho/FastHeartRate/blob/master/media/camera.jpg"/></div>

#### 3.2 分析得到的图像，获得每一帧图像的平均灰度值
Android平台中摄像头使用的图片格式为YUV格式，其中Y为像素点的亮度信息，U和V为像素点的色度信息。YUV格式有两大类，plane和packed。在plane类型的YUV格式图像中，一个像素点的Y值和UV值是分开存储的，先连续存储所有像素点的Y值，再存储所有像素点的UV值。而在packed类型的YUV格式图像中，像素点的Y值和UV值是连续存储的，依次存储每个像素点的Y值U值V值。

Android平台中摄像头传回图像的格式为YUV420sp格式，属于plane类型，且每四个Y值公用一组UV分量。其格式如下图所示。

<div align=center><img src="https://github.com/WoHohohoho/FastHeartRate/blob/master/media/yuv.jpg"/></div>

其中相同颜色的块为一个共用组，如Y1，Y2，Y9，Y10共用了U1V1，所以第1，2，9，10个像素点的YUV值依次为Y1U1V1，Y2U1V1，Y9U1V1，Y10U1V1。

这里用图像的平均灰度值代表图像的明暗程度，平均灰度值大，即图像比较暗，平均灰度值小，即图像比较亮。我们需要得到的是图像的亮度信息，即YUV图像格式的Y值。由于传回图像的格式中的亮度值是连续存储的，所以只需要遍历图像中前像素点个数的字节，将他们求和取平均即可得到平均灰度值。响应代码如下所示。

```
public float calculateAvgGrey(byte[]yuv420sp, int width, int height){
    float sumofGrey = 0;
    int frameSize = width*height;
    for (int i = 0; i < frameSize; i++) {
        int grey = yuv420sp[i] & 0xff;
        sumofGrey += grey;
    }
    return (float)sumofGrey / (float)frameSize;
}
```

#### 3.3 实现一个实时展示灰度值的实时曲线图
该控件需要能够实时地绘制出摄像头捕获的图像的平均灰度值曲线图。实现上主要有两个点，按获得的灰度值绘制曲线和在新捕获一帧图像时更新曲线。绘制曲线的代码如下。

```
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
```

其中，points是一个平均灰度值的列表，里面按顺序存储了得到的图像的平均灰度值。

新捕获一帧图像时更新曲线，只需要在新捕获一帧图像时，将得到的平均灰度值加入到points列表最后，并删掉points列表的第一个点，然后重绘曲线即可。相关代码如下。

```
public void putPoint(float y){
    // 移除第一个点
    points.remove(0);
    // 加入最新获得的平均灰度值
    points.add(y);
    // 重绘曲线
    invalidate();
}
```

该控件可以以如下方式使用。


```
<com.bigboss.heartrate.widget.CardiogView
    android:id="@+id/cardiogview"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:distanceX="8dp"
    app:startY="50"
    app:endY="200"
    />
```

效果如下：

<div align=center><img src="https://github.com/WoHohohoho/FastHeartRate/blob/master/media/line_custom_view.jpg"/></div>

### 3.4 通过傅立叶变换得到新率
在前几小节中，已经可以得到图像的灰度值并将其绘制出来了，最后需要根据得到的这些灰度值计算出心率。

<div align=center><img src="https://github.com/WoHohohoho/FastHeartRate/blob/master/media/line_fft.jpg"/></div>

上图中的波形周期性非常的明显，大概11到12个点为一个波形，以此也可以计算出心率。但是以编程方式实现上述计算方式比较麻烦。通过傅里叶变化，可以将上述波形分解为不同频率波形的叠加，很明显我们需要的心率即是其中幅值最大的那个波的频率，所以，我们只需要对得到的灰度值进行一个傅里叶变换，得到的结果中，幅值最大的那个波的频率即是心率。










