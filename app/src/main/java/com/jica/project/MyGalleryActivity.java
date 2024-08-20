package com.jica.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyGalleryActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseAuth firebaseAuth;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private RecyclerView recyclerViewList, recyclerViewPic;
    private com.jica.project.ImageAdapter ImageAdapter;
    private com.jica.project.ImagePicAdapter ImagePicAdapter;
    private List<ImageModel> ImageList;
    private List<ImagePicModel> ImageListPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gallery);

        // 프래그먼트 추가 확인하기
        Fragment underBar1 = new underBar();
        getSupportFragmentManager().beginTransaction().replace(R.id.galleryUnderbar, underBar1).commit();

        // recycleView 출력하기
        recyclerViewList = findViewById(R.id.doneList);
        recyclerViewPic = findViewById(R.id.doneImage);
        int numberOfColumns = 2; // 열의 개수를 설정 (예: 2열)
        recyclerViewList.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerViewPic.setLayoutManager(new GridLayoutManager(this, numberOfColumns));

        // recyclerView1(글씨만)
        ImageList = new ArrayList<>();
        ImageAdapter = new ImageAdapter(ImageList);
        recyclerViewList.setAdapter(ImageAdapter);

        // recyclerView2(사진만)
        ImageListPic = new ArrayList<>();
        ImagePicAdapter = new ImagePicAdapter(ImageListPic);
        recyclerViewPic.setAdapter(ImagePicAdapter);

        // Firebase 데이터 가져오기(글씨 데이터만)
        firebaseAuth = FirebaseAuth.getInstance();
        String memEmail = firebaseAuth.getCurrentUser().getEmail();
        String safeEmail = memEmail.replace(".", ",");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("memberInfo").child(safeEmail).child("imageInfo");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("noAnswer", "firebase Connected");
                ImageList.clear(); // 기존 데이터를 지우고 새로 추가
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ImageModel activity = snapshot.getValue(ImageModel.class);
                    if (activity != null) {
                        ImageList.add(activity);
                    } else {
                        Log.e("noAnswer", "activity is null");
                    }
                }
                Log.e("noAnswer", ImageList.toString());
                ImageAdapter.notifyDataSetChanged(); // 데이터 변경 알리기
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });

        // FirebaseStore 데이터 가져오기(사진 데이터만)
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
/*
        databaseReference = FirebaseDatabase.getInstance().getReference("memberInfo").child(safeEmail).child("imageInfo");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("noAnswer", "firebase Connected");
                ImageListPic.clear(); // 기존 데이터를 지우고 새로 추가
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ImagePicModel imagePicModel = snapshot.getValue(ImagePicModel.class);
                    if (imagePicModel  != null) {
                        ImageListPic.add(imagePicModel);
                    } else {
                        Log.e("noAnswer", "activity is null");
                    }
                }
                Log.e("noAnswer", ImageListPic.toString());
                ImagePicAdapter.notifyDataSetChanged(); // 데이터 변경 알리기
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });*/

        // 환경 보호 종류 position으로 나누기
        int position = getIntent().getIntExtra(ActivityAdapter.ViewHolder.POSITION_KEY, -1); // 기본값 -1
        if (position != -1) {
            Toast.makeText(this, "my gallery 받은 포지션: " + position, Toast.LENGTH_SHORT).show();
            Log.d("noAnswer : ", "my gallery 받은 포지션: " + position);
        }

        // 사진 이름 현재 날짜로 구별하기
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "photo_" + timeStamp + ".jpg"; // 예: photo_20230818_123456.jpg

        // UID로 user 구분하기
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // UID 가져오기

        // 이미지를 다운로드하거나 업로드하는 코드 추가
            StorageReference imagesRef = storageRef.child(userId + "/" + position + "/" + fileName);
            imagesRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // 성공적으로 URL을 가져온 후의 처리
            }).addOnFailureListener(exception -> {
                // 오류 처리
            });
        }

    }
}