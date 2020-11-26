package com.certified.jobfinder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;

import com.certified.jobfinder.model.User;
import com.certified.jobfinder.util.PreferenceKeys;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import static android.text.TextUtils.isEmpty;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "RegisterFragment";
    private NavController mNavController;

    private StartActivityViewModel mViewModel;

//    Firebase
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;

    //    widgets
    private TextInputEditText mEmail, mPassword, mConfirmPassword, mName, mPhoneNo;
    private TextView mLogin;
    private Button mRegister;
    private ProgressBar mProgressBar;
    private ImageView mBack;
    private Spinner mSpinner;
    private TextInputLayout etNameLayout;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Login fragment created");

        mViewModel = new StartActivityViewModel(getActivity().getApplication());
        setupFirebaseAuth();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSpinner = view.findViewById(R.id.spinner);
        mEmail = view.findViewById(R.id.et_email);
        mPassword = view.findViewById(R.id.et_password);
        mConfirmPassword = view.findViewById(R.id.et_confirm_password);
        mName = view.findViewById(R.id.et_name);
        mPhoneNo = view.findViewById(R.id.et_phone_no);
        mProgressBar = view.findViewById(R.id.progressBar);
        mRegister = view.findViewById(R.id.btn_register);
        mLogin = view.findViewById(R.id.tv_login);
        mBack = view.findViewById(R.id.btn_back);
        mSpinner = view.findViewById(R.id.spinner);
        etNameLayout = view.findViewById(R.id.et_name_layout);

        mBack.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mLogin.setOnClickListener(this);

        mNavController = Navigation.findNavController(view);

        ArrayAdapter<String> level = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item);
        level.add("Choose account type");
        level.add("Business");
        level.add("Individual");
        level.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(level);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (mSpinner.getSelectedItem().toString().equals(getString(R.string.business))) {
                    etNameLayout.setHint(getString(R.string.business_name));
                } else if (mSpinner.getSelectedItem().toString().equals(getString(R.string.individual))) {
                    etNameLayout.setHint(getString(R.string.name));
                } else {
                    etNameLayout.setHint("Name");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                registrationCheck();
                break;

            case R.id.tv_login:
                Log.d(TAG, "onClick: button login clicked");
                mNavController.navigate(R.id.action_registerFragment_to_loginFragment);
                break;

            case R.id.btn_back:
                Log.d(TAG, "onClick: back clicked");
                mNavController.navigate(R.id.action_registerFragment_to_onboardingFragment);
//                NavOptions navOptions = new NavOptions().Builder().setPopUpTo(R.id.startFragment, true).build();
                break;
        }
    }

    private void registrationCheck() {

//        check for null valued EditText fields
        if (!isEmpty(mEmail.getText().toString())
                && !isEmpty(mPassword.getText().toString())
                && !isEmpty(mConfirmPassword.getText().toString())
                && !isEmpty(mName.getText().toString())
                && !isEmpty(mPhoneNo.getText().toString())) {

//                check if password length is greater than 6
            if (mPassword.getText().toString().length() >= 6) {

//                    check if password match
                if (doStringsMatch(mPassword.getText().toString(), mConfirmPassword.getText().toString())) {

                    if (!mSpinner.getSelectedItem().toString().equals("Choose account type")) {

                        mViewModel.registerNewUser(
                                getContext(),
                                mUser,
                                mEmail.getText().toString().trim(),
                                mPassword.getText().toString().trim(),
                                mProgressBar,
                                mName.getText().toString().trim(),
                                mPhoneNo.getText().toString().trim(),
                                mSpinner,
                                mNavController);

                    } else {
                        Toast.makeText(getContext(), "Choose an account type", Toast.LENGTH_SHORT).show();
                        mSpinner.requestFocus();
                    }

                } else {
                    Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                    mConfirmPassword.requestFocus();
                }
            } else {
                Toast.makeText(getContext(), "Password shouldn't be less than 6 characters", Toast.LENGTH_SHORT).show();
                mPassword.requestFocus();
            }
        } else {
            Toast.makeText(getContext(), "You must fill out all fields", Toast.LENGTH_LONG).show();
        }
    }

    private boolean doStringsMatch(String s1, String s2) {
        return s1.equals(s2);
    }

    private boolean isValidDomain(String email) {
        Log.d(TAG, "isValidDomain: verifying email has correct domain: " + email);
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();
        Log.d(TAG, "isValidDomain: users domail: " + domain);
//        return domain.equals(DOMAIN_NAME);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUser != null) {
            mViewModel.checkIfUserIsVerified(getContext(), mUser);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }

    /*
    -------------------------------- Firebase Setup -------------------------
    */
    public void setupFirebaseAuth() {
        mAuthStateListener = firebaseAuth -> {
            mUser = firebaseAuth.getCurrentUser();
            if (mUser != null) {
                Log.d(TAG, "onAuthStateChanged: signed in" + mUser.getUid());
                mViewModel.checkIfUserIsVerified(getContext(), mUser);
            } else {
                Log.d(TAG, "onAuthStateChanged: signed out");
            }
        };
    }
}