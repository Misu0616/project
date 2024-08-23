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
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class admin_AddList extends AppCompatActivity {
/*
    GridView showGallery;

    // 그리드 개수 결정됨
    Integer[] posterId = {R.drawable.logo, R.drawable.recyclebag, R.drawable.logo, R.drawable.recyclebag, R.drawable.logo, R.drawable.recyclebag, R.drawable.logo};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_list);

        showGallery = findViewById(R.id.admin_gallerys);
        showGallery.setAdapter(new GalleryAdapter(this));

        // 프래그먼트 추가
        Fragment underBar1 = new underBar();
        getSupportFragmentManager().beginTransaction().replace(R.id.galleryUnderbar, underBar1).commit();

    }

    public class GalleryAdapter extends BaseAdapter {
        Context context;

        public GalleryAdapter(Context context) {
            this.context = context;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.fragment_admin_gallery_info, parent, false);

            Button admin_checkbox = itemView.findViewById(R.id.admin_checkbox);
            TextView changeStatus = itemView.findViewById(R.id.changeStatus);

            admin_checkbox.setOnClickListener(new View.OnClickListener() {
                int i = 0;
                @Override
                public void onClick(View view) {
                    i = i % 2;

                    if (i == 0) {
                        Toast.makeText(getApplicationContext(), "인증 완료", Toast.LENGTH_SHORT).show();
                        changeStatus.setText("인증 완료");
                    }

                    if (i == 1) {
                        Toast.makeText(getApplicationContext(), "인증 확인 중", Toast.LENGTH_SHORT).show();
                        changeStatus.setText("인증 확인 중");
                    }
                    i++;
                }
            });
            return itemView;
        }

        @Override
        public int getCount() {
            return posterId.length;
        }

        @Override
        public Object getItem(int position) {
            return posterId[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }*/

        private FirebaseAuth firebaseAuth;
        private FirebaseStorage firebaseStorage;
        private RecyclerView recyclerViewList;
        private AdminImageAdapter adminImageAdapter;
        private List<AdminImageModel> AdminImageList;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_admin_add_list);

            Toast.makeText(getApplicationContext(), "toased", Toast.LENGTH_SHORT).show();

            // 하단 네비게이션 바
            Fragment underBar1 = new underBar();
            getSupportFragmentManager().beginTransaction().replace(R.id.galleryUnderbar, underBar1).commit();

            // RecyclerView 초기화
            initRecyclerViews();

            // 데이터 로드
            loadData();
        }

        private void initRecyclerViews() {
            recyclerViewList = findViewById(R.id.adminRecycle);
            int numberOfColumns = 2;

            recyclerViewList.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

            AdminImageList = new ArrayList<>();
            adminImageAdapter = new AdminImageAdapter(AdminImageList);
            recyclerViewList.setAdapter(adminImageAdapter);
        }

        private void loadData() {
            // Firebase 사용자 인증 초기화
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseStorage = FirebaseStorage.getInstance();
            String memEmail = firebaseAuth.getCurrentUser().getEmail();
            String safeEmail = memEmail != null ? memEmail.replace(".", ",") : "";

            String userId = FirebaseAuth.getInstance().getUid();
            Log.e("noAnswer", "userId     ---- > " + userId);
            if (userId == null) {
                Log.e("Firestore", "User ID is null");
                return;
            }

            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                    .getReference("memberInfo").child(safeEmail).child("imageInfo");

            // Firebase Realtime Database에서 데이터 로드
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    AdminImageList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        AdminImageModel activity = snapshot.getValue(AdminImageModel.class);
                        Log.e("noAnswer", "activity : " + activity);
                        if (activity != null) {
                            AdminImageList.add(activity);
                        } else {
                            Log.e("noAnswer", "activity is null or imgURL is null");
                        }
                    }

                    // 어댑터에 데이터 변경 사항을 알림
                    adminImageAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("FirebaseError", databaseError.getMessage());
                }
            });
    }
}