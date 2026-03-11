package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button masuk = findViewById(R.id.btnlogin);
        Button daftar = findViewById(R.id.btnregister);


       masuk.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
       daftar.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));



    }
}