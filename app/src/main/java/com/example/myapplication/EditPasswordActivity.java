package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;


import java.util.HashMap;
import java.util.Map;

public class EditPasswordActivity extends AppCompatActivity {
    private TextInputEditText textViewPwLama, textViewPwBaru;
    private  Button btnSimpan;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseFirestore db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pw);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.editpw_tlbr);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        textViewPwLama = findViewById(R.id.edit_pw_lama);
        textViewPwBaru = findViewById(R.id.edit_pw_baru);
        btnSimpan = findViewById(R.id.btn_simpan_pw);


        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        btnSimpan.setOnClickListener(v -> updatePassword());




    }

    private void updatePassword() {
        String pwLama = textViewPwLama.getText().toString();
        String pwBaru = textViewPwBaru.getText().toString();

        if (currentUser == null) {
            Toast.makeText(this, "Anda harus masuk untuk mengubah kata sandi.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pwLama.isEmpty() || pwBaru.isEmpty()) {
            Toast.makeText(this, "Kata sandi lama dan baru harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pwBaru.length() < 6) { // Misalkan kita ingin kata sandi baru minimal 6 karakter
            textViewPwBaru.setError("Kata sandi baru harus minimal 6 karakter.");
            return;
        } else {
            textViewPwBaru.setError(null);
        }

        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), pwLama);

        currentUser.reauthenticate(credential).addOnSuccessListener(unused -> {
            currentUser.updatePassword(pwBaru).addOnSuccessListener(aVoid -> {
                db.collection("users").document(currentUser.getUid()).update("password", pwBaru)
                        .addOnSuccessListener(aVoid1 -> Toast.makeText(this, "Kata sandi berhasil diperbarui",
                                Toast.LENGTH_SHORT).show()).addOnFailureListener(e -> {
                                    Log.e("EditPasswordActivity", "Gagal menyimpan di Firestore: ", e);
                                    Toast.makeText(this, "Gagal menyimpan Email.", Toast.LENGTH_SHORT).show();
                        });
            }).addOnFailureListener(e -> {
                Log.e("EditPasswordActivity", "Gagal memperbarui kata sandi: ", e);
                Toast.makeText(this, "Gagal memperbarui kata sandi", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.e("EditPasswordActivity", "Autentikasi ulang gagal: ", e);
            Toast.makeText(this, "Autentikasi ulang gagal. Cek kata sandi lama Anda.", Toast.LENGTH_SHORT).show();
        });
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
           finish();
           return true;
        }
        return super.onOptionsItemSelected(item);
    }



}
