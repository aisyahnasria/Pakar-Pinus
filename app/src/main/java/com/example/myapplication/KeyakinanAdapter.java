package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class KeyakinanAdapter extends RecyclerView.Adapter<KeyakinanAdapter.ViewHolder> {
    private final List<Gejala> gejalaList;
    private final String[] keyakinanList;

    public KeyakinanAdapter(List<Gejala> gejalaList, String[] keyakinanList) {
        this.gejalaList = gejalaList;
        this.keyakinanList = keyakinanList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_keyakinan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Gejala gejala = gejalaList.get(position);
        holder.namaGejala.setText(gejala.getNamaGejala());

        // Setup Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.itemView.getContext(),
                android.R.layout.simple_spinner_item, keyakinanList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerKeyakinan.setAdapter(adapter);

        // Set listener untuk menyimpan pilihan keyakinan
        holder.spinnerKeyakinan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gejala.setKeyakinan(keyakinanList[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gejala.setKeyakinan(null);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gejalaList.size();
    }

    public List<Gejala> getGejalaWithKeyakinan() {
        return gejalaList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView namaGejala;
        Spinner spinnerKeyakinan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            namaGejala = itemView.findViewById(R.id.tv_Gejala);
            spinnerKeyakinan = itemView.findViewById(R.id.spinner_keyakinan);
        }
    }
}
