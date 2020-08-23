package com.certified.jobfinder;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = "LoginFragment";
    public NavController mNavController;

    //    firebase
    public FirebaseAuth.AuthStateListener mAuthStateListener;

    //    widgets
    public TextView mForgotPassword, mResendEmail, mRegister;
    public EditText mEmail, mPassword;
    public ProgressBar mProgressBar;
    public Button mLogin;
    public ImageView mBack;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Login fragment created");

        setupFirebaseAuth();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_login, container, false);

        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEmail =  view.findViewById(R.id.et_email);
        mPassword = view.findViewById(R.id.et_password);
        mProgressBar = view.findViewById(R.id.progressBar);
        mLogin = view.findViewById(R.id.btn_login);
        mForgotPassword = view.findViewById(R.id.tv_password_reset);
        mRegister = view.findViewById(R.id.tv_register);
        mBack = view.findViewById(R.id.back);

        mNavController = Navigation.findNavController(view);

        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (!isEmpty(mEmail.getText().toString()) && !isEmpty(mPassword.getText().toString())) {

                    Log.d(TAG, "onClick: Attempting to authenticate");
                    mProgressBar.setVisibility(View.VISIBLE);

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail.getText().toString(),
                            mPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: AuthState: " + FirebaseAuth.getInstance()
                                                .getCurrentUser().getUid());
                                        mProgressBar.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Authenticated with: " +
                                                        FirebaseAuth.getInstance().getCurrentUser().getUid(),
                                                Toast.LENGTH_LONG).show();
                                        Log.d(TAG, "onComplete: Navigating to individual activity");
                                        Intent intent = new Intent(getContext(), IndividualActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Authentication failed: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
//                    hideSoftKeyboard();
                } else {
                    Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.tv_register:
                Log.d(TAG, "onClick: button register clicked");
                mNavController.navigate(R.id.action_loginFragment_to_registerFragment);
                break;

            case R.id.back:
                Log.d(TAG, "onClick: back clicked");
                mNavController.navigate(R.id.action_loginFragment_to_startFragment);
                break;

            case R.id.google:

            case R.id.facebook:

            case R.id.twitter:

            case R.id.btn_facebook:

            case R.id.btn_google:

            case R.id.btn_twitter:
                Toast.makeText(getContext(), "This feature is not available yet. " +
                        "Kindly check back later", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private boolean isEmpty(String String) {
        return String.equals("");
    }

    private void hideSoftKeyboard() {
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*
    -------------------------------- Firebase Setup -------------------------
    */
    private void setupFirebaseAuth() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null) {
                    Log.d(TAG, "onAuthStateChanged: signed in" + user.getUid());
                    checkIfUserIsVerified(user);
                } else {
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }

            }
        };
    }

    public void checkIfUserIsVerified(FirebaseUser user) {
        if(user.isEmailVerified()) {
            Log.d(TAG, "onAuthStateChanged: signed in" + user.getUid());
            Toast.makeText(getContext(), "Authenticated with: " + user.getEmail(),
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getContext(), IndividualActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Check your Email inbox for a verification " +
                    "link", Toast.LENGTH_LONG).show();
            FirebaseAuth.getInstance().signOut();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
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
            Log.d(TAG, "checkAuthenticationState: User is null.");
        } else {
            Log.d(TAG, "checkAuthenticationState: User is authenticated");
//            checkIfIsIndividual();
            startActivity(new Intent(getContext(), IndividualActivity.class));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }
}