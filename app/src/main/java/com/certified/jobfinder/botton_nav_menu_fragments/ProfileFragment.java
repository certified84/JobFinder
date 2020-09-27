package com.certified.jobfinder.botton_nav_menu_fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.certified.jobfinder.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private AlertDialog mAlertDialog;
    private AlertDialog.Builder mBuilder;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvName, tvEmail, tvPhone, tvLocation;
        tvName = view.findViewById(R.id.et_name);
        tvEmail = view.findViewById(R.id.et_email);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvLocation = view.findViewById(R.id.tv_location);

        mBuilder = new AlertDialog.Builder(getContext());

        CircleImageView profileImage = view.findViewById(R.id.profile_image);
        Glide.with(getContext())
                .load(R.drawable.google)
                .into(profileImage);

        Group nameGroup, phoneGroup, locationGroup, emailGroup;

        nameGroup = view.findViewById(R.id.group_business_name);
        nameGroup.setOnClickListener(this::onClick);

        phoneGroup = view.findViewById(R.id.group_business_phone);
        phoneGroup.setOnClickListener(this::onClick);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.group_business_email:
                initChangeEmailDialog();
                break;

            case R.id.group_business_name:
                initChangeNameDialog();
                break;

            case R.id.group_business_phone:
                initChangePhoneDialog();
                break;

            case R.id.group_business_location:
                initChangeLocationDialog();
                break;
        }
    }

    private void initChangeLocationDialog() {
        Log.d(TAG, "initChangeLocationDialog: Initializing change location dialog");

    }

    private void initChangePhoneDialog() {
        Log.d(TAG, "initChangePhoneDialog: Initializing change phone dialog");

        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_change_phone, null);

        mAlertDialog = mBuilder.create();
        mAlertDialog.setView(v);

        TextView cancel = v.findViewById(R.id.tv_cancel);
        TextView save = v.findViewById(R.id.tv_save_phone_no);

        cancel.setOnClickListener(view -> mAlertDialog.dismiss());
        save.setOnClickListener(view -> savePhone());

        mAlertDialog.show();
    }

    private void initChangeNameDialog() {
        Log.d(TAG, "initChangeNameDialog: Initializing change name dialog");

        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_change_name, null);

        mAlertDialog = mBuilder.create();
        mAlertDialog.setView(v);

        TextView cancel = v.findViewById(R.id.tv_cancel);
        TextView save = v.findViewById(R.id.tv_save);
        ProgressBar progressBar = v.findViewById(R.id.progressBar);

        cancel.setOnClickListener(view -> mAlertDialog.dismiss());
        save.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
//            saveName();
        });

        mAlertDialog.show();
    }

    private void initChangeEmailDialog() {
        Log.d(TAG, "initChangeEmailDialog: Initializing change email dialog");

        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_change_name, null);

        mAlertDialog = mBuilder.create();
        mAlertDialog.setView(v);

        TextView cancel = v.findViewById(R.id.tv_cancel);
        TextView save = v.findViewById(R.id.tv_save);

        cancel.setOnClickListener(view -> mAlertDialog.dismiss());
        save.setOnClickListener(view -> saveEmail());

        mAlertDialog.show();
    }

    private void saveName() {
        EditText etName = getActivity().findViewById(R.id.et_change_name);
        String name = etName.getText().toString();
        mAlertDialog.dismiss();
    }

    private void saveEmail() {
        mAlertDialog.dismiss();
    }

    private void savePhone() {
        mAlertDialog.dismiss();
    }

    private void saveLocation() {
        mAlertDialog.dismiss();
    }
}