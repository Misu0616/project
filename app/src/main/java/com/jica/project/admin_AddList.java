package com.jica.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class admin_AddList extends AppCompatActivity {

    GridView showGallery;

    // 그리드 개수 결정됨
    Integer[] posterId = {R.drawable.logo, R.drawable.recyclebag, R.drawable.logo, R.drawable.recyclebag, R.drawable.logo, R.drawable.recyclebag, R.drawable.logo};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_list);

        showGallery = findViewById(R.id.admin_gallerys);
        showGallery.setAdapter(new GalleryAdapter(this));

        // 프래그먼트 추가
        Fragment underBar1 = new underBar();
        getSupportFragmentManager().beginTransaction().replace(R.id.galleryUnderbar, underBar1).commit();

    }

    public class GalleryAdapter extends BaseAdapter {
        Context context;

        public GalleryAdapter(Context context) {
            this.context = context;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View itemView = inflater.inflate(R.layout.fragment_admin_gallery_info, parent, false);

            Button admin_checkbox = itemView.findViewById(R.id.admin_checkbox);
            TextView changeStatus = itemView.findViewById(R.id.changeStatus);

            admin_checkbox.setOnClickListener(new View.OnClickListener() {
                int i = 0;
                @Override
                public void onClick(View view) {
                    i = i % 2;

                    if (i == 0) {
                        Toast.makeText(getApplicationContext(), "인증 완료", Toast.LENGTH_SHORT).show();
                        changeStatus.setText("인증 완료");
                    }

                    if (i == 1) {
                        Toast.makeText(getApplicationContext(), "인증 확인 중", Toast.LENGTH_SHORT).show();
                        changeStatus.setText("인증 확인 중");
                    }
                    i++;
                }
            });
            return itemView;
        }


       /* public void hi(){
            admin_checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    i = i % 2;

                    if (i == 0) {
                        Toast.makeText(getApplicationContext(), "인증 완료", Toast.LENGTH_SHORT).show();
                        changeStatus.setText("인증 완료");
                    }

                    if (i == 1) {
                        Toast.makeText(getApplicationContext(), "인증 확인 중", Toast.LENGTH_SHORT).show();
                        changeStatus.setText("인증 확인 중");
                    }
                    i++;
                }
            });
        }*/

        @Override
        public int getCount() {
            return posterId.length;
        }

        @Override
        public Object getItem(int position) {
            return posterId[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}