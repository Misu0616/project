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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;
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
            Fragment adminUnderBar = new AdminUnderBar();
            getSupportFragmentManager().beginTransaction().replace(R.id.galleryUnderbar, adminUnderBar).commit();

            // RecyclerView 초기화
            initRecyclerViews();

            // 데이터 로드
            loadData();

            // FirebaseFirestore 인스턴스 초기화
            firebaseFirestore = FirebaseFirestore.getInstance();
        }

        private void initRecyclerViews() {
            recyclerViewList = findViewById(R.id.adminRecycle);
            int numberOfColumns = 2;

            recyclerViewList.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

            AdminImageList = new ArrayList<>();
            adminImageAdapter = new AdminImageAdapter(AdminImageList);
            recyclerViewList.setAdapter(adminImageAdapter);
        }

        // 파이어 스토어에서 회원 인증 내역 출력
       private void loadData() {
            
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseFirestore = FirebaseFirestore.getInstance();

            String userId = FirebaseAuth.getInstance().getUid();

            if (userId == null) {
                Toast.makeText(getApplicationContext(), "User ID 가 없습니다", Toast.LENGTH_SHORT).show();
                return;
            }

           List<String> collectionNames = new ArrayList<>();
           Collections.addAll(collectionNames,
                   "mlV0XBUHPpVa6zyIE7rYzGieNwp1",
                   "hT20GF9j8AW05aWLPgGUpOUgsgh1");

           AdminImageList.clear();
           for (String collectionName : collectionNames) {
               firebaseFirestore.collection(collectionName)
                       .get()
                       .addOnCompleteListener(task -> {
                           if (task.isSuccessful()) {
                               for (QueryDocumentSnapshot document : task.getResult()) {

                                   String date = document.getString("date");
                                   String getUserId = document.getString("userId");
                                   String title = document.getString("title");
                                   Boolean admin_check = document.getBoolean("admin_check");
                                   String downloadurl = document.getString("downloadUrl");

                                   AdminImageList.add(new AdminImageModel(document.getId(), title, date, admin_check, downloadurl, getUserId));
                               }
                               Collections.reverse(AdminImageList); // 데이터 역순 저장(근데 이거 필요한가...?)
                               adminImageAdapter.updateImageList(AdminImageList);

                           } else {
                               Log.e("FirestoreError", "Error getting documents: ", task.getException());
                           }
                       });
           }
    }

}