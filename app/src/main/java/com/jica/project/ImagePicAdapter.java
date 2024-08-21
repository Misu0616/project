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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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

    // Firestore와 Storage 초기화
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private StorageReference storageRef = storage.getReference();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String uid = firebaseAuth.getUid();

    // Firestore 데이터 가져오기
    public void fetchData(String uid) {
        CollectionReference collectionReference = firestore.collection(uid)
                .document(uid)
                .collection("5");

        collectionReference.addSnapshotListener((snapshots, e) -> {
            if (e != null) {
                Log.e("FirestoreError", e.getMessage());
                return;
            }

            if (snapshots != null) {
                Log.d("Firestore", "Firestore Connected");
                ImagePicList.clear(); // 기존 데이터를 지우고 새로 추가

                for (QueryDocumentSnapshot document : snapshots) {
                    ImagePicModel imagePicModel = document.toObject(ImagePicModel.class);

                    if (imagePicModel != null) {
                        ImagePicList.add(imagePicModel);
                        Log.d("Firestore", "Image URL: " + imagePicModel.getImgURL());
                        Log.d("Firestore1", "imagePicModel : " + imagePicModel.toString());
                    } else {
                        Log.e("Firestore", "ImagePicModel is null");
                    }
                }
                notifyDataSetChanged(); // 데이터 변경 알리기
            }
        });
    }

}
