package com.certified.jobfinder.individual_bottom_nav_menu_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.certified.jobfinder.R;
import com.certified.jobfinder.SettingsActivity;
import com.certified.jobfinder.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ProfileFragment";

//    widgets
    private Group groupEditProfile, groupMyLocation, groupUploadCV, groupHelpCenter, groupAboutUs, groupLogOut;
    private ImageView ivProfileImage, ivShare, ivSettings;
    private TextView tvDisplayName, tvProfile, tvLocation, tvUploadCV, tvHelp, tvAbout, tvLogout;

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tvDisplayName = view.findViewById(R.id.tv_display_name);
        tvProfile = view.findViewById(R.id.tv_profile);
        tvLocation = view.findViewById(R.id.tv_location);
        tvUploadCV = view.findViewById(R.id.tv_upload_cv);
        tvHelp = view.findViewById(R.id.tv_help);
        tvAbout = view.findViewById(R.id.tv_about);
        tvLogout = view.findViewById(R.id.tv_log_out);

        ivProfileImage = view.findViewById(R.id.iv_profile_image);
        ivShare = view.findViewById(R.id.iv_share);
        ivSettings = view.findViewById(R.id.iv_settings);

        groupEditProfile = view.findViewById(R.id.group_edit_profile);
        groupMyLocation = view.findViewById(R.id.group_my_location);
        groupUploadCV = view.findViewById(R.id.group_upload_cv);
        groupHelpCenter = view.findViewById(R.id.group_help_center);
        groupAboutUs = view.findViewById(R.id.group_about_us);
        groupLogOut = view.findViewById(R.id.group_log_out);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CircleImageView profileImage = view.findViewById(R.id.iv_profile_image);
        Glide.with(getContext())
                .load(R.drawable.logo)
                .into(profileImage);

        tvDisplayName.setText(mUser.getDisplayName());

        groupEditProfile.setOnClickListener(this);
        groupMyLocation.setOnClickListener(this);
        groupUploadCV.setOnClickListener(this);
        groupHelpCenter.setOnClickListener(this);
        groupAboutUs.setOnClickListener(this);
        groupLogOut.setOnClickListener(this);

        ivProfileImage.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.group_log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), StartActivity.class));
                getActivity().finish();

            case R.id.group_edit_profile:
                break;

            case R.id.group_my_location:
                break;

            case R.id.group_upload_cv:
                break;

            case R.id.group_help_center:
                break;

            case R.id.group_about_us:
                break;

            case R.id.iv_settings:
                startActivity(new Intent(getContext(), SettingsActivity.class));
                break;

            case R.id.iv_share:
                break;
        }
    }
}