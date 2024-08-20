package com.jica.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

public class MyTreeActivity extends AppCompatActivity {

        MyTreeActivity myTree;
        underBar underBar1;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_my_tree);

            myTree = new MyTreeActivity();
            Fragment underBar1 = new underBar();

            getSupportFragmentManager().beginTransaction().replace(R.id.galleryUnderbar, underBar1).commit();

        }
}