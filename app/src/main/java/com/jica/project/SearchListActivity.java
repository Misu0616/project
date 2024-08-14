package com.jica.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchListActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    private RecyclerView recyclerView;
    private ActivityAdapter activityAdapter;
    private List<ActivityModel> activityList;

    ImageButton search;

    String todoList[] = {
            "양치컵 사용하기",
            "장바구니 사용하기",
            "대중교통 이용하기",
            "걷기",
            "분리수거 하기",
            "다회용 용기 사용하기",
            "채식하기",
            "에어컨 적정 온도 유지하기",
            "텀블러 사용하기",
            "쓰레기 줍기",
            "계단 이용하기"
    };

    ArrayAdapter adapter;
    ListView showTodoList;
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
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(search);
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
                    activityList.add(activity);
                }
                activityAdapter.notifyDataSetChanged(); // 데이터 변경 알리기
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 오류 처리
            }
        });

        /*showTodoList = findViewById(R.id.showList);

        adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, todoList){
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView tv = (TextView)view.findViewById(android.R.id.text1);
                tv.setBackgroundColor(Color.argb(128, 241, 241, 241));
                return view;
            }
        };

        showTodoList.setAdapter(adapter);*/
    }
}