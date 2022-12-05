package com.example.wallet.Repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.wallet.Model.Card;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.MetadataChanges;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Repository {
    final private FirebaseAuth mAuth;
    private final FirebaseFirestore db;

    public Repository() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void deleteItem(String nameDocument, String nameCollection) {
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection(nameCollection).document(nameDocument)
                .delete();
    }

    public void createItem(Map<String, Object> item, String nameDocument, String nameCollection) {
        db.collection("users").document(mAuth.getCurrentUser().getUid())
                .collection(nameCollection)
                .document(nameDocument)
                .set(item);
    }

    private List<Card> getCards(String userId) {
        List<Card> activities = new ArrayList<>();
        CollectionReference collectionReference = db.collection("users").document(userId).collection("activities");
        collectionReference.addSnapshotListener(MetadataChanges.INCLUDE, (value, e) -> {
            List<DocumentSnapshot> documentSnapshot = value.getDocuments();
            for (DocumentSnapshot document : documentSnapshot) {
                Card activity = new Card();
                activity.setNameCard(String.valueOf(document.getData().get("name")));
                activities.add(activity);
            }
        });
        return activities;
    }

    public LiveData<List<Card>> listenCard() {
        final MutableLiveData<List<Card>> data = new MutableLiveData<>();
        CollectionReference collectionReference = db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("card");
        collectionReference.addSnapshotListener((value, error) -> {
            if (error == null) {
                System.out.println("*27237172319HFHUDHSUF");
                List<DocumentSnapshot> documentSnapshot = value.getDocuments();
                List<Card> cards = new ArrayList<>();
                for (DocumentSnapshot document : documentSnapshot) {
                    Card item = new Card();
                    item.setNameCard(String.valueOf(document.getData().get("name")));
                    cards.add(item);
                }
                cards.sort(Comparator.comparing(Card::getNameCard));
                data.setValue(cards);
            }
        });
        return data;
    }
}