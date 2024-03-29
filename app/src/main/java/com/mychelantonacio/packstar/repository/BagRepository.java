package com.mychelantonacio.packstar.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.util.database.BagRoomDatabase;
import java.util.List;


public class BagRepository implements BagDao{

    private BagDao bagDao;
    private LiveData<List<Bag>> allBagsSortedByName;


    public BagRepository(Application application) {
        BagRoomDatabase db = BagRoomDatabase.getDatabase(application);
        bagDao = db.bagDao();
        allBagsSortedByName = bagDao.getAllBagsSortedByName();
    }

    public void insert(Bag bag) {
        BagRoomDatabase.databaseWriteExecutor.execute(() -> {
            bagDao.insert(bag);
        });
    }

    public void update(Bag bag) {
        BagRoomDatabase.databaseWriteExecutor.execute(() -> {
            bagDao.update(bag);
        });
    }

    public void delete(Bag bag){
        BagRoomDatabase.databaseWriteExecutor.execute(() -> {
            bagDao.delete(bag);
        });
    }

    public LiveData<List<Bag>> getAllBagsSortedByName() {
        return allBagsSortedByName;
    }


    public int getCount() {
        return bagDao.getCount();
    }
}