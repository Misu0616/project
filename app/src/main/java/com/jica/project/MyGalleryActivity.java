package com.jica.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class MyGalleryActivity extends AppCompatActivity {

    View itemView;
    MyGalleryActivity myGallery;
    GridView showGallery;
    // 그리드 개수 결정됨
    Integer[] posterId = {R.drawable.logo, R.drawable.recyclebag, R.drawable.logo, R.drawable.recyclebag, R.drawable.logo};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gallery);

        showGallery = findViewById(R.id.gallerys);
        showGallery.setAdapter(new GalleryAdapter(this));
    }

    public class GalleryAdapter extends BaseAdapter {
        Context context;

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            
            myGallery = new MyGalleryActivity();

            // 프래그먼트 추가 확인하기
            Fragment underBar1 = new underBar();
            getSupportFragmentManager().beginTransaction().replace(R.id.galleryUnderbar, underBar1).commit();

            LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            itemView = inflater.inflate(R.layout.fragment_gallery_info, parent, false);


            /* 이미지만
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(200, 300));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(5, 5, 5, 5);
            imageView.setImageResource(posterId[position]);*/


           /* imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View dialogView = View.inflate(context, R.layout.dialog, null);
                    AlertDialog.Builder dlg = new AlertDialog.Builder(context);
                    ImageView ivPoster = dialogView.findViewById(R.id.imagePost);
                    ivPoster.setImageResource(posterId[position]);
                    dlg.setTitle(mvName[position]);
                    dlg.setView(dialogView);
                    dlg.setNegativeButton("닫기", null);
                    dlg.show();
                }
            });*/

            return itemView;
        }

        public GalleryAdapter(Context context) {
            this.context = context;
        }

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