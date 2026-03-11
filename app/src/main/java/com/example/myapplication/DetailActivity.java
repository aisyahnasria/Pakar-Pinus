package com.example.myapplication;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {
    private ImageView ivGambar;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_penyakit);

        String namaPenyakit = getIntent().getStringExtra("nama");
        Log.d("DetailActivity", "Nama penyakit dari Intent: " + namaPenyakit);


        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowTitleEnabled(true);
//        }

        TextView tvnama = findViewById(R.id.nama_penyakit);
        TextView tvgejala = findViewById(R.id.tv_gejala);
        TextView tvpenanganan = findViewById(R.id.tv_penanganan);
        ImageView pestImage = findViewById(R.id.img_pest);



        db = FirebaseFirestore.getInstance();

        db.collection("HamaPenyakit")
                .whereEqualTo("nama", namaPenyakit)
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) {
                        Log.d("DetailActivity", "Gagal mengambil data Firestore: ", e);
                        return;
                    }

                    if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                        // Dapatkan data dari dokumen
                        String jenis = documentSnapshot.getString("jenis");
                        String nama = documentSnapshot.getString("nama");
                        String penanganan = documentSnapshot.getString("penanganan");
                        String gambarurl = documentSnapshot.getString("gambar");

                        if (jenis != null) {
                            getSupportActionBar().setTitle(jenis);
                        }

                        if (nama != null) {
                            tvnama.setText(nama);
                        }

                        if (penanganan != null) {
                            tvpenanganan.setText(penanganan);
                        }

                        if (gambarurl != null) {
                            Glide.with(DetailActivity.this)
                                    .load(gambarurl)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .placeholder(R.drawable.img_pest)
                                    .error(R.drawable.img_pest)
                                    .timeout(1000)
                                    .into(pestImage);
                        }


                        SpannableStringBuilder gejalaBuilder = new SpannableStringBuilder();
                        List<Map<String, Object>> gejalalist = (List<Map<String, Object>>) documentSnapshot.get("gejala");
                        if (gejalalist != null) {
                            for (Map<String, Object> gejala : gejalalist) {
                                String namaGejala = (String) gejala.get("nama_gejala");
                                if (namaGejala != null) {
                                    gejalaBuilder.append(namaGejala).append("\n");
                                    gejalaBuilder.setSpan(new BulletSpan(20), gejalaBuilder.length() - namaGejala.length() - 1, gejalaBuilder.length(), 0);
                                }
                            }
                            tvgejala.setText(gejalaBuilder);
                        }

                    } else {
                        Log.d("DetailActivity", "Dokumen tidak ditemukan");
                        Toast.makeText(DetailActivity.this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                    }

                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                })
        ;









    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
