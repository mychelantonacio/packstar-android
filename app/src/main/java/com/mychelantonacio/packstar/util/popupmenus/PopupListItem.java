package com.mychelantonacio.packstar.util.popupmenus;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Item;
import com.mychelantonacio.packstar.util.enums.ItemStatusEnum;
import com.mychelantonacio.packstar.view.adapters.ItemListAdapter;


public class PopupListItem {

    String itemStatusUpdated;

    public void showPopupWindow(Context context, View view, Item currentItem, int position, ItemListAdapter adapter) {
        PopupMenu popup = new PopupMenu(context, view.findViewById(R.id.imageView_chip_status));
        popup.getMenuInflater().inflate(R.menu.chip_status_menu, popup.getMenu());
        popup.setGravity(0);

        itemStatusUpdated = context.getResources().getString(R.string.menu_chip_updated_item);
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_chip_need_to_buy) {
                currentItem.setStatus(ItemStatusEnum.NEED_TO_BUY.getStatusCode());
                Toast.makeText(context, itemStatusUpdated, Toast.LENGTH_SHORT).show();
            }
            if (item.getItemId() == R.id.menu_chip_already_have) {
                currentItem.setStatus(ItemStatusEnum.ALREADY_HAVE.getStatusCode());
                Toast.makeText(context, itemStatusUpdated, Toast.LENGTH_SHORT).show();
            }
            if (item.getItemId() == R.id.menu_chip_remove) {
                currentItem.setStatus(ItemStatusEnum.NON_INFORMATION.getStatusCode());
                Toast.makeText(context, itemStatusUpdated, Toast.LENGTH_SHORT).show();
            }
            adapter.updateStatus(currentItem, position);
            return true;
        });
        popup.show();
    }

    private void popupCustomText(PopupMenu popup) {
        for(int i = 0; i < popup.getMenu().size(); i++){
            MenuItem item = popup.getMenu().getItem(i);
            String tmpText = item.getTitle().toString();
            SpannableString s = new SpannableString(tmpText);
            s.setSpan(new RelativeSizeSpan(0.8f), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            s.setSpan(new ForegroundColorSpan(Color.BLUE), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            item.setTitle(s);
        }
    }
}