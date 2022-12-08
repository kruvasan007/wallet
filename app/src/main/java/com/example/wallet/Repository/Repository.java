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
import java.util.Objects;

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

    public LiveData<List<Card>> listenCard(String type) {
        final MutableLiveData<List<Card>> data = new MutableLiveData<>();
        CollectionReference collectionReference = db.collection("users")
                .document(mAuth.getCurrentUser().getUid())
                .collection("card");
        collectionReference.addSnapshotListener((value, error) -> {
            if (error == null) {
                List<DocumentSnapshot> documentSnapshot = value.getDocuments();
                List<Card> cards = new ArrayList<>();
                for (DocumentSnapshot document : documentSnapshot) {
                    if(!type.equals("all")) {
                        if (String.valueOf(Objects.requireNonNull(document.getData()).get("type")).equals(type)) {
                            Card item = new Card();
                            item.setDistance(1000000.0);
                            item.setNameCard(String.valueOf(document.getData().get("name")));
                            item.setBarcode(String.valueOf(document.getData().get("code")));
                            item.setBarcode(String.valueOf(document.getData().get("type")));
                            cards.add(item);
                        }
                    } else{
                        Card item = new Card();
                        item.setNameCard(String.valueOf(document.getData().get("name")));
                        item.setBarcode(String.valueOf(document.getData().get("code")));
                        item.setBarcode(String.valueOf(document.getData().get("type")));
                        cards.add(item);
                    }
                }
                cards.sort(Comparator.comparing(Card::getNameCard));
                data.setValue(cards);
            }
        });
        return data;
    }
}