package com.certified.jobfinder.individual_bottom_nav_menu_fragments;

import android.content.Intent;
import android.content.SharedPreferences;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.work.PeriodicWorkRequest;

import com.bumptech.glide.Glide;
import com.certified.jobfinder.MyWorker;
import com.certified.jobfinder.R;
import com.certified.jobfinder.SettingsActivity;
import com.certified.jobfinder.StartActivity;
import com.certified.jobfinder.util.PreferenceKeys;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ProfileFragment";

    //    widgets
    private Group groupEditProfile, groupMyLocation, groupUploadCV, groupHelpCenter, groupAboutUs, groupLogOut;
    private ImageView ivShare, ivSettings;
    private TextView tvDisplayName, tvProfile, tvLocation, tvUploadCV, tvHelp, tvAbout, tvLogout;

    private NavController mNavController;

    private FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private PeriodicWorkRequest request;
    private CircleImageView ivProfileImage;

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

        mNavController = Navigation.findNavController(getActivity(), R.id.individual_host_fragment);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        groupEditProfile.setOnClickListener(this);
        groupMyLocation.setOnClickListener(this);
        groupUploadCV.setOnClickListener(this);
        groupHelpCenter.setOnClickListener(this);
        groupAboutUs.setOnClickListener(this);
        groupLogOut.setOnClickListener(this);

        ivProfileImage.setOnClickListener(this);
        ivShare.setOnClickListener(this);
        ivSettings.setOnClickListener(this);

        request = new PeriodicWorkRequest.Builder(MyWorker.class, 15, TimeUnit.MINUTES).build();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String displayName = preferences.getString(PreferenceKeys.NAME, "");
        Glide.with(getContext())
                .load(R.drawable.logo)
                .into(ivProfileImage);
        tvDisplayName.setText(displayName);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.group_log_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getContext(), StartActivity.class));
            getActivity().finish();
        } else if (id == R.id.group_edit_profile) {
            mNavController.navigate(R.id.editProfileFragment);
        } else if (id == R.id.group_my_location) {
//            Do something here
        } else if (id == R.id.group_upload_cv) {

        } else if (id == R.id.group_help_center) {

        } else if (id == R.id.group_about_us) {

        } else if (id == R.id.iv_settings) {
            startActivity(new Intent(getContext(), SettingsActivity.class));
//            int nightMode = AppCompatDelegate.getDefaultNightMode();
//            //Set the theme mode for the restarted activity
//            if (nightMode == AppCompatDelegate.MODE_NIGHT_NO) {
//                AppCompatDelegate.setDefaultNightMode
//                        (AppCompatDelegate.MODE_NIGHT_YES);
//            }
//            getActivity().recreate();
        } else if (id == R.id.iv_share) {
//            WorkManager.getInstance(getContext()).cancelAllWork();
        }
    }
}