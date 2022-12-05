package com.example.wallet.Repository;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {
    private static final String TAG = "E";
    private final Application app;
    private final FirebaseAuth firebaseAuth;
    private final MutableLiveData<FirebaseUser> userData;
    private final MutableLiveData<Boolean> loggedOutData;
    private final FirebaseFirestore db;
    private DatabaseReference mDatabase;

    public UserRepository(Application application) {
        this.app = application;
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.userData = new MutableLiveData<>();
        this.loggedOutData = new MutableLiveData<>();
        this.db = FirebaseFirestore.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            userData.postValue(firebaseAuth.getCurrentUser());
            loggedOutData.postValue(false);
        }
    }

    @SuppressLint("NewApi")
    public void registerUser(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userData.postValue(firebaseAuth.getCurrentUser());
                        mDatabase = FirebaseDatabase.getInstance().getReference();
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        mDatabase.child("users").child(user.getUid()).child("email").setValue(user.getEmail());
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(app.getApplicationContext(), "Failed registration", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @SuppressLint("NewApi")
    public void singUpUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        userData.postValue(firebaseAuth.getCurrentUser());
                    } else {
                        Toast.makeText(app.getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(app.getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void logOut() {
        firebaseAuth.signOut();
        loggedOutData.postValue(true);
    }

    public MutableLiveData<FirebaseUser> getUserData() {
        return userData;
    }

    public MutableLiveData<String> getUserName() {
        final MutableLiveData<String> data = new MutableLiveData<>();
        DocumentReference documentReference = db.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        documentReference.addSnapshotListener((value, error) -> {
            if (error == null) {
                Map<String, Object> name = value.getData();
                if (name != null) {
                    data.setValue(String.valueOf(name.get("name")));
                } else data.setValue("");
            } else {
                Log.e("E", "Error: " + error.getMessage());
            }
        });
        return data;
    }

    public void setUserName(String name) {
        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        db.collection("users").document(firebaseAuth.getCurrentUser().getUid()).set(item);
    }

    public MutableLiveData<Boolean> getLoggedOutData() {
        return loggedOutData;
    }
}
