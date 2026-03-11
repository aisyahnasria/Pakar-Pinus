package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {
    private TextInputEditText editNama, editNpk, editJabatan, editEmail, editPw;
    private  CircleImageView imageView;
    private Button buttonSimpan, btnEditPw, btnEditEmail;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private StorageReference storageRef;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);

        // Inisialisasi toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Inisialisasi View dan Firebase
        editNama = findViewById(R.id.edit_nama);
        editNpk = findViewById(R.id.edit_npk);
        editJabatan = findViewById(R.id.edit_jabatan);
        editEmail = findViewById(R.id.edit_email);
        editPw = findViewById(R.id.pw);
        buttonSimpan = findViewById(R.id.btn_simpan);
        btnEditPw = findViewById(R.id.btn_editPw);
        btnEditEmail = findViewById(R.id.btn_editEmail);
        ImageButton imageButton = findViewById(R.id.edit_pic);
        imageView = findViewById(R.id.profileImageView);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference("profile_images");

        // Setup peluncuran pengambilan gambar
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        imageView.setImageURI(imageUri);
                    }
                }
        );

        loadUserProfile();

        imageButton.setOnClickListener(v -> openFileChooser());

        buttonSimpan.setOnClickListener(view -> {
            if (imageUri != null) {
                uploadImageAndSaveProfile();
            } else {
                saveUserProfile(null);
            }
        });

        btnEditPw.setOnClickListener(v ->
                startActivity(new Intent(this, EditPasswordActivity.class)));

        btnEditEmail.setOnClickListener(v -> startActivity(new Intent(this, EditEmailActivity.class)));
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        pickImageLauncher.launch(intent);
    }

    private void uploadImageAndSaveProfile() {
        if (currentUser == null) {
            Toast.makeText(this, "User belum login.", Toast.LENGTH_SHORT).show();
            return;
        }

        StorageReference fileReference = storageRef.child(currentUser.getUid() + ".jpg");
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl()
                        .addOnSuccessListener(uri -> saveUserProfile(uri.toString())))
                .addOnFailureListener(e -> {
                    Log.e("EditProfileActivity", "Gagal upload gambar: ", e);
                    Toast.makeText(this, "Gagal menyimpan foto profil.", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadUserProfile() {
        if (currentUser == null) {
            Toast.makeText(this, "User belum login.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        editNama.setText(documentSnapshot.getString("nama"));
                        editNpk.setText(documentSnapshot.getString("npk"));
                        editJabatan.setText(documentSnapshot.getString("jabatan"));
                        editEmail.setText(documentSnapshot.getString("email"));
                        editPw.setText(documentSnapshot.getString("password"));


                        String profileImageUrl = documentSnapshot.getString("profileImage");


                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(this)
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.ic_person)
                                    .error(R.drawable.ic_profile)
                                    .into(imageView);

                        } else {
                            imageView.setImageResource(R.drawable.ic_person); // Gambar default
                        }

                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("EditProfileActivity", "Gagal memuat profil: ", e);
                    Toast.makeText(this, "Gagal memuat data profil.", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserProfile(String imageUrl) {
        String nama = editNama.getText().toString();
        String npk = editNpk.getText().toString();
        String jabatan = editJabatan.getText().toString();
        String email = editEmail.getText().toString();
        String pw = editPw.getText().toString();

        if (currentUser == null) {
            Toast.makeText(this, "User belum login.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ambil data user yang ada, termasuk URL gambar
        db.collection("users").document(currentUser.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Map<String, Object> userProfile = new HashMap<>();
                    userProfile.put("nama", nama);
                    userProfile.put("npk", npk);
                    userProfile.put("jabatan", jabatan);
                    userProfile.put("email", email);
                    userProfile.put("Uid", currentUser.getUid());
                    userProfile.put("password", pw);

                    // Gunakan URL gambar lama jika imageUrl yang baru tidak ada
                    if (imageUrl != null) {
                        userProfile.put("profileImage", imageUrl);
                    } else if (documentSnapshot.contains("profileImage")) {
                        userProfile.put("profileImage", documentSnapshot.getString("profileImage"));
                    }

                    // Update data user di Firestore
                    db.collection("users").document(currentUser.getUid()).set(userProfile)
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(this, "Profil berhasil disimpan.", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> {
                                Log.e("EditProfileActivity", "Gagal menyimpan profil di Firestore: ", e);
                                Toast.makeText(this, "Gagal menyimpan data profil.", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e("EditProfileActivity", "Gagal memuat profil: ", e);
                    Toast.makeText(this, "Gagal memuat data profil.", Toast.LENGTH_SHORT).show();
                });
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getOnBackPressedDispatcher().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
