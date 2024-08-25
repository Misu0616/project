package com.jica.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyTreeActivity extends AppCompatActivity {

        TextView dday, levelNum;
        ProgressBar levelbar;
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference();
        FirebaseAuth firebaseAuth;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_my_tree);

            Fragment underBar1 = new underBar();
            getSupportFragmentManager().beginTransaction().replace(R.id.galleryUnderbar, underBar1).commit();

            dday = findViewById(R.id.dday);
            levelbar = findViewById(R.id.levelBar);
            int progress = levelbar.getProgress();
            levelbar.setProgress(progress);

            firebaseAuth = FirebaseAuth.getInstance();

            String email = firebaseAuth.getCurrentUser().getEmail();

            databaseReference = FirebaseDatabase.getInstance().getReference("memberInfo").child((email.replace(".", ",")));

            fetchDataFromFirebase();
            progressbar();

        }

    private void progressbar() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Firebase에서 가져온 프로그레스 바 정보 표시하기
                String levelStr = dataSnapshot.child("level").getValue(String.class);
                int intLevel = Integer.parseInt(levelStr);
                levelNum = findViewById(R.id.level);

                // 레벨에 따라 이미지 변경
                ImageView seedPic = findViewById(R.id.seed);
                switch (intLevel) {
                    case 1:
                        levelNum.setText("1");
                        seedPic.setImageResource(R.drawable.level_seed);
                        break;
                    case 2:
                        levelNum.setText("2");
                        seedPic.setImageResource(R.drawable.level_seed2);
                        break;
                    case 3:
                        levelNum.setText("3");
                        seedPic.setImageResource(R.drawable.level_seed3);
                        break;
                    case 4:
                        levelNum.setText("4");
                        seedPic.setImageResource(R.drawable.level_seed4);
                        break;
                    default:
                        seedPic.setImageResource(R.drawable.level_seed);
                        break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // 데이터베이스 읽기 실패 시 처리
                Log.w("progress", "Failed to read value.", error.toException());
            }
        });
    }


    private void fetchDataFromFirebase() {
        databaseReference.child("dayInfo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // realtimedatabase 안 dayInfo에 있는 데이터 출력
                String initialDateString = dataSnapshot.child("initialDate").getValue(String.class);

                if (initialDateString != null) {
                    // 현재 날짜 및 회원가입일 계산
                    int daysElapsed = calculateDaysElapsed(initialDateString) + 1;

                    // 결과 문자열 생성
                    String result = String.valueOf(daysElapsed);

                    // TextView에 결과 문자열 출력
                    dday.setText(result);
                } else {
                    dday.setText("회원가입일을 가져오는 데 실패했습니다.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 가져오기 실패 처리
                dday.setText("데이터를 가져오는 데 실패했습니다.");
            }
        });
    }

    private int calculateDaysElapsed(String initialDateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
            Date initialDate = sdf.parse(initialDateString);
            Date currentDate = new Date();

            if (initialDate != null) {
                long differenceInMillis = currentDate.getTime() - initialDate.getTime();
                long differenceInDays = differenceInMillis / (1000 * 60 * 60 * 24);
                return (int) differenceInDays;
            }
        } catch (Exception e) {
            Log.e("MyTreeActivity", "날짜 계산 오류", e);
        }
        return 0;
    }
}