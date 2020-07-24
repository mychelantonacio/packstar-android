package com.mychelantonacio.packstar.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mychelantonacio.packstar.R;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.model.Item;
import com.mychelantonacio.packstar.util.Dialogs.CommentFragmentDialog;
import com.mychelantonacio.packstar.util.popupmenus.PopupListBag;
import com.mychelantonacio.packstar.view.activities.CreateItemActivity;
import com.mychelantonacio.packstar.view.activities.EmptyItemActivity;
import com.mychelantonacio.packstar.view.activities.ListItemActivity;
import com.mychelantonacio.packstar.view.adapters.BagListAdapter;
import com.mychelantonacio.packstar.view.adapters.ItemListAdapter;
import com.mychelantonacio.packstar.viewmodel.BagViewModel;
import com.mychelantonacio.packstar.viewmodel.ItemViewModel;
import java.util.List;


public class ListBagFragment extends Fragment implements CommentFragmentDialog.NoticeDialogListener {

    private BagListAdapter bagAdapter;
    private BagViewModel bagViewModel;
    private ItemViewModel itemViewModel;

    private CommentFragmentDialog commentFragmentDialog;
    private static final String DIALOG_COMMENT = "CommentFragmentDialog";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recyclerview_list_bags, container, false);
        RecyclerView recyclerView = view.getRootView().findViewById(R.id.recyclerview_bags);
        bagAdapter = new BagListAdapter(getActivity());
        recyclerView.setAdapter(bagAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        layoutManager.scrollToPosition(0);
        recyclerView.setLayoutManager(layoutManager);

        bagViewModel = new ViewModelProvider(this).get(BagViewModel.class);
        bagViewModel.getAllBagsSortedByName().observe(getActivity(), new Observer<List<Bag>>() {
            @Override
            public void onChanged(List<Bag> bags) {
                bagAdapter.setBags(bags);
            }
        });

        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);
        itemViewModel.getAllItems().observe(getActivity(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                bagAdapter.setItems(items);
            }
        });

        bagAdapter.setOnItemClickListener(new BagListAdapter.OnItemClickListener() {
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
                int itemsInCurrentBag = bagAdapter.getItemsAttatchedWithCurrentBag(currentBag).size();
                Log.d("jojoba", "itemsInCurrentBag " + itemsInCurrentBag);

                if(itemsInCurrentBag > 0){
                    Intent intent = new Intent(getActivity(), ListItemActivity.class);
                    intent.putExtra("selected_bag", currentBag);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(getActivity(), EmptyItemActivity.class);
                    intent.putExtra("selected_bag", currentBag);
                    startActivity(intent);
                }
            }

            @Override
            public void onPopupMenuItemClick(int position, View v) {
                Bag currentBag = bagAdapter.findBagByPosition(position);
                List<Item> currentItems = bagAdapter.getItemsAttatchedWithCurrentBag(currentBag);
                if(currentBag != null) {
                    PopupListBag popupListBag = new PopupListBag();
                    popupListBag.showPopupWindow(getContext(), v.findViewById(R.id.imageButton_menu_dots), currentBag, position, currentItems, itemViewModel, bagViewModel);
                }
            }

            @Override
            public void onCommentClick(int position) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                Bag currentBag = bagViewModel.getAllBagsSortedByName().getValue().get(position);
                if(currentBag != null){
                    if (!currentBag.getComment().equals(null) && !currentBag.getComment().equals(""))    {
                        commentFragmentDialog = new CommentFragmentDialog(currentBag.getComment());
                        commentFragmentDialog.show(fragmentManager, DIALOG_COMMENT);
                    }
                    else {
                        commentFragmentDialog = new CommentFragmentDialog(getResources().getString(R.string.list_bag_comment_empty));
                        commentFragmentDialog.show(fragmentManager, DIALOG_COMMENT);
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
    }
}
