package com.mychelantonacio.packstar.util.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mychelantonacio.packstar.R;

public class HelpFragmentDialog extends DialogFragment {


    private HelpFragmentDialog.NoticeDialogListener listener;


    public HelpFragmentDialog(){
    }

    public interface NoticeDialogListener {
        void onDialogOkClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (HelpFragmentDialog.NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.dialog_help)    )
                .setPositiveButton(getResources().getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDialogOkClick(HelpFragmentDialog.this);
                    }
                });
        return builder.create();
    }

}