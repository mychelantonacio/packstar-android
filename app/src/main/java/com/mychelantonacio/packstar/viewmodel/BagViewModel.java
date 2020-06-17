package com.mychelantonacio.packstar.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.mychelantonacio.packstar.model.Bag;
import com.mychelantonacio.packstar.repository.BagRepository;
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
    public void deleteAll(){
        bagRepository.deleteAll();
    }
    public Bag findBagById(long id){
        return bagRepository.findBagById(id);
    }
    public LiveData<List<Bag>> getAllBagsSortedByName() {
        return allBagsSortedByName;
    }
}