package com.example.torre.yora.activities;


import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Debug;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.torre.yora.R;
import com.example.torre.yora.dialogs.ChangePasswordDialog;
import com.example.torre.yora.infrastructure.User;
import com.example.torre.yora.services.Account;
import com.example.torre.yora.views.MainNavDrawer;
import com.soundcloud.android.crop.Crop;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends BaseAuthenticatedActivity implements View.OnClickListener
{
    private static final int REQUEST_SELECT_IMAGE = 100;

    private static final int STATE_VIEWING = 1;
    private static final int STATE_EDITING = 2;

    private static final String BUNDLE_STATE = "BUNDLE_STATE";

    private boolean isProgressBarVisible;

    private int currentState;
    private EditText displayNameText;
    private EditText emailText;
    private View changeAvatarButton;
    private ActionMode editProfileActionMode;

    private ImageView avatarView;
    private View avatarProgressFrame;
    private File tempOutputFile;


    private Dialog progressDialog;
    private boolean progressBarVisible;

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
        changeAvatarButton = findViewById(R.id.activity_profile_changeAvatar);
        tempOutputFile = new File(getExternalCacheDir(), "temp-image.jpg");

        displayNameText = (EditText) findViewById(R.id.activity_profile_displayName);
        emailText = (EditText) findViewById(R.id.activity_profile_email);

        avatarView.setOnClickListener(this);
        changeAvatarButton.setOnClickListener(this);

        avatarProgressFrame.setVisibility(View.GONE);

        User user = application.getAuth().getUser();
        getSupportActionBar().setTitle(user.getDisplayName());

        Picasso.with(this).load(user.getAvatarUrl()).into(avatarView);

        if (savedInstanceState == null)
        {
            displayNameText.setText(user.getDisplayName());
            emailText.setText(user.getEmail());

            changeState(STATE_VIEWING); //default state
        }
        else
        {
            changeState(savedInstanceState.getInt(BUNDLE_STATE));
        }

        if (progressBarVisible)
        {
            setProgressBarVisible(true);
        }
    }

    private void changeState(int state)
    {
        if (state == currentState)
        {
            return;
        }

        currentState = state;

        if (state == STATE_VIEWING)
        {
            displayNameText.setEnabled(false);
            emailText.setEnabled(false);
            changeAvatarButton.setVisibility(View.VISIBLE);

            if (editProfileActionMode != null)
            {
                editProfileActionMode.finish();
                editProfileActionMode = null;
            }
        }

        else if (state == STATE_EDITING)
        {
            displayNameText.setEnabled(true);
            emailText.setEnabled(true);
            changeAvatarButton.setVisibility(View.GONE);

            editProfileActionMode = toolbar.startActionMode(new EditProfileActionCallback());
        }
        else
        {
            throw new IllegalArgumentException("Invalid state " + state);
        }
    }

    @Subscribe
    public void onUserDetailsUpdated(Account.UserDetailsUpdatedEvent event)
    {
        //Change our username in the action bar when we update our details
        getSupportActionBar().setTitle(event.user.getDisplayName());
        Picasso.with(this).load(event.user.getAvatarUrl()).into(avatarView);
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

        else if (requestCode == Crop.REQUEST_CROP)
        {
            avatarProgressFrame.setVisibility(View.VISIBLE);

            bus.post(new Account.ChangeAvatarRequest(Uri.fromFile(tempOutputFile)));

        }
    }

    @Subscribe
    public void onAvatarUpdated(Account.ChangeAvatarResponse response)
    {
        avatarProgressFrame.setVisibility(View.GONE);
        if (!response.succeeded())
        {
            response.showErrorToast(this);
        }
    }

    @Subscribe
    public void onProfileUpdated(Account.UpdateProfileResponse response)
    {
        if (!response.succeeded())
        {
            response.showErrorToast(this);

            changeState(STATE_EDITING);
        }

        displayNameText.setError(response.getPropertyError("displayName"));
        displayNameText.setError(response.getPropertyError("email"));
        setProgressBarVisible(false);
    }

    public void setProgressBarVisible(boolean newVisible)
    {
        if (newVisible)
        {
            progressDialog = new ProgressDialog.Builder(this)
                    .setTitle("Updating profile")
                    .setCancelable(false)
                    .show();
        }
        else if (progressDialog != null)
        {
            progressDialog.dismiss();
            progressDialog = null;
        }


        this.progressBarVisible = newVisible;
    }

    private class EditProfileActionCallback implements ActionMode.Callback
    {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu)
        {
            getMenuInflater().inflate(R.menu.activity_profile_edit, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu)
        {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item)
        {
            int itemId = item.getItemId();

            switch (itemId)
            {
                case R.id.activity_profile_edit_menuDone:
                    setProgressBarVisible(true);
                    changeState(STATE_VIEWING);
                    bus.post(new Account.UpdateProfileRequest(displayNameText.getText().toString(),
                                                                emailText.getText().toString()));
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode)
        {
            //When we were editing but hit cancel
            if (currentState != STATE_VIEWING)
            {
                changeState(STATE_VIEWING);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch (id)
        {
            case R.id.activity_profile_menuEdit:
                changeState(STATE_EDITING);
                return true;
            case R.id.activity_profile_menuChangePassword:
                FragmentTransaction transaction = getFragmentManager().beginTransaction().addToBackStack(null);
                ChangePasswordDialog dialog = new ChangePasswordDialog();
                dialog.show(transaction, null);
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putInt(BUNDLE_STATE, currentState);
    }

}
