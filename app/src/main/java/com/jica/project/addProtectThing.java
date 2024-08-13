package com.jica.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class addProtectThing extends AppCompatActivity {

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
        btnAddActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addActivities(addActivityT.getText().toString());

                Intent intent = new Intent(getApplicationContext(), SearchListActivity.class);
                startActivity(intent);
            }
        });
    }
    public void addActivities(String addActivity){
        addActivity addActivityJava = new addActivity(addActivity);

        String actNumber = addActivity;
        databaseReference.child("activityInfo").child(actNumber).setValue(addActivityJava);
    }
}