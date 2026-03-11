package com.example.myapplication;

import static android.content.ContentValues.TAG;

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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DiagnosaActivity extends AppCompatActivity {
    private GejalaAdapter adapter;
    private List<Gejala> gejalaList = new ArrayList<>();
    private Set<String> gejalaSet = new HashSet<>();
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konsultasi);

        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.diagnosa_tlbr);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Setup RecyclerView and Adapter
        RecyclerView recyclerView = findViewById(R.id.item);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GejalaAdapter(gejalaList);
        recyclerView.setAdapter(adapter);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Load gejala data from Firebase
        loadGejalaData();


        // Handle Diagnosa button click
        Button btnDiagnosa = findViewById(R.id.btn_diagnosa);
        btnDiagnosa.setOnClickListener(v -> {
            List<Gejala> selectedGejala = adapter.getSelectedGejala();
            if (selectedGejala.isEmpty()) {
                Toast.makeText(this, "Pilih setidaknya satu gejala untuk melanjutkan.", Toast.LENGTH_SHORT).show();
                return;
            }

            ArrayList<String> selectedGejalaNames = new ArrayList<>();


            for (Gejala gejala : selectedGejala) {
                selectedGejalaNames.add(gejala.getNamaGejala());
                Log.d(TAG, "onCreate: gejala yang dipilih" + selectedGejalaNames);

            }

            Intent intent = new Intent(DiagnosaActivity.this, KeyakinanActivity.class);
            intent.putStringArrayListExtra("selectedGejala", selectedGejalaNames);
            startActivity(intent);
        });
    }

    private void loadGejalaData() {
        CollectionReference hamaPenyakitRef = db.collection("HamaPenyakit");

        hamaPenyakitRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    List<Map<String, Object>> gejalaArray = (List<Map<String, Object>>) document.get("gejala");

                    if (gejalaArray != null) {
                        for (Map<String, Object> gejalaMap : gejalaArray) {
                            String namaGejala = (String) gejalaMap.get("nama_gejala");

                            if (gejalaSet.add(namaGejala)) {
                                gejalaList.add(new Gejala(namaGejala));
                            }
                        }
                    }
                }
                adapter.notifyDataSetChanged(); // Refresh RecyclerView dengan data baru
                Log.d("DiagnosaActivity", "Jumlah gejala unik yang dimuat: " + gejalaList.size());
            } else {
                Log.d("DiagnosaActivity", "Error getting documents: ", task.getException());
                Toast.makeText(this, "Gagal memuat data gejala", Toast.LENGTH_SHORT).show();
            }
        });
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
