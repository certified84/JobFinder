package com.certified.jobfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "LoginActivity";

    //    firebase
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //    widgets
    private TextView mForgotPassword, mResendEmail, mRegister;
    private EditText mEmail, mPassword;
    private ProgressBar mProgressBar;
    private Button mLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: Login Activity called");

        mEmail =  findViewById(R.id.et_email);
        mPassword = findViewById(R.id.et_password);
        mProgressBar = findViewById(R.id.progressBar);
        mLogin = findViewById(R.id.btn_login);
        mForgotPassword = findViewById(R.id.tv_password_reset);
        mRegister = findViewById(R.id.tv_register);

        setupFirebaseAuth();

        mLogin.setOnClickListener(this);
        mRegister.setOnClickListener(this);

        hideDialog();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_register:
                Log.d(TAG, "onClick: TextView Register clicked. Navigating to register activity");
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;

            case R.id.btn_login:
                if (!isEmpty(mEmail.getText().toString()) && !isEmpty(mPassword.getText().toString())) {
                    Log.d(TAG, "onClick: Attempting to authenticate");
                    showDialog();

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(mEmail.getText().toString(),
                            mPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    hideDialog();
                                    Log.d(TAG, "onComplete: Navigating to individual activity");
                                    Intent intent = new Intent(LoginActivity.this, IndividualActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Authentication failed",
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        }
                    });
                    hideSoftKeyboard();
                } else {
                    Toast.makeText(LoginActivity.this, "All fields are required",
                            Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.back:
                Log.d(TAG, "onClick: back button clicked. Navigating to start activity");
                startActivity(new Intent(this, StartActivity.class));
                break;

            case R.id.google:

            case R.id.facebook:

            case R.id.twitter:

            case R.id.btn_facebook:

            case R.id.btn_google:

            case R.id.btn_twitter:
                Toast.makeText(this, "This feature is not available yet. " +
                        "Kindly check back later", Toast.LENGTH_LONG).show();
                break;

//            case R.id.tv_resend_email:
//                Log.d(TAG, "onClick: Launching resend verification dialog");
//                ResendVerificationDialog dialog = new ResendVerificationDialog();
//                dialog.show(getSupportFragmentManager(), "dialog_resend_email_verification");

            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    private boolean isEmpty(String String) {
        return String.equals("");
    }

    private void hideDialog() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

    private void checkIfUserIsVerified(FirebaseUser user) {
        if(user.isEmailVerified()) {
            Log.d(TAG, "onAuthStateChanged: signed in" + user.getUid());
            Toast.makeText(LoginActivity.this, "Authenticated with: " + user.getEmail(),
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(LoginActivity.this, IndividualActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "Check your Email inbox for a verification " +
                    "link", Toast.LENGTH_LONG).show();
            FirebaseAuth.getInstance().signOut();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
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
            startActivity(new Intent(this, IndividualActivity.class));
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
        }
    }
}
