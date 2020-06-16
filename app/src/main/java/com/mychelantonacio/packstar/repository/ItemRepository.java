package com.mychelantonacio.packstar.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.mychelantonacio.packstar.model.Item;
import com.mychelantonacio.packstar.util.database.BagRoomDatabase;
import java.util.List;

public class ItemRepository {

    ItemDao itemDao;


    public ItemRepository(Application application){
        BagRoomDatabase db = BagRoomDatabase.getDatabase(application);
        itemDao = db.itemDao();
    }

    public void insert(Item item){
        BagRoomDatabase.databaseWriteExecutor.execute(() -> {
            itemDao.insert(item);
        });
    }

    public LiveData<List<Item>> getAllItemsWithBag(long id) {
        LiveData<List<Item>> items;
        items = itemDao.getAllItemsWithBag(id);
        return items;
    }
}