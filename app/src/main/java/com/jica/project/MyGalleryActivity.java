package com.jica.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
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

public class MyGalleryActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firestore;
    private RecyclerView recyclerViewList;
    private ImageAdapter imageAdapter;
    private List<ImageModel> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gallery);

        // 하단 네비게이션 바
        Fragment underBar1 = new underBar();
        getSupportFragmentManager().beginTransaction().replace(R.id.galleryUnderbar, underBar1).commit();

        // RecyclerView 초기화
        initRecyclerViews();


        // 데이터 로드
       loadData();
    }

    private void initRecyclerViews() {
        recyclerViewList = findViewById(R.id.doneList);
        int numberOfColumns = 2;

        recyclerViewList.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageList);
        recyclerViewList.setAdapter(imageAdapter);
    }

    private void loadData() {
        // Firebase 사용자 인증 초기화
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        String userId = FirebaseAuth.getInstance().getUid();
        Log.e("noAnswer", "userId     ---- > " + userId);
        if (userId == null) {
            Log.e("Firestore", "User ID is null");
            return;
        }

        firestore.collection(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("TAG", task.toString());
                        imageList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // 문서에서 텍스트와 이미지 URL을 추출
                            String date = document.getString("date");
                            String title = document.getString("title");
                            Boolean admin_check = document.getBoolean("admin_check");
                            String downloadurl = document.getString("downloadUrl");
                            Log.e("answer", "date : " +  date);
                            Log.e("answer", "title : " +  title);
                            Log.e("answer", "admin_check : " +  admin_check);
                            Log.e("answer", "downloadurl : " +  downloadurl);
                            Log.e("answer", "imageList : " +  imageList.toString());

                            imageList.add(new ImageModel(title, date, admin_check, downloadurl));
                            Log.e("answer", "imageList : " +  imageList.toString());
                        }
                        // 어댑터에 데이터 변경 사항을 알립니다.
                        imageAdapter.updateData(imageList);

                        Log.e("answer", "Error getting documents: " +  imageList.toString());

                    } else {
                        Log.e("FirestoreError", "Error getting documents: ", task.getException());
                    }
                });
    }
}