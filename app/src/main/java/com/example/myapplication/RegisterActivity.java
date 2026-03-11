package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText inputNama, inputEmail, inputNpk, inputJabatan, inputPswd, inputPswd2;
    private CheckBox checkBox;
    private Button btnDaftar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private AlertDialog progressDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inisialisasi FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_regist);
        setSupportActionBar(toolbar);

        inputNama = findViewById(R.id.input_nama);
        inputEmail = findViewById(R.id.input_email);
        inputNpk = findViewById(R.id.input_npk);
        inputJabatan = findViewById(R.id.input_jabatan);
        inputPswd = findViewById(R.id.input_pswd);
        inputPswd2 = findViewById(R.id.input_pswd2);
        checkBox = findViewById(R.id.checkBox);
        btnDaftar = findViewById(R.id.btn_daftar);

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


    }

    private void registerUser() {
        String nama = inputNama.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String npk = inputNpk.getText().toString().trim();
        String jabatan = inputJabatan.getText().toString().trim();
        String password = inputPswd.getText().toString().trim();
        String password2 = inputPswd2.getText().toString().trim();

        // Validasi input
        if (TextUtils.isEmpty(nama) || TextUtils.isEmpty(email) || TextUtils.isEmpty(npk) ||
                TextUtils.isEmpty(jabatan) || TextUtils.isEmpty(password) || TextUtils.isEmpty(password2)) {
            Toast.makeText(this, "Harap isi semua data", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(password2)) {
            Toast.makeText(this, "Password tidak sesuai", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!checkBox.isChecked()) {
            Toast.makeText(this, "Setujui syarat dan ketentuan", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgressDialog();

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                    // Jika pendaftaran sukses, ambil UID pengguna
                    String userId = mAuth.getCurrentUser().getUid();

                    // Buat objek data untuk disimpan di Firestore
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("nama", nama);
                    userData.put("email", email);
                    userData.put("npk", npk);
                    userData.put("jabatan", jabatan);
                    userData.put("password", password);
                    userData.put("Uid", userId);


                    // Simpan data ke koleksi "users" di Firestore
                    db.collection("users").document(userId).set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Toast.makeText(RegisterActivity.this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show();
                               showAlertDialog("Pendaftaran Berhasil", "Anda telah berhasil masuk.",true);
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registrasi gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(RegisterActivity.this, "Pendaftaran gagal: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAlertDialog(String title, String message, boolean isSuccess) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (isSuccess) {
                        // Jika login berhasil, navigasi ke HomeActivity
                        Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        dialog.dismiss();
                    }
                });
        builder.show();

}

    private void showProgressDialog() {
        // Inflate layout dialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_loading, null);

        // Buat AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false); // Agar tidak bisa ditutup dengan klik di luar dialog

        progressDialog = builder.create();
        progressDialog.show();
    }
}