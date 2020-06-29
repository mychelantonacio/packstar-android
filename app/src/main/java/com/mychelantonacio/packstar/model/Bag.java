package com.mychelantonacio.packstar.model;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "tb_bag")
public class Bag implements Parcelable{

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Long id;

    @NonNull
    private String name;

    @NonNull
    @ColumnInfo(name = "travel_date")
    private String travelDate;

    private Double weight;
    private String comment;


    public Bag(){}

    @Ignore
    public Bag(String name, String travelDate, Double weight, String comment){
        this.name = name;
        this.travelDate = travelDate;
        this.weight = weight;
        this.comment = comment;
    }

    //Getters-Setters
    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getTravelDate() {
        return travelDate;
    }

    public void setTravelDate(@NonNull String travelDate) {
        this.travelDate = travelDate;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }



    //Parcelable
    @Ignore
    protected Bag(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        name = in.readString();
        travelDate = in.readString();
        if (in.readByte() == 0) {
            weight = null;
        } else {
            weight = in.readDouble();
        }
        comment = in.readString();
    }

    public static final Parcelable.Creator<Bag> CREATOR = new Parcelable.Creator<Bag>() {
        @Override
        public Bag createFromParcel(Parcel in) {
            return new Bag(in);
        }
        @Override
        public Bag[] newArray(int size) {
            return new Bag[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeString(name);
        dest.writeString(travelDate);
        if (weight == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(weight);
        }
        dest.writeString(comment);
    }
}