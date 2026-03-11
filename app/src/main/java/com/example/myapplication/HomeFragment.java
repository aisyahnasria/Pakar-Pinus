package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        CardView cardPine = view.findViewById(R.id.card_pine);
        CardView cardPest = view.findViewById(R.id.card_pest);
        CardView cardDiagnosa = view.findViewById(R.id.card_diagnosa);
        CardView cardlahan = view.findViewById(R.id.card_lahan);

        cardPine.setOnClickListener(v -> startActivity(new Intent(getActivity(),PineActivity.class)));
        cardPest.setOnClickListener(v -> startActivity(new Intent(getActivity(),PestActivity.class)));
        cardDiagnosa.setOnClickListener(v -> startActivity(new Intent(getActivity(),DiagnosaActivity.class)));
        cardlahan.setOnClickListener(v -> startActivity(new Intent(getActivity(),LahanActivity.class)));



        return view;
    }


}