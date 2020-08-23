package com.certified.jobfinder;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;

public class RegisterFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "RegisterFragment";
    private NavController mNavController;

    //    widgets
    private EditText mEmail, mPassword, mConfirmPassword, mName, mPhoneNo;
    private TextView mLogin;
    private Button mRegister;
    private ProgressBar mProgressBar;
    private ImageView mBack, facebook, google, twitter;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register2, container, false);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        mEmail = view.findViewById(R.id.et_email);
        mPassword = view.findViewById(R.id.et_password);
        mConfirmPassword = view.findViewById(R.id.et_confirm_password);
        mName = view.findViewById(R.id.et_name);
        mPhoneNo = view.findViewById(R.id.et_phone_no);
        mProgressBar = view.findViewById(R.id.progressBar);
        mRegister = view.findViewById(R.id.btn_register);
        mLogin = view.findViewById(R.id.tv_login);
        mBack = view.findViewById(R.id.back);
        facebook = view.findViewById(R.id.facebook);
        google = view.findViewById(R.id.google);
        twitter = view.findViewById(R.id.twitter);

        mBack.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        facebook.setOnClickListener(this);
        google.setOnClickListener(this);
        twitter.setOnClickListener(this);

        mNavController = Navigation.findNavController(view);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register:
                Log.d(TAG, "onClick: button register clicked");
                registrationCheck();
                break;

            case R.id.tv_login:
                Log.d(TAG, "onClick: button login clicked");
                mNavController.navigate(R.id.action_registerFragment_to_loginFragment);
                break;

            case R.id.back:
                Log.d(TAG, "onClick: back clicked");
                mNavController.navigate(R.id.action_registerFragment_to_startFragment);
//                NavOptions navOptions = new NavOptions().Builder().setPopUpTo(R.id.startFragment, true).build();
                break;

            case R.id.google:

            case R.id.facebook:

            case R.id.twitter:

            case R.id.btn_facebook:

            case R.id.btn_google:

            case R.id.btn_twitter:
//                Toast.makeText(this, "This feature is not available yet. " +
//                        "Kindly check back later", Toast.LENGTH_LONG).show();
                Snackbar.make(v, "This feature isn't available yet. " +
                        "Kindly check back later", Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    private void registrationCheck() {

//        check for null valued EditText fields
        if(!isEmpty(mEmail.getText().toString())
                && !isEmpty(mPassword.getText().toString())
                && !isEmpty(mConfirmPassword.getText().toString())
                && !isEmpty(mName.getText().toString())
                && !isEmpty(mPhoneNo.getText().toString())) {

//            check if user has a company email address
            if(isValidDomain(mEmail.getText().toString())) {

//                check if passwords match
                if(mPassword.getText().toString().length() >= 6) {
                    if (doStringsMatch(mPassword.getText().toString(), mConfirmPassword.getText().toString())) {
                        registerNewEmail(mEmail.getText().toString(), mPassword.getText().toString());
                    } else {
                        Toast.makeText(getContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                        mConfirmPassword.requestFocus();
                    }
                } else {
                    Toast.makeText(getContext(), "Password shouldn't be less than 6 characters", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(getContext(), "Please register with company email", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(getContext(), "You must fill out all fields", Toast.LENGTH_LONG).show();
//            mEmail.requestFocus();
        }
    }

    private void registerNewEmail(String email, String password) {
        mProgressBar.setVisibility(View.VISIBLE);

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "onComplete: onComplete: " + task.isSuccessful());

                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance()
                                    .getCurrentUser().getUid());

//                            Send the newly created user a verification mail
                            sendVerificationEmail();

//                            Sign out the user
                            FirebaseAuth.getInstance().signOut();

//                            Redirect the user to the login screen
                            Log.d(TAG, "onComplete: redirecting to login screen");
                            redirectToLoginScreen();
                        }
                        else {
//                            Toast.makeText(getContext(), "Unable to register",
//                                    Toast.LENGTH_LONG).show();
                            if(task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(getContext(), "You are already registered", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Unable to register: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void redirectToLoginScreen() {
        Log.d(TAG, "redirectToLoginScreen: called");
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
//        checkAuthenticationState();
    }

    private void checkAuthenticationState() {
        Log.d(TAG, "checkAuthenticationState: checking authentication state");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(TAG, "checkAuthenticationState: User is null. Navigating back to home screen");

            Intent intent = new Intent(getContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Log.d(TAG, "checkAuthenticationState: User is authenticated");
        }
    }
}