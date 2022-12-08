package com.example.wallet.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.wallet.ViewModel.UserViewModel;
import com.example.wallet.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {
    private ActivityWelcomeBinding binding;
    private UserViewModel userAuthViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        userAuthViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        userAuthViewModel.getUserLiveData().observe(this, firebaseUser -> {
            if (firebaseUser != null) {
                startMainActivity();
            }
        });

        binding.logInButton.setOnClickListener(v ->  {
            String login = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            if (login.length() > 0 && password.length() > 0) {
                userAuthViewModel.login(login, password);
            } else {
                Toast.makeText(this, "Email Address and Password Must Be Entered", Toast.LENGTH_SHORT).show();
            }
        });

        binding.registrationButton.setOnClickListener(v -> {
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            if (email.length() > 0 && password.length() > 0) {
                userAuthViewModel.register(email, password);
            } else {
                Toast.makeText(this, "Email Address and Password Must Be Entered", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}