package com.mychelantonacio.packstar.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.mychelantonacio.packstar.model.Item;
import java.util.List;


@Dao
public interface ItemDao {

    @Insert
    void insert(Item item);

    @Delete
    void delete(Item item);


    @Query("SELECT * from tb_item")
    LiveData<List<Item>> getAllItems();

    @Query("SELECT * from tb_item WHERE bagId = :idBag")
    LiveData<List<Item>> getAllItemsWithBag(long idBag);

}
