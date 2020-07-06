package com.mychelantonacio.packstar.view.activities;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.mychelantonacio.packstar.util.Dialogs.CommentFragmentDialog;
import com.mychelantonacio.packstar.view.fragments.ListBagFragment;
import com.mychelantonacio.packstar.view.fragments.SingleFragmentActivity;


public class ListBagActivity extends SingleFragmentActivity implements CommentFragmentDialog.NoticeDialogListener {

    @Override
    protected Fragment createFragment() {
        return new ListBagFragment();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
    }
}