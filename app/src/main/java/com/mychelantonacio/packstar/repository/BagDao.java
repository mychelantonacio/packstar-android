package com.mychelantonacio.packstar.repository;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mychelantonacio.packstar.model.Bag;

import java.util.List;


@Dao
public interface BagDao {

    @Insert
    void insert(Bag bag);

    @Update
    void update(Bag bag);

    @Delete
    void delete(Bag bag);

    @Query("SELECT * from tb_bag ORDER BY id DESC")
    LiveData<List<Bag>> getAllBagsSortedByName();

    @Query("SELECT COUNT(*) FROM tb_bag")
    int getCount();
}