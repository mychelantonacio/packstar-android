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
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Item;
import java.util.List;


public class BagItemListAdapter extends RecyclerView.Adapter<BagItemListAdapter.BagItemViewHolder> {

    private List<Item> items;
    private final LayoutInflater inflater;


    public BagItemListAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public BagItemListAdapter.BagItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_bag_item, parent, false);
        return new BagItemListAdapter.BagItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BagItemListAdapter.BagItemViewHolder holder, int position) {
        Resources res = holder.itemView.getResources();//or using context too, holder.itemView.context.getResources()

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
        if (items != null) {
            return items.size();
        }
        else return 0;
    }

    public void setItems(List<Item> items){
        this.items = items;
        notifyDataSetChanged();
    }

    public class BagItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView itemNameItemView;
        private final TextView itemQuantityItemView;
        private final TextView itemWeightItemView;
        private final ImageView itemStatusItemView;

        public BagItemViewHolder(@NonNull View itemView) {
            super(itemView);

            itemNameItemView = itemView.findViewById(R.id.text_view_list_item_name);
            itemQuantityItemView = itemView.findViewById(R.id.textview_quantity_value);
            itemWeightItemView = itemView.findViewById(R.id.textview_weight_value);
            itemStatusItemView = itemView.findViewById(R.id.imageView_status);
        }
    }
}