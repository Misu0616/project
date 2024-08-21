package com.jica.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class MyGalleryActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private RecyclerView recyclerViewList, recyclerViewPic;
    private ImageAdapter imageAdapter;
    private ImagePicAdapter imagePicAdapter;
    private List<ImageModel> imageList;
    private List<ImagePicModel> imageListPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gallery);

        int position = getIntent().getIntExtra(ActivityAdapter.ViewHolder.POSITION_KEY, -1);
        String fileName = getIntent().getStringExtra(RealCameraActivity.FILENUM_KEY);

        if (fileName != "") {
            Toast.makeText(this, "myGallery 받은 filenum: " + fileName, Toast.LENGTH_SHORT).show();
            Log.d("noAnswer : ", "myGallery 받은 filenum: " + fileName);
            Log.d("noAnswer : ", "myGallery 받은 position: " + position);
        }

        // 프래그먼트 추가
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.galleryUnderbar, new underBar())
                .commit();

        // RecyclerView 초기화
        initRecyclerViews();

        // Firebase 사용자 인증 초기화
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        loadImageData();
        loadImageUrlsFromFirestore(fileName, position);
    }

    private void initRecyclerViews() {
        recyclerViewList = findViewById(R.id.doneList);
        recyclerViewPic = findViewById(R.id.doneImage);
        int numberOfColumns = 2;

        recyclerViewList.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerViewPic.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageList);
        recyclerViewList.setAdapter(imageAdapter);

        imageListPic = new ArrayList<>();
        imagePicAdapter = new ImagePicAdapter(this, imageListPic);
        recyclerViewPic.setAdapter(imagePicAdapter);
    }

    private void loadImageData() {
        String memEmail = firebaseAuth.getCurrentUser().getEmail();
        String safeEmail = memEmail.replace(".", ",");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("memberInfo").child(safeEmail).child("imageInfo");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imageList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ImageModel activity = snapshot.getValue(ImageModel.class);
                    if (activity != null) {
                        imageList.add(activity);
                    } else {
                        Log.e("noAnswer", "activity is null");
                    }
                }
                imageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }
    private void loadImageUrlsFromFirestore(String fileName, int position) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getUid();
        Log.d("noAnswer", "userID : " + userId);
        firestore.collection(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ImagePicModel> imageUrls = new ArrayList<>();

                        Log.d("noAnswer", "imageUrls : " + imageUrls);
                        Log.d("noAnswer", "imageUrls toString : " + imageUrls.toString());
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String downloadUrl = document.getString(fileName);
                                if (downloadUrl != null) {
                                    imageUrls.add(new ImagePicModel(downloadUrl));
                                    Log.d("noAnswer", "image adapter downloadUrl : " + downloadUrl);
                                }
                            }
                            imagePicAdapter.updateImageList(imageUrls);
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imagesRef = storageRef.child(userId).child(fileName);

        imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
            String downloadUrl = uri.toString();
            Log.d("FirebaseStorage", "imagesRef URL: " + imagesRef);
            Log.d("FirebaseStorage", "Download URL: " + downloadUrl);
        }).addOnFailureListener(exception -> {
            Log.w("FirebaseStorage", "Error getting download URL.", exception);
        });
    }

}
