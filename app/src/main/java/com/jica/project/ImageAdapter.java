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

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private List<ActivityModel> activityList;

    public ImageAdapter(List<ActivityModel> activityList) {
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_my_gallery, parent, false);
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
            activityTextView = itemView.findViewById(R.id.date);

        }

    }
}
