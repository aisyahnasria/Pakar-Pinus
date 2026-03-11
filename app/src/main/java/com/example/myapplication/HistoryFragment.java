package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<HistoryItem> historyList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new HistoryAdapter(getContext(), historyList);
        recyclerView.setAdapter(adapter);

        loadHistoryFromFirestore();

        return view;
    }

    private void loadHistoryFromFirestore() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null?
                FirebaseAuth.getInstance().getCurrentUser().getUid():null;

        if (currentUserId == null) {
            Log.e("HistoryFragment","User is not logged in");
            return;
        }

        historyList.clear();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("History")
                .whereEqualTo("Uid", currentUserId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String name = doc.getString("nama");
                        String gambar = doc.getString("gambar");
                        String penanganan = doc.getString("penanganan");
                        double persentase = doc.getDouble("persentase");
                        String subtext = "Hasil diagnosa: " +  String.format(Locale.getDefault(), "%.0f", persentase) + " %";
                        Timestamp ddate = doc.getTimestamp("timestamp");
                        List<Map<String, String>> diagnosisList = (List<Map<String, String>>) doc.get("diagnosa");

                        // Membuat string untuk menampilkan gejala dan keyakinan
                        StringBuilder gejalaList = new StringBuilder();
                        if (diagnosisList != null) {
                            for (Map<String, String> diagnosis : diagnosisList) {
                                String gejala = diagnosis.get("gejala");
                                String keyakinan = diagnosis.get("keyakinan");
                                gejalaList.append(gejala).append(" (").append(keyakinan).append("), ");
                            }
                            // Menghapus koma terakhir
                            if (gejalaList.length() > 0) {
                                gejalaList.setLength(gejalaList.length() - 2);
                            }
                        }



                        if (ddate != null) {
                            Date date = ddate.toDate();
                            historyList.add(new HistoryItem(name, subtext, date, diagnosisList, gambar, penanganan));
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("HistoryFragment", "Error loading history", e));
    }



}
