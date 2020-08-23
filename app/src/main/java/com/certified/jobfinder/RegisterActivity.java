package com.certified.jobfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "RegisterActivity";

    //    widgets
    private EditText mEmail, mPassword, mConfirmPassword;
    private TextView mLogin;
    private Button mRegister;
    private ProgressBar mProgressBar;
    private ImageView mBack, facebook, google, twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmail = findViewById(R.id.et_email);
        mPassword = findViewById(R.id.et_password);
        mConfirmPassword = findViewById(R.id.et_confirm_password);
        mProgressBar = findViewById(R.id.progressBar);
        mRegister = findViewById(R.id.btn_register);
        mLogin = findViewById(R.id.tv_login);
        mBack = findViewById(R.id.back);
        facebook = findViewById(R.id.facebook);
        google = findViewById(R.id.google);
        twitter = findViewById(R.id.twitter);

        hideDialog();

        mBack.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mLogin.setOnClickListener(this);
        facebook.setOnClickListener(this);
        google.setOnClickListener(this);
        twitter.setOnClickListener(this);
        hideSoftKeyboard();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_register:
                registrationCheck();
                break;

            case R.id.back:
                startActivity(new Intent(this, StartActivity.class));
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

            case R.id.tv_login:
                startActivity(new Intent(this, LoginActivity.class));
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }

    }

    private void hideDialog() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showDialog() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void registrationCheck() {

//        check for null valued EditText fields
        if(!isEmpty(mEmail.getText().toString())
                && !isEmpty(mPassword.getText().toString())
                && !isEmpty(mConfirmPassword.getText().toString())) {

//            check if user has a company email address
            if(isValidDomain(mEmail.getText().toString())) {

//                check if passwords match
                if(doStringsMatch(mPassword.getText().toString(), mConfirmPassword.getText().toString())) {
                    registerNewEmail(mEmail.getText().toString(), mPassword.getText().toString());
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                }
            }
            else {
                Toast.makeText(RegisterActivity.this, "Please register with company email", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(RegisterActivity.this, "You must fill out all fields", Toast.LENGTH_LONG).show();
        }
    }

    private void registerNewEmail(String email, String password) {
        showDialog();

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
                            redirectToLoginScreen();
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "Unable to register",
                                    Toast.LENGTH_LONG).show();
                        }
                        hideDialog();
                    }
                });
    }

    private void redirectToLoginScreen() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    private void sendVerificationEmail() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Verification email sent",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, "Couldn't send verification " +
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

    private void hideSoftKeyboard() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        checkAuthenticationState();
    }

    private void checkAuthenticationState() {
        Log.d(TAG, "checkAuthenticationState: checking authentication state");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(TAG, "checkAuthenticationState: User is null. Navigating back to home screen");

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Log.d(TAG, "checkAuthenticationState: User is authenticated");
        }
    }
}