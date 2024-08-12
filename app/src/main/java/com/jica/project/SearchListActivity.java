package com.jica.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

public class SearchListActivity extends AppCompatActivity {

    SearchListActivity searchList;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 검색창
        setContentView(R.layout.activity_search_list);

        // searchList = new SearchListActivity();

        // 하단 네비게이션 바
        Fragment underBar1 = new underBar();

        getSupportFragmentManager().beginTransaction().replace(R.id.underbarSearch, underBar1).commit();

        search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent search = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(search);
            }
        });

        // 여기부터 리스트
        showTodoList = findViewById(R.id.showList);

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

        showTodoList.setAdapter(adapter);
    }
}