package com.mychelantonacio.packstar.util.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mychelantonacio.packstar.R;


public class CommentFragmentDialog extends DialogFragment  {

    private NoticeDialogListener listener;
    private String comment;


    public CommentFragmentDialog(String comment){
        this.comment = comment;
    }


    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(this.comment)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogPositiveClick(CommentFragmentDialog.this);
                    }
                });
        return builder.create();
    }
}