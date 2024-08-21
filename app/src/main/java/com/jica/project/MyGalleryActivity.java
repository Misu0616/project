package com.jica.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

public class MyGalleryActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerViewList, recyclerViewPic;
    private ImageAdapter imageAdapter;
    private ImagePicAdapter imagePicAdapter;
    private List<ImageModel> imageList;
    private List<ImagePicModel> imageListPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gallery);

        // 프래그먼트 추가
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.galleryUnderbar, new underBar())
                .commit();

        // RecyclerView 초기화
        initRecyclerViews();

        // Firebase 사용자 인증 초기화
        firebaseAuth = FirebaseAuth.getInstance();
        loadImageData();
        loadImageUrlsFromFirestore();
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
        imagePicAdapter = new ImagePicAdapter(imageListPic);
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

    private void loadImageUrlsFromFirestore() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(FirebaseAuth.getInstance().getUid()).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> imageUrls = new ArrayList<>();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                String downloadUrl = document.getString("https://firebasestorage.googleapis.com/v0/b/project-7a683.appspot.com/o/gjniSyDcE9XyV5GcMTYJKHBdmQI3%2Fphoto_20240821_085529.jpg?alt=media&token=5cc5aa15-f703-4b40-9451-cac9afff6487");
                                if (downloadUrl != null) {
                                    imageUrls.add(downloadUrl);
                                }
                            }
                            displayImages(imageUrls);
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    private void displayImages(List<String> imageUrls) {
        if (imageUrls != null && !imageUrls.isEmpty()) {
            // 예제로 첫 번째 이미지만 표시
            Picasso.get().load(imageUrls.get(0)).into((Target) recyclerViewPic);
        }
    }
}
