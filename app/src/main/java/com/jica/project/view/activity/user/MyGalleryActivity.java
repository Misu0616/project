package com.jica.project.view.activity.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.jica.project.R;
import com.jica.project.model.ImageModel;
import com.jica.project.view.adapter.ImageAdapter;
import com.jica.project.view.fragment.underBar;

import java.util.ArrayList;
import java.util.List;

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

        imageList = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageList);
        recyclerViewList.setAdapter(imageAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerViewList.setLayoutManager(gridLayoutManager);
    }

    private void loadData() {
        // Firebase 사용자 인증 초기화
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();

        String userId = FirebaseAuth.getInstance().getUid();

        if (userId == null) {
            return;
        }

        firestore.collection(userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        imageList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // 문서에서 텍스트와 이미지 URL을 추출
                            String date = document.getString("date");
                            String title = document.getString("title");
                            Boolean admin_check = document.getBoolean("admin_check");
                            String downloadurl = document.getString("downloadUrl");

                            imageList.add(new ImageModel(title, date, admin_check, downloadurl));
                        }
                        // Collections.reverse(imageList); // 데이터 역순 저장(근데 이거 필요한가...?)
                        imageAdapter.updateData(imageList);
                    } else {
                        Log.e("FirestoreError", "Error getting documents: ", task.getException());
                    }
                });
    }
}