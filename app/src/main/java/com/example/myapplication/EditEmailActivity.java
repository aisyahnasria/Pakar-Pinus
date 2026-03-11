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

public class EditEmailActivity extends AppCompatActivity {
    private TextInputEditText textViewEmail, textViewPw;
    private  Button btnSimpan;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseFirestore db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_email);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.editemail_tlbr);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        textViewEmail = findViewById(R.id.edit_email);
        textViewPw = findViewById(R.id.pw_editEmail);
        btnSimpan = findViewById(R.id.btn_simpan_email);


        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();


        btnSimpan.setOnClickListener(v -> updateEmail());




    }

    private void updateEmail() {
        String emailBaru = textViewEmail.getText().toString();
        String currentPassword = textViewPw.getText().toString();

        if (currentUser == null) {
            Toast.makeText(this, "Anda harus masuk untuk mengubah email.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (emailBaru.isEmpty()) {
            Toast.makeText(this, "Email harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }



        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);
        currentUser.reauthenticate(credential).addOnSuccessListener(unused -> {
            // Update email
            currentUser.verifyBeforeUpdateEmail(emailBaru).addOnSuccessListener(aVoid -> {
                db.collection("users").document(currentUser.getUid()).update("email", emailBaru)
                        .addOnSuccessListener(aVoid1 -> Toast.makeText(this, "Profil berhasil disimpan. Email verifikasi telah dikirim.", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> {
                            Log.e("EditProfileActivity", "Gagal menyimpan profil di Firestore: ", e);
                            Toast.makeText(this, "Gagal menyimpan data profil.", Toast.LENGTH_SHORT).show();
                        });
            }).addOnFailureListener(e -> {
                Log.e("EditEmailActivity", "Gagal memperbarui email: ", e);
                Toast.makeText(this, "Gagal memperbarui email. Pastikan email baru valid.", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.e("EditEmailActivity", "Reautentikasi gagal: ", e);
            Toast.makeText(this, "Gagal reautentikasi. Periksa password Anda.", Toast.LENGTH_SHORT).show();
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
