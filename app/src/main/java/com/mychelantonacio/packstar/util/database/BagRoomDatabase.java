package com.mychelantonacio.packstar.util.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.model.Item;
import com.mychelantonacio.packstar.repository.BagDao;
import com.mychelantonacio.packstar.repository.ItemDao;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {Bag.class, Item.class}, version = 3, exportSchema = false)
public abstract class BagRoomDatabase extends RoomDatabase {

    public abstract BagDao bagDao();
    public abstract ItemDao itemDao();

    private static volatile BagRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public synchronized static BagRoomDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    BagRoomDatabase.class, "db_packstar")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }
}
