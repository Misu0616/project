package com.jica.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JoinActivity extends AppCompatActivity {

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = database.getReference();
    private FirebaseAuth firebaseAuth;

    Button btnJoin, btnFinalJoin, btnCheckMail;
    EditText memIdT, memPasswordT, checkMemPWT, memEmailT;
    ImageButton PasswordVisibility1, PasswordVisibility2;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        firebaseAuth = FirebaseAuth.getInstance();

        btnFinalJoin = findViewById(R.id.finalJoin);
        btnCheckMail = findViewById(R.id.btnCheckMail);
        memIdT = findViewById(R.id.memId);
        memPasswordT = findViewById(R.id.memPW);
        checkMemPWT = findViewById(R.id.checkMemPW);
        memEmailT = findViewById(R.id.memEmail);
        PasswordVisibility1 = findViewById(R.id.PasswordVisibility1);
        PasswordVisibility2 = findViewById(R.id.PasswordVisibility2);

        btnCheckMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = memEmailT.getText().toString();
                String safeEmail = email.replace(".", ",");
                DatabaseReference memberRef = databaseReference.child("memberInfo").child(safeEmail);
                memberRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // 아이디가 이미 존재하는 경우
                            Toast.makeText(JoinActivity.this, "이미 사용 중인 이메일입니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            // 아이디가 존재하지 않는 경우
                            Toast.makeText(JoinActivity.this, "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // 데이터베이스 오류 처리
                        Toast.makeText(JoinActivity.this, "오류 발생: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // 비밀번호 글자 보이기
        PasswordVisibility1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPasswordVisible) {
                    memPasswordT.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    PasswordVisibility1.setImageResource(R.drawable.visibility_off);
                } else {
                    memPasswordT.setInputType(InputType.TYPE_CLASS_TEXT);
                    PasswordVisibility1.setImageResource(R.drawable.visibility);
                }
                isPasswordVisible = !isPasswordVisible;
                memPasswordT.setSelection(memPasswordT.length());
            }
        });
        PasswordVisibility2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPasswordVisible) {
                    checkMemPWT.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    PasswordVisibility1.setImageResource(R.drawable.visibility_off);
                } else {
                    checkMemPWT.setInputType(InputType.TYPE_CLASS_TEXT);
                    PasswordVisibility2.setImageResource(R.drawable.visibility);
                }
                isPasswordVisible = !isPasswordVisible;
                checkMemPWT.setSelection(checkMemPWT.length());
            }
        });

        // 최종 회원가입 버튼
        btnFinalJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = memEmailT.getText().toString();
                String id = memIdT.getText().toString();
                String pwd = memPasswordT.getText().toString();
                String pwdchk = checkMemPWT.getText().toString();

                if (id.isEmpty()) {
                    Toast.makeText(JoinActivity.this, "아이디를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (pwd.isEmpty()) {
                    Toast.makeText(JoinActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (id.length() < 4) {
                    Toast.makeText(JoinActivity.this, "아이디는 최소 4자 이상 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }else if (!isPasswordValid(pwd)) {
                    Toast.makeText(JoinActivity.this, "최소 8자 이상, 영문자, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!pwd.equals(pwdchk)) {
                    Toast.makeText(JoinActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!isValidEmail(email)) {
                    Toast.makeText(JoinActivity.this, "이메일 형식에 맞게 입력해주세요", Toast.LENGTH_SHORT).show();
                    return;
                }

        // 모든 검증을 통과한 후 사용자 등록 시도
        firebaseAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener(JoinActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            saveUserInfoToDatabase(email);
                            Intent intent = new Intent(JoinActivity.this, MyTreeActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(JoinActivity.this, "회원 가입되셨습니다.\n" + id + "님 환영합니다", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(JoinActivity.this, "회원 가입에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // 사용자 정보를 Firebase에 저장
                String memKey = id;
                String safeEmail = email.replace(".", ",");
                memberInfo memberInfo = new memberInfo(id, pwd, safeEmail);

               databaseReference.child("memberInfo").child(safeEmail).setValue(memberInfo).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                    } else {
                    }
                });
                databaseReference.child("memberInfo").child(safeEmail).child("progressbar").setValue("0").addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                    } else {
                    }
                });
                databaseReference.child("memberInfo").child(safeEmail).child("level").setValue("1").addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                    } else {
                    }
                });
            }



        });

    }

    private void saveUserInfoToDatabase(String email) {
        // 현재 날짜 및 기준 날짜 계산
        Calendar calendar = Calendar.getInstance();
        Date initialDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        String initialDateString = sdf.format(initialDate);

        // d+1 코드 생성
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Date futureDate = calendar.getTime();
        String code = sdf.format(futureDate);

        // 사용자 정보 및 기준 날짜와 코드 저장
        Map<String, Object> dday = new HashMap<>();
        dday.put("initialDate", initialDateString);
        dday.put("registrationCode", code);

        // Realtime Database에 사용자 정보 저장
        databaseReference.child("memberInfo").child(email.replace(".", ",")).child("dayInfo").setValue(dday)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.w("SignUp", "Realtime Database에 기준 날짜와 코드 저장 실패");
                    } else {
                        Log.w("SignUp", "Realtime Database에 사용자 정보 저장 실패", task.getException());
                    }
                });
}

    // 이메일 유효성 검사
    public boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // 비밀번호 유효성 검사
    private boolean isPasswordValid(String password) {
        Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$");
        boolean isValid = pattern.matcher(password).matches();

       /* if (!isValid) {
            passwordError.setText("최소 8자 이상, 영문자, 숫자, 특수문자를 각각 1개 이상 포함해야 합니다.");
            passwordError.setVisibility(View.VISIBLE);
        } else {
            passwordError.setVisibility(View.GONE);
            isPasswordValid = true;
        }*/
        return isValid;
    }
}
