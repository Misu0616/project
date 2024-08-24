package com.jica.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class admin_AddList extends AppCompatActivity {

        private FirebaseAuth firebaseAuth;
        private FirebaseFirestore firebaseFirestore;
        private RecyclerView recyclerViewList;
        private AdminImageAdapter adminImageAdapter;
        private List<AdminImageModel> AdminImageList;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin_add_list);

            Toast.makeText(getApplicationContext(), "관리자 페이지", Toast.LENGTH_SHORT).show();

            // 하단 네비게이션 바
            Fragment underBar1 = new underBar();
            getSupportFragmentManager().beginTransaction().replace(R.id.galleryUnderbar, underBar1).commit();

            // RecyclerView 초기화
            initRecyclerViews();

            // 데이터 로드
            loadData();

            // FirebaseFirestore 인스턴스 초기화
            firebaseFirestore = FirebaseFirestore.getInstance();

            // Firestore 탐색 시작
            //listAllCollections();

        }

        private void initRecyclerViews() {
            recyclerViewList = findViewById(R.id.adminRecycle);
            int numberOfColumns = 2;

            recyclerViewList.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

            AdminImageList = new ArrayList<>();
            adminImageAdapter = new AdminImageAdapter(AdminImageList);
            recyclerViewList.setAdapter(adminImageAdapter);
        }
/*
    public void listAllCollections() {
        firebaseFirestore.listCollections()
                .addOnCompleteListener(new OnCompleteListener<List<CollectionReference>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<CollectionReference>> task) {
                        if (task.isSuccessful()) {
                            List<CollectionReference> collections = task.getResult();
                            if (collections != null) {
                                for (CollectionReference collection : collections) {
                                    Log.d("TAG, ", "Collection: " + collection.getId());
                                    listDocumentsInCollection(collection);
                                    listSubcollections(collection);
                                }
                            }
                        } else {
                            Log.w("TAG, ", "Error getting collections.", task.getException());
                        }
                    }
                });
    }

    private void listDocumentsInCollection(CollectionReference collection) {
        collection.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if (snapshot != null) {
                                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                                    Log.d("TAG, ", "  Document: " + doc.getId());
                                    // Optionally, print document data
                                    // Log.d(TAG, "    Data: " + doc.getData());
                                }
                            }
                        } else {
                            Log.w("TAG, ", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void listSubcollections(CollectionReference collection) {
        collection.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if (snapshot != null) {
                                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                                    doc.getReference().listCollections()
                                            .addOnCompleteListener(new OnCompleteListener<List<CollectionReference>>() {
                                                @Override
                                                public void onComplete(@NonNull Task<List<CollectionReference>> task) {
                                                    if (task.isSuccessful()) {
                                                        List<CollectionReference> subcollections = task.getResult();
                                                        if (subcollections != null) {
                                                            for (CollectionReference subcollection : subcollections) {
                                                                Log.d("TAG, ",    "Subcollection: " + subcollection.getId());
                                                                listDocumentsInCollection(subcollection);
                                                                listSubcollections(subcollection);
                                                            }
                                                        }
                                                    } else {
                                                        Log.w("TAG, ", "Error getting subcollections.", task.getException());
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            Log.w("TAG, ", "Error getting documents.", task.getException());
                        }
                    }
                });*/

        // 파이어 스토어에서 회원 인증 내역 출력
       private void loadData() {
            
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseFirestore = FirebaseFirestore.getInstance();

            String userId = FirebaseAuth.getInstance().getUid();

            if (userId == null) {
                Toast.makeText(getApplicationContext(), "User ID 가 없습니다", Toast.LENGTH_SHORT).show();
                return;
            }

            firebaseFirestore.collection(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            AdminImageList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                
                                String date = document.getString("date");
                                String getUserId = document.getString("userId");
                                String title = document.getString("title");
                                Boolean admin_check = document.getBoolean("admin_check");
                                String downloadurl = document.getString("downloadUrl");
                                Log.d("answer1",  "document id : " + document.getId());

                                Log.e("answer", "date : " +  date);
                                Log.e("answer", "title : " +  title);
                                Log.e("answer", "admin_check : " +  admin_check);
                                Log.e("answer", "downloadurl : " +  downloadurl);
                                Log.e("answer", "getUserId : " +  getUserId);
                                Log.e("answer", "imageList : " +  AdminImageList.toString());

                                AdminImageList.add(new AdminImageModel(document.getId(), title, date, admin_check,downloadurl, getUserId));
                                Log.e("answer", "imageList : " +  AdminImageList.toString());
                            }
                            
                            adminImageAdapter.updateImageList(AdminImageList);

                            Log.e("answer", "Error getting documents: " +  AdminImageList.toString());

                        } else {
                            Log.e("FirestoreError", "Error getting documents: ", task.getException());
                        }
                    });

    }
}