package com.mychelantonacio.packstar.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.mychelantonacio.packstar.model.Bag;
import java.util.List;


@Dao
public interface BagDao {

    //BAG
    @Insert
    void insert(Bag bag);

    @Query("DELETE FROM tb_bag")
    void deleteAll();

    @Query("SELECT * from tb_bag ORDER BY name ASC")
    LiveData<List<Bag>> getAllBagsSortedByName();

    @Query("SELECT * from tb_bag WHERE id = :id")
    Bag findBagById(long id);
}