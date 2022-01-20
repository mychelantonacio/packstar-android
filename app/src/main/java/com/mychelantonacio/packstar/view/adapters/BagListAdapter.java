package com.mychelantonacio.packstar.view.adapters;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private String TEXT_ORANGE = "#EE6C4D";
    private Context context;

    public BagListAdapter(Context context){
        inflater = LayoutInflater.from(context);
        this.context = context;
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

            if(getItemQuantity(currentBag) == 1 ){
                holder.unitItemItemView.setText(context.getResources().getString(R.string.label_bag_item));
            }
            else{
                holder.unitItemItemView.setText(context.getResources().getString(R.string.label_bag_items));
            }
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
                holder.reminderTextViewItemView.setAlpha(1f);

                holder.noReminderBulletImageItemView.setVisibility(View.GONE);
                holder.withReminderBulletImageItemView.setVisibility(View.VISIBLE);
            }
            else {
                holder.reminderTextViewItemView.setText( context.getResources().getString(R.string.label_no_reminders) == null ? "No reminders" : context.getResources().getString(R.string.label_no_reminders)  );
                holder.reminderTextViewItemView.setAlpha(0.5f);
                holder.noReminderBulletImageItemView.setVisibility(View.VISIBLE);
                holder.withReminderBulletImageItemView.setVisibility(View.GONE);
            }
            if(currentBag.getComment() != null && !currentBag.getComment().isEmpty()){
                holder.commentTextItemView.setText(context.getResources().getString(R.string.label_read_comment) == null ? "Read comment" : context.getResources().getString(R.string.label_read_comment));
                holder.commentTextItemView.setTextColor(Color.parseColor(TEXT_ORANGE));
                holder.commentTextItemView.setAlpha(1f);

                holder.noCommentBulletImageItemView.setVisibility(View.GONE);
                holder.withCommentBulletImageItemView.setVisibility(View.VISIBLE);

            }
            else {
                holder.commentTextItemView.setText(context.getResources().getString(R.string.label_no_comments) == null ? "No comments" : context.getResources().getString(R.string.label_no_comments));
                holder.commentTextItemView.setTextColor(Color.BLACK);
                holder.commentTextItemView.setAlpha(0.5f);
                holder.noCommentBulletImageItemView.setVisibility(View.VISIBLE);
                holder.withCommentBulletImageItemView.setVisibility(View.GONE);

                //enlarge no comment touchable area
                final View noComment = (View) holder.commentTextItemView.getParent();
                noComment.post(() -> {
                    final Rect rect = new Rect();
                    holder.commentTextItemView.getHitRect(rect);
                    rect.top -= 100;
                    rect.left -= 100;
                    rect.bottom += 100;
                    rect.right += 100;
                    noComment.setTouchDelegate( new TouchDelegate( rect , holder.commentTextItemView));
                });
            }
            //enlarge menu touchable area
            final View parentMenuThreeDots = (View) holder.popMenuImageButtonItemView.getParent();
            parentMenuThreeDots.post(() -> {
                final Rect rect = new Rect();
                holder.popMenuImageButtonItemView.getHitRect(rect);
                rect.top -= 50;
                rect.left -= 50;
                rect.bottom += 50;
                rect.right += 50;
                parentMenuThreeDots.setTouchDelegate( new TouchDelegate( rect , holder.popMenuImageButtonItemView));
            });
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

    public double getItemWeight(Bag bag){
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

    public void delete(Bag bag, ContentResolver cr){
        if(bag.isEventSet()){
            bag.deleteReminder(cr);
        }
        this.bags.remove(bag);
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
        private final Button addButtonItemView;
        private final TextView addItemView;
        private final TextView itemQuantityItemView;
        private final TextView itemWeightItemView;
        private final MaterialCardView cardViewItemView;
        private final ImageButton popMenuImageButtonItemView;
        private final ImageView redBulletItemView;
        private final TextView overweightItemView;
        private final TextView commentTextItemView;
        private final ImageView noCommentBulletImageItemView;
        private final ImageView withCommentBulletImageItemView;
        private final TextView reminderTextViewItemView;
        private final ImageView noReminderBulletImageItemView;
        private final ImageView withReminderBulletImageItemView;
        private final TextView unitItemItemView;

        private BagViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            bagNameItemView = itemView.findViewById(R.id.text_view_bag_name);
            bagDateItemView = itemView.findViewById(R.id.text_view_bag_date);
            bagWeightItemView = itemView.findViewById(R.id.text_view_bag_weight);
            addButtonItemView = itemView.findViewById(R.id.button_add_item);
            addItemView = itemView.findViewById(R.id.textView_add_items);
            itemQuantityItemView = itemView.findViewById(R.id.textview_counter_item);
            itemWeightItemView = itemView.findViewById(R.id.textview_counter_weight);
            cardViewItemView = itemView.findViewById(R.id.cardview_bag);
            popMenuImageButtonItemView = itemView.findViewById(R.id.imageButton_menu_dots);
            redBulletItemView = itemView.findViewById(R.id.imageView_red_bullet);
            overweightItemView = itemView.findViewById(R.id.textView_overweight);
            commentTextItemView = itemView.findViewById(R.id.textView_no_comments);
            noCommentBulletImageItemView = itemView.findViewById(R.id.imageView_bullet_no_comment);
            withCommentBulletImageItemView = itemView.findViewById(R.id.imageView_bullet_with_comment);
            reminderTextViewItemView = itemView.findViewById(R.id.textView_no_reminders);
            noReminderBulletImageItemView = itemView.findViewById(R.id.imageView_bullet_no_reminders);
            withReminderBulletImageItemView = itemView.findViewById(R.id.imageView_bullet_with_reminders);
            unitItemItemView = itemView.findViewById(R.id.textview_unit_item);

            addButtonItemView.setOnClickListener(v -> {
                if(listener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onAddItemClick(position);
                    }
                }
            });

            addItemView.setOnClickListener(v -> {
                if(listener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onAddItemClick(position);
                    }
                }
            });

            cardViewItemView.setOnClickListener(v -> {
                if(listener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onCardItemClick(position);
                    }
                }
            });

            popMenuImageButtonItemView.setOnClickListener(v -> {
                if(listener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onPopupMenuItemClick(position, itemView);
                    }
                }
            });

            commentTextItemView.setOnClickListener(v -> {
                if(listener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onCommentClick(position);
                    }
                }
            });

            withCommentBulletImageItemView.setOnClickListener(v -> {
                if(listener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onCommentClick(position);
                    }
                }
            });
        }
    }
}