package com.certified.jobfinder.adapters;import android.content.Context;import android.net.Uri;import android.util.Log;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.widget.ImageView;import android.widget.TextView;import androidx.annotation.NonNull;import androidx.cardview.widget.CardView;import androidx.recyclerview.widget.RecyclerView;import com.bumptech.glide.Glide;import com.certified.jobfinder.R;import com.like.LikeButton;public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.JobsViewHolder> {    private static final String TAG = "ViewPagerAdapter";    public Context mContext;    @NonNull    @Override    public JobsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {        mContext = parent.getContext();        View view = LayoutInflater.from(mContext).inflate(R.layout.jobs_list, parent, false);        Log.d(TAG, "onCreateViewHolder: view created");        return new ViewPagerAdapter.JobsViewHolder(view);    }    @Override    public void onBindViewHolder(@NonNull JobsViewHolder holder, int position) {        Log.d(TAG, "onBindViewHolder: called");        Glide.with(mContext)                .load(R.drawable.logo)                .into(holder.ivBusinessProfileImage);        holder.tvJobTitle.setText("Flutter Developer");        holder.tvBusinessName.setText("Certified Group --- Mountain view, Califonia.");        holder.tvDescription.setText("Description: Just have sense my guy.");    }    @Override    public int getItemCount() {        return 5;    }    public class JobsViewHolder extends RecyclerView.ViewHolder {        private CardView jobDetails;        private ImageView ivBusinessProfileImage;        private TextView tvJobTitle, tvBusinessName, tvDescription;        public LikeButton likeButton;        public JobsViewHolder(@NonNull View itemView) {            super(itemView);            jobDetails = itemView.findViewById(R.id.job_card_view);            ivBusinessProfileImage = itemView.findViewById(R.id.business_profile_image);            tvBusinessName = itemView.findViewById(R.id.tv_business_name_location);            tvJobTitle = itemView.findViewById(R.id.tv_job_title);            tvDescription = itemView.findViewById(R.id.tv_description);            likeButton = itemView.findViewById(R.id.likeButton);        }    }}