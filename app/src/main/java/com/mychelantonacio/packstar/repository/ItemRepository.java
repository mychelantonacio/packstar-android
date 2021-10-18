package com.mychelantonacio.packstar.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.mychelantonacio.packstar.model.Item;
import com.mychelantonacio.packstar.util.database.BagRoomDatabase;
import java.util.List;


public class ItemRepository implements ItemDao{

    private ItemDao itemDao;


    public ItemRepository(Application application){
        BagRoomDatabase db = BagRoomDatabase.getDatabase(application);
        itemDao = db.itemDao();
    }

    public void insert(Item item){
        BagRoomDatabase.databaseWriteExecutor.execute(() -> {
            itemDao.insert(item);
        });
    }

    public void delete(Item item){
        BagRoomDatabase.databaseWriteExecutor.execute(() -> {
            itemDao.delete(item);
        });
    }

    public void update(Item item){
        BagRoomDatabase.databaseWriteExecutor.execute(() -> {
            itemDao.update(item);
        });
    }

    public LiveData<List<Item>> getAllItems() {
        LiveData<List<Item>> items;
        items = itemDao.getAllItems();
        return items;
    }

    public LiveData<List<Item>> getAllItemsWithBag(long id) {
        LiveData<List<Item>> items;
        items = itemDao.getAllItemsWithBag(id);
        return items;
    }

    public Item findItemById(long id){
        return itemDao.findItemById(id);
    }
}