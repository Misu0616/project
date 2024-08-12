package com.jica.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();

    Button btnJoin, btnFinalJoin;
    EditText memIdT, memPasswordT, checkMemPWT, memEmailT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        btnFinalJoin = findViewById(R.id.finalJoin);
        btnJoin = findViewById(R.id.btnJoin);
        memIdT = findViewById(R.id.memId);
        memPasswordT = findViewById(R.id.memPW);
        checkMemPWT = findViewById(R.id.checkMemPW);
        memEmailT = findViewById(R.id.memEmail);

        // 아이디 중복 확인 버튼
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String memId = memIdT.getText().toString();
                checkId(memId);
            }
        });

        // 최종 회원가입 버튼
        btnFinalJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMemInfo(memIdT.getText().toString(), memPasswordT.getText().toString(),
                        checkMemPWT.getText().toString(), memEmailT.getText().toString());
            }
        });

    }
    // 아이디 중복 확인 메서드
    public void checkId(String memId){
        DatabaseReference memberRef = databaseReference.child("memberInfo").child(memId);

        memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 아이디가 이미 존재하는 경우
                    Toast.makeText(JoinActivity.this, "이미 사용 중인 아이디입니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 아이디가 존재하지 않는 경우
                    Toast.makeText(JoinActivity.this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 데이터베이스 오류 처리
                Toast.makeText(JoinActivity.this, "오류 발생: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 이메일 유효성 검사
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void addMemInfo(String memId, String memPassword, String checkMemPW, String memEmail) {
        if (!memPassword.equals(checkMemPW)) {
            // 비밀 번호 확인 실패 시 처리
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (memId.isEmpty()) {
            Toast.makeText(this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (memPassword.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (memEmail.isEmpty()) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(memId.length() < 4 || memPassword.length() < 4) {
            Toast.makeText(this, "아이디와 비밀번호는 4글자 이상 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isValidEmail(memEmail)) {
            Toast.makeText(this, "이메일 형식에 맞게 입력해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        String memKey = memId;
        memberInfo memberInfo = new memberInfo(memId, memPassword, memEmail);

        // 사용자 정보를 Firebase에 저장
        databaseReference.child("memberInfo").child(memKey).setValue(memberInfo).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(JoinActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                Intent home = new Intent(getApplicationContext(), MyTreeActivity.class);
                startActivity(home);
            } else {
                Toast.makeText(JoinActivity.this, "회원가입 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }
}