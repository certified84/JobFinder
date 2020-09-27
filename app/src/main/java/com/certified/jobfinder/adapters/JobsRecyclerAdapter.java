package com.certified.jobfinder.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.certified.jobfinder.model.Job;
import com.certified.jobfinder.R;

import java.util.List;

public class JobsRecyclerAdapter extends RecyclerView.Adapter<JobsRecyclerAdapter.JobsViewHolder> {

    private static final String TAG = "JobsRecyclerAdapter";
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Job> jobsList;

    public JobsRecyclerAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    private void setJobsList(List<Job> jobsList) {
        this.jobsList = jobsList;
    }

    @NonNull
    @Override
    public JobsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.jobs_list, parent, false);
        return new JobsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull JobsViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

//        Job jobs = jobsList.get(position);
//        String jobTitle = jobs.getJobTitle();
//        String businessName = jobs.getBusinessName();
//        String location = jobs.getLocation();
//        Uri businessProfileUrl = jobs.getProfileImageUrl();

        Glide.with(mContext)
                .load(R.drawable.google)
                .into(holder.ivBusinessProfileImage);
        holder.tvJobTitle.setText("Android Developer");
        holder.tvBusinessName.setText("Certified Group");
        holder.tvLocation.setText("Business district, Abuja");
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class JobsViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivBusinessProfileImage;
        private TextView tvJobTitle, tvBusinessName, tvLocation;

        public JobsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBusinessProfileImage = itemView.findViewById(R.id.business_profile_image);
            tvBusinessName = itemView.findViewById(R.id.tv_business_name);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvLocation = itemView.findViewById(R.id.tv_location);
        }
    }
}
