package com.example.torre.yora.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.torre.yora.R;
import com.example.torre.yora.services.Account;
import com.squareup.otto.Subscribe;

public class ChangePasswordDialog extends BaseDialogFragment implements View.OnClickListener
{
    private EditText currentPassword;
    private EditText newPassword;
    private EditText confirmNewPassword;
    private Dialog progressDialog;

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
                progressDialog = new ProgressDialog.Builder(getActivity())
                        .setTitle("Updating password")
                        .setCancelable(false)
                        .show();

                bus.post(new Account.ChangePasswordRequest(currentPassword.getText().toString(), newPassword.getText().toString(), confirmNewPassword.getText().toString()));
                return;
        }
    }

    @Subscribe
    public void onPasswordUpdated(Account.ChangePasswordResponse response)
    {
        progressDialog.dismiss();
        progressDialog = null;

        if (response.succeeded())
        {
            Toast.makeText(getActivity(), "Password updated", Toast.LENGTH_LONG).show();
            dismiss();
            application.getAuth().getUser().setHasPassword(true);
            return;
        }

        currentPassword.setError(response.getPropertyError("currentPassword"));
        newPassword.setError(response.getPropertyError("newPassword"));
        confirmNewPassword.setError(response.getPropertyError("confirmNewPassword"));

        response.showErrorToast(getActivity());
    }
}
