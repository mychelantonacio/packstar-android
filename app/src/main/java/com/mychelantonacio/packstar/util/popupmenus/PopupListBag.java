package com.mychelantonacio.packstar.util.popupmenus;

import android.content.Context;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.mychelantonacio.packstar.R;


public class PopupListBag {

    public void showPopupWindow(Context context, View view) {
        Log.d("showPopupWindow", "view " + view);

        PopupMenu popup = new PopupMenu(context, view.findViewById(R.id.imageButton_menu_dots));
        popup.getMenuInflater().inflate(R.menu.three_dots_menu, popup.getMenu());
        popup.setGravity(5);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.menu_dots_edit) {
                    //TODO: implement edit bag
                    Toast.makeText(context, "Edit bag", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.menu_dots_delete) {
                    //TODO: implement delete bag
                    Toast.makeText(context, "Delete bag", Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.menu_dots_share) {
                    //TODO: implement share bag
                    Toast.makeText(context, "Share bag", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        popup.show();

    }
}