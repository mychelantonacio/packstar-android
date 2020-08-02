package com.mychelantonacio.packstar.util.Dialogs;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mychelantonacio.packstar.R;


public class OverSystemWeightFragmentDialog extends DialogFragment {

    private OverSystemWeightFragmentDialog.NoticeDialogListener listener;


    public OverSystemWeightFragmentDialog(){
    }

    public interface NoticeDialogListener {
        void onDialogOverWeightPositiveClick(DialogFragment dialog);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listener = (OverSystemWeightFragmentDialog.NoticeDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(e.toString());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage( R.string.dialog_over_system_weight)
                .setPositiveButton(getResources().getString(R.string.dialog_ok), (dialog, id) ->
                        listener.onDialogOverWeightPositiveClick(OverSystemWeightFragmentDialog.this));
        return builder.create();
    }
}