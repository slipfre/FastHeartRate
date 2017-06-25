package com.bigboss.heartrate.util;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;

import java.util.List;

public class CameraHelper {

    private CameraHelper(){}

    public static Camera openCamera(int cameraId) {
        try{
            return Camera.open(cameraId);
        }catch(Exception e) {
            return null;
        }
    }

    public static void followScreenOrientation(Context context, Camera camera){
        final int orientation = context.getResources().getConfiguration().orientation;
        if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
            camera.setDisplayOrientation(180);
        }else if(orientation == Configuration.ORIENTATION_PORTRAIT) {
            camera.setDisplayOrientation(90);
        }
    }

    static public void transformYUV420SPtoGreyScale(byte[] yuv420, int width, int height){
        int uvIndex = width*height;

        for (int i = uvIndex; i < yuv420.length; i++) {
            yuv420[i] = 0;
        }
    }

    static public void decodeYUV420SP(int[] rgb, byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;

        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);

                if (r < 0) r = 0; else if (r > 262143) r = 262143;
                if (g < 0) g = 0; else if (g > 262143) g = 262143;
                if (b < 0) b = 0; else if (b > 262143) b = 262143;

                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000) | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }

    static public void decodeYUV420StoGreyRGBandRotate(int[] rgb, byte[] yuv420sp, int width, int height) {
        int rowPos = 0;
        for (int i = 0; i < height; i++) {
            int columnPos = 0;
            for (int j = 0; j < width; j++) {
                int grey = yuv420sp[rowPos + j] & 0xff;
                rgb[columnPos + height - i - 1] = 0xFF000000 | (grey * 0x00010101);
                columnPos += height;
            }
            rowPos += width;
        }
    }

    static public void decodeYUV420StoGreyRGB(int[] rgb, byte[] yuv420sp, int width, int height) {
        for (int i = 0; i < width*height; i++) {
            int grey = yuv420sp[i] & 0xff;
            rgb[i] = 0xFF000000 | (grey * 0x00010101);
        }
    }

    static public float decodeYUV420StoGreyRGBandGetAvgGrey(int[] rgb, byte[] yuv420sp, int width, int height) {
        float sumofGrey = 0;
        int frameSize = width*height;
        for (int i = 0; i < frameSize; i++) {
            int grey = yuv420sp[i] & 0xff;
            sumofGrey += grey;
            rgb[i] = 0xFF000000 | (grey * 0x00010101);
        }
        return (float)sumofGrey/(float)frameSize;
    }

    static public float calculateAvgGrey(byte[] yuv420sp, int width, int height){
        float sumofGrey = 0;
        int frameSize = width*height;
        for (int i = 0; i < frameSize; i++) {
            int grey = yuv420sp[i] & 0xff;
            sumofGrey += grey;
        }
        return (float)sumofGrey/(float)frameSize;
    }

    public static void setAutoFocusMode(Camera camera) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.size() > 0 && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                camera.setParameters(parameters);
            } else if (focusModes.size() > 0) {
                parameters.setFocusMode(focusModes.get(0));
                camera.setParameters(parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTouchFocusMode(Camera camera) {
        try {
            Camera.Parameters parameters = camera.getParameters();
            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes.size() > 0 && focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                camera.setParameters(parameters);
            } else if (focusModes.size() > 0) {
                parameters.setFocusMode(focusModes.get(0));
                camera.setParameters(parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
