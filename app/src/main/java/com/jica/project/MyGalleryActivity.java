package com.jica.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyGalleryActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
    FirebaseAuth firebaseAuth;

    private RecyclerView recyclerView;
    private ActivityAdapter activityAdapter;
    private List<ActivityModel> activityList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gallery);

        // 프래그먼트 추가 확인하기
        Fragment underBar1 = new underBar();
        getSupportFragmentManager().beginTransaction().replace(R.id.galleryUnderbar, underBar1).commit();

        // 리스트 출력하기
        recyclerView = findViewById(R.id.doneList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityList = new ArrayList<>();
        activityAdapter = new ActivityAdapter(activityList);
        recyclerView.setAdapter(activityAdapter);

        // Firebase 데이터 가져오기

        String memEmail = firebaseAuth.getCurrentUser().getEmail();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("memberInfo").child(memEmail).child("imageInfo");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("noAnswer", "firebase Connected");
                activityList.clear(); // 기존 데이터를 지우고 새로 추가
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActivityModel activity = snapshot.getValue(ActivityModel.class);
                    if (activity != null) {
                        activityList.add(activity);
                    } else {
                        Log.e("noAnswer", "activity is null");
                    }
                }
                Log.e("noAnswer", activityList.toString());
                activityAdapter.notifyDataSetChanged(); // 데이터 변경 알리기
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", databaseError.getMessage());
            }
        });

    }
}