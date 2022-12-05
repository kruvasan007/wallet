package com.example.wallet.View;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.wallet.Adpter.CardAdapter;
import com.example.wallet.Model.Card;
import com.example.wallet.ViewModel.CardViewModel;
import com.example.wallet.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private CardAdapter cardsAdapter;
    private RecyclerView cardsRecycler;
    private LinearSnapHelper snapHelper;
    private CardViewModel cardViewModel;
    private ImageView addButton;
    private ArrayList<Card> cardArrayList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initRecycler();
        addButton = binding.addButton;
        addButton.setOnClickListener(v -> {

        });
    }

    private void initRecycler() {
        cardViewModel = new ViewModelProvider(this).get(CardViewModel.class);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        cardArrayList = new ArrayList<>();
        cardViewModel.createCard( "KUSINA");
        getTasks();
        cardsRecycler = binding.cardRecycler;
        cardsAdapter = new CardAdapter(this, cardArrayList);
        cardsRecycler.setAdapter(cardsAdapter);
        snapHelper = new LinearSnapHelper();
        cardsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                View newSnapPosition = snapHelper.findSnapView(recyclerView.getLayoutManager());
                if (newSnapPosition != null && dy < 2) {
                    if (layoutManager.getPosition(newSnapPosition) != cardsAdapter.getLastSnapPosition()) {
                        cardsAdapter.setSnapPosition(layoutManager.getPosition(newSnapPosition));
                    }
                }
            }
        });
        cardsRecycler.post(() -> cardsAdapter.notifyDataSetChanged());
        cardsRecycler.setLayoutManager(layoutManager);
        cardsRecycler.setItemAnimator(new DefaultItemAnimator());
        snapHelper.attachToRecyclerView(cardsRecycler);
    }

    private void getTasks() {
        cardViewModel.listenActivitiesResponse().observe(this, items -> {
            cardArrayList.clear();
            cardArrayList.addAll(items);
            cardsAdapter.notifyDataSetChanged();
        });
    }
}
