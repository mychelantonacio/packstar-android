package com.mychelantonacio.packstar.util.popupmenus;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.model.Item;
import com.mychelantonacio.packstar.view.activities.EditBagActivity;
import com.mychelantonacio.packstar.viewmodel.BagViewModel;
import com.mychelantonacio.packstar.viewmodel.ItemViewModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;


public class PopupListBag {

    public void showPopupWindow(Context context, View view, Bag currentBag, int position, List<Item> currentItems,
                                ItemViewModel itemViewModel, BagViewModel bagViewModel) {

        PopupMenu popup = new PopupMenu(context, view.findViewById(R.id.imageButton_menu_dots));
        forceShowIcons(popup);
        popup.getMenuInflater().inflate(R.menu.three_dots_menu, popup.getMenu());
        popup.setGravity(5);

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.getItemId() == R.id.menu_dots_edit) {
                    Intent intent = new Intent(context, EditBagActivity.class);
                    intent.putExtra("bag_parcelable", currentBag);
                    context.startActivity(intent);
                }
                if (item.getItemId() == R.id.menu_dots_delete) {
                    for(Item currentItem : currentItems){
                        itemViewModel.delete(currentItem);
                    }
                    bagViewModel.deleteById(currentBag);
                    Toast.makeText(context, context.getResources().getString(R.string.list_bag_popup_delete), Toast.LENGTH_SHORT).show();
                }
                if (item.getItemId() == R.id.menu_dots_share) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, getSharingMessage(currentBag));
                    sendIntent.setType("text/plain");
                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                    context.startActivity(shareIntent);
                }
                return true;
            }
        });
        popup.show();
    }

    private void forceShowIcons(PopupMenu popupMenu){
        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSharingMessage(Bag currentBag){
        String message = currentBag.getName() + " " + currentBag.getTravelDate();
        return message;
    }
}