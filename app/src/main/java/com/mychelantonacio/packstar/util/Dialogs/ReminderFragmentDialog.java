package com.mychelantonacio.packstar.util.Dialogs;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mychelantonacio.packstar.R;


public class ReminderFragmentDialog extends DialogFragment {


    private NoticeDialogListener listener;

    public interface NoticeDialogListener {
        public void onDialogEditClick(DialogFragment dialog);
        public void onDialogDeleteClick(DialogFragment dialog);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage(R.string.dialog_reminder_title)
                .setPositiveButton(R.string.dialog_reminder_edit, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogEditClick(ReminderFragmentDialog.this);
                    }
                })
                .setNegativeButton(R.string.dialog_reminder_delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogDeleteClick(ReminderFragmentDialog.this);
                    }
                });
        return builder.create();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (ReminderFragmentDialog.NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString());
        }
    }



}
