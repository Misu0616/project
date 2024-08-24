package com.jica.project;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
            if (firebaseAuth.getCurrentUser().getEmail().toString().equals("admin@naver.com")) {
                Toast.makeText(MainActivity.this, "관리자 페이지 로그인", Toast.LENGTH_SHORT).show();
                Log.d("hihihihi", "member Login111 : " + firebaseAuth.getCurrentUser().getEmail());
                Intent intent2 = new Intent(MainActivity.this, admin_AddList.class);
                startActivity(intent2);
                finish();
            } else {
                Toast.makeText(MainActivity.this, "자동 로그인되었습니다", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, MyTreeActivity.class);
                startActivity(intent);
                finish();
            }
            // 환경 보호 활동 인증하기 위한 이메일 정보 전달
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("USER_EMAIL", firebaseAuth.getCurrentUser().getEmail());
            editor.apply();

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

    // 사용자가 권한에 대한 대화 상자에서 예(승인),아니오(거부)를 선택 했을 때 호출된 callback 메서드
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "카메라 권한을 사용자가 승인함.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "카메라 권한 거부됨.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
    // 로그인하기
    public void Login(String emailT, String pwT) {

        firebaseAuth.signInWithEmailAndPassword(emailT, pwT)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (firebaseAuth.getCurrentUser().getEmail().toString().equals("admin@naver.com")) {
                                Log.d("hihihihi", "member Login333 : " + firebaseAuth.getCurrentUser().getEmail());
                                Toast.makeText(MainActivity.this, "관리자 페이지 로그인", Toast.LENGTH_SHORT).show();
                                Intent intent2 = new Intent(MainActivity.this, admin_AddList.class);
                                startActivity(intent2);
                                finish();
                            } else {
                                Intent intent = new Intent(MainActivity.this, MyTreeActivity.class);
                                intent.putExtra("EMAIL", emailT);
                                startActivity(intent);
                                Toast.makeText(MainActivity.this, "로그인되었습니다", Toast.LENGTH_SHORT).show();
                            }
                        } else { // 실패
                            Toast.makeText(MainActivity.this, "입력하신 정보를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        
        // 환경보호 활동 인증하기 위한 이메일 정보 전달 
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_EMAIL", emailT);
        editor.apply(); // 변경 사항 저장
    }

}
