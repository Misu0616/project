package com.jica.project;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminImageAdapter extends RecyclerView.Adapter<AdminImageAdapter.ViewHolder> {
    List<AdminImageModel> AdminImageList;
    FirebaseFirestore firestore;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = database.getReference();

    public AdminImageAdapter(List<AdminImageModel> AdminImageList) {
        this.AdminImageList = AdminImageList;
        this.firestore = FirebaseFirestore.getInstance();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_gallery_info, parent, false);
        Log.d("ImageAdapter", "View created: " + view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdminImageModel image = AdminImageList.get(position);

    // 이미지가 있을 경우 Glide를 사용하여 로드
           if (image.getDownloadurl() != null && !image.getDownloadurl().isEmpty()) {
                Glide.with(holder.imageView.getContext())
                        .load(image.getDownloadurl())
                        .into(holder.imageView);
            } else {
                holder.imageView.setImageResource(android.R.color.transparent); // 이미지가 없을 경우 투명하게 설정
            }

        if (holder.datelist != null) {
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String formattedDate = dateFormat.format(now);
            holder.datelist.setText(formattedDate);
        } else {
            Log.e("ActivityAdapter", "datelist is null");
        }

        if (holder.userName != null) {
            holder.userName.setText(image.getUserID());
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
            holder.adminCheck.setText(image.isAdmin_check() ? "인증됨" : "인증 확인 중");
        } else {
            Log.e("ActivityAdapter", "adminCheck is null");
        }

        firebaseFirestore = FirebaseFirestore.getInstance();

        List<String> documentIds = new ArrayList<>();

        firestore.collection("mlV0XBUHPpVa6zyIE7rYzGieNwp1")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        documentIds.clear(); // 기존 리스트를 비웁니다
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            String documentId = document.getId();
                            documentIds.add("mlV0XBUHPpVa6zyIE7rYzGieNwp1");
                            Log.d("answer123", "document id : " + documentId);

                            // CheckBox 클릭 리스너 설정
                            holder.admin_checkbox.setOnClickListener(view -> {

                                // 클릭 횟수 추적 및 메서드 호출
                                holder.clickCount++;
                                if (holder.clickCount % 2 == 1) {
                                    increaseProgressbar();
                                } else {
                                    decreaseProgressbar();
                                }

                                boolean newCheckStatus = !image.isAdmin_check();
                                holder.adminCheck.setText(newCheckStatus ? "인증 완료" : "인증 확인 중");

                                image.setAdmin_check(newCheckStatus);

                                // 문서 ID를 통해 Firestore 문서 업데이트
                                if (!documentIds.isEmpty()) {
                                    for(int i=0; i<documentIds.size(); i++) {
                                        //String documentId = documentIds.get(i);
                                        Log.d("checkthing", "document 객체 : " + documentIds);
                                        Log.d("checkthing", "documentIds.size 객체 : " + documentIds.size());
                                        Log.d("checkthing", "documentId: " + documentId);
                                        firestore.collection("mlV0XBUHPpVa6zyIE7rYzGieNwp1")
                                                .document(documentId) // 문서 ID로 문서 참조
                                                .update("admin_check", newCheckStatus)
                                                .addOnSuccessListener(aVoid -> {
                                                    Log.d("AdminImageAdapter", "DocumentSnapshot successfully updated!");
                                                })
                                                .addOnFailureListener(e ->
                                                        Log.w("AdminImageAdapter", "Error updating document", e));
                                    }
                                }
                            });
                        }

                    } else {
                        Log.e("FirestoreError", "Error getting documents: ", task.getException());
                    }
                });
    }

    private void increaseProgressbar() {

        databaseReference = FirebaseDatabase.getInstance().getReference("memberInfo").child("cyniaa@naver,com");

        // 데이터 변경 리스너 추가
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // 현재 progressbar 값 가져오기
                String progressNumStr = dataSnapshot.child("progressbar").getValue(String.class);
                String levelStr = dataSnapshot.child("level").getValue(String.class);

                int progressNumber = Integer.parseInt(progressNumStr);
                int intLevel = Integer.parseInt(levelStr); // 기본 레벨값

                if (progressNumStr != null) {
                    try {
                        // progressNum 값을 정수로 변환
                        progressNumber = Integer.parseInt(progressNumStr);
                    } catch (NumberFormatException e) {
                        // 숫자로 변환할 수 없는 경우 기본값 0으로 설정
                        progressNumber = 0;
                    }
                }

                if (levelStr != null) {
                    try {
                        // level 값을 정수로 변환
                        intLevel = Integer.parseInt(levelStr);
                    } catch (NumberFormatException e) {
                        // 숫자로 변환할 수 없는 경우 기본값 1로 설정
                        intLevel = 1;
                    }
                }

                // progressNum 값을 5씩 증가
                if(intLevel < 4) {
                    progressNumber += 5;

                    if (progressNumber >= 100) {
                        progressNumber = 0; // progressNum이 100이 되면 0으로 리셋

                        if (intLevel < 4) {
                            intLevel++; // 레벨이 4 미만일 때만 증가
                        }
                        if (intLevel == 4) {
                            // Toast.makeText( "축하드립니다 최종 레벨에 도달하셨습니다", Toast.LENGTH_SHORT).show();
                            Log.d("adapter level", "축하드립니다 최종 레벨에 도달하셨습니다");
                        }
                    }
                }

                // Firebase에 업데이트
                Map<String, Object> updates = new HashMap<>();
                updates.put("progressbar", String.valueOf(progressNumber));
                updates.put("level", String.valueOf(intLevel));

                databaseReference.updateChildren(updates)
                        .addOnSuccessListener(aVoid -> {
                            // 업데이트 성공 시 로그
                            Log.d("progress", "버튼 확인 +5");
                        })
                        .addOnFailureListener(e -> {
                            // 업데이트 실패 시 로그
                            Log.w("progress", "Failed to update progressbar and level", e);
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // 데이터 읽기 오류 처리
                Log.w("progress", "Failed to read progressbar value.", databaseError.toException());
            }
        });
    }

    private void decreaseProgressbar() {
        databaseReference = FirebaseDatabase.getInstance().getReference("memberInfo").child("cyniaa@naver,쵸com");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String progressNumStr = dataSnapshot.child("progressbar").getValue(String.class);
                String levelStr = dataSnapshot.child("level").getValue(String.class);

                int progressNumber = 0;
                int intLevel = 1;

                if (progressNumStr != null) {
                    try {
                        progressNumber = Integer.parseInt(progressNumStr);
                    } catch (NumberFormatException e) {
                        progressNumber = 0;
                    }
                }

                if (levelStr != null) {
                    try {
                        intLevel = Integer.parseInt(levelStr);
                    } catch (NumberFormatException e) {
                        intLevel = 1;
                    }
                }

                if (progressNumber > 0) {
                    progressNumber -= 5;
                }

                if (progressNumber < 0) {
                    progressNumber = 0;
                }

                Map<String, Object> updates = new HashMap<>();
                updates.put("progressbar", String.valueOf(progressNumber));
                updates.put("level", String.valueOf(intLevel));

                databaseReference.updateChildren(updates)
                        .addOnSuccessListener(aVoid -> Log.d("progress", "버튼 취소 -5"))
                        .addOnFailureListener(e -> Log.w("progress", "Failed to update progressbar and level", e));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("progress", "Failed to read progressbar value.", databaseError.toException());
            }
        });
    }

    @Override
    public int getItemCount() {
        return AdminImageList.size();
    }

    // 데이터 리스트 업데이트 메서드 추가
    public void updateImageList(List<AdminImageModel> newImageList) {
        this.AdminImageList = new ArrayList<>(newImageList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView datelist;
        TextView userName;
        TextView titleList;
        TextView adminCheck;
        ImageView imageView;
        CheckBox admin_checkbox;
        int clickCount = 0;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            datelist = itemView.findViewById(R.id.adminDate);
            userName = itemView.findViewById(R.id.adminUser);
            titleList = itemView.findViewById(R.id.adminTitle);
            adminCheck = itemView.findViewById(R.id.adminCheckCom);
            imageView = itemView.findViewById(R.id.memberPic);
            admin_checkbox = itemView.findViewById(R.id.adminCheckBox);

        }

    }
}
