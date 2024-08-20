package com.jica.project;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImagePicAdapter extends RecyclerView.Adapter<ImagePicAdapter.ViewHolder> {
    private List<ImagePicModel> ImagePicList;

    public ImagePicAdapter(List<ImagePicModel> ImagePicList) {
        this.ImagePicList = ImagePicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.imageviewlist, parent, false);
        Log.d("ImagePicAdapter", "View created: " + view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImagePicModel imagePicModel = ImagePicList.get(position);
        // Glide를 사용하여 이미지를 로드
        Glide.with(holder.itemView.getContext())
                .load(imagePicModel.getImgURL()) // ImagePicModel의 URL 또는 이미지 경로
                .into(holder.picList);
    }

    @Override
    public int getItemCount() {
        return ImagePicList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView picList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            picList = itemView.findViewById(R.id.imagePicList);
        }

    }
}
