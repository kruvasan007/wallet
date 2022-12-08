package com.example.wallet.ViewModel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.wallet.Model.Card;
import com.example.wallet.Repository.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CardViewModel extends AndroidViewModel {
    private Repository repository;

    public CardViewModel(@NonNull Application application) {
        super(application);
        repository = new Repository();
    }

    public void deleteCard(String name) {
        repository.deleteItem(name, "card");
        Toast.makeText(getApplication().getBaseContext(), "Delete", Toast.LENGTH_SHORT).show();
    }

    public LiveData<List<Card>> listenActivitiesResponse(String type) {
        return repository.listenCard(type);
    }

    public void createCard(Card card) {
        if (!card.getNameCard().equals("")) {
            Map<String, Object> activity = new HashMap<>();
            activity.put("name", card.getNameCard());
            activity.put("code", card.getBarcode());
            activity.put("type", card.getType());
            repository.createItem(activity, card.getNameCard(), "card");
            Toast.makeText(getApplication().getBaseContext(), "Add new card", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplication().getBaseContext(), "Null field", Toast.LENGTH_SHORT).show();
        }
    }
}
