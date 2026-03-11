package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PestActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PestAdapter pestAdapter;
    private List<PestItem> pestItemList;
    private FirebaseFirestore db;


    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peyakit);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.pest_tlbr);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.pest_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        db.collection("HamaPenyakit")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("FirestoreError", "Listen failed.", e);
                            return;
                        }
                        if (value != null && !value.isEmpty()) {
                            pestItemList.clear();
                            for (QueryDocumentSnapshot doc : value) {
                                String namaPenyakit = doc.getString("nama");
                                PestItem pestItem = new PestItem(namaPenyakit);
                                pestItemList.add(pestItem);
                            }
                            pestAdapter.notifyDataSetChanged();
                        } else {
                            Log.d("FirestoreData", "Tidak ada data dalam koleksi HamaPenyakit");
                        }
                    }
                });

        // Inisialisasi daftar penyakit
        pestItemList = new ArrayList<>();

        pestAdapter = new PestAdapter(pestItemList, pestItem -> {
            Intent intent = new Intent(PestActivity.this, DetailActivity.class);

            intent.putExtra("nama", pestItem.getName());
            startActivity(intent);
            Log.d(TAG, "onItemClickListener: gagal klik pindah halaman");



        });
        recyclerView.setAdapter(pestAdapter);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
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
