package com.example.myapplication;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PestAdapter extends RecyclerView.Adapter<PestAdapter.PestViewHolder> {

    private List<PestItem> pestItemList;
    private onItemClickListener listener;

    public interface onItemClickListener {
        void onItemClick(PestItem pestItem);
    }


    public PestAdapter (List<PestItem> pestItemList, onItemClickListener onItemClickListener){
        this.pestItemList = pestItemList;
        this.listener = onItemClickListener;

    }

    @NonNull
    @Override
    public PestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hama_penyakit, parent, false);
        Log.d(TAG, "onCreateViewHolder called.");
        return new PestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PestAdapter.PestViewHolder holder, int position) {
        PestItem pestItem = pestItemList.get(position);
        holder.tvDiseaseName.setText(pestItem.getName());

        holder.bind(pestItem, listener);

        Log.d(TAG, "sukses on bindviewholder");
        Log.d(TAG, "Binding item at position: " + position + ", with name: " + pestItem.getName());



    }

    @Override
    public int getItemCount() {
        return pestItemList.size();
    }

    public static class PestViewHolder extends RecyclerView.ViewHolder{
        TextView tvDiseaseName;
        ImageView ivArrow;

        public PestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDiseaseName = itemView.findViewById(R.id.tvDiseaseName);
            ivArrow = itemView.findViewById(R.id.ivArrow);
            Log.d(TAG, "PestViewHolder created.");
        }

        public void bind(PestItem pestItem, onItemClickListener listener) {
            tvDiseaseName.setText(pestItem.getName());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("PestAdapter", "Item clicked: " + pestItem.getName());
                    if (listener != null) {
                        listener.onItemClick(pestItem);
                    } else {
                        Log.e("PestAdapter", "Listener is null!");
                    }


                }
            });

        }
    }


}

