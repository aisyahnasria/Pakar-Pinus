package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
    private List<HistoryItem> historyList;
    private Context context;



    public HistoryAdapter(Context context, List<HistoryItem> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history_diagnosa, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        HistoryItem item = historyList.get(position);
        holder.name.setText(item.getName());
        holder.subtext.setText(item.getSubtext());

        // Format tanggal sebelum menampilkan
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy \n HH:mm", Locale.getDefault());
        String dateString = sdf.format(item.getTimestamp());
        holder.date.setText(dateString);



        // Set OnClickListener untuk berpindah ke halaman hasil diagnosa
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailHistoryActivity.class);
            intent.putExtra("nama", item.getName());
            intent.putExtra("subtext", item.getSubtext().split(":")[1].trim());
            intent.putExtra("penanganan", item.getPenanganan());
            intent.putExtra("tanggal", dateString);
            intent.putExtra("gambar_url", item.getGambar());

            // Kirim data gejala dan keyakinan yang sudah digabungkan
            intent.putExtra("diagnosis_list", (Serializable) item.getDiagnosisList());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView name, subtext, date;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            subtext = itemView.findViewById(R.id.subtext);
            date = itemView.findViewById(R.id.date);
        }
    }
}
