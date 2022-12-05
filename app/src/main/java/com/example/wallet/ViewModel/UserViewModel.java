package com.example.wallet.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.wallet.Repository.UserRepository;
import com.google.firebase.auth.FirebaseUser;

public class UserViewModel extends AndroidViewModel {
    private UserRepository authRepository;
    private MutableLiveData<FirebaseUser> userLiveData;

    public UserViewModel(@NonNull Application application) {
        super(application);
        authRepository = new UserRepository(application);
        userLiveData = authRepository.getUserData();
    }

    public void login(String email, String password) {
        authRepository.singUpUser(email, password);
        userLiveData = authRepository.getUserData();
    }

    public void register(String email, String password) {
        authRepository.registerUser(email, password);
        userLiveData = authRepository.getUserData();
    }

    public LiveData<FirebaseUser> getUserLiveData() {
        return userLiveData;
    }

    public LiveData<String> getUserName() {
        return authRepository.getUserName();
    }

    public void logOut() {
        authRepository.logOut();
    }
}
