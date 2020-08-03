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

    @Query("DELETE FROM tb_bag")
    void deleteAll();

    @Delete
    void delete(Bag bag);

    //TODO refactor to getAllBagsSortedById
    @Query("SELECT * from tb_bag ORDER BY id DESC")
    LiveData<List<Bag>> getAllBagsSortedByName();

    @Query("SELECT * from tb_bag WHERE id = :id")
    Bag findBagById(long id);

    @Query("SELECT COUNT(*) FROM tb_bag")
    int getCount();
}