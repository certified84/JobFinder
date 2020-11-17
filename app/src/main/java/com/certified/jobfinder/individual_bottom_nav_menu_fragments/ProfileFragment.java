package com.certified.jobfinder.individual_bottom_nav_menu_fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.certified.jobfinder.R;
import com.certified.jobfinder.StartActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "ProfileFragment";

//    widgets
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
        tvLogout.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), StartActivity.class));
                getActivity().finish();
        }
    }
}