package com.bigboss.heartrate.widget;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import com.bigboss.heartrate.util.CameraHelper;

import java.io.IOException;

public class CameraPreviewView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;

    public CameraPreviewView(Context context) {
        super(context);
        init();
    }

    public CameraPreviewView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CameraPreviewView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
        openCamera();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreviewDisplay(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mSurfaceHolder.getSurface() == null){
            return;
        }
        CameraHelper.followScreenOrientation(getContext(), mCamera);
        Log.d("grandfather", "Restart preview display[SURFACE-CHANGED]");
        stopPreviewDisplay();
        startPreviewDisplay(mSurfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopPreviewDisplay();
    }

    private void startPreviewDisplay(SurfaceHolder holder){
        checkCamera();
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.e("grandfather", "Error while START preview for camera", e);
        }
    }

    private void checkCamera(){
        if(mCamera == null) {
            throw new IllegalStateException("Camera must be set when start/stop preview, call <setCamera(Camera)> to set");
        }
    }

    private void stopPreviewDisplay(){
        checkCamera();
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            Log.e("grandfather", "Error while STOP preview for camera", e);
        }
    }

    public void openCamera(){
        mCamera = Camera.open();
        final Camera.Parameters params = mCamera.getParameters();
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        openCameraFlashMode();
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        CameraHelper.setAutoFocusMode(mCamera);
    }

    public void releaseCamera(){
        if (mCamera != null)
            mCamera.release();
    }

    public void openCameraFlashMode(){
        checkCamera();
        Camera.Parameters mParameters;
        mParameters = mCamera.getParameters();
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mParameters.setPreviewFormat(ImageFormat.NV21);
        mCamera.setParameters(mParameters);
    }

    public void closeCameraFlashMode(){
        checkCamera();
        Camera.Parameters mParameters;
        mParameters = mCamera.getParameters();
        mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(mParameters);
    }

    public void setPreviewCallback(Camera.PreviewCallback mPreviewCallback){
        if (mCamera != null)
            mCamera.setPreviewCallback(mPreviewCallback);
    }
}
