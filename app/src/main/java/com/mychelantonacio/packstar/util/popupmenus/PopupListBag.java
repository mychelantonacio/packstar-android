package com.mychelantonacio.packstar.util.popupmenus;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.model.Item;
import com.mychelantonacio.packstar.util.enums.ItemStatusEnum;
import com.mychelantonacio.packstar.view.activities.EditBagActivity;
import com.mychelantonacio.packstar.view.activities.EmptyBagActivity;
import com.mychelantonacio.packstar.view.adapters.BagListAdapter;
import com.mychelantonacio.packstar.viewmodel.BagViewModel;
import com.mychelantonacio.packstar.viewmodel.ItemViewModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;


public class PopupListBag {

    public void showPopupWindow(Context context, View view, Bag currentBag, List<Item> currentItems,
                                ItemViewModel itemViewModel, BagViewModel bagViewModel, BagListAdapter bagAdapter, Activity activity) {

        PopupMenu popup = new PopupMenu(context, view.findViewById(R.id.imageButton_menu_dots));
        forceShowIcons(popup);
        popup.getMenuInflater().inflate(R.menu.three_dots_menu, popup.getMenu());
        popup.setGravity(5);

        popup.setOnMenuItemClickListener(item -> {

            if (item.getItemId() == R.id.menu_dots_edit) {
                Intent intent = new Intent(context, EditBagActivity.class);
                intent.putExtra("bag_parcelable", currentBag);
                context.startActivity(intent);
            }
            if (item.getItemId() == R.id.menu_dots_delete) {
                boolean isLastBag = false;

                ContentResolver cr = context.getContentResolver();
                currentItems.stream().forEach(i -> itemViewModel.delete(i));

                if(bagAdapter.getItemCount() == 1)
                    isLastBag = true;

                if(currentBag.isEventSet()){
                    bagViewModel.delete(currentBag, cr);
                }
                else {
                    bagViewModel.delete(currentBag);
                }

                Toast.makeText(context, context.getResources().getString(R.string.list_bag_popup_delete), Toast.LENGTH_SHORT).show();

                if(isLastBag) {
                    Intent intent = new Intent(context, EmptyBagActivity.class);
                    context.startActivity(intent);
                    activity.finish();
                }
            }
            if (item.getItemId() == R.id.menu_dots_share) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, getSharingMessage(currentBag, currentItems));
                sendIntent.setType("text/plain");
                Intent shareIntent = Intent.createChooser(sendIntent, null);
                context.startActivity(shareIntent);
            }
            return true;
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

    private String getSharingMessage(Bag currentBag, List<Item> currentItems){
        String itemsString = new String();
        for(int i = 0; i < currentItems.size(); i++){
            if(currentItems.get(i).getStatus().equals(ItemStatusEnum.NEED_TO_BUY.getStatusCode())) {
                itemsString += currentItems.get(i).getName()  +
                         "(" + currentItems.get(i).getQuantity() + ")" + " - " + "Need to buy" + "\n";
            }
            else if(currentItems.get(i).getStatus().equals(ItemStatusEnum.ALREADY_HAVE.getStatusCode())) {
                itemsString += currentItems.get(i).getName() +
                        "(" + currentItems.get(i).getQuantity() + ")" + " - " + "Already have" + "\n";
            }
            else  {
                itemsString += currentItems.get(i).getName() +
                        "(" + currentItems.get(i).getQuantity() + ")" + " - " + "Non information" + "\n";
            }
        }
        String message = "PACKSTAR \n\n" + currentBag.getName() + "\n" + currentBag.getTravelDate() +"\n\n" + itemsString;
        return message;
    }
}