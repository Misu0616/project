package com.jica.project;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {
    private List<ActivityModel> activityList;

    public ActivityAdapter(List<ActivityModel> activityList) {
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycleview, parent, false);
        Log.d("ActivityAdapter", "View created: " + view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActivityModel activity = activityList.get(position);
        // holder.activityTextView.setText(activity.getActNumber());
        if (holder.activityTextView != null) {
            holder.activityTextView.setText(activity.getActNumber());
        } else {
            Log.e("ActivityAdapter", "activityTextView is null");
        }
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView activityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            activityTextView = itemView.findViewById(R.id.textView2);

            activityTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // position 순서 뜬다ㅏㅏ
                    Toast.makeText(itemView.getContext(),  "포지션이 뜨려나? " + getPosition(), Toast.LENGTH_SHORT).show();
                    
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setTitle("활동 인증") // 팝업창 제목
                            .setMessage("인증하시갰습니까?") // 팝업창 메시지
                            .setPositiveButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setNegativeButton("등록", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent camera = new Intent(itemView.getContext(), CameraActivity.class);
                                    itemView.getContext().startActivity(camera);
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show(); // 다이얼로그 표시
                }
            });
        }

    }
}
