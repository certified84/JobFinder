package com.certified.jobfinder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.certified.jobfinder.util.IntentExtra;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.like.LikeButton;

public class JobDetailActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "JobDetailActivity";

    //    widgets
    private ImageButton btnBack, btnShare;
    private Button btnApplyForJob;
    private ImageView ivBusinessProfileImage;
    private TextView tvJobTitle, tvBusinessName, tvBusinessLocation;
    private Chip chipJobDescription, chipBusinessDetails, chipBusinessReview;
    private MaterialCardView likeCardView;
    private LikeButton mLikeButton;
    private NavController mNavController;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);

        btnBack = findViewById(R.id.btn_back);
        btnShare = findViewById(R.id.btn_share);
        likeCardView = findViewById(R.id.cardview_like_job);
        mLikeButton = findViewById(R.id.likeButton);
        btnApplyForJob = findViewById(R.id.btn_apply_for_job);
        ivBusinessProfileImage = findViewById(R.id.iv_business_profile_image);
        tvJobTitle = findViewById(R.id.tv_job_title);
        tvBusinessName = findViewById(R.id.tv_business_name);
        tvBusinessLocation = findViewById(R.id.tv_business_location);
        chipJobDescription = findViewById(R.id.chip_job_description);
        chipBusinessDetails = findViewById(R.id.chip_business_details);
        chipBusinessReview = findViewById(R.id.chip_business_review);

        chipJobDescription.setOnClickListener(this);
        chipBusinessDetails.setOnClickListener(this);
        chipBusinessReview.setOnClickListener(this);

        btnShare.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        likeCardView.setOnClickListener(this);
        btnApplyForJob.setOnClickListener(this);

        intent = getIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();

        chipJobDescription.setChecked(true);
        init();
        loadJobDetails();
    }

    private void init() {
        mNavController = Navigation.findNavController(this, R.id.description_host_fragment);
        if (chipJobDescription.isChecked()) {
            String jobDescription = intent.getStringExtra(IntentExtra.JOB_DESCRIPTION);
//            if (jobDescription.length() < 50) {
//                jobDescription = "No qualifications found";
//            }
            Bundle bundle = new Bundle();
            bundle.putString(IntentExtra.JOB_DESCRIPTION, jobDescription);
            mNavController.navigate(R.id.jobDescriptionFragment, bundle, null);
        } else if (chipBusinessDetails.isChecked()) {
            mNavController.navigate(R.id.jobDescriptionFragment);
        } else if (chipBusinessReview.isChecked()) {
            mNavController.navigate(R.id.jobDescriptionFragment);
        }
    }

    public void loadJobDetails() {
        String businessProfileImageUrl = intent.getStringExtra(IntentExtra.BUSINESS_PROFILE_IMAGE);
        String jobTitle = intent.getStringExtra(IntentExtra.JOB_TITLE);
        String businessName = intent.getStringExtra(IntentExtra.BUSINESS_NAME);
        String businessLocation = intent.getStringExtra(IntentExtra.JOB_LOCATION);
        boolean isSaved = intent.getBooleanExtra(String.valueOf(IntentExtra.JOB_IS_SAVED), false);

        if (businessProfileImageUrl != null) {
            Glide.with(this)
                    .load(businessProfileImageUrl)
                    .into(ivBusinessProfileImage);
        } else {
            Glide.with(this)
                    .load(R.drawable.logo)
                    .into(ivBusinessProfileImage);
        }

        if (isSaved) {
            mLikeButton.setLiked(true);
        }

        tvJobTitle.setText(jobTitle);
        tvBusinessName.setText(businessName);
        tvBusinessLocation.setText(businessLocation);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.btn_share) {
            Toast.makeText(this, "You clicked share", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btn_back) {
            super.onBackPressed();
            finish();
        } else if (id == R.id.btn_apply_for_job) {
            Toast.makeText(this, "You clicked apply", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.cardview_like_job) {
            Toast.makeText(this, "You liked this job", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.chip_job_description) {
            Bundle bundle = new Bundle();
            bundle.putString(IntentExtra.JOB_DESCRIPTION, intent.getStringExtra(IntentExtra.JOB_DESCRIPTION));
            mNavController.navigate(R.id.jobDescriptionFragment, bundle, null);
        } else if (id == R.id.chip_business_details) {
            mNavController.navigate(R.id.businessDescriptionFragment, null, null);
        } else if (id == R.id.chip_business_review) {
            mNavController.navigate(R.id.businessReviewFragment, null, null);
        }
    }
}