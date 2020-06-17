package com.mychelantonacio.packstar.view.activities;

import androidx.fragment.app.Fragment;
import android.view.View;
import android.widget.PopupMenu;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.view.fragments.ListBagFragment;
import com.mychelantonacio.packstar.view.fragments.ListItemFragment;
import com.mychelantonacio.packstar.view.fragments.SingleFragmentActivity;


public class ListBagActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new ListBagFragment();
    }

    public void showThreeDotsMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.three_dots_menu, popup.getMenu());
        popup.setGravity(5);
        popup.show();
    }

}