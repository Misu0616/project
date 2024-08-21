package com.jica.project;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ImagePicAdapter extends RecyclerView.Adapter<ImagePicAdapter.ViewHolder> {
    private List<ImagePicModel> imagePicList;
    private Context context;

    public ImagePicAdapter(Context context, List<ImagePicModel> imagePicList) {

        this.context = context;
        this.imagePicList = imagePicList;
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
        String imageUrl  = imagePicList.get(position).getImgURL();
        Glide.with(context).load(imageUrl).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imagePicList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imagePicList);
        }

    }

    // 데이터 리스트 업데이트 메서드 추가
    public void updateImageList(List<ImagePicModel> newImageList) {
        this.imagePicList = newImageList;
        notifyDataSetChanged();
    }

}
