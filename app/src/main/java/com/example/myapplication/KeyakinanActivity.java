package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyakinanActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button btnDiagnosa;
    private ArrayList<String> selectedGejala;
    private ArrayList<Double> keyakinanValues;
    private List<Gejala> gejalaList = new ArrayList<>();
    FirebaseFirestore db;

    // Daftar keyakinan
    private final String[] keyakinanList = {
            "Tidak Yakin",
            "Hampir Tidak Yakin",
            "Kemungkinan Besar Tidak Yakin",
            "Mungkin Tidak Yakin",
            "Tidak Tahu",
            "Mungkin",
            "Kemungkinan Besar Yakin",
            "Hampir Yakin",
            "Yakin"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        // memanggil database
        db = FirebaseFirestore.getInstance();

        // Inisialisasi
        recyclerView = findViewById(R.id.rv_keyakinan);
        btnDiagnosa = findViewById(R.id.btn_choose_diagnosa);

        // Terima data intent
        Intent intent = getIntent();
        ArrayList<String> selectedGejalaNames = intent.getStringArrayListExtra("selectedGejala");
        keyakinanValues = new ArrayList<>();

        // Menyiapkan daftar gejala
        if (selectedGejalaNames != null) {
            for (String namaGejala : selectedGejalaNames) {
                Gejala gejala = new Gejala(namaGejala);
                gejalaList.add(gejala);
            }
        }

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        KeyakinanAdapter adapter = new KeyakinanAdapter(gejalaList, keyakinanList);
        recyclerView.setAdapter(adapter);

        // Tombol Diagnosa
        btnDiagnosa.setOnClickListener(v -> {
            // Ambil data gejala dengan keyakinan dari adapter
            List<Gejala> gejalaDenganKeyakinan = adapter.getGejalaWithKeyakinan();

            // List untuk menyimpan data yang akan dikirim
            ArrayList<String> selectedGejala = new ArrayList<>();
            ArrayList<String> keyakinanNames = new ArrayList<>();

            // Isi data dari gejala yang memiliki keyakinan
            for (Gejala gejala : gejalaDenganKeyakinan) {
                if (gejala.getKeyakinan() != null) { // Pastikan keyakinan telah diisi
                    selectedGejala.add(gejala.getNamaGejala());
                    keyakinanNames.add(gejala.getKeyakinan());
                }
            }

            if (selectedGejala.isEmpty() || keyakinanNames.size() != selectedGejala.size()) {
                Toast.makeText(this, "Lengkapi semua tingkat keyakinan!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kirim data ke HasilDiagnosaActivity
            Intent intent2 = new Intent(KeyakinanActivity.this, HasilDiagnosaActivity.class);
            intent2.putStringArrayListExtra("selectedGejala", selectedGejala);
            intent2.putStringArrayListExtra("keyakinanNames", keyakinanNames);
            startActivity(intent2);
            Log.d("keyakinanActivity", "onCreate: data terkirim dengan gejala"+ selectedGejala + "dan keyakinan" + keyakinanNames);
        });




        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.choose_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


