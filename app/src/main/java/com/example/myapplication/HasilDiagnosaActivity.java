package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//noinspection UselessParent
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HasilDiagnosaActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private List<Rule> rules;
    private List<Gejala> gejalaList;
    private ArrayList<String> keyakinanNames, selectedGejala;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasil_diagnosa);


        gejalaList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // Menerima data gejala terpilih dari intent
        Intent intent = getIntent();
        selectedGejala = intent.getStringArrayListExtra("selectedGejala");
        keyakinanNames = intent.getStringArrayListExtra("keyakinanNames");

        if (selectedGejala != null) {
            for (String namaGejala : selectedGejala) {
                Gejala gejala = new Gejala(namaGejala);
                gejala.setSelected(true);
                gejalaList.add(gejala);
            }
        }

        if (selectedGejala != null && keyakinanNames != null) {
            for (int i = 0; i < selectedGejala.size(); i++) {
                Log.d("HasilDiagnosa", "Gejala: " + selectedGejala.get(i) + ", Keyakinan: " + keyakinanNames.get(i));
            }

            // Lakukan konversi keyakinanNames menjadi CF User dan tetapkan pada gejalaList
            for (int i = 0; i < gejalaList.size(); i++) {
                Gejala gejala = gejalaList.get(i);
                double cfUser = convertKeyakinanToDouble(keyakinanNames.get(i)); // Pastikan fungsi konversi tersedia
                gejala.setCfUser(cfUser); // Menambahkan nilai CF User ke setiap gejala
            }


        } else {
            Toast.makeText(this, "Data tidak tersedia!", Toast.LENGTH_SHORT).show();
        }

        // Mengambil aturan dari Firestore dan memulai diagnosa setelah data diambil
        loadRulesFromFirestore();

        //Mengulang Diagnosa
        Button ulangiBtn = findViewById(R.id.btn_ulangi);
        ulangiBtn.setOnClickListener(v -> {
            Intent ulangiIntent = new Intent(this, DiagnosaActivity.class);
            startActivity(ulangiIntent);
            //finish();
        });

        Toolbar toolbar = findViewById(R.id.hasil_diagnosa_tlbr);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbar.setNavigationOnClickListener(v -> {
            Intent back = new Intent(HasilDiagnosaActivity.this, DashboardActivity.class);
            back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(back);
            finish();
        });


    }



    private void loadRulesFromFirestore() {
        rules = new ArrayList<>();
        CollectionReference rulesRef = db.collection("HamaPenyakit");

        rulesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    String conclusion = document.getString("nama");
                    List<Map<String, Object>> premisesArray = (List<Map<String, Object>>) document.get("gejala");
                    String penanganan = document.getString("penanganan");
                    String gambarUrl = document.getString("gambar");
                    List<Gejala> premises = new ArrayList<>();

                    if (premisesArray != null) {
                        for (Map<String, Object> gejalaMap : premisesArray) {
                            String namaGejala = (String) gejalaMap.get("nama_gejala");
                            Double bobot = 0.0;


                            Object bobotObj = gejalaMap.get("bobot");
                            if (bobotObj instanceof Double) {
                                bobot = (Double) bobotObj;
                            } else if (bobotObj instanceof Long) {
                                bobot = ((Long) bobotObj).doubleValue();
                            }

                            premises.add(new Gejala(namaGejala, bobot));
                            //Log.d("Diagnosa", "Gejala ditambahkan: Nama = " + namaGejala + ", Bobot = " + bobot);


                        }



                    }

                    Rule rule = new Rule(premises, conclusion, penanganan, gambarUrl);
                    rules.add(rule);
                }



                Map<String, Double> hasilDiagnosa = diagnoseWithCertaintyFactor();
                displayDiagnosa(hasilDiagnosa);

            } else {
                Log.d("HasilDiagnosaActivity", "Error getting documents: ", task.getException());
            }
        });
    }



    private Map<String, Double> diagnoseWithCertaintyFactor() {
        Map<String, Double> conclusionsWithCF = new HashMap<>();

        // Variabel untuk menyimpan diagnosa dengan CF tertinggi
        String highestCFConclusion = null;
        double highestCFValue = -1.0;  // Nilai negatif karena CF bisa negatif
        int maxMatchedSymptoms = 0; // Jumlah gejala cocok terbanyak

        for (Rule rule : rules) {
            double cf = 0.0;
            int matchedSymptoms = 0;
            boolean anyGejalaMatched = false;

            for (Gejala ruleGejala : rule.getPremises()) {
                for (Gejala selectedGejala : gejalaList) {
                    if (ruleGejala.getNamaGejala().equals(selectedGejala.getNamaGejala()) && selectedGejala.isSelected()) {
                        anyGejalaMatched = true;
                        double cfUser = selectedGejala.getCfUser();
                        double cfPakar = ruleGejala.getBobot();
                        double cfBaru = cfUser * cfPakar;

                        cf = (cf >= 0 && cfBaru >= 0) ? cf + cfBaru * (1 - cf) :
                                (cf < 0 && cfBaru < 0) ? cf + cfBaru * (1 + cf) :
                                        (cf + cfBaru) / (1 - Math.min(Math.abs(cf), Math.abs(cfBaru)));

                        matchedSymptoms++;
                        break;
                    }
                }
            }

            if (anyGejalaMatched) {
                conclusionsWithCF.put(rule.getConclusion(), cf * 100);
                Log.d("Diagnosa", "Rule " + rule.getConclusion() + " terpenuhi dengan CF: " + cf * 100 + "%");


                if (cf > highestCFValue || (cf == highestCFValue && matchedSymptoms > maxMatchedSymptoms)) {
                    highestCFValue = cf;
                    highestCFConclusion = rule.getConclusion();
                    maxMatchedSymptoms = matchedSymptoms;
                }
                Log.d("Diagnosa", "Rule " + rule.getConclusion() + " terpenuhi dengan CF: " + cf * 100 + "% dan " +
                        matchedSymptoms + " gejala cocok.");
            } else {
                Log.d("Diagnosa", "Rule " + rule.getConclusion() + " tidak terpenuhi.");
            }
        }


        // Menampilkan hanya hasil dengan CF tertinggi
        Map<String, Double> finalDiagnosa = new HashMap<>();
        if (highestCFConclusion != null) {
            Log.d("Diagnosa", "Update Diagnosa: Sebelumnya = " + highestCFConclusion + ", CF Sebelumnya = " + highestCFValue);
            finalDiagnosa.put(highestCFConclusion, highestCFValue * 100);
            Log.d("Diagnosa", "Diagnosa Baru: " + highestCFConclusion + ", CF Baru = " + highestCFValue);
            Log.d("Diagnosa", "Hasil Diagnosa Akhir: " + highestCFConclusion + ", CF Akhir = " + highestCFValue);

            Log.d("Diagnosa", "Diagnosa akhir: " + highestCFConclusion + " dengan CF tertinggi: " + highestCFValue * 100 +
                    "% dan gejala cocok terbanyak: " + maxMatchedSymptoms);
        }

        return finalDiagnosa;
    }




    private void displayDiagnosa(Map<String, Double> hasilDiagnosa) {
        TextView diagnosaNamaTextView = findViewById(R.id.diagnosa_nama);
        TextView diagnosaPersentasiTextView = findViewById(R.id.diagnosa_persentasi);
        TextView diagnosaPenangananTextView = findViewById(R.id.diagnosa_penanganan);
        ImageView diagnosaGambarImageView = findViewById(R.id.diagnosa_gambar);

        if (!hasilDiagnosa.isEmpty()) {
            // Mendapatkan diagnosa dengan nilai CF tertinggi
            Map.Entry<String, Double> highestDiagnosis = null;
            for (Map.Entry<String, Double> entry : hasilDiagnosa.entrySet()) {
                if (highestDiagnosis == null || entry.getValue() > highestDiagnosis.getValue()) {
                    highestDiagnosis = entry;
                }
            }

            if (highestDiagnosis != null) {
                String namaDiagnosa = highestDiagnosis.getKey();
                String persentaseDiagnosa = String.format("%.0f", highestDiagnosis.getValue()) + "%";



                diagnosaNamaTextView.setText(namaDiagnosa);
                diagnosaPersentasiTextView.setText(persentaseDiagnosa);

                double persentaseValue = highestDiagnosis.getValue();

                // Cari rule yang sesuai dengan nama diagnosa untuk mendapatkan penanganan dan gambar
                Rule matchedRule = null;
                for (Rule rule : rules) {
                    if (rule.getConclusion().equals(namaDiagnosa)) {
                        matchedRule = rule;
                        break;
                    }
                }

                if (matchedRule != null) {
                    // Menampilkan penanganan
                    if (matchedRule.getPenanganan() != null) {
                        diagnosaPenangananTextView.setText(matchedRule.getPenanganan());
                    } else {
                        diagnosaPenangananTextView.setText("Tidak ada penanganan yang tersedia");
                    }

                    // Menampilkan gambar menggunakan Glide
                    if (matchedRule.getGambarUrl() != null && !matchedRule.getGambarUrl().isEmpty()) {
                        Glide.with(this)
                                .load(matchedRule.getGambarUrl())
                                .error(R.drawable.ic_iamge) // Gambar default jika load gagal
                                .into(diagnosaGambarImageView);
                    } else {
                        diagnosaGambarImageView.setImageResource(R.drawable.ic_iamge); // Gambar default jika URL kosong
                    }
                }

                saveDiagnosisToHistory(namaDiagnosa, persentaseValue, matchedRule.getPenanganan(), matchedRule.getGambarUrl());
            }
        } else {
            // Jika hasilDiagnosa kosong, tampilkan pesan default
            diagnosaNamaTextView.setText("Tidak ada diagnosa yang sesuai");
            diagnosaPersentasiTextView.setText("0%");
            diagnosaPenangananTextView.setText("Tidak ada penanganan yang tersedia");
            diagnosaGambarImageView.setImageResource(R.drawable.ic_iamge); // Gambar default
        }
    }

    private double convertKeyakinanToDouble(String keyakinanName) {
        switch (keyakinanName.toLowerCase()) {
            case "tidak yakin":
                return -1.0;
            case "hampir tidak yakin":
                return -0.8;
            case "kemungkinan besar tidak yakin":
                return -0.6;
            case "mungkin tidak yakin":
                return -0.4;
            case "tidak tahu":
                return 0.0;
            case "mungkin":
                return 0.4;
            case "kemungkinan besar yakin":
                return 0.6;
            case "hampir yakin":
                return 0.8;
            case "yakin":
                return 1.0;
            default:
                throw new IllegalArgumentException("Nilai keyakinan tidak valid: " + keyakinanName);
        }
    }


    private void saveDiagnosisToHistory(String diagnosisName, double diagnosisPercentage, String treatment, String imageUrl) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Membuat list untuk menyimpan map pasangan gejala dan keyakinan
        List<Map<String, String>> diagnosisList = new ArrayList<>();

        // Iterasi untuk setiap gejala dan keyakinannya
        for (int i = 0; i < selectedGejala.size(); i++) {
            // Membuat map untuk setiap gejala dan keyakinan
            Map<String, String> diagnosisMap = new HashMap<>();

            // Pastikan keyakinanNames memiliki ukuran yang cukup
            String keyakinan = (i < keyakinanNames.size()) ? keyakinanNames.get(i) : "Tidak Diketahui";

            // Memasukkan gejala dan keyakinan ke dalam map
            diagnosisMap.put("gejala", selectedGejala.get(i));
            diagnosisMap.put("keyakinan", keyakinan);

            // Menambahkan map ke list diagnosisList
            diagnosisList.add(diagnosisMap);
        }

        // Membuat map untuk data diagnosa
        Map<String, Object> diagnosisData = new HashMap<>();

        diagnosisData.put("nama", diagnosisName);
        diagnosisData.put("persentase", diagnosisPercentage);
        diagnosisData.put("penanganan", treatment);
        diagnosisData.put("gambar", imageUrl);
        diagnosisData.put("timestamp",new Date()); // Menyimpan waktu diagnosa
        diagnosisData.put("Uid", currentUserId);
        diagnosisData.put("diagnosa", diagnosisList);






        // Menyimpan data ke koleksi 'history'
        db.collection("History").add(diagnosisData)
                .addOnSuccessListener(documentReference -> Log.d("HasilDiagnosaActivity", "Diagnosa berhasil disimpan dengan ID: " + documentReference.getId()))
                .addOnFailureListener(e -> Log.w("HasilDiagnosaActivity", "Gagal menyimpan diagnosa", e));
    }









}
