package com.example.torre.yora.activities;


import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.torre.yora.R;
import com.example.torre.yora.views.MainNavDrawer;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends BaseAuthenticatedActivity implements View.OnClickListener
{
    private static final int REQUEST_SELECT_IMAGE = 100;
    private ImageView avatarView;
    private View avatarProgressFrame;
    private File tempOutputFile;

    @Override
    protected void onYoraCreate(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Profile");
        setNavDrawer(new MainNavDrawer(this));

        //By default our activity_profile layout is for tablets. If we're not in a tablet, then we modify the layout
        if (!isTablet)
        {
            View textFieldsContainer = findViewById(R.id.activity_profile_textFieldsContainer);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)textFieldsContainer.getLayoutParams();
            params.setMargins(0, params.getMarginStart(), 0, 0);
            params.removeRule(RelativeLayout.END_OF);
            params.addRule(RelativeLayout.BELOW, R.id.activity_profile_changeAvatar);

            textFieldsContainer.setLayoutParams(params);
        }

        avatarView = (ImageView) findViewById(R.id.activity_profile_avatar);
        avatarProgressFrame = findViewById(R.id.activity_profile_avatarProgressFrame);
        tempOutputFile = new File(getExternalCacheDir(), "temp-image.jpg");

        avatarView.setOnClickListener(this);
        findViewById(R.id.activity_profile_changeAvatar).setOnClickListener(this);

        avatarProgressFrame.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        if (id == R.id.activity_profile_changeAvatar || id == R.id.activity_profile_avatar)
        {
            changeAvatar();
        }
    }

    private void changeAvatar()
    {
        List<Intent> otherImageCaptureIntents = new ArrayList<>();
        List<ResolveInfo> otherImageCaptureActivites = getPackageManager().queryIntentActivities(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), 0);

        for (ResolveInfo info : otherImageCaptureActivites)
        {
            Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempOutputFile));
            otherImageCaptureIntents.add(captureIntent);
        }

        Intent selectImageIntent = new Intent(Intent.ACTION_PICK);
        selectImageIntent.setType("image/*");

        Intent chooser = Intent.createChooser(selectImageIntent, "Choose avatar");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, otherImageCaptureIntents.toArray(new Parcelable[otherImageCaptureIntents.size()]));

        startActivityForResult(chooser, REQUEST_SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != RESULT_OK)
        {
            tempOutputFile.delete();
            Toast.makeText(this, "resultCode != RESULT_OK", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == REQUEST_SELECT_IMAGE)
        {
            Uri outputFile;
            Uri tempFileUri = Uri.fromFile(tempOutputFile);

            if (data != null && (data.getAction() == null || !data.getAction().equals(MediaStore.ACTION_IMAGE_CAPTURE)))
                outputFile = data.getData();

            else
                outputFile = tempFileUri;

            new Crop(outputFile)
                    .asSquare()
                    .output(tempFileUri)
                    .start(this);
        }
        //TODO: si no sirve probar con "if" en vez de "else if"
        else if (requestCode == Crop.REQUEST_CROP)
        {
            Toast.makeText(this, "Crop.REQUEST_CROP", Toast.LENGTH_SHORT).show();
            //TODO: send tempFileUri to server as new avatar
            avatarView.setImageResource(0); //First, clear out the image that was previously there
            avatarView.setImageURI(Uri.fromFile(tempOutputFile));
        }
    }

}
