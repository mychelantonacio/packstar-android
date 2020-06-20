package com.mychelantonacio.packstar.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.model.Item;
import com.mychelantonacio.packstar.view.activities.CreateItemActivity;
import com.mychelantonacio.packstar.view.activities.ListItemActivity;
import com.mychelantonacio.packstar.view.adapters.BagListAdapter;
import com.mychelantonacio.packstar.viewmodel.BagViewModel;
import com.mychelantonacio.packstar.viewmodel.ItemViewModel;
import java.util.List;


public class ListBagFragment extends Fragment {

    private BagListAdapter adapter;
    private BagViewModel bagViewModel;
    private ItemViewModel itemViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_list_bags, container, false);
        RecyclerView recyclerView = view.getRootView().findViewById(R.id.recyclerview_bags);
        adapter = new BagListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        bagViewModel = new ViewModelProvider(this).get(BagViewModel.class);
        bagViewModel.getAllBagsSortedByName().observe(getActivity(), new Observer<List<Bag>>() {
            @Override
            public void onChanged(List<Bag> bags) {
                adapter.setBags(bags);
            }
        });

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        itemViewModel.getAllItems().observe(getActivity(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                adapter.setItems(items);
            }
        });

        adapter.setOnItemClickListener(new BagListAdapter.OnItemClickListener() {

            @Override
            public void onAddItemClick(int position) {
                Bag currentBag = bagViewModel.getAllBagsSortedByName().getValue().get(position);
                Intent intent = new Intent(getActivity(), CreateItemActivity.class);
                intent.putExtra("bag_parcelable", currentBag);
                startActivity(intent);
            }

            @Override
            public void onCardItemClick(int position) {
                Bag currentBag = bagViewModel.getAllBagsSortedByName().getValue().get(position);
                Intent intent = new Intent(getActivity(), ListItemActivity.class);
                intent.putExtra("selected_bag", currentBag);
                startActivity(intent);
            }
        });
        return view;
    }
}
