package com.mychelantonacio.packstar.viewmodel;

import android.app.Application;
import android.content.ContentResolver;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.repository.BagRepository;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class BagViewModel extends AndroidViewModel {

    private BagRepository bagRepository;
    private LiveData<List<Bag>> allBagsSortedByName;


    public BagViewModel(@NonNull Application application) {
        super(application);
        bagRepository = new BagRepository(application);
        allBagsSortedByName = bagRepository.getAllBagsSortedByName();
    }

    public void insert(Bag bag) {
        bagRepository.insert(bag);
    }

    public void update(Bag bag) {
        bagRepository.update(bag);
    }

    public void delete(@NotNull Bag bag, ContentResolver cr){
        bag.deleteReminder(cr);
        bagRepository.delete(bag);
    }

    public void delete(Bag bag){
        bagRepository.delete(bag);
    }

    public LiveData<List<Bag>> getAllBagsSortedByName() {
        return allBagsSortedByName;
    }
}