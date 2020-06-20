package com.mychelantonacio.packstar.view.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.snackbar.Snackbar;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Item;
import com.mychelantonacio.packstar.util.enums.ItemStatusEnum;
import com.mychelantonacio.packstar.util.helpers.ItemTouchHelperAdapter;
import com.mychelantonacio.packstar.util.helpers.ItemTouchHelperViewHolder;
import com.mychelantonacio.packstar.util.helpers.OnStartDragListener;
import com.mychelantonacio.packstar.viewmodel.ItemViewModel;
import java.util.List;


public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemViewHolder>
        implements ItemTouchHelperAdapter {

    private ItemListAdapter adapter;
    private OnItemClickListener listener;
    private List<Item> items;
    private final LayoutInflater inflater;
    private ItemViewModel itemViewModel;
    private Item recentlyDeletedItem;
    private int recentlyDeletedItemPosition;
    private View itemView;
    private final OnStartDragListener dragStartListener;


    public ItemListAdapter(Context context, ItemViewModel itemViewModel, OnStartDragListener dragStartListener){
        inflater = LayoutInflater.from(context);
        this.itemViewModel = itemViewModel;
        this.dragStartListener = dragStartListener;
    }

    //drag & drop
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Item prev = items.remove(fromPosition);
        items.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    /*
    @Override
    public void onItemDismiss(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }
     */

    public interface OnDragStartListener {
        void onDragStarted(RecyclerView.ViewHolder viewHolder);
    }

    //event click on recyclerview items
    public interface OnItemClickListener {
        void onStatusItemClick(int position, View v);
    }

    public void setOnItemClickListener(ItemListAdapter.OnItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemView = inflater.inflate(R.layout.recyclerview_item_bag_item, parent, false);
        return new ItemViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Resources res = holder.itemView.getResources();

        if (items != null) {
            Item currentItem = items.get(position);

            holder.itemNameItemView.setText(currentItem.getName());
            holder.itemQuantityItemView.setText(String.valueOf(currentItem.getQuantity()));

            if(currentItem.getWeight() != null)
                holder.itemWeightItemView.setText(String.format("%.1f", currentItem.getWeight()));
            else
                holder.itemWeightItemView.setText("-");

            if(currentItem.getStatus().equals(ItemStatusEnum.NEED_TO_BUY.getStatusCode())){
                holder.itemStatusItemView.setImageDrawable(res.getDrawable(R.drawable.ic_arrow_right_red, null));
            }
            else if(currentItem.getStatus().equals(ItemStatusEnum.ALREADY_HAVE.getStatusCode())){
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

        //drag & drop
        holder.itemHandlerItemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    dragStartListener.onStartDrag(holder);
                }
                return false;
            }
        });
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
    public class ItemViewHolder extends RecyclerView.ViewHolder implements
            ItemTouchHelperViewHolder {

        private final TextView itemNameItemView;
        private final TextView itemQuantityItemView;
        private final TextView itemWeightItemView;
        private final ImageView itemStatusItemView;
        private final ImageView itemHandlerItemView;


        public ItemViewHolder(@NonNull View itemView,  final OnItemClickListener listener) {
            super(itemView);

            itemNameItemView = itemView.findViewById(R.id.text_view_list_item_name);
            itemQuantityItemView = itemView.findViewById(R.id.textview_quantity_value);
            itemWeightItemView = itemView.findViewById(R.id.textview_weight_value);
            itemStatusItemView = itemView.findViewById(R.id.imageView_chip_status);
            itemHandlerItemView = itemView.findViewById(R.id.handle);

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

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.rgb(252,252,252));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }
}