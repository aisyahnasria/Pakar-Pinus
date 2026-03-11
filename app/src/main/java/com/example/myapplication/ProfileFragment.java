package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfileFragment extends Fragment {
    private TextView textViewName, textViewNpk, textViewJabatan, textViewEmail, textViewPw;
    ImageView profileImage;
    private ImageButton btnLogout, btnEdit;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Inisialisasi Firebase Auth dan Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Inisialisasi view
        initializeViews(view);

        loadUserProfile();

        btnLogout.setOnClickListener(v -> logout());
        btnEdit.setOnClickListener(v -> startActivity(new Intent(getActivity(),EditProfileActivity.class)));

        return view;
    }

    private void initializeViews(View view) {
        textViewName = view.findViewById(R.id.textView10);
        textViewNpk = view.findViewById(R.id.textView9);
        textViewJabatan = view.findViewById(R.id.textView12);
        textViewEmail = view.findViewById(R.id.textView14);
        textViewPw = view.findViewById(R.id.textView16);
        btnLogout = view.findViewById(R.id.btn_logout);
        btnEdit = view.findViewById(R.id.btn_edit);
        profileImage = view.findViewById(R.id.imageView);
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String nama = document.getString("nama");
                            String npk = document.getString("npk");
                            String jabatan = document.getString("jabatan");
                            String email = document.getString("email");
                            String pw = document.getString("password");
                            String profileImageUrl = document.getString("profileImage");

                            Log.d("ProfileFragment", "Profile Image URL: " + profileImageUrl);

                            updateUserProfile(nama, npk, jabatan, email, pw, profileImageUrl);
                        } else {
                            showToast("Data pengguna tidak ditemukan");
                        }
                    })
                    .addOnFailureListener(e -> showToast("Gagal mengambil data pengguna"));
        }
    }

    private void updateUserProfile(String nama, String npk, String jabatan, String email, String pw, String profileImageUrl) {
        textViewName.setText(nama);
        textViewNpk.setText(npk);
        textViewJabatan.setText(jabatan);
        textViewEmail.setText(email);
        textViewPw.setText(getMaskedPassword(pw));

        // Menampilkan profileImage menggunakan Glide
        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_profile)
                    .into(profileImage);

        } else {
            profileImage.setImageResource(R.drawable.ic_profile); // Gambar default
        }



        setVisibility(View.VISIBLE, textViewName, textViewNpk, textViewJabatan, textViewEmail, textViewPw, textViewPw, profileImage);
    }

    private void setVisibility(int visibility, View... views) {
        for (View view : views) {
            view.setVisibility(visibility);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(getActivity(), MainActivity.class));
    }

    private String getMaskedPassword(String password) {
        return new String(new char[password.length()]).replace("\0", "*");
    }
}
