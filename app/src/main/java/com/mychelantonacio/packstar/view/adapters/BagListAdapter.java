package com.mychelantonacio.packstar.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.model.Item;
import java.util.List;


public class BagListAdapter extends RecyclerView.Adapter<BagListAdapter.BagViewHolder> {

    private List<Bag> bags;
    private List<Item> items;
    private final LayoutInflater inflater;
    private OnItemClickListener listener;



    public BagListAdapter(Context context){
        inflater = LayoutInflater.from(context);
    }

    public interface OnItemClickListener {
        void onAddItemClick(int position);
        void onCardItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public BagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.recyclerview_item_bag, parent, false);
        return new BagViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(BagViewHolder holder, int position) {
        if (bags != null) {
            Bag currentBag = bags.get(position);
            holder.bagNameItemView.setText(currentBag.getName());
            holder.bagDateItemView.setText(currentBag.getTravelDate());
            holder.bagWeightItemView.setText(String.valueOf(currentBag.getWeight()) + " kg");
            holder.itemQuantityItemView.setText(String.valueOf(getItemQuantity(currentBag)));
            holder.itemWeightItemView.setText( String.format("%.1f", getItemWeight(currentBag)) );
        } else {
            holder.bagNameItemView.setText("Bag name");
            holder.bagDateItemView.setText("Bag Date");
            holder.bagWeightItemView.setText("Bag Weight");
        }
    }

    private int getItemQuantity(Bag bag){
        int countItems = 0;
        if(this.items != null) {
            for (Item item : items) {
                if (bag.getId().equals(item.getBagId())) {
                    countItems = countItems + item.getQuantity();
                }
            }
        }
        return countItems;
    }

    private double getItemWeight(Bag bag){
        double totalWeightItems = 0.0;
        double weightItem = 0.0;

        if(this.items != null) {
            for (Item item : items) {
                if (bag.getId().equals(item.getBagId())) {
                    weightItem = item.getWeight() != null ? item.getWeight() : 0.0;
                    totalWeightItems = totalWeightItems + (item.getQuantity() * weightItem);
                    weightItem = 0.0;
                }
            }
        }
        return totalWeightItems;
    }

    public void setBags(List<Bag> bags){
        this.bags = bags;
        notifyDataSetChanged();
    }

    public void setItems(List<Item> items){
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (bags != null)
            return bags.size();
        else return 0;
    }

    class BagViewHolder extends RecyclerView.ViewHolder {

        private final TextView bagNameItemView;
        private final TextView bagDateItemView;
        private final TextView bagWeightItemView;
        private final TextView bagAddItemView;
        private final ImageButton addImageButtonItemView;
        private final TextView itemQuantityItemView;
        private final TextView itemWeightItemView;
        private final MaterialCardView cardViewItemView;


        private BagViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            bagNameItemView = itemView.findViewById(R.id.text_view_bag_name);
            bagDateItemView = itemView.findViewById(R.id.text_view_bag_date);
            bagWeightItemView = itemView.findViewById(R.id.text_view_bag_weight);
            bagAddItemView = itemView.findViewById(R.id.textView_add);
            addImageButtonItemView = itemView.findViewById(R.id.imageButton_add);
            itemQuantityItemView = itemView.findViewById(R.id.textview_counter_item);
            itemWeightItemView = itemView.findViewById(R.id.textview_counter_weight);
            cardViewItemView = itemView.findViewById(R.id.cardview_bag);


            addImageButtonItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onAddItemClick(position);
                        }
                    }
                }
            });

            bagAddItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onAddItemClick(position);
                        }
                    }
                }
            });

            cardViewItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onCardItemClick(position);
                        }
                    }
                }
            });

        }
    }
}//endClass...
