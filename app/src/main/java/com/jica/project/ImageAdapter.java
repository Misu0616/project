package com.jica.project;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private List<ImageModel> imageList;

    public ImageAdapter(List<ImageModel> imageList) {
        this.imageList = imageList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.galleryviewlist, parent, false);
        Log.d("ImageAdapter", "View created: " + view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageModel image = imageList.get(position);

    // 이미지가 있을 경우 Glide를 사용하여 로드
          if (image.getDownloadurl() != null && !image.getDownloadurl().isEmpty()) {
                Glide.with(holder.imageView.getContext())
                        .load(image.getDownloadurl())
                        .into(holder.imageView);
            }

        if (holder.datelist != null) {
            String date = image.getDate();
            String frontDate = date.substring(0, 8);
            holder.datelist.setText(frontDate);
        } else {
            Log.e("ActivityAdapter", "datelist is null");
        }

        if (holder.titleList != null) {
            switch(image.getTitle()) {
                case "0": holder.titleList.setText("걷기");
                    break;
                case "1": holder.titleList.setText("계단 이용하기");
                    break;
                case "2": holder.titleList.setText("다회용 용기 사용하기");
                    break;
                case "3": holder.titleList.setText("대중교통 이용하기");
                    break;
                case "4": holder.titleList.setText("분리수거하기");
                    break;
                case "5": holder.titleList.setText("양치컵 사용하기");
                    break;
                case "6": holder.titleList.setText("장바구니 사용하기");
                    break;
                case "7": holder.titleList.setText("채식하기");
                    break;
                case "8": holder.titleList.setText("에어컨 적정 온도 유지하기");
                    break;
                case "9": holder.titleList.setText("텀블러 사용하기");
                    break;
                case "10": holder.titleList.setText("쓰레기 줍기");
                    break;
            }
        } else {
            Log.e("ActivityAdapter", "titleList is null");
        }
        if (holder.adminCheck != null) {
            // 불리언 값을 문자열로 변환하여 TextView에 설정
            holder.adminCheck.setText(image.isAdmin_check() ? "인증됨" : "인증 확인 중");
        } else {
            Log.e("ActivityAdapter", "adminCheck is null");
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    // 데이터 리스트 업데이트 메서드 추가
    public void updateData(List<ImageModel> newImageList) {
        this.imageList = new ArrayList<>(newImageList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public static final String POSITION_KEY = "position_key";
        TextView datelist;
        TextView titleList;
        TextView adminCheck;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            datelist = itemView.findViewById(R.id.date);
            titleList = itemView.findViewById(R.id.activityTitle);
            adminCheck = itemView.findViewById(R.id.adminCheck);
            imageView = itemView.findViewById(R.id.imagePicList);

        }

    }
}
