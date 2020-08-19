package com.mychelantonacio.packstar.util.Dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.mychelantonacio.packstar.R;

import java.time.LocalDate;
import java.util.Calendar;


public class DatePickerFragmentDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    private DatePickerFragmentListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_title)
                .setPositiveButton(R.string.dialog_discard, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue() - 1;//datepicker 0 index based
        int day = LocalDate.now().getDayOfMonth();



        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        listener.onDateSet(year, month, dayOfMonth);
    }

    public interface DatePickerFragmentListener{
        void onDateSet(int year, int month, int day);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (DatePickerFragmentListener) context;
    }
}