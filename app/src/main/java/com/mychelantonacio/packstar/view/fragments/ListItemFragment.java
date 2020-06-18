package com.mychelantonacio.packstar.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.model.Item;
import com.mychelantonacio.packstar.util.enums.ItemStatusEnum;
import com.mychelantonacio.packstar.util.helpers.SwipeToDeleteCallback;
import com.mychelantonacio.packstar.view.adapters.BagItemListAdapter;
import com.mychelantonacio.packstar.viewmodel.ItemViewModel;
import java.util.List;
import static androidx.recyclerview.widget.DividerItemDecoration.VERTICAL;


public class ListItemFragment extends Fragment {

    private Bag currentBag;
    private BagItemListAdapter adapter;
    private ItemViewModel itemViewModel;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_list_bag_items, container, false);
        RecyclerView recyclerView = view.getRootView().findViewById(R.id.recyclerview_items);
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        adapter = new BagItemListAdapter(getActivity(), itemViewModel);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(adapter, getContext()));
        itemTouchHelper.attachToRecyclerView(recyclerView);

        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        getItemsFromSelectedBag();


        adapter.setOnItemClickListener(new BagItemListAdapter.OnItemClickListener() {
            @Override
            public void onStatusItemClick(int position, View v) {
                Item currentItem = adapter.findItemByPosition(position);
                if(currentItem != null) {
                    PopupMenu popup = new PopupMenu(getContext(), v.findViewById(R.id.imageView_chip_status));
                    popup.getMenuInflater().inflate(R.menu.chip_status_menu, popup.getMenu());
                    popup.setGravity(0);

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.menu_chip_need_to_buy) {
                                currentItem.setStatus(ItemStatusEnum.NEED_TO_BUY.getStatusCode());
                                Toast.makeText(getContext(), "Status updated!", Toast.LENGTH_SHORT).show();
                            }
                            if (item.getItemId() == R.id.menu_chip_already_have) {
                                currentItem.setStatus(ItemStatusEnum.ALREADY_HAVE.getStatusCode());
                                Toast.makeText(getContext(), "Status updated!", Toast.LENGTH_SHORT).show();
                            }
                            if (item.getItemId() == R.id.menu_chip_remove) {
                                currentItem.setStatus(ItemStatusEnum.NON_INFORMATION.getStatusCode());
                                Toast.makeText(getContext(), "Status updated!", Toast.LENGTH_SHORT).show();
                            }
                            adapter.updateStatus(currentItem, position);
                            return true;
                        }
                    });
                    popup.show();
                }
            }
        });
        return view;
    }

    private void getItemsFromSelectedBag(){
        Intent intent = getActivity().getIntent();
        currentBag = (Bag) intent.getParcelableExtra("selected_bag");
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        itemViewModel.getAllItemsWithBag(currentBag.getId()).observe(getActivity(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                adapter.setItems(items);
            }
        });
    }
}