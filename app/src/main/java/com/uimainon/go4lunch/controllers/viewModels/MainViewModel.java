package com.uimainon.go4lunch.controllers.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.uimainon.go4lunch.models.User;

import java.util.List;

public class MainViewModel extends ViewModel {

    private MutableLiveData<List<User>> users;
    public LiveData<List<User>> getUser() {
        if (users == null) {
            users = new MutableLiveData<List<User>>();
            loadUsers();
        }
        return users;
    }


    private void loadUsers() {
        // do async operation to fetch articles
    }
}
