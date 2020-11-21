package com.certified.jobfinder.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.certified.jobfinder.R;
import com.certified.jobfinder.model.JobSeeker;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class JobSeekersAdapter extends RecyclerView.Adapter<JobSeekersAdapter.JobSeekersViewHolder> {

    private static final String TAG = "JobSeekersAdapter";
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<JobSeeker> jobSeekersList;

    public JobSeekersAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    private void setJobsList(List<JobSeeker> jobSeekersList) {
        this.jobSeekersList = jobSeekersList;
    }

    @NonNull
    @Override
    public JobSeekersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.job_seekers_list, parent, false);
        CollectionReference jobRef = FirebaseFirestore.getInstance()
                .collection(String.valueOf(R.string.dbnode_jobs));
        jobRef.getId();
        return new JobSeekersViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull JobSeekersViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called");

//        Job jobs = jobsList.get(position);
//        String jobTitle = jobs.getJobTitle();
//        String businessName = jobs.getBusinessName();
//        String location = jobs.getLocation();
//        Uri businessProfileUrl = jobs.getProfileImageUrl();

        Glide.with(mContext)
                .load(R.drawable.logo)
                .into(holder.ivJobSeekerProfileImage);
        holder.tvName.setText("Samson Achiaga");
        holder.tvRole.setText("Android Developer");
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class JobSeekersViewHolder extends RecyclerView.ViewHolder {
        private CardView jobSeekerDetails;
        private ImageView ivJobSeekerProfileImage;
        private TextView tvName, tvRole, tvLocation;

        public JobSeekersViewHolder(@NonNull View itemView) {
            super(itemView);
            jobSeekerDetails = itemView.findViewById(R.id.job_seeker_card_view);
            ivJobSeekerProfileImage = itemView.findViewById(R.id.business_profile_image);
            tvRole = itemView.findViewById(R.id.tv_role);
            tvName = itemView.findViewById(R.id.tv_job_seeker_name);
            tvLocation = itemView.findViewById(R.id.tv_location);
        }
    }
}
