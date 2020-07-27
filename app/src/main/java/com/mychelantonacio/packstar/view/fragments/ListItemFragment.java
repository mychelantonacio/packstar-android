package com.mychelantonacio.packstar.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.model.Item;
import com.mychelantonacio.packstar.util.helpers.ItemTouchHelperCallback;
import com.mychelantonacio.packstar.util.helpers.SwipeToDeleteCallback;
import com.mychelantonacio.packstar.util.popupmenus.PopupListItem;
import com.mychelantonacio.packstar.view.activities.CreateItemActivity;
import com.mychelantonacio.packstar.view.activities.EditItemActivity;
import com.mychelantonacio.packstar.view.adapters.ItemListAdapter;
import com.mychelantonacio.packstar.viewmodel.ItemViewModel;
import java.util.List;

import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;
import com.mychelantonacio.packstar.util.helpers.OnStartDragListener;


public class ListItemFragment extends Fragment implements OnStartDragListener {

    private Bag currentBag;
    private ItemListAdapter adapter;
    private ItemViewModel itemViewModel;
    private FloatingActionButton fab;
    private ItemTouchHelper itemTouchHelper;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_list_bag_items, container, false);
        RecyclerView recyclerView = view.getRootView().findViewById(R.id.recyclerview_items);

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        adapter = new ItemListAdapter(getActivity(), itemViewModel, this);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter, getContext(), getActivity()));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        ItemTouchHelper.Callback callback =  new ItemTouchHelperCallback(adapter);
        this.itemTouchHelper = new ItemTouchHelper(callback);
        this.itemTouchHelper.attachToRecyclerView(recyclerView);

        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        getItemsFromSelectedBag();

        fab = (FloatingActionButton) view.getRootView().findViewById(R.id.fab_home);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CreateItemActivity.class);
            intent.putExtra("bag_parcelable", currentBag);
            startActivity(intent);
            getActivity().finish();
        });

        adapter.setOnItemClickListener(new ItemListAdapter.OnItemClickListener() {
            @Override
            public void onItemContainerItemClick(int position, View v) {
                Item currentItem = adapter.findItemByPosition(position);
                if(currentItem != null) {
                    Intent intent = new Intent(getActivity(), EditItemActivity.class);
                    intent.putExtra("item_parcelable", currentItem);
                    startActivity(intent);
                }
            }

            @Override
            public void onStatusItemClick(int position, View v) {
                Item currentItem = adapter.findItemByPosition(position);
                if(currentItem != null) {
                    PopupListItem popupListItem = new PopupListItem();
                    popupListItem.showPopupWindow(getContext(), v.findViewById(R.id.imageView_chip_status), currentItem, position, adapter);
                }
            }
        });
        return view;
    }

    private void getItemsFromSelectedBag(){
        Intent intent = getActivity().getIntent();
        currentBag = (Bag) intent.getParcelableExtra("selected_bag");
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        if(currentBag != null){
            itemViewModel.getAllItemsWithBag(currentBag.getId()).observe(getActivity(), new Observer<List<Item>>() {
                @Override
                public void onChanged(List<Item> items) {
                    adapter.setItems(items);
                }
            });
        }
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }
}