package com.jica.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Person;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class SearchListActivity extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    RecyclerView recyclerView;
    ActivityAdapter activityAdapter;
    List<ActivityModel> activityList;

    ImageButton search;
    EditText searchWord;

    public SearchListActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 검색창 layout
        setContentView(R.layout.activity_search_list);

        // 하단 네비게이션 바
        Fragment underBar1 = new underBar();
        getSupportFragmentManager().beginTransaction().replace(R.id.underbarSearch, underBar1).commit();

        // 검색창 돋보기 클릭 
        search = findViewById(R.id.search);
        searchWord = findViewById(R.id.searchWord);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterActivities();
            }
        });

        // 리스트 출력하기
        recyclerView = findViewById(R.id.showList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        activityList = new ArrayList<>();
        activityAdapter = new ActivityAdapter(activityList);
        recyclerView.setAdapter(activityAdapter);

        // Firebase 데이터 가져오기
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("activityInfo");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
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
    private void filterActivities() {
        String query = searchWord.getText().toString().toLowerCase(Locale.ROOT);
        List<ActivityModel> filteredList = new ArrayList<>();

        if (!query.isEmpty()) {
            filteredList = activityList.stream()
                    .filter(activity -> activity.getActNumber().toLowerCase(Locale.ROOT).contains(query))
                    .collect(Collectors.toList());
        } else {
            filteredList = new ArrayList<>(activityList); // 쿼리가 비어있으면 전체 리스트를 반환
        }

        activityAdapter.updateActivityList(filteredList); // 필터링된 데이터를 어댑터에 업데이트
    }
}