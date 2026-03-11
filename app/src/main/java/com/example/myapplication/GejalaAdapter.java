package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GejalaAdapter extends RecyclerView.Adapter<GejalaAdapter.ViewHolder> {

    private final List<Gejala> gejalaList;

    public GejalaAdapter(List<Gejala> gejalaList) {
        this.gejalaList = gejalaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diagnosa_gejala, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Gejala gejala = gejalaList.get(position);
        holder.checkBox.setText(gejala.getNamaGejala());
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(gejala.isSelected());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> gejala.setSelected(isChecked));
    }

    @Override
    public int getItemCount() {
        return gejalaList.size();
    }

    public List<Gejala> getSelectedGejala() {
        List<Gejala> selectedGejala = new ArrayList<>();
        for (Gejala gejala : gejalaList) {
            if (gejala.isSelected()) {
                selectedGejala.add(gejala);
            }
        }
        return selectedGejala;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}
