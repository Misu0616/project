package com.jica.project;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;


public class underBar extends Fragment {
    ImageButton btncamera, btnHome, btnGallery;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View underbar =  inflater.inflate(R.layout.fragment_under_menu, container, false);


        btncamera = (ImageButton) underbar.findViewById(R.id.camerabtn);
        btnHome = (ImageButton) underbar.findViewById(R.id.homebtn);
        btnGallery = (ImageButton) underbar.findViewById(R.id.gallerybtn);

        btncamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "camera 페이지 성공", Toast.LENGTH_SHORT).show();
                Intent camera = new Intent(getActivity(), admin_AddList.class);
                camera.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(camera);
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "home 페이지 성공", Toast.LENGTH_SHORT).show();
                Intent home = new Intent(getActivity(), MyTreeActivity.class);
                home.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(home);
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "gallery 페이지 성공", Toast.LENGTH_SHORT).show();
                Intent gallery = new Intent(getActivity(), MyGalleryActivity.class);
                gallery.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(gallery);
            }
        });
        return underbar;
    }
}