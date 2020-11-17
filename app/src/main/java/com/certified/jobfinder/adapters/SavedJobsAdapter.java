package com.certified.jobfinder.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.certified.jobfinder.BusinessActivity;
import com.certified.jobfinder.BusinessProfileActivity;
import com.certified.jobfinder.R;
import com.certified.jobfinder.model.SavedJob;
import com.certified.jobfinder.util.IntentExtra;
import com.certified.jobfinder.util.PreferenceKeys;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.HashSet;
import java.util.Set;

public class SavedJobsAdapter extends FirestoreRecyclerAdapter<SavedJob, SavedJobsAdapter.SavedJobsViewHolder> {

    private static final String TAG = "SavedJobsAdapter";
    public static Context mContext;

    //    Firebase
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference savedJobRef;

    public SavedJobsAdapter(@NonNull FirestoreRecyclerOptions<SavedJob> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull SavedJobsViewHolder holder, int position, @NonNull SavedJob model) {
        Log.d(TAG, "onBindViewHolder: called");

        Glide.with(mContext)
                .load(R.drawable.logo)
                .into(holder.ivBusinessProfileImage);
        holder.tvJobTitle.setText(model.getJob_title());
        holder.tvBusinessName.setText(model.getBusiness_name() + " --- " + model.getLocation());
        holder.tvDescription.setText("Description: " + model.getDescription());
        holder.likeButton.setLiked(true);
        holder.likeButton.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                Log.d(TAG, "unliked: " + model.getOriginal_job_id());
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
                SharedPreferences.Editor editor = preferences.edit();

                Set<String> savedJobs = preferences.getStringSet(PreferenceKeys.SAVED_JOBS, new HashSet<>());
                savedJobs.remove(model.getOriginal_job_id());

                editor.putStringSet(PreferenceKeys.SAVED_JOBS, savedJobs);
                Log.d(TAG, "unliked: unsaved: " + model.getOriginal_job_id());
                editor.apply();

                savedJobRef = getSnapshots().getSnapshot(position).getReference();
                savedJobRef.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "unLiked: Job unsaved: " + savedJobRef.getId());
                                Toast.makeText(mContext, "Job unsaved", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "unLiked: Unable to un save job");
                                Toast.makeText(mContext, "An error occurred: " + task.getException(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        holder.jobDetails.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, BusinessActivity.class);
            mContext.startActivity(intent);
        });

        holder.ivBusinessProfileImage.setOnClickListener(view -> {
            Intent intent = new Intent(mContext, BusinessProfileActivity.class);
            intent.putExtra(IntentExtra.BUSINESS_PROFILE_IMAGE, model.getBusiness_profile_image_url());
            intent.putExtra(IntentExtra.BUSINESS_NAME, model.getBusiness_name());
            intent.putExtra(IntentExtra.BUSINESS_EMAIL, model.getBusiness_email());
            intent.putExtra(IntentExtra.BUSINESS_PHONE, model.getBusiness_phone());
            intent.putExtra(IntentExtra.BUSINESS_LOCATION, model.getLocation());
            mContext.startActivity(intent);
        });
    }

    public void deleteJob(int position) {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    @NonNull
    @Override
    public SavedJobsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(mContext).inflate(R.layout.saved_jobs_list, parent, false);
        Log.d(TAG, "onCreateViewHolder: Created");

        return new SavedJobsViewHolder(view);
    }

    public static class SavedJobsViewHolder extends RecyclerView.ViewHolder {
        private CardView jobDetails;
        private ImageView ivBusinessProfileImage;
        private TextView tvJobTitle, tvBusinessName, tvDescription;
        public LikeButton likeButton;

        public SavedJobsViewHolder(@NonNull View itemView) {
            super(itemView);
            jobDetails = itemView.findViewById(R.id.job_card_view);
            ivBusinessProfileImage = itemView.findViewById(R.id.business_profile_image);
            tvBusinessName = itemView.findViewById(R.id.tv_business_name_location);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvDescription = itemView.findViewById(R.id.tv_description);
            likeButton = itemView.findViewById(R.id.likeButton);
        }
    }
}
