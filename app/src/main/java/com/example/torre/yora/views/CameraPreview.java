package com.example.torre.yora.views;

import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.torre.yora.activities.BaseActivity;

import java.util.List;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback
{
    private static final String TAG = "CameraPreview";

    private SurfaceHolder surfaceHolder; //Responsible for drawing pixels on the Surface
    private Camera camera;
    private Camera.CameraInfo cameraInfo;
    private boolean isSurfaceCreated;

    public CameraPreview(BaseActivity activity)
    {
        super(activity);
        isSurfaceCreated = false;

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    public void setCamera(Camera camera, Camera.CameraInfo cameraInfo)
    {
        if (this.camera != null) //if we're resetting the camera to a new camera
        {
            try
            {
                this.camera.stopPreview();
            }
            catch (Exception e)
            {
                Log.e(TAG, "Could not stop camera preview", e);
            }
        }

        this.camera = camera;
        this.cameraInfo = cameraInfo;

        if (camera == null)
        {
            return;
        }

        if (!isSurfaceCreated)
        {
            return;
        }

        try
        {
            camera.setPreviewDisplay(surfaceHolder);
            configureCamera();
            camera.startPreview();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Could not start camera preview", e);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        //if there's a new holder available, treat it as the new one
        if (surfaceHolder != holder)
        {
            surfaceHolder = holder;
            surfaceHolder.addCallback(this);
        }
        isSurfaceCreated = true;

        if(camera != null)
        {
            setCamera(camera, cameraInfo);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        isSurfaceCreated = false;
        surfaceHolder.removeCallback(this);
        surfaceHolder = null;

        if (camera == null)
        {
            return;
        }

        try
        {
            camera.stopPreview();
            camera = null;
            cameraInfo = null;
        }
        catch (Exception e)
        {
            Log.e(TAG, "Can't stop preview", e);
        }
    }

    @Override
    protected void onMeasure(int width, int height)
    {
        //Solves issues with stretching
        width = resolveSize(getSuggestedMinimumWidth(), width);
        height = resolveSize(getSuggestedMinimumHeight(), height);

        setMeasuredDimension(width, height);
    }

    private void configureCamera()
    {
        Camera.Parameters parameters = camera.getParameters();

        Camera.Size targetPreviewSize = getClosestSize(getWidth(), getHeight(), parameters.getSupportedPreviewSizes());
        parameters.setPreviewSize(targetPreviewSize.width, targetPreviewSize.height);

        Camera.Size targetImageSize = getClosestSize(1024, 1280, parameters.getSupportedPictureSizes());
        parameters.setPictureSize(targetImageSize.width, targetImageSize.height);

        camera.setDisplayOrientation(90); //we assume portrait layout
        camera.setParameters(parameters);
    }

    //Returns the resolution that the camera supports, that is closest to our desired resolution
    private Camera.Size getClosestSize(int width, int height, List<Camera.Size> supportedSizes)
    {
        final double ASPECT_TOLERANCE = .1; //How close we want the camera resolution to be our desired resolution

        double targetRatio = (double)height/width;

        Camera.Size targetSize = null; //What ultimately our chosen size is gonna be set to
        double minDifference = Double.MAX_VALUE; //How close to the aspect ratio we've got to

        for (Camera.Size size : supportedSizes)
        {
            double ratio = (double)size.width/size.height;

            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
            {
                continue;
            }

            int heightDifference = Math.abs(size.height - height);

            if (heightDifference < minDifference)
            {
                targetSize = size;
                minDifference = heightDifference;
            }
        }

        if (targetSize == null) //if we didn't find an appropriate size that matches our parameters, then at least get the one that is closest to what we want
        {
            minDifference = Double.MAX_VALUE;

            for (Camera.Size size : supportedSizes)
            {
                int heightDifference = Math.abs(size.height - height);

                if (heightDifference < minDifference)
                {
                    targetSize = size;
                    minDifference = heightDifference;
                }
            }
        }

        return targetSize;
    }
}
