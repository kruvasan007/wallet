package com.example.wallet.View;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wallet.databinding.ActivityMapsBinding;
import com.yandex.mapkit.MapKitFactory;

public class MapsActivity extends AppCompatActivity {
    private ActivityMapsBinding binding;
    private ImageView homeButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapKitFactory.initialize(this);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        homeButton = binding.homeButton;
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            onStop();
        });


    }
}
