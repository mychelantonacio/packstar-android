package com.mychelantonacio.packstar.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.model.Item;

import java.util.ArrayList;
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
        void onPopupMenuItemClick(int position, View view);
        void onCommentClick(int position);
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
            holder.bagWeightItemView.setText( currentBag.getWeight() == null ? "0 kg" :  String.valueOf(currentBag.getWeight()) + " kg");
            holder.itemQuantityItemView.setText(String.valueOf(getItemQuantity(currentBag)));
            holder.itemWeightItemView.setText( String.format("%.1f", getItemWeight(currentBag)) );

            if(isOverweight(currentBag)){
                holder.redBulletItemView.setVisibility(View.VISIBLE);
                holder.overweightItemView.setVisibility(View.VISIBLE);
            }
            else{
                holder.redBulletItemView.setVisibility(View.GONE);
                holder.overweightItemView.setVisibility(View.GONE);
            }

            if(currentBag.isEventSet()){
                holder.reminderTextViewItemView.setText(currentBag.getEventDateTime());
            }

            if(currentBag.getComment() != null && !currentBag.getComment().isEmpty()){
                holder.oneCommentImageItemView.setVisibility(View.VISIBLE);
                holder.commentTextItemView.setVisibility(View.GONE);

            }
            else {
                holder.oneCommentImageItemView.setVisibility(View.GONE);
                holder.commentTextItemView.setVisibility(View.VISIBLE);
            }

        } else {
            holder.bagNameItemView.setText("Bag name");
            holder.bagDateItemView.setText("Bag Date");
            holder.bagWeightItemView.setText("Bag Weight");
            holder.itemQuantityItemView.setText("0");
            holder.itemWeightItemView.setText("0");
        }
    }

    private boolean isOverweight(Bag bag){
        if(bag.getWeight() != null){
            if( getItemWeight(bag) > bag.getWeight() ){
                return true;
            }
        }
        return false;
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

    public Bag findBagById(long id){
        if(this.bags != null){
            for(Bag bag : bags){
                if(bag.getId() == id){
                    return bag;
                }
            }
        }
        return null;
    }

    public Bag findBagByPosition(int position){
        if(bags != null)
            return bags.get(position);
        return null;
    }

    public List<Item> getItemsAttatchedWithCurrentBag(Bag currentBag){
        List<Item> currentItems = new ArrayList<>();
        for(Item item : this.items){
            if(item.getBagId().equals(currentBag.getId())){
                currentItems.add(item);
            }
        }
        return currentItems;
    }

    @Override
    public int getItemCount() {
        if (bags != null){
            return bags.size();
        }
        else{
            return 0;
        }
    }

    class BagViewHolder extends RecyclerView.ViewHolder {

        private final TextView bagNameItemView;
        private final TextView bagDateItemView;
        private final TextView bagWeightItemView;
        private final ImageButton addImageButtonItemView;
        private final TextView itemQuantityItemView;
        private final TextView itemWeightItemView;
        private final MaterialCardView cardViewItemView;
        private final ImageButton popMenuImageButtonItemView;
        private final ImageView redBulletItemView;
        private final TextView overweightItemView;
        private final TextView reminderTextViewItemView;
        private final ImageView oneCommentImageItemView;
        private final TextView commentTextItemView;



        private BagViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            bagNameItemView = itemView.findViewById(R.id.text_view_bag_name);
            bagDateItemView = itemView.findViewById(R.id.text_view_bag_date);
            bagWeightItemView = itemView.findViewById(R.id.text_view_bag_weight);
            addImageButtonItemView = itemView.findViewById(R.id.imageButton_add_item);
            itemQuantityItemView = itemView.findViewById(R.id.textview_counter_item);
            itemWeightItemView = itemView.findViewById(R.id.textview_counter_weight);
            cardViewItemView = itemView.findViewById(R.id.cardview_bag);
            popMenuImageButtonItemView = itemView.findViewById(R.id.imageButton_menu_dots);
            redBulletItemView = itemView.findViewById(R.id.imageView_red_bullet);
            overweightItemView = itemView.findViewById(R.id.textView_overweight);
            reminderTextViewItemView = itemView.findViewById(R.id.textView_no_rerminders);
            oneCommentImageItemView = itemView.findViewById(R.id.imageView_one_comment);
            commentTextItemView = itemView.findViewById(R.id.textView_no_comments);


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

            popMenuImageButtonItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onPopupMenuItemClick(position, itemView);
                        }
                    }
                }
            });

            oneCommentImageItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onCommentClick(position);
                        }
                    }
                }
            });

            commentTextItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onCommentClick(position);
                        }
                    }
                }
            });
        }
    }
}