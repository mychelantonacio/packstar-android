package com.mychelantonacio.packstar.view.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Item;
import com.mychelantonacio.packstar.viewmodel.ItemViewModel;
import java.util.List;


public class BagItemListAdapter extends RecyclerView.Adapter<BagItemListAdapter.BagItemViewHolder> {

    private BagItemListAdapter adapter;
    private OnItemClickListener listener;
    private List<Item> items;
    private final LayoutInflater inflater;
    private ItemViewModel itemViewModel;
    private Item recentlyDeletedItem;
    private int recentlyDeletedItemPosition;
    private View itemView;


    public BagItemListAdapter(Context context, ItemViewModel itemViewModel){
        inflater = LayoutInflater.from(context);
        this.itemViewModel = itemViewModel;
    }


    public interface OnItemClickListener {
        void onStatusItemClick(int position, View v);
    }

    public void setOnItemClickListener(BagItemListAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public BagItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        itemView = inflater.inflate(R.layout.recyclerview_item_bag_item, parent, false);
        return new BagItemViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull BagItemViewHolder holder, int position) {
        Resources res = holder.itemView.getResources();

        if (items != null) {
            Item currentItem = items.get(position);

            holder.itemNameItemView.setText(currentItem.getName());
            holder.itemQuantityItemView.setText(String.valueOf(currentItem.getQuantity()));

            if(currentItem.getWeight() != null)
                holder.itemWeightItemView.setText(String.format("%.1f", currentItem.getWeight()));
            else
                holder.itemWeightItemView.setText("-");

            if(currentItem.getStatus().equals("B")){
                holder.itemStatusItemView.setImageDrawable(res.getDrawable(R.drawable.ic_arrow_right_red, null));
            }
            else if(currentItem.getStatus().equals("A")){
                holder.itemStatusItemView.setImageDrawable(res.getDrawable(R.drawable.ic_arrow_right_green, null));
            }
            else{
                holder.itemStatusItemView.setImageDrawable(res.getDrawable(R.drawable.ic_arrow_right_gray, null));
            }
        } else {
            holder.itemNameItemView.setText("Item name");
            holder.itemQuantityItemView.setText("-");
            holder.itemWeightItemView.setText("-");
        }
    }

    @Override
    public int getItemCount() {
        if(items != null)
            return items.size();
        else return 0;
    }

    public void setItems(List<Item> items){
        this.items = items;
        notifyDataSetChanged();
    }

    public Item findItemByPosition(int position){
        if(items != null)
           return items.get(position);
        return null;
    }

    public void updateStatus(Item item, int position){
        items.get(position).setStatus(item.getStatus());
        itemViewModel.update(item);
        notifyDataSetChanged();
    }


    public void deleteItem(int position) {
        recentlyDeletedItem = items.get(position);
        recentlyDeletedItemPosition = position;
        items.remove(position);
        notifyItemRemoved(position);
        showUndoSnackbar();
        //itemViewModel.delete(recentlyDeletedItem); uncommit it after testing...
    }

    private void showUndoSnackbar() {
        View view = itemView.findViewById(R.id.constraintlayout_bag_item);
        Snackbar snackbar = Snackbar.make(view, recentlyDeletedItem.getName(), Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.snackbar_undo, v -> undoDelete());
        snackbar.show();
    }

    private void undoDelete() {
        items.add(recentlyDeletedItemPosition, recentlyDeletedItem);
        notifyItemInserted(recentlyDeletedItemPosition);
        //itemViewModel.insert(recentlyDeletedItem); uncommit it after testing...
    }



    //VIEW_HOLDER
    public class BagItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemNameItemView;
        private final TextView itemQuantityItemView;
        private final TextView itemWeightItemView;
        private final ImageView itemStatusItemView;


        public BagItemViewHolder(@NonNull View itemView,  final OnItemClickListener listener) {
            super(itemView);

            itemNameItemView = itemView.findViewById(R.id.text_view_list_item_name);
            itemQuantityItemView = itemView.findViewById(R.id.textview_quantity_value);
            itemWeightItemView = itemView.findViewById(R.id.textview_weight_value);
            itemStatusItemView = itemView.findViewById(R.id.imageView_chip_status);

            itemStatusItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onStatusItemClick(position, itemView);
                        }
                    }
                }
            });
        }
    }
}