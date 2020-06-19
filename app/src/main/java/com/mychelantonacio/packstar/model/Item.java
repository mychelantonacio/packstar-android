package com.mychelantonacio.packstar.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.mychelantonacio.packstar.util.enums.ItemStatusEnum;


@Entity(tableName = "tb_item")
public class Item implements Comparable{

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;

    private Long bagId;

    @NonNull
    private String name;

    @NonNull
    private Integer quantity;

    private Double weight;

    //B - (Need to buy)
    //A - (Already have)
    //N - (N/A)
    @ColumnInfo(defaultValue = "N")
    private String status = "N";
    

    //Getters / Setters
    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public Long getBagId() {
        return bagId;
    }

    public void setBagId(Long bagId) {
        this.bagId = bagId;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(@NonNull Integer quantity) {
        this.quantity = quantity;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int compareTo(Object o) {
        if(this.getStatus().equals(ItemStatusEnum.NEED_TO_BUY.getStatusCode())){
            return -1;
        }
        else if(this.getStatus().equals(ItemStatusEnum.NEED_TO_BUY.getStatusCode())
                && ((Item) o).getStatus().equals(ItemStatusEnum.NEED_TO_BUY.getStatusCode())){
            return 0;
        }
        else{
            return 1;
        }
    }
}