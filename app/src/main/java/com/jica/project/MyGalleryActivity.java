package com.jica.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MyGalleryActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private RecyclerView recyclerViewList;
    private ImageAdapter imageAdapter;
    private List<ImageModel> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gallery);

        int position = getIntent().getIntExtra(ActivityAdapter.ViewHolder.POSITION_KEY, -1);
        String fileName = getIntent().getStringExtra(RealCameraActivity.FILENUM_KEY);

        if (fileName != null && !fileName.isEmpty()) {
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

        // 데이터 로드
        loadData();
    }

    private void initRecyclerViews() {
        recyclerViewList = findViewById(R.id.doneList);
        int numberOfColumns = 2;

        recyclerViewList.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(this, imageList);
        recyclerViewList.setAdapter(imageAdapter);
    }

    private void loadData() {
        String memEmail = firebaseAuth.getCurrentUser().getEmail();
        String safeEmail = memEmail != null ? memEmail.replace(".", ",") : "";

        Map<String, ImageModel> imageMap = new HashMap<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("memberInfo").child(safeEmail).child("imageInfo");

        // Firebase Realtime Database에서 데이터 로드
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<ImageModel> imageList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ImageModel activity = snapshot.getValue(ImageModel.class);
                    ImageModel imageModel = snapshot.getValue(ImageModel.class);
                    if (activity != null && activity.getImgURL() != null) {
                        imageMap.put(activity.getImgURL(), activity);
                    } else {
                        Log.e("noAnswer", "activity is null or imgURL is null");
                    }
                    if (imageModel != null) {
                        imageList.add(imageModel);
                    } else {
                        Log.e("MyGalleryActivity", "imageModel is null for snapshot: " + snapshot.getKey());
                    }
                }
                // Firestore에서 데이터 로드
                loadImageUrlsFromFirestore(imageMap);
                imageAdapter.updateImageList(imageList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });
    }

    private void loadImageUrlsFromFirestore(Map<String, ImageModel> imageMap) {
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Log.e("Firestore", "User ID is null");
            return;
        }
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        firestore.collection(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("downloadUrl", "task.isSuccessful() ");
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            Log.d("downloadUrl", "querySnapshot");
                            int totalDocuments = querySnapshot.size();
                            if (totalDocuments == 0) {
                                Log.d("downloadUrl", "No documents found.");
                                return;
                            }
                            List<Task<Uri>> downloadTasks = new ArrayList<>();
                            for (QueryDocumentSnapshot document : querySnapshot) {
                                Log.d("downloadUrl", "QueryDocumentSnapshot");
                                String imageFileName = document.getString("fileName");
                                Timestamp timestamp = document.getTimestamp("timestamp");
                                String dateString = timestamp.toDate().toString();
                                Log.d("downloadUrl", "imageFileName -> " + imageFileName);
                                if (imageFileName != null) {
                                    StorageReference imageRef = storageRef.child(userId).child(imageFileName);
                                    Log.d("downloadUrl", "imageRef -> " + imageRef);
                                    Log.d("downloadUrl", "Path to check: " + imageRef.getPath());
                                    downloadTasks.add(imageRef.getDownloadUrl()
                                            .addOnSuccessListener(uri -> {
                                                String downloadUrl = uri.toString();
                                                Log.d("Firestore", "downloadUrl -> " + downloadUrl);
                                                ImageModel image = imageMap.get(downloadUrl);
                                                if (image == null) {
                                                    image = new ImageModel();
                                                }
                                                image.setImgURL(downloadUrl);
                                                image.setTitle(image.getTitle());
                                                image.setDate(dateString);
                                                //imageMap.put(downloadUrl, image);

                                                Log.d("Firestore", "Image list updated333: " + image.getImgURL());
                                                Log.d("Firestore", "Image list updated111: " + imageList);
                                            })
                                            .addOnFailureListener(exception -> {
                                                Log.w("FirebaseStorage", "Error getting download URL.", exception);
                                            }));
                                } else {
                                    Log.w("Firestore", "Image file name is null for document: " + document.getId());
                                }
                            }

                            // When all download URL tasks are completed
                            Tasks.whenAllComplete(downloadTasks).addOnCompleteListener(completedTask -> {
                                List<String> imageUrls = imageList.stream()
                                        .map(ImageModel::getImgURL)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toList());

                                // Log or use the extracted image URLs
                                Log.d("Firestore", "Image list updated2: " + imageList);

                                imageAdapter.updateImageList(imageList);

                            });

                        } else {
                            Log.w("Firestore", "QuerySnapshot is null.");
                        }
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });

    }
}
