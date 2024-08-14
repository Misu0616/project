package com.jica.project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.perf.session.SessionManager;

public class MainActivity extends AppCompatActivity {

    Button btnlogin, btnjoin;
    EditText emailT, pwT;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
    FirebaseAuth firebaseAuth;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);

        firebaseAuth = firebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("memberInfo/memId");

        btnlogin = findViewById(R.id.btnLogin);
        btnjoin = findViewById(R.id.btnJoin);
        emailT = findViewById(R.id.email);
        pwT = findViewById(R.id.password);

        // 자동 로그인
        if (firebaseAuth.getCurrentUser() != null) {
            Toast.makeText(MainActivity.this, "자동 로그인되었습니다", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, MyTreeActivity.class);
            startActivity(intent);
            finish();
        }

        // 로그인 버튼
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailT.getText().toString();
                String memId = pwT.getText().toString();

                Login(email, memId);
            }
        });

        // 회원가입 버튼
        btnjoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                startActivity(intent);
            }
        });

    }

    // 로그인하기
    public void Login(String emailT, String pwT) {

        firebaseAuth.signInWithEmailAndPassword(emailT, pwT)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) { // 성공
                            Intent intent = new Intent(MainActivity.this, MyTreeActivity.class);
                            startActivity(intent);
                            Toast.makeText(MainActivity.this, "로그인되었습니다", Toast.LENGTH_SHORT).show();
                        } else { // 실패
                            Toast.makeText(MainActivity.this, "입력하신 정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
