package com.jica.project;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        public static final String POSITION_KEY = "position_key";
        TextView activityTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            activityTextView = itemView.findViewById(R.id.textView2);

            activityTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getPosition();
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setTitle("활동 인증") // 팝업창 제목
                            .setMessage("인증하시겠습니까?") // 팝업창 메시지
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
                                    Intent camera = new Intent(itemView.getContext(), RealCameraActivity.class);
                                    camera.putExtra(ActivityAdapter.ViewHolder.POSITION_KEY, position);
                                    itemView.getContext().startActivity(camera);

                                    /*Intent list = new Intent(itemView.getContext(), MyGalleryActivity.class);
                                    camera.putExtra(ActivityAdapter.ViewHolder.POSITION_KEY, position);
                                    // itemView.getContext().startActivity(list);*/
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show(); // 다이얼로그 표시
                }
            });
        }

    }
}
