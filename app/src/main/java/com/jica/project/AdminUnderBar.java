package com.jica.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;


public class AdminUnderBar extends Fragment {
    ImageButton btnAdminList, btnAdminAdd, btnLogout, btnActivityList;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View underbar =  inflater.inflate(R.layout.fragment_admin_under_menu, container, false);


        btnAdminList = (ImageButton) underbar.findViewById(R.id.adminList);
        btnAdminAdd = (ImageButton) underbar.findViewById(R.id.adminAdd);
        btnActivityList = (ImageButton) underbar.findViewById(R.id.adminActivityList);
        btnLogout = (ImageButton) underbar.findViewById(R.id.logout);

        btnAdminList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "camera 페이지 성공", Toast.LENGTH_SHORT).show();
                Intent camera = new Intent(getActivity(), addmin_addProtectThing.class);
                camera.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(camera);
            }
        });

        btnAdminAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "home 페이지 성공", Toast.LENGTH_SHORT).show();
                Intent home = new Intent(getActivity(), admin_AddList.class);
                home.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(home);
            }
        });

        btnActivityList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 얘는 무슨 페이지로 할지 보류
                Intent gallery = new Intent(getActivity(), AdminSearchListActivity.class);
                gallery.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(gallery);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 로그아웃
                firebaseAuth.signOut();

                Toast.makeText(getContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

                // 로그인 화면으로 이동
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        return underbar;
    }

}