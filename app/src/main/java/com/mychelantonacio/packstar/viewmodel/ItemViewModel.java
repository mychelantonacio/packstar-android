package com.mychelantonacio.packstar.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.mychelantonacio.packstar.model.Item;
import com.mychelantonacio.packstar.repository.ItemRepository;
import java.util.List;
import javax.inject.Inject;


public class ItemViewModel extends AndroidViewModel {

    private ItemRepository itemRepository;


    @Inject
    public ItemViewModel(@NonNull Application application) {
        super(application);
        itemRepository = new ItemRepository(application);
    }

    public void insert(Item item) {
        itemRepository.insert(item);
    }

    public LiveData<List<Item>> getAllItems() {
        LiveData<List<Item>> items;
        items = itemRepository.getAllItems();
        return items;
    }

    public LiveData<List<Item>> getAllItemsWithBag(long id) {
        LiveData<List<Item>> items;
        items = itemRepository.getAllItemsWithBag(id);
        return items;
    }
}