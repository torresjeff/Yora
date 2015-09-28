package com.example.torre.yora.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.torre.yora.R;

public class ChangePasswordDialog extends BaseDialogFragment implements View.OnClickListener
{
    private EditText currentPassword;
    private EditText newPassword;
    private EditText confirmNewPassword;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_change_password, null, false);

        currentPassword = (EditText) dialogView.findViewById(R.id.dialog_change_password_currentPassword);
        newPassword = (EditText) dialogView.findViewById(R.id.dialog_change_password_newPassword);
        confirmNewPassword = (EditText) dialogView.findViewById(R.id.dialog_change_password_confirmNewPassword);

        //If they don't have a password on their account, then don't show the current password. Users don't have password for example when they login with Google.
        if (!application.getAuth().getUser().isHasPassword())
        {
            currentPassword.setVisibility(View.GONE);
        }

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(dialogView)
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", null)
                .setTitle("Change password")
                .show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(this);

        return dialog;
    }

    @Override
    public void onClick(View view)
    {
        int id = view.getId();

        switch(id)
        {
            case AlertDialog.BUTTON_POSITIVE:
                //TODO: send new password to server
                Toast.makeText(getActivity(), "Password updated!", Toast.LENGTH_SHORT).show();
                dismiss(); //Dismisses this Dialog Fragment
                return;
        }
    }
}
