package com.example.torre.yora.activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.torre.yora.R;
import com.example.torre.yora.services.entities.Message;
import com.example.torre.yora.services.entities.UserDetails;
import com.example.torre.yora.views.CameraPreview;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class NewMessageActivity extends BaseAuthenticatedActivity implements View.OnClickListener, Camera.PictureCallback
{
    private static final String TAG = "NewMessageActivity";
    private static final String STATE_CAMERA_INDEX = "STATE_CAMERA_INDEX"; //To keep track of what camera we're using (front/back) when the device is rotated

    public static final String EXTRA_CONTACT = "EXTRA_CONTACT";
    private static final int REQUEST_SEND_MESSAGE = 1;
    
    private Camera camera;
    private Camera.CameraInfo cameraInfo;
    private int currentCamaraIndex;
    private CameraPreview cameraPreview;


    @Override
    protected void onYoraCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_new_message);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //Prevents the screen from going black, we want to be able to see the camera preview all the time.

        if (savedInstanceState != null)
        {
            currentCamaraIndex = savedInstanceState.getInt(STATE_CAMERA_INDEX);
        }
        else
        {
            currentCamaraIndex = 0;
        }

        cameraPreview = new CameraPreview(this);

        FrameLayout frameLayout = (FrameLayout) findViewById(R.id.activity_new_message_frame);
        frameLayout.addView(cameraPreview, 0); //Makes the camera preview appear all the way at the back. So that new buttons appear in front of it.

        findViewById(R.id.activity_new_message_switchCamera).setOnClickListener(this);
        findViewById(R.id.activity_new_message_takePicture).setOnClickListener(this);
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        switch (id)
        {
            case R.id.activity_new_message_takePicture:
                takePicture();
                return;
            case R.id.activity_new_message_switchCamera:
                switchCamera();
            return;

        }
    }

    private void switchCamera()
    {
        currentCamaraIndex = currentCamaraIndex + 1 < Camera.getNumberOfCameras() ? currentCamaraIndex + 1 : 0;
        establishCamera();
    }

    private void takePicture()
    {
        camera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera)
    {
        Bitmap bitmap = processBitmap(data);

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, output); //takes the bitmap and compress it into a jpeg format

        File outputFile = new File(getCacheDir(), "temp-image");
        outputFile.delete(); //make sure the outputFile is cleared before we write to it

        try
        {
            FileOutputStream fileOutput = new FileOutputStream(outputFile);
            fileOutput.write(output.toByteArray());
            fileOutput.close();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Could not save bitmap", e);
            Toast.makeText(this, "Could not save image to temp directory", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, SendMessageActivity.class);
        intent.putExtra(SendMessageActivity.EXTRA_IMAGE_PATH, Uri.fromFile(outputFile));

        UserDetails user = getIntent().getParcelableExtra(EXTRA_CONTACT);

        if (user != null)
        {
            intent.putExtra(SendMessageActivity.EXTRA_CONTACT, user);
        }

        startActivityForResult(intent, REQUEST_SEND_MESSAGE);
        bitmap.recycle(); //cleans up the bitmap
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_SEND_MESSAGE && resultCode == RESULT_OK)
        {
            setResult(RESULT_OK);
            finish();

            Message message = data.getParcelableExtra(SendMessageActivity.RESULT_MESSAGE);

            Intent intent = new Intent(this, MessageActivity.class);
            intent.putExtra(MessageActivity.EXTRA_MESSAGE, message);
            startActivity(intent);
        }
    }

    private Bitmap processBitmap(byte[] data)
    {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        if (bitmap.getWidth() > SendMessageActivity.MAX_IMAGE_HEIGHT)
        {
            float scale = (float) SendMessageActivity.MAX_IMAGE_HEIGHT/bitmap.getWidth();
            int finalWidth = (int) (bitmap.getHeight() * scale);

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, SendMessageActivity.MAX_IMAGE_HEIGHT, finalWidth, false);

            if (resizedBitmap != bitmap)
            {
                bitmap.recycle();
                bitmap = resizedBitmap;
            }
        }

        Matrix matrix = new Matrix();

        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
        {
            Matrix matrixMirror = new Matrix();
            matrixMirror.setValues(new float[]{
                    -1, 0, 0,
                    0, 1, 0,
                    0, 0, 1
            });

            matrix.postConcat(matrixMirror);
        }

        matrix.postRotate(90);
        Bitmap processedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        if (bitmap != processedBitmap)
        {
            bitmap.recycle();
        }

        return processedBitmap;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        establishCamera(); //Gets a reference to the camera from the operating system
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        //If this Activity is paused, release the camera and set the preview to blank.
        if (camera != null)
        {
            cameraPreview.setCamera(null, null);
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_CAMERA_INDEX, currentCamaraIndex);
    }

    private void establishCamera()
    {
        if (camera != null)
        {
            cameraPreview.setCamera(null, null);
            camera.release();
            camera = null;
        }

        try
        {
            camera = Camera.open(currentCamaraIndex);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Could not open camera " + currentCamaraIndex, e);
            Toast.makeText(this, "Error establishing camera", Toast.LENGTH_LONG).show();
            return;
        }

        cameraInfo = new Camera.CameraInfo();
        Camera.getCameraInfo(currentCamaraIndex, cameraInfo); //Populates the cameraInfo class with additional data about the camera
        cameraPreview.setCamera(camera, cameraInfo);

        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK)
        {
            Toast.makeText(this, "Using back camera", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Using front camera", Toast.LENGTH_SHORT).show();
        }
    }
}
