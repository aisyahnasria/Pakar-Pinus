package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DetailHistoryActivity extends AppCompatActivity {
    private TextView historyNama,historyPenanganan, historyGejala;
    private ImageView historyGambar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_history);

        // Initialize views
        historyNama = findViewById(R.id.history_nama);
        historyPenanganan = findViewById(R.id.history_penanganan);
        historyGejala = findViewById(R.id.history_gejala);
        historyGambar = findViewById(R.id.history_gambar);


        androidx.appcompat.widget.Toolbar historyToolbar = findViewById(R.id.history_tlbr);
        setSupportActionBar(historyToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        // Get data from the intent
        Intent intent = getIntent();
        String namaPenyakit = intent.getStringExtra("nama");
        String persentaseKeyakinan = intent.getStringExtra("subtext");
        String penanganan = intent.getStringExtra("penanganan");
        String gambarUrl = intent.getStringExtra("gambar_url");
        //ArrayList<String> gejalaList = intent.getStringArrayListExtra("gejala");
        // Terima diagnosisList sebagai Serializable
        List<Map<String, String>> diagnosisList = (List<Map<String, String>>) intent.getSerializableExtra("diagnosis_list");


        // Set data to views
        historyNama.setText(namaPenyakit + " " + persentaseKeyakinan);
        historyPenanganan.setText(penanganan);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (diagnosisList != null) {
            for (Map<String, String> diagnosis : diagnosisList) {
                String gejala = diagnosis.get("gejala");
                String keyakinan = diagnosis.get("keyakinan");
                if (gejala != null && keyakinan != null) {
                    // Buat Spannable untuk gejala
                    SpannableString gejalaSpannable = new SpannableString(gejala);
                    gejalaSpannable.setSpan(new BulletSpan(10), 0, gejalaSpannable.length(), 0);
                    String keyakinanText = " (" + keyakinan + ")";
                    SpannableString keyakinanSpannable = new SpannableString(keyakinanText);
                    spannableStringBuilder.append(gejalaSpannable);
                    spannableStringBuilder.append(keyakinanSpannable);
                    spannableStringBuilder.append("\n");
                }
            }
        }
        historyGejala.setText(spannableStringBuilder);

        // Use Glide to load the image
        Glide.with(this)
                .load(gambarUrl)
                .into(historyGambar);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
