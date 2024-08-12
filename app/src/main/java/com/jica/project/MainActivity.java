package com.jica.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    Button btnlogin, btnjoin;
    EditText emailT, pwT;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        btnlogin = findViewById(R.id.btnLogin);
        btnjoin = findViewById(R.id.btnJoin);
        emailT = findViewById(R.id.email);
        pwT = findViewById(R.id.password);

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailT.getText().toString();
                String memId = pwT.getText().toString();

                Log.d("login", "email : " + email + " pw : " + memId);
                Login(email, memId);
            }
        });

        btnjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });

    }

    // 로그인하기
    public void Login(String memId, String pwT) {
        DatabaseReference memberRef = databaseReference.child("memberInfo").child(memId);

        memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // path를 파이어베이스에 있는 이름과 똑같이 맞추기 
                    String storedPassword = dataSnapshot.child("memPassword").getValue(String.class);
                    Log.d("pw", "pwd : " + storedPassword);
                    // storedPassword가 null인지 체크
                    if (storedPassword != null && storedPassword.equals(pwT)) {
                        // 로그인 성공
                        Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        Intent home = new Intent(getApplicationContext(), MyTreeActivity.class);
                        startActivity(home);
                    } else {
                        // 비밀번호 불일치
                        Toast.makeText(MainActivity.this, "비밀번호가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 사용자 없음
                    Toast.makeText(MainActivity.this, "해당 이메일로 등록된 사용자가 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 오류 처리
                Toast.makeText(MainActivity.this, "오류 발생: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
