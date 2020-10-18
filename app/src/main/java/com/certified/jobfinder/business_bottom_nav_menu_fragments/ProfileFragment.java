package com.certified.jobfinder.business_bottom_nav_menu_fragments;

import androidx.appcompat.app.AlertDialog;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.certified.jobfinder.R;
import com.certified.jobfinder.model.User;
import com.certified.jobfinder.util.PreferenceKeys;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.text.TextUtils.isEmpty;
import static com.certified.jobfinder.util.PreferenceKeys.*;
import static com.certified.jobfinder.util.PreferenceKeys.EMAIL;
import static com.certified.jobfinder.util.PreferenceKeys.LOCATION;
import static com.certified.jobfinder.util.PreferenceKeys.NAME;
import static com.certified.jobfinder.util.PreferenceKeys.PHONE;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private AlertDialog mAlertDialog;
    private AlertDialog.Builder mBuilder;
    private TextView tvName;
    private TextView tvEmail;
    private TextView tvPhone;
    private TextView tvLocation;
    private TextView tvHintName;
    private Button btnUploadResume;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mBuilder = new AlertDialog.Builder(getContext());

        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvLocation = view.findViewById(R.id.tv_location);
        tvHintName = view.findViewById(R.id.tv_hint_name);
        btnUploadResume = view.findViewById(R.id.btn_upload_resume);

        isFirstOpen();
        getUserAccountDetails();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        CircleImageView profileImage = view.findViewById(R.id.profile_image);
        Glide.with(getContext())
                .load(R.drawable.icon_two)
                .into(profileImage);

        Group nameGroup, phoneGroup, locationGroup, emailGroup;

        nameGroup = view.findViewById(R.id.group_name);
        nameGroup.setOnClickListener(this::onClick);

        phoneGroup = view.findViewById(R.id.group_user_phone);
        phoneGroup.setOnClickListener(this::onClick);

        tvHintName.setText(R.string.business_name);
        btnUploadResume.setVisibility(View.GONE);
    }

    private void isFirstOpen() {
        Log.d(TAG, "isFirstLogin: checking if this is the first login");
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean isFirstOpen = preferences.getBoolean(FIRST_TIME_OPEN, true);

        if (isFirstOpen) {
            Log.d(TAG, "isFirstLogin: Launching alert dialog");
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getString(R.string.first_time_open_user_message));
            builder.setPositiveButton("Ok", (dialog, which) -> {
                Log.d(TAG, "onClick: closing AlertDialog");
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(FIRST_TIME_OPEN, false);
                editor.apply();
                setUserAccountDetails();
                dialog.dismiss();
            });
            builder.setIcon(R.drawable.logo_one);
            builder.setTitle(" ");

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }

    private void setUserAccountDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection(getString(R.string.dbnode_users)).document(user.getUid());
        userRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String name = document.getString("name");
                            String email = document.getString("email");
                            String phone = document.getString("phone");
                            String location = document.getString("location");
//                            Uri profileImageUrl = Uri.parse(document.getString("profile_image"));

                            tvName.setText(name);
                            tvEmail.setText(email);
                            tvPhone.setText(phone);
                            tvLocation.setText(location);

                            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            SharedPreferences.Editor editor = preferences.edit();

                            editor.putString(NAME, name);
                            editor.putString(EMAIL, email);
                            editor.putString(PHONE, phone);
                            editor.putString(LOCATION, location);

                            editor.apply();
                        }
                    }
                });
    }

    private void getUserAccountDetails() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            String name = user.getDisplayName();
            String email = user.getEmail();
            String phone = user.getPhoneNumber();
            Uri photoUrl = user.getPhotoUrl();

            Log.d(TAG, "getUserAccountDetails: Phone: " + phone);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String name = preferences.getString(NAME, "");
        String email = preferences.getString(EMAIL, "");
        String phone = preferences.getString(PHONE, "");
        String location = preferences.getString(LOCATION, "");

        tvName.setText(name);
        tvEmail.setText(email);
        tvPhone.setText(phone);
        tvLocation.setText(location);

        Log.d(TAG, "getUserAccountDetails: Name: " + name + "\n" +
                "Email: " + email + "\n" +
                "Phone: " + phone + "\n" +
                "Location: " + location + "\n");
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.group_user_email:
                initChangeEmailDialog();
                break;

            case R.id.group_name:
                initChangeNameDialog();
                break;

            case R.id.group_user_phone:
                initChangePhoneDialog();
                break;

            case R.id.group_user_location:
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

        ProgressBar progressBar = v.findViewById(R.id.progressBar);
        TextView cancel = v.findViewById(R.id.tv_cancel);
        TextView save = v.findViewById(R.id.tv_save_phone_no);
        EditText phone = v.findViewById(R.id.et_change_phone);

        phone.setText(tvPhone.getText().toString());
        cancel.setOnClickListener(view -> mAlertDialog.dismiss());
        save.setOnClickListener(view -> {

            /*    Change phone task   */
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            String currentPhone = preferences.getString(PHONE, "");

            if (!isEmpty(phone.getText().toString().trim())) {
                if (!phone.getText().toString().equals(currentPhone)) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection(getString(R.string.dbnode_users)).document(user.getUid());

                    String newPhone = phone.getText().toString();
                    userRef.update("phone", newPhone);

                    tvPhone.setText(newPhone);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(PHONE, newPhone);
                    Toast.makeText(getContext(), "Phone changed", Toast.LENGTH_SHORT).show();
                    editor.apply();

                    mAlertDialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Phone not changed", Toast.LENGTH_SHORT).show();
                    mAlertDialog.dismiss();
                }
            } else {
                Toast.makeText(getContext(), "Phone field is required", Toast.LENGTH_SHORT).show();
            }
        });
        mAlertDialog.show();
    }

    private void initChangeNameDialog() {
        Log.d(TAG, "initChangeNameDialog: Initializing change name dialog");

        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_change_name, null);

        mAlertDialog = mBuilder.create();
        mAlertDialog.setView(v);

        EditText name = v.findViewById(R.id.et_change_name);
        TextView cancel = v.findViewById(R.id.tv_cancel);
        TextView save = v.findViewById(R.id.tv_save);

        name.setText(tvName.getText().toString());

        cancel.setOnClickListener(view -> mAlertDialog.dismiss());
        save.setOnClickListener(view -> {

            /*    Change name task   */
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            String currentName = preferences.getString(NAME, "");

            if (!isEmpty(name.getText().toString().trim())) {
                if (!name.getText().toString().equals(currentName)) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection(getString(R.string.dbnode_users)).document(user.getUid());

                    String newName = name.getText().toString();
                    userRef.update("name", newName);

                    tvName.setText(newName);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(NAME, newName);
                    Toast.makeText(getContext(), "Name changed", Toast.LENGTH_SHORT).show();
                    editor.apply();

//                    Update the user display name in firebase auth
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newName)
                            .build();
                    user.updateProfile(profileChangeRequest);

                    mAlertDialog.dismiss();

                } else {
                    Toast.makeText(getContext(), "Name not changed", Toast.LENGTH_SHORT).show();
                    mAlertDialog.dismiss();
                }
            } else {
                Toast.makeText(getContext(), "Name field is required", Toast.LENGTH_SHORT).show();
            }
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