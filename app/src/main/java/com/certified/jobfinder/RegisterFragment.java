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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "RegisterFragment";
    private NavController mNavController;

    //    widgets
    private EditText mEmail, mPassword, mConfirmPassword, mName, mPhoneNo;
    private TextView mLogin;
    private Button mRegister;
    private ProgressBar mProgressBar;
    private ImageView mBack;
    private Spinner mSpinner;

    public RegisterFragment() {
        // Required empty public constructor
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
                    mName.setHint(getString(R.string.business_name));
                } else if (mSpinner.getSelectedItem().toString().equals(getString(R.string.individual))) {
                    mName.setHint(getString(R.string.name));
                } else {
                    mName.setHint("Name");
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

                        registerNewUser(mEmail.getText().toString(), mPassword.getText().toString());

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

    private void registerNewUser(String email, String password) {
        mProgressBar.setVisibility(View.VISIBLE);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: Created new user with: " + email);

//                            Send the newly created user a verification mail
                        sendVerificationEmail();

//                            Save new user info in firestore
                        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        DocumentReference userRef = db.collection("users").document(mUser.getUid());

                        User user = new User();
                        user.setName(mName.getText().toString());
                        user.setPhone(mPhoneNo.getText().toString());
                        user.setEmail(mEmail.getText().toString());
                        user.setAccount_type(mSpinner.getSelectedItem().toString());
                        user.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        user.setLocation("");
                        user.setProfile_image(null);

                        userRef.set(user)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {

//                                    Update default firestore user data
                                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(user.getName())
                                                .setPhotoUri(user.getProfile_image())
                                                .build();
                                        mUser.updateProfile(profileChangeRequest);

//                                   Sign out the user
                                        FirebaseAuth.getInstance().signOut();

//                                   Redirect the user to the login screen
                                        Log.d(TAG, "onComplete: redirecting to login screen");
                                        redirectToLoginScreen();
                                    } else {
                                        Log.d(TAG, "onComplete: User Detail upload failed");
                                        Toast.makeText(getContext(), "Unable to save detail", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                            redirectToLoginScreen();
                        } else {
                            Toast.makeText(getContext(), "Unable to register: "
                                    + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    mProgressBar.setVisibility(View.GONE);
                });
    }

    private void redirectToLoginScreen() {
        Log.d(TAG, "redirectToLoginScreen: redirecting");
        mNavController.navigate(R.id.action_registerFragment_to_loginFragment);
    }

    private void sendVerificationEmail() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getContext(), "Verification email sent",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), "Couldn't send verification " +
                                "email. Try again", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private boolean isEmpty(String String) {
        return String.equals("");
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
    public void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState() {
        Log.d(TAG, "checkAuthenticationState: checking authentication state");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(TAG, "checkAuthenticationState: User is null");
        } else {
            queryDatabase();
        }
    }

    private void queryDatabase() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String accountType = preferences.getString(PreferenceKeys.ACCOUNT_TYPE, "");
        if (!isEmpty(accountType)) {
            if (accountType.equals("Business")) {
                navigateToBusinessActivity();
            } else if (accountType.equals("Individual")) {
                navigateToIndividualActivity();
            }
        } else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            if (user != null) {
                DocumentReference userRef = db.collection(getString(R.string.dbnode_users)).document(user.getUid());
                userRef.get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String accountType1 = documentSnapshot.getString("account_type");
                            if (accountType1.equals(getString(R.string.individual))) {
                                Log.d(TAG, "checkAuthenticationState: User is authenticated with an individual account");
                                navigateToIndividualActivity();
                            } else if (accountType1.equals(getString(R.string.business))) {
                                Log.d(TAG, "checkAuthenticationState: User is authenticated with a business account");
                                navigateToBusinessActivity();
                            }
                        });
            }
        }
    }

    public void navigateToIndividualActivity() {
        Intent intent = new Intent(getContext(), IndividualActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void navigateToBusinessActivity() {
        Intent intent = new Intent(getContext(), BusinessActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}