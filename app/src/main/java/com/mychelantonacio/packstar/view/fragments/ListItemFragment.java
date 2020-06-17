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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.model.Item;
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
        adapter = new BagItemListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getContext(), VERTICAL);
        recyclerView.addItemDecoration(itemDecor);
        getItemsFromSelectedBag();
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
