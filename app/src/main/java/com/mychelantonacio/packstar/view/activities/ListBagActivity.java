package com.mychelantonacio.packstar.view.activities;

import androidx.fragment.app.Fragment;
import com.mychelantonacio.packstar.view.fragments.ListBagFragment;
import com.mychelantonacio.packstar.view.fragments.SingleFragmentActivity;


public class ListBagActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ListBagFragment();
    }
}