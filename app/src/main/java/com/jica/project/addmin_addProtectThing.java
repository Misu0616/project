package com.jica.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class addmin_addProtectThing extends AppCompatActivity {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();

    Button btnAddActivity;
    EditText addActivityT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_protect_thing);

        btnAddActivity = findViewById(R.id.btnAddActivity);
        addActivityT = findViewById(R.id.addActivity);

        // 하단 네비게이션 바
        Fragment adminUnderBar = new AdminUnderBar();
        getSupportFragmentManager().beginTransaction().replace(R.id.addProtect, adminUnderBar).commit();

        btnAddActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addActivities(addActivityT.getText().toString());

                Intent intent = new Intent(getApplicationContext(), AdminSearchListActivity.class);
                startActivity(intent);
            }
        });
    }
    public void addActivities(String addActivity){
        addActivity addActivityJava = new addActivity(addActivity);

        databaseReference.child("activityInfo").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int actNumber = (int) dataSnapshot.getChildrenCount(); // 현재 저장된 활동 수를 가져옴

                // 새로운 활동 정보를 저장
                databaseReference.child("activityInfo").child(String.valueOf(actNumber)).setValue(addActivityJava);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 오류 처리
                Log.e("DatabaseError", databaseError.getMessage());
            }
        });
    }
}