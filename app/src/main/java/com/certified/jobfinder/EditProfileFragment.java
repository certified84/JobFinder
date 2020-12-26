package com.certified.jobfinder;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.text.TextUtils.isEmpty;
import static com.certified.jobfinder.util.PreferenceKeys.ACCOUNT_TYPE;
import static com.certified.jobfinder.util.PreferenceKeys.EMAIL;
import static com.certified.jobfinder.util.PreferenceKeys.LOCATION;
import static com.certified.jobfinder.util.PreferenceKeys.NAME;
import static com.certified.jobfinder.util.PreferenceKeys.PHONE;

public class EditProfileFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "EditProfileFragment";

    private FirebaseUser mUser;

    private TextView tvName, tvEmail, tvPhone, tvLocation;
    private Group groupEditName, groupEditEmail, groupEditPhone, groupEditLocation;
    private ImageView ivProfileImage;
//    private ImageButton btnBack;
    private MaterialAlertDialogBuilder mBuilder;
    private AlertDialog mAlertDialog;
    private FirebaseFirestore db;
    private DocumentReference userRef;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mBuilder = new MaterialAlertDialogBuilder(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        tvName = view.findViewById(R.id.tv_name);
        tvEmail = view.findViewById(R.id.tv_email);
        tvPhone = view.findViewById(R.id.tv_phone);
        tvLocation = view.findViewById(R.id.tv_location);

        groupEditName = view.findViewById(R.id.group_edit_name);
        groupEditEmail = view.findViewById(R.id.group_edit_email);
        groupEditPhone = view.findViewById(R.id.group_edit_phone_no);
        groupEditLocation = view.findViewById(R.id.group_edit_location);

        ivProfileImage = view.findViewById(R.id.iv_profile_image);
//        btnBack = view.findViewById(R.id.btn_back);

        groupEditName.setOnClickListener(this);
        groupEditEmail.setOnClickListener(this);
        groupEditPhone.setOnClickListener(this);
        groupEditLocation.setOnClickListener(this);

//        btnBack.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        getUserAccountDetails();
        setUserAccountDetails();
    }

    public void setUserAccountDetails() {

        String name = mPreferences.getString(NAME, "");
        String email = mPreferences.getString(EMAIL, "");
        String phone = mPreferences.getString(PHONE, "");
        String location = mPreferences.getString(LOCATION, "");

        tvName.setText(name);
        tvEmail.setText(email);
        tvPhone.setText(phone);
        tvLocation.setText(location);

        if (mUser.getPhotoUrl() == null) {
            Glide.with(getContext())
                    .load(R.drawable.logo)
                    .into(ivProfileImage);
        }
    }

    private void getUserAccountDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        userRef = db.collection(getString(R.string.dbnode_users)).document(user.getUid());
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
                            SharedPreferences.Editor editor = mPreferences.edit();

                            editor.putString(NAME, name);
                            editor.putString(EMAIL, email);
                            editor.putString(PHONE, phone);
                            editor.putString(LOCATION, location);

                            editor.apply();
                        }
                    }
                });

    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.group_edit_name) {
            initChangeNameDialog();
        } else if (id == R.id.group_edit_email) {
            initChangeEmailDialog();
        } else if (id == R.id.group_edit_phone_no) {
            initChangePhoneDialog();
        } else if (id == R.id.group_edit_location) {
            initChangeLocationDialog();
        } else if (id == R.id.btn_back) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.individual_host_fragment);
            navController.navigate(R.id.profileFragment);
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

        TextView title = v.findViewById(R.id.tv_change_phone_title);
        TextView cancel = v.findViewById(R.id.tv_cancel);
        TextView save = v.findViewById(R.id.tv_save_phone_no);
        EditText phone = v.findViewById(R.id.et_change_phone);
        TextInputLayout phoneLayout = v.findViewById(R.id.et_change_phone_layout);

        String accountType = mPreferences.getString(ACCOUNT_TYPE, "");
        Log.d(TAG, "initChangePhoneDialog: accountType = " + accountType);

        if (accountType.equals(getString(R.string.individual))) {
            phoneLayout.setHint(R.string.phone);
            title.setText(R.string.enter_phone);
        } else if (accountType.equals(getString(R.string.business))) {
            phoneLayout.setHint(R.string.business_phone);
            title.setText(R.string.enter_business_phone);
        }

        phone.setText(tvPhone.getText().toString());

        cancel.setOnClickListener(view -> dismissDialog());
        save.setOnClickListener(view -> {

            /*    Change phone task   */
            String currentPhone = mPreferences.getString(PHONE, "");

            if (!isEmpty(phone.getText().toString().trim())) {
                if (!phone.getText().toString().equals(currentPhone)) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection(getString(R.string.dbnode_users)).document(user.getUid());

                    String newPhone = phone.getText().toString();
                    userRef.update("phone", newPhone);

                    tvPhone.setText(newPhone);

                    mEditor.putString(PHONE, newPhone);
                    Toast.makeText(getContext(), "Phone changed", Toast.LENGTH_SHORT).show();
                    mEditor.apply();

                } else {
                    Toast.makeText(getContext(), "Phone not changed", Toast.LENGTH_SHORT).show();
                }
                dismissDialog();
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
        TextView title = v.findViewById(R.id.tv_change_name_title);
        TextView cancel = v.findViewById(R.id.tv_cancel);
        TextView save = v.findViewById(R.id.tv_save);
        TextInputLayout phoneLayout = v.findViewById(R.id.et_change_name_layout);

        String accountType = mPreferences.getString(ACCOUNT_TYPE, "");

        if (accountType.equals(getString(R.string.individual))) {
            phoneLayout.setHint(R.string.name);
            title.setText(R.string.enter_name);
        } else if (accountType.equals(getString(R.string.business))) {
            phoneLayout.setHint(R.string.business_name);
            title.setText(R.string.enter_business_name);
        }

        name.setText(tvName.getText().toString());

        cancel.setOnClickListener(view -> dismissDialog());
        save.setOnClickListener(view -> {

            /*    Change name task   */
            String currentName = mPreferences.getString(NAME, "");

            if (!isEmpty(name.getText().toString().trim())) {
                if (!name.getText().toString().equals(currentName)) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference userRef = db.collection(getString(R.string.dbnode_users)).document(user.getUid());

                    String newName = name.getText().toString();
                    userRef.update("name", newName);

                    tvName.setText(newName);
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.putString(NAME, newName);
                    Toast.makeText(getContext(), "Name changed", Toast.LENGTH_SHORT).show();
                    editor.apply();

//                    Update the user display name in firebase auth
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                            .setDisplayName(newName)
                            .build();
                    user.updateProfile(profileChangeRequest);

                } else {
                    Toast.makeText(getContext(), "Name not changed", Toast.LENGTH_SHORT).show();
                }
                dismissDialog();
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

        cancel.setOnClickListener(view -> dismissDialog());
        save.setOnClickListener(view -> dismissDialog());

//        mAlertDialog.show();
    }

    private void dismissDialog() {
        mAlertDialog.dismiss();
    }
}